package com.sup.market.service;

import com.alibaba.fastjson.JSON;
import com.sup.common.mq.MqTag;
import com.sup.common.mq.MqTopic;
import com.sup.common.mq.UserStateMessage;
import com.sup.common.mq.UserStateMessageWrapper;
import com.sup.market.util.ToolUtils;
import com.sup.market.util.WheelTimer;
import org.apache.log4j.Logger;
import org.apache.rocketmq.common.message.Message;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by xidongzhou1 on 2019/9/23.
 */
@Service
public class OnTimeDeliveryService {
  public static Logger logger = Logger.getLogger(OnTimeDeliveryService.class);

  @Value("${ontime-delivery.redisson.host}")
  private String redisson_host;

  @Value("${ontime-delivery.redisson.port}")
  private Integer redisson_port;

  @Value("${ontime-delivery.redisson.auth}")
  private String auth;

  private RedissonClient redisson_client;
  private RMap<String, UserStateMessageWrapper> pending_messages;

  @Autowired
  MqProducerService producerService;

  private WheelTimer timer;
  private WheelTimer.TimerCb tcb;


  @PostConstruct
  public void Init() {
    Config redlock_cfg = new Config();
    redlock_cfg.useSingleServer().setAddress(String.format("redis://%s:%d", redisson_host, redisson_port)).setTimeout(10000).setPassword(auth);
    redisson_client = Redisson.create(redlock_cfg);
    pending_messages = redisson_client.getMap("ontime_delivery_servcie");
    logger.info("create_redisson_client_succ: " + redlock_cfg.toString());

    timer = new WheelTimer();
    tcb = ctx -> {
      try {
        UserStateMessageWrapper user_msg = (UserStateMessageWrapper) ctx;
        if (!pending_messages.containsKey(user_msg.getMsg_id())) {
          logger.warn("ignore_duplicate_message: " + JSON.toJSONString(user_msg));
          return;
        }

        Message msg = new Message(user_msg.getTopic(), user_msg.getTag(), user_msg.getMsg_id(), JSON.toJSONString(user_msg).getBytes());
        producerService.sendMessage(msg);
        pending_messages.remove(user_msg.getMsg_id());
      } catch (Exception e) {
      }
    };
    logger.info("create_timer_and_cb_succ");

    pending_messages.forEach((key, msg) -> {
      UserStateMessageWrapper user_msg_wrapper = (UserStateMessageWrapper)msg;
      long fresh = ToolUtils.NormTime(user_msg_wrapper.getCreate_time()).getTime() - System.currentTimeMillis();
      if (fresh < 0) {
        logger.warn("expire_message:" + JSON.toJSONString(user_msg_wrapper));
        if (fresh > (-30*60*1000l)) {
          tcb.Run(user_msg_wrapper);
        }
        return;
      }
      timer.AddTimer(ToolUtils.NormTime(msg.getCreate_time()).getTime(), tcb, msg);
      logger.info(String.format("restore_msg: %s", JSON.toJSONString(msg)));
    });

    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(1000l);
            timer.Run();
          } catch (Exception e) {
          }
        }
      }
    }).start();
  }

  public void ScheduleMessage(String topic, String tag, UserStateMessage msg) {
    UserStateMessageWrapper wrapper = new UserStateMessageWrapper();
    wrapper.setCreate_time(msg.getCreate_time());
    wrapper.setExt(msg.getExt());
    wrapper.setMobile(msg.getMobile());
    wrapper.setState(msg.getState());
    wrapper.setUser_id(msg.getUser_id());
    wrapper.setRel_id(msg.getRel_id());
    wrapper.setTopic(topic);
    wrapper.setTag(tag);
    String sig = String.format("%d|%s|%s|%s|%s|%s|%s", wrapper.getUser_id(), wrapper.getTopic(), wrapper.getTag(), wrapper.getRel_id(), wrapper.getMobile(), wrapper.getCreate_time(), wrapper.getExt());
    wrapper.setMsg_id(sig);

    long expire = ToolUtils.NormTime(wrapper.getCreate_time()).getTime();
    if ((expire < (System.currentTimeMillis() +  60*1000l))) {
      tcb.Run(wrapper);
    } else {
      pending_messages.put(wrapper.getMsg_id(), wrapper);
      timer.AddTimer(expire, tcb, wrapper);
    }
  }
}
