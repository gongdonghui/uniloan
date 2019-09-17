package com.sup.backend.service;

import com.alibaba.fastjson.JSON;
import com.sup.backend.mapper.TbMarketPlanMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbMarketPlanBean;
import com.sup.common.mq.UserStateMessage;
import org.apache.log4j.Logger;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
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
public class MqProducerService {
  @Value("${rocket.producer.endpoint")
  private String endpoint;

  @Value("${rocket.producer.group}")
  private String group;

  private DefaultMQProducer producer;
  public static Logger logger = Logger.getLogger(MqProducerService.class);

  @PostConstruct
  public void init() throws Exception {
    logger.info("do mqclient producer post_init...");
    producer = new DefaultMQProducer();
    producer.setProducerGroup(group);
    producer.setNamesrvAddr(endpoint);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      producer.shutdown();
      logger.info("shutdown the mq producer ...");
    }));
    producer.start();
  }

  public SendResult sendMessage(Message msg) throws Exception {
    return producer.send(msg);
  }

}
