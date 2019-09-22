package com.sup.backend.service;

import com.sup.backend.util.ToolUtils;
import org.apache.log4j.Logger;
import org.nutz.ssdb4j.impl.SimpleClient;
import org.nutz.ssdb4j.impl.SocketSSDBStream;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;
import org.nutz.ssdb4j.spi.SSDBStream;
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

  @Value("${ssdb.auth}")
  private String auth;

  private SSDB ssdb;

  @PostConstruct
  public void init() {
    try {
      SSDBStream stream = new SocketSSDBStream(host, port, timeout, auth.getBytes());
      ssdb = new SimpleClient(stream);
    } catch (Exception e) {
      logger.error(String.format("init_ssdb_exeception: %s", ToolUtils.GetTrace(e)));
    }
  }

  public byte[] GetBytes(String key) {
    Response resp =  ssdb.get(key);
    if (resp.notFound()) {
      return null;
    }
    return resp.datas.get(0);
  }

  public boolean SetBytes(String key, byte[] val) {
    Response resp = ssdb.set(key, val);
    return resp.ok();
  }

  public boolean Set(String key, String val) {
    Response resp = ssdb.set(key, val);
    return resp.ok();
  }

}
