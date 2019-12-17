package com.sup.backend.service;

import org.apache.log4j.Logger;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


@Service
public class RedissonService {
  private static Logger logger = Logger.getLogger(RedissonService.class);
  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private Integer port;

  @Value("${spring.redis.password}")
  private String password;

  @Bean(name = "red_client")
  public RedissonClient getRedClient() {
    String redis_cfg = String.format("redis://%s:%d", host, port);
    Config redlock_config = new Config();
    redlock_config.useSingleServer().setAddress(redis_cfg).setDatabase(1).setPassword(password).setTimeout(30000);
    logger.info("create_red_client_using_config: " + redis_cfg);
    return Redisson.create(redlock_config);
  }
}
