package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sup.backend.bean.AppApplyInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.bean.TbProductInfoBean;
import com.sup.common.bean.*;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.core.Result;
import com.sup.backend.mapper.*;
import com.sup.backend.service.RedisClient;
import com.sup.backend.service.SkyLineSmsService;
import com.sup.backend.util.ToolUtils;
import com.sup.common.loan.ApplyStatusEnum;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
  TbUserCitizenIdentityCardInfoMapper tb_user_citizen_identity_card_info_mapper;
  @Autowired
  TbUserBasicInfoMapper tb_user_basic_info_mapper;
  @Autowired
  TbUserEmploymentInfoMapper tb_user_employment_info_mapper;
  @Autowired
  TbUserEmergencyContactMapper tb_user_emergency_contact_mapper;
  @Autowired
  TbUserBankAccountInfoMapper tb_user_bank_account_mapper;
  @Autowired
  TbApplyMaterialInfoMapper tb_apply_info_material_mapper;
  @Autowired
  TbProductInfoMapper tb_product_info_mapper;
  @Autowired
  ApplyInfoMapper apply_info_mapper;
  @Autowired
  TbRepayPlanMapper tb_repay_plan_mapper;

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
    /*
  1. check is used to apply,
    1.1   no, return the one !
    2.2   check expire_time and now
      2.2.1   expire > now, copy return the copy_one !!
      2.2.2   expire < now, return null !!
   */

  // add/update/get user IDCard info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"idCard/add", "idCard/update"}, produces = "application/json;charset=UTF-8")
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
  @RequestMapping(value = "idCard/get", produces = "application/json;charset=UTF-8")
  public Object getUserIdCardInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserCitizenIdentityCardInfoBean> mapper = tb_user_citizen_identity_card_info_mapper;
    Wrapper<TbUserCitizenIdentityCardInfoBean> cond = new QueryWrapper<TbUserCitizenIdentityCardInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time");
    TbUserCitizenIdentityCardInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return Result.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return old_bean;
  }

  // add/update/get user basic info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"basic/add", "basic/update"}, produces = "application/json;charset=UTF-8")
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
  @RequestMapping(value = "basic/get", produces = "application/json;charset=UTF-8")
  public Object getUserBasicInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserBasicInfoBean> mapper = tb_user_basic_info_mapper;
    Wrapper<TbUserBasicInfoBean> cond = new QueryWrapper<TbUserBasicInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time");
    TbUserBasicInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return Result.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return old_bean;
  }


  // add/update/get user contact info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"contact/add", "contact/update"}, produces = "application/json;charset=UTF-8")
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
  @RequestMapping(value = "contact/get", produces = "application/json;charset=UTF-8")
  public Object getUserContactInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserEmergencyContactBean> mapper = tb_user_emergency_contact_mapper;
    Wrapper<TbUserEmergencyContactBean> cond = new QueryWrapper<TbUserEmergencyContactBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time");
    TbUserEmergencyContactBean old_bean = mapper.selectOne(cond);
    if (old_bean == null) {
      return Result.succ(null);
    }
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return Result.succ(null);
    }

    List<TbUserEmergencyContactBean> cands = tb_user_emergency_contact_mapper.selectList(new QueryWrapper<TbUserEmergencyContactBean>().eq("info_id", old_bean.getInfo_id()));
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      for (int i = 0; i < cands.size(); ++i) {
        TbUserEmergencyContactBean b = cands.get(i);
        b.setId(null);
        b.setInfo_id(new_info_id);
        b.setCreate_time(new Date());
        b.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
        tb_user_emergency_contact_mapper.insert(b);
      }
    }
    return cands;
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "contact/del", produces = "application/json;charset=UTF-8")
  public Object deleteEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody TbUserEmergencyContactBean bean) {
    if (bean.getId() != null) {
      tb_user_emergency_contact_mapper.deleteById(bean.getId());
    }
    return Result.succ("ok");
  }

  // add/update/get user employment info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"employment/add", "employment/update"}, produces = "application/json;charset=UTF-8")
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
  @RequestMapping(value = "product/list", produces = "application/json;charset=UTF-8")
  public Object getProduct(@LoginInfo LoginInfoCtx li) {
    List<TbProductInfoBean> beans = tb_product_info_mapper.selectList(null);
    return Result.succ(beans);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "apply/list", produces = "application/json;charset=UTF-8")
  public Object listApply(@LoginInfo LoginInfoCtx li) {
    QueryWrapper<TbApplyInfoBean> query = new QueryWrapper<TbApplyInfoBean>().eq("user_id", li.getUser_id());
    List<TbApplyInfoBean> beans = apply_info_mapper.selectList(query);
    JSONObject all_applys = new JSONObject();
    List<AppApplyInfo> ret_app_beans = new ArrayList<>();
    for (TbApplyInfoBean bean : beans) {
      if (!bean.getStatus().equals(ApplyStatusEnum.APPLY_FINAL_PASS)) {
        continue;
      }
      List<TbRepayPlanBean> plans = tb_repay_plan_mapper.selectList(new QueryWrapper<TbRepayPlanBean>().eq("apply_id", bean.getApp_id()).orderByAsc("repay_start_date"));
      for (TbRepayPlanBean plan : plans) {
        if (plan.getRepay_status().equals(1)) {
          continue;
        }
        if (plan.getRepay_start_date().getTime() <= System.currentTimeMillis()) {
          AppApplyInfo ai = new AppApplyInfo();
          ai.setCurr_amount_to_be_repaid(plan.getNeed_total().toString());
          ai.setDest_account_no("xx");
          ai.setIs_overdue(plan.getRepay_end_date().getTime() > System.currentTimeMillis() ? 1: 0);
          ai.setOrder_id(bean.getApp_id());
          ai.setPlan_id(plan.getId());
          ai.setPeriod(plan.getSeq_no().toString());
          ret_app_beans.add(ai);
        }
      }
    }
    all_applys.put("pending_list", ret_app_beans);
    return Result.succ(all_applys);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "employment/get", produces = "application/json;charset=UTF-8")
  public Object getUserEmploymentInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserEmploymentInfoBean> mapper = tb_user_employment_info_mapper;
    Wrapper<TbUserEmploymentInfoBean> cond = new QueryWrapper<TbUserEmploymentInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time");
    TbUserEmploymentInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return Result.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return old_bean;
  }


  // add/update/get user bank account info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"bank/add", "bank/update"}, produces = "application/json;charset=UTF-8")
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
  @RequestMapping(value = "bank/get", produces = "application/json;charset=UTF-8")
  public Object getUserBankInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserBankAccountInfoBean> mapper = tb_user_bank_account_mapper;
    Wrapper<TbUserBankAccountInfoBean> cond = new QueryWrapper<TbUserBankAccountInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time");
    TbUserBankAccountInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return Result.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return old_bean;
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
    String abs_path = upload_path + "/" + tag;
    logger.info("will save to: " + abs_path);
    file.transferTo(new File(abs_path));
    return Result.succ(tag);
  }
}

