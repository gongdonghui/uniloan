package com.sup.backend.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableList;
import com.sup.backend.mapper.TbMarketPlanMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbMarketPlanBean;
import com.sup.common.mq.UserStateMessage;
import org.apache.log4j.Logger;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by xidongzhou1 on 2019/9/16.
 */
@Service
public class MqConsumerService {
  @Value("${rocket.consumer.endpoint}")
  private String endpoint;

  @Value("${rocket.consumer.group}")
  private String group;

  @Autowired
  private TbMarketPlanMapper tb_market_plan_mapper;

  private DefaultMQPushConsumer consumer;

  public static Logger logger = Logger.getLogger(MqConsumerService.class);


  private Map<String, Set<String>> subscribe_map = new HashMap<>();

  private void DoSubscribe() throws Exception {
    List<TbMarketPlanBean> plans = tb_market_plan_mapper.selectList(null);
    Map<String, Set<String>> new_subs = new HashMap<>();
    for (TbMarketPlanBean plan : plans) {
      if (plan.getStatus().equals(0)) {
        continue;
      }
      String topic = plan.getTopic();
      String tag = plan.getTag();
      if (!new_subs.containsKey(topic)) {
        new_subs.put(topic, new HashSet<>());
      }
      new_subs.get(topic).add("*");
    }

    if (new_subs.isEmpty()) {
      new_subs.put("dummy", new HashSet<String>(){{add("dummy");}});
    }

    if (subscribe_map.equals(new_subs)) {
      return;
    }

    // do subscribe !!
    consumer.suspend();
    for (String old_topic : subscribe_map.keySet()) {
      consumer.unsubscribe(old_topic);
    }
    for (Map.Entry<String, Set<String>> kv : new_subs.entrySet()) {
      String topic = kv.getKey();
      String pattern = String.join("||", kv.getValue());
      consumer.subscribe(topic, pattern);
    }
    consumer.resume();
    subscribe_map = new_subs;
    logger.info("final_topic_info:" + JSON.toJSONString(subscribe_map));
  }

  @PostConstruct
  public void init() throws Exception {
    logger.info("init_rocket_mq_consumer: " + endpoint + "@" + group);
    consumer = new DefaultMQPushConsumer();
    consumer.setNamesrvAddr(endpoint);
    consumer.setConsumerGroup(group);
    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
    consumer.setConsumeThreadMax(1);
    consumer.setConsumeThreadMin(1);
    consumer.setConsumeMessageBatchMaxSize(1);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      consumer.shutdown();
      logger.info("shutdown the mq consumer...");
    }));

    DoSubscribe();

    consumer.registerMessageListener(new MessageListenerConcurrently() {
      @Override
      public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
          for (MessageExt msg : list) {
            UserStateMessage state_message = JSON.parseObject(msg.getBody(), UserStateMessage.class);
            HandleMessage(msg.getTopic(), msg.getTags(), state_message);
          }
        } catch (Exception e) {
          System.out.println("exception: " + ToolUtils.GetTrace(e));
        } finally {
          return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
      }
    });
    consumer.start();
    // every 10 minutes to update topic, tag !!
    new Thread(() -> {
      try {
        while (true) {
          Thread.sleep(10*60*1000l);
          DoSubscribe();
        }
      } catch (Exception e) {
      }
    }).start();
  }

  public void HandleMessage(String topic, String tag, UserStateMessage msg) throws Exception {
    logger.info(String.format("[recv_msg]:[%s|%s|%s]", topic, tag, JSON.toJSONString(msg)));
  }
}
