package com.sup.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableList;
import com.sup.bean.LoginInfoCtx;
import com.sup.bean.TbUserRegistInfoBean;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/user")
public class UserController {
  public static Logger logger = Logger.getLogger(UserController.class);
  @Autowired
  RedisClient rc;

  @Autowired
  SkyLineSmsService sms_service;

  @Autowired
  TbUserRegistInfoMapper tb_user_regist_info_mapper;

  @ResponseBody
  @RequestMapping(value = "issue_verify_code", produces = "application/json;charset=UTF-8")
  public Object IssueVerifyCode(@RequestParam(value="mobile") String mobile) throws Exception {
    // check pre_mobile !!
    String verify_key = String.format("verify_%s", mobile);
    if (rc.Exist(verify_key)) {
      return Result.succ("");
    }

    String verify_code = String.valueOf(RandomUtils.nextInt(1000, 9999));
    //sms_service.SendSms(ImmutableList.of(mobile), verify_code);
    rc.Set(verify_key, verify_code, 5l, TimeUnit.MINUTES);
    return Result.succ("succ");
  }

  @ResponseBody
  @RequestMapping(value = "login", produces = "application/json;charset=UTF-8")
  public Object Login(@RequestParam(value="mobile") String mobile, @RequestParam(value="verify_code") String verify_code) {
    String verify_key = String.format("verify_%s", mobile);
    String verify_ans = rc.Get(verify_key);
    if (verify_ans == null || (!verify_ans.equals(verify_code))) {
      return Result.fail("error_verify_code");
    }

    TbUserRegistInfoBean regist_info = tb_user_regist_info_mapper.selectOne(new QueryWrapper<TbUserRegistInfoBean>().eq("mobile", mobile));
    if (regist_info == null) {
      regist_info = new TbUserRegistInfoBean();
      regist_info.setMobile(mobile);
      regist_info.setCreate_time(new Date());
      tb_user_regist_info_mapper.insert(regist_info);
      logger.info(String.format("---new_user: %s ---", JSON.toJSONString(regist_info)));
    }

    String token = ToolUtils.getToken();
    LoginInfoCtx li = new LoginInfoCtx(regist_info.getId(), ToolUtils.NormTime(new Date()));
    rc.Set(token, li.toString(), 30l, TimeUnit.DAYS);
    return Result.succ(token);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "info", produces = "application/json;charset=UTF-8")
  public Object TestMessage(@LoginInfo LoginInfoCtx li) {
    JSONObject parms = new JSONObject();
    parms.put("li", li);
    return Result.succ(parms);
  }
}

