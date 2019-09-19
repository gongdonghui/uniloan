package com.sup.core.service;

import org.apache.log4j.Logger;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by xidongzhou1 on 2019/9/16.
 */
@Service
public class MqProducerService {
  @Value("${rocket.producer.endpoint}")
  private String endpoint;

  @Value("${rocket.producer.group}")
  private String group;

  private DefaultMQProducer producer;
  public static Logger logger = Logger.getLogger(MqProducerService.class);

  @PostConstruct
  public void init() throws Exception {
    logger.info("init_rocket_mq_producer: " + endpoint + "@" + group);
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
