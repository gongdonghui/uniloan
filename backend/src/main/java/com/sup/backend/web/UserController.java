package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.sup.backend.bean.AppApplyInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.service.MqProducerService;
import com.sup.backend.service.SSDBClient;
import com.sup.common.bean.TbProductInfoBean;
import com.sup.common.bean.*;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.*;
import com.sup.backend.service.RedisClient;
import com.sup.backend.service.SkyLineSmsService;
import com.sup.backend.util.ToolUtils;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.mq.MqTag;
import com.sup.common.mq.MqTopic;
import com.sup.common.mq.UserStateMessage;
import com.sup.common.util.Result;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.applet.resources.MsgAppletViewer_zh_TW;

import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/user")
public class UserController {
  public static Logger logger = Logger.getLogger(UserController.class);
  @Value("upload_path")
  private String upload_path;
  @Autowired
  RedisClient rc;
  @Autowired
  SkyLineSmsService sms_service;
  @Autowired
  TbUserRegistInfoMapper tb_user_regist_info_mapper;
  @Autowired
  MqProducerService mqProducerService;
  @Autowired
  SSDBClient ssdb;

  @ResponseBody
  @RequestMapping(value = "issue_verify_code", produces = "application/json;charset=UTF-8")
  public Object IssueVerifyCode(@RequestParam(value="mobile") String mobile) throws Exception {
    // check pre_mobile !!
    String verify_key = String.format("verify_%s", mobile);
    if (rc.Exist(verify_key)) {
      return Result.succ("already_send_please_wait");
    }

    String verify_code = "1234"; //String.valueOf(RandomUtils.nextInt(1000, 9999));

    // send to queue to send to user ....
    UserStateMessage usm = new UserStateMessage();
    usm.setUser_id(-1);
    usm.setState(MqTag.kIssueVerifyCode);
    usm.setMobile(mobile);
    usm.setExt((new JSONObject(ImmutableMap.of("verify_code", verify_code))).toJSONString());
    usm.setCreate_time(ToolUtils.NormTime(new Date()));
    Message msg = new Message(MqTopic.kUserState, MqTag.kIssueVerifyCode, "", JSON.toJSONString(usm).getBytes());

    mqProducerService.sendMessage(msg);
    // save to redis to avoid duplicating sending message
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

  @LoginRequired
  @ResponseBody
  @PutMapping("upload")
  public Object uploadFile(@RequestParam("upload_file") MultipartFile file, @LoginInfo LoginInfoCtx li) throws Exception {
    String tag = String.format("%d_%s", li.getUser_id(), (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()));
    //String abs_path = upload_path + "/" + tag;
    //logger.info("will save to: " + abs_path);
    //file.transferTo(new File(abs_path));
    String file_content = CharStreams.toString(new InputStreamReader(file.getInputStream()));
    boolean succ = ssdb.Set(tag, file_content);
    if (succ) {
      return Result.succ(tag);
    } else {
      return Result.fail("save_to_ssdb_error");
    }
  }
}

