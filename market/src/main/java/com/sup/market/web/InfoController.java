package com.sup.market.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sup.common.util.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.print.attribute.standard.Media;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/plan")
public class InfoController {
  public static Logger logger = Logger.getLogger(InfoController.class);

  @Autowired
  private CronTask cron_task;

  @ResponseBody
  @RequestMapping(value = "test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object Test() {
    return Result.succ("succ");
  }

  @ResponseBody
  @RequestMapping(value = "exec_repay_user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object ExecRepayUser() {
    logger.info("manual_call_notify_repay_user");
    cron_task.NotifyRepayUser();
    return Result.succ("ok");
  }
}

