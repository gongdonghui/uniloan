package com.sup.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.bean.*;
import com.sup.core.LoginInfo;
import com.sup.core.LoginRequired;
import com.sup.core.Result;
import com.sup.mapper.*;
import com.sup.service.RedisClient;
import com.sup.service.SkyLineSmsService;
import com.sup.util.ToolUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/authmaterial")
public class AuthMaterialController {
  public static Logger logger = Logger.getLogger(AuthMaterialController.class);

  @Value("upload_path")
  private String upload_path;
  @Autowired
  RedisClient rc;
  @Autowired
  SkyLineSmsService sms_service;
  @Autowired
  TbUserRegistInfoMapper tb_user_regist_info_mapper;
  @Autowired
  TbUserCitizenIdentityCardInfoMapper tb_user_citizen_identity_card_info_mapper;
  @Autowired
  TbUserBasicInfoMapper tb_user_basic_info_mapper;
  @Autowired
  TbUserEmploymentInfoMapper tb_user_employment_info_mapper;
  @Autowired
  TbUserEmergencyContactMapper tb_user_emergency_contact_mapper;
  @Autowired
  TbUserBankAccountInfoMapper tb_user_bank_account_mapper;

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "upload_citizen_identity_card_info", produces = "application/json;charset=UTF-8")
  public Object UpdateCIC(@LoginInfo LoginInfoCtx li, @RequestBody TbUserCitizenIdentityCardInfoBean bean) {
    if (bean.getInfo_id() == null) {
      bean.setInfo_id(ToolUtils.getToken());
      bean.setUser_id(li.getUser_id());
      bean.setCreate_time(new Date());
    }
    if (bean.getId() == null) {
      tb_user_citizen_identity_card_info_mapper.insert(bean);
    } else {
      tb_user_citizen_identity_card_info_mapper.updateById(bean);
    }
    return Result.succ("succ");
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "upload_basic_info", produces = "application/json;charset=UTF-8")
  public Object updateBasicInfo(@LoginInfo LoginInfoCtx li, @RequestBody TbUserBasicInfoBean bean) {
    if (bean.getInfo_id() == null) {
      bean.setInfo_id(ToolUtils.getToken());
      bean.setUser_id(li.getUser_id());
      bean.setCreate_time(new Date());
    }
    if (bean.getId() == null) {
      tb_user_basic_info_mapper.insert(bean);
    } else {
      tb_user_basic_info_mapper.updateById(bean);
    }
    return Result.succ("succ");
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "upload_employment_info", produces = "application/json;charset=UTF-8")
  public Object updateEmploymentInfo(@LoginInfo LoginInfoCtx li, @RequestBody TbUserEmploymentInfoBean bean) {
    if (bean.getInfo_id() == null) {
      bean.setInfo_id(ToolUtils.getToken());
      bean.setUser_id(li.getUser_id());
      bean.setCreate_time(new Date());
    }
    if (bean.getId() == null) {
      tb_user_employment_info_mapper.insert(bean);
    } else {
      tb_user_employment_info_mapper.updateById(bean);
    }
    return Result.succ("succ");
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "upload_bank_account_info", produces = "application/json;charset=UTF-8")
  public Object updateEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody TbUserBankAccountInfoBean bean) {
    if (bean.getInfo_id() == null) {
      bean.setInfo_id(ToolUtils.getToken());
      bean.setUser_id(li.getUser_id());
      bean.setCreate_time(new Date());
    }
    if (bean.getId() == null) {
      tb_user_bank_account_mapper.insert(bean);
    } else {
      tb_user_bank_account_mapper.updateById(bean);
    }
    return Result.succ("succ");
  }


  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "delete_emergency_contact", produces = "application/json;charset=UTF-8")
  public Object deleteEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody TbUserEmergencyContactBean bean) {
    if (bean.getId() != null) {
      tb_user_emergency_contact_mapper.deleteById(bean.getId());
    }
    return Result.succ("ok");
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "upload_emergency_contact", produces = "application/json;charset=UTF-8")
  public Object updateEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody List<TbUserEmergencyContactBean> beans) {
    String info_id = null;
    for (TbUserEmergencyContactBean bean : beans) {
      if (bean.getInfo_id() != null) {
        info_id = bean.getInfo_id();
        break;
      }
    }
    final String the_info_id = info_id;
    beans.forEach(bean -> {bean.setInfo_id(the_info_id);});
    beans.forEach(bean -> {
      if (bean.getInfo_id() == null) {
        bean.setInfo_id(ToolUtils.getToken());
        bean.setUser_id(li.getUser_id());
        bean.setCreate_time(new Date());
      }
      if (bean.getId() == null) {
        tb_user_emergency_contact_mapper.insert(bean);
      } else {
        tb_user_emergency_contact_mapper.updateById(bean);
      }
    });
    return Result.succ("succ");
  }

  @LoginRequired
  @ResponseBody
  @PutMapping("upload_img")
  public Object uploadFile(@RequestParam("upload_file") MultipartFile file, @LoginInfo LoginInfoCtx li) throws Exception {
    String tag = String.format("%d_%s", li.getUser_id(), (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()));
    String abs_path = upload_path + "/" + tag;
    logger.info("will save to: " + abs_path);
    file.transferTo(new File(abs_path));
    return Result.succ(tag);
  }
}

