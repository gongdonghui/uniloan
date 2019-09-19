package com.sup.backend.service;

import com.sup.backend.util.ToolUtils;
import org.apache.log4j.Logger;
import org.nutz.ssdb4j.impl.SimpleClient;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by xidongzhou1 on 2019/9/18.
 */
@Service
public class SSDBClient {
  public static Logger logger = Logger.getLogger(SSDBClient.class);
  @Value("${ssdb.host}")
  private String host;

  @Value("${ssdb.port}")
  private Integer port;

  @Value("${ssdb.timeout}")
  private Integer timeout;

  private SSDB ssdb;

  @PostConstruct
  public void init() {
    try {
      ssdb = new SimpleClient(host, port, timeout);
    } catch (Exception e) {
      logger.error(String.format("init_ssdb_exeception: %s", ToolUtils.GetTrace(e)));
    }
  }

  public String Get(String key) {
    Response resp =  ssdb.get(key);
    if (resp.notFound()) {
      return null;
    }
    return resp.asString();
  }

  public boolean Set(String key, String val) {
    Response resp = ssdb.set(key, val);
    return resp.ok();
  }
}
