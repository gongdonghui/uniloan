package com.sup.core.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Service
public class RedisClient {
  @Resource
  private RedisTemplate<String, String> redisTemplate;

  public void Set(String key, String val, Long to, TimeUnit tu) {
    ValueOperations<String, String> vo = redisTemplate.opsForValue();
    vo.set(key, val, to, tu);
  }

  public String Get(String key) {
    ValueOperations<String, String> vo = redisTemplate.opsForValue();
    String val = vo.get(key);
    return val;
  }

  public boolean Exist(String key) {
    return redisTemplate.hasKey(key);

  }
}
