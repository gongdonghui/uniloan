package com.sup.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableList;
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
  @RequestMapping(value = "test", produces = "application/json;charset=UTF-8")
  public Object Test() throws Exception {
    // check pre_mobile !!
    return Result.succ(tb_user_regist_info_mapper.selectList(null));
  }

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


  //////////////////////////////
  // 申请资料CRUD接口
  //////////////////////////////

  // add/update/get user IDCard info
  @ResponseBody
  @RequestMapping(value = "idCard/add", produces = "application/json;charset=UTF-8")
  public Object addUserIdCardInfo(@RequestBody TbUserCitizenIdentityCardInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "idCard/update", produces = "application/json;charset=UTF-8")
  public Object updateUserIdCardInfo(@RequestBody TbUserCitizenIdentityCardInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "idCard/get", produces = "application/json;charset=UTF-8")
  public Object getUserIdCardInfo(@RequestParam(value = "userId") String userId) {
    return null;
  }

  // add/update/get user basic info
  @ResponseBody
  @RequestMapping(value = "basic/add", produces = "application/json;charset=UTF-8")
  public Object addUserBasicInfo(@RequestBody TbUserBasicInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "basic/update", produces = "application/json;charset=UTF-8")
  public Object updateUserBasicInfo(@RequestBody TbUserBasicInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "basic/get", produces = "application/json;charset=UTF-8")
  public Object getUserBasicInfo(@RequestParam(value = "userId") String userId) {
    return null;
  }


  // add/update/get user contact info
  @ResponseBody
  @RequestMapping(value = "contact/add", produces = "application/json;charset=UTF-8")
  public Object addUserContactInfo(@RequestBody TbUserEmergencyContactBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "contact/update", produces = "application/json;charset=UTF-8")
  public Object updateUserContactInfo(@RequestBody TbUserEmergencyContactBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "contact/get", produces = "application/json;charset=UTF-8")
  public Object getUserContactInfo(@RequestParam(value = "userId") String userId) {
    return null;
  }


  // add/update/get user employment info
  @ResponseBody
  @RequestMapping(value = "employment/add", produces = "application/json;charset=UTF-8")
  public Object addUserEmploymentInfo(@RequestBody TbUserEmploymentInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "employment/update", produces = "application/json;charset=UTF-8")
  public Object updateUserEmploymentInfo(@RequestBody TbUserEmploymentInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "employment/get", produces = "application/json;charset=UTF-8")
  public Object getUserEmploymentInfo(@RequestParam(value = "userId") String userId) {
    return null;
  }


  // add/update/get user bank account info
  @ResponseBody
  @RequestMapping(value = "bank/add", produces = "application/json;charset=UTF-8")
  public Object addUserBankInfo(@RequestBody TbUserBankAccountInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "bank/update", produces = "application/json;charset=UTF-8")
  public Object updateUserBankInfo(@RequestBody TbUserBankAccountInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "bank/get", produces = "application/json;charset=UTF-8")
  public Object getUserBankInfo(@RequestParam(value = "userId") String userId) {
    return null;
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

