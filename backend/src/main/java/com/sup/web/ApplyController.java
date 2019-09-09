package com.sup.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.bean.*;
import com.sup.core.LoginInfo;
import com.sup.core.LoginRequired;
import com.sup.core.Result;
import com.sup.mapper.TbUserRegistInfoMapper;
import com.sup.service.RedisClient;
import com.sup.service.SkyLineSmsService;
import com.sup.util.ToolUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/apply")
public class ApplyController {
  public static Logger logger = Logger.getLogger(ApplyController.class);

  // add/update/get apply info
  @ResponseBody
  @RequestMapping(value = "add", produces = "application/json;charset=UTF-8")
  public Object addApplyInfo(String userId, String productId, String channelId, String appId) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
  public Object getApplyInfoList(String userId) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "current", produces = "application/json;charset=UTF-8")
  public Object getCurrentApplyInfo(String userId) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "detail", produces = "application/json;charset=UTF-8")
  public Object getApplyInfo(String applyId) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "repayPlan/get", produces = "application/json;charset=UTF-8")
  public Object getRepayPlan(String userId, String applyId) {
    return null;
  }
}

