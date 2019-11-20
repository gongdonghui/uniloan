package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.collect.ImmutableMap;
import com.sup.backend.bean.AppTbUserBasicInfoBean;
import com.sup.backend.bean.AppTbUserCitizenIdentityCardInfoBean;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.*;
import com.sup.backend.service.RedisClient;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.*;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/cert")
public class CertController {
  public static Logger logger = Logger.getLogger(CertController.class);
  @Autowired
  RedisClient rc;
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


  private boolean CheckDuplicateSubmit(String uri, Integer user_id) {
    String dup_key = String.format("submit|%d_%s", user_id, uri);
    return rc.SetEx(dup_key, "ok", 5l, TimeUnit.SECONDS);
  }

  private void ClearDuplicateSubmit(String uri, Integer user_id) {
    String dup_key = String.format("submit|%d_%s", user_id, uri);
    rc.Delete(dup_key);
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
  @RequestMapping(value = {"idcard/add", "idcard/update"}, produces = "application/json;charset=UTF-8")
  public Object UpdateCIC(@LoginInfo LoginInfoCtx li, @RequestBody @Valid AppTbUserCitizenIdentityCardInfoBean app_bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(app_bean));
    if (!CheckDuplicateSubmit("idcard", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }

    TbUserCitizenIdentityCardInfoBean bean = new TbUserCitizenIdentityCardInfoBean();
    BeanUtils.copyProperties(app_bean, bean);

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

    TbUserRegistInfoBean regist_info = tb_user_regist_info_mapper.selectById(li.getUser_id());
    if (StringUtils.isEmpty(regist_info.getName()) && (!StringUtils.isEmpty(bean.getName()))) {
      UpdateWrapper<TbUserRegistInfoBean> upd_wrapper = new UpdateWrapper<>();
      upd_wrapper.eq("id", regist_info.getId());
      upd_wrapper.set("name", bean.getName());
      tb_user_regist_info_mapper.update(null, upd_wrapper);
      logger.info(String.format("update user_id: %d, name: (%s) => (%s)", li.getUser_id(), regist_info.getName(), bean.getName()));
    }
    ClearDuplicateSubmit("idcard", li.getUser_id());
    return ToolUtils.succ(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "idcard/get", produces = "application/json;charset=UTF-8")
  public Object getUserIdCardInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserCitizenIdentityCardInfoBean> mapper = tb_user_citizen_identity_card_info_mapper;
    Wrapper<TbUserCitizenIdentityCardInfoBean> cond = new QueryWrapper<TbUserCitizenIdentityCardInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserCitizenIdentityCardInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return ToolUtils.succ(old_bean);
  }

  // add/update/get user basic info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"basic/add", "basic/update"}, produces = "application/json;charset=UTF-8")
  public Object updateBasicInfo(@LoginInfo LoginInfoCtx li, @RequestBody @Valid AppTbUserBasicInfoBean app_bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(app_bean));
    if (!CheckDuplicateSubmit("basic", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }
    TbUserBasicInfoBean bean = new TbUserBasicInfoBean();
    BeanUtils.copyProperties(app_bean, bean);

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
    ClearDuplicateSubmit("basic", li.getUser_id());
    return ToolUtils.succ(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "basic/get", produces = "application/json;charset=UTF-8")
  public Object getUserBasicInfo(@LoginInfo LoginInfoCtx li) {
    BaseMapper<TbUserBasicInfoBean> mapper = tb_user_basic_info_mapper;
    Wrapper<TbUserBasicInfoBean> cond = new QueryWrapper<TbUserBasicInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserBasicInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return ToolUtils.succ(old_bean);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"contact/add", "contact/update"}, produces = "application/json;charset=UTF-8")
  public Object updateEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody List<TbUserEmergencyContactBean> beans) {
    logger.info("----- recv bean: " + JSON.toJSONString(beans));
    if (!CheckDuplicateSubmit("contact", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }

    String info_id = null;
    for (TbUserEmergencyContactBean bean : beans) {
      if (bean.getInfo_id() != null) {
        info_id = bean.getInfo_id();
        break;
      }
    }
    Date new_create_time = new Date();
    final String the_info_id = info_id;
    beans.forEach(bean -> {
      bean.setInfo_id(the_info_id);
      if (bean.getUser_id() == null) {
        bean.setUser_id(li.getUser_id());
      }
      if (bean.getCreate_time() == null) {
        bean.setCreate_time(new_create_time);
      }
    });

    List<Map> rets = new ArrayList<>();
    String new_info_id = ToolUtils.getToken();
    beans.forEach(bean -> {
      if (bean.getInfo_id() == null) {
        bean.setUser_id(li.getUser_id());
        bean.setInfo_id(new_info_id);
        bean.setCreate_time(new_create_time);
      }
      if (bean.getId() == null) {
        tb_user_emergency_contact_mapper.insert(bean);
      } else {
        tb_user_emergency_contact_mapper.updateById(bean);
      }
      rets.add(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
    });
    ClearDuplicateSubmit("contact", li.getUser_id());
    logger.info("----- return bean: " + JSON.toJSONString(rets));
    return ToolUtils.succ(rets);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "contact/get", produces = "application/json;charset=UTF-8")
  public Object getUserContactInfo(@LoginInfo LoginInfoCtx li) {
    logger.info("recv request ...");
    BaseMapper<TbUserEmergencyContactBean> mapper = tb_user_emergency_contact_mapper;
    Wrapper<TbUserEmergencyContactBean> cond = new QueryWrapper<TbUserEmergencyContactBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserEmergencyContactBean old_bean = mapper.selectOne(cond);
    if (old_bean == null) {
      return ToolUtils.succ(null);
    }
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }

    List<TbUserEmergencyContactBean> cands = tb_user_emergency_contact_mapper.selectList(new QueryWrapper<TbUserEmergencyContactBean>().eq("info_id", old_bean.getInfo_id()));
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
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
    return ToolUtils.succ(cands);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "contact/del", produces = "application/json;charset=UTF-8")
  public Object deleteEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody TbUserEmergencyContactBean bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(bean));
    if (bean.getId() != null) {
      tb_user_emergency_contact_mapper.deleteById(bean.getId());
    }
    return ToolUtils.succ("contact_del_succ");
  }

  // add/update/get user employment info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"employment/add", "employment/update"}, produces = "application/json;charset=UTF-8")
  public Object updateEmploymentInfo(@LoginInfo LoginInfoCtx li, @RequestBody TbUserEmploymentInfoBean bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(bean));
    if (!CheckDuplicateSubmit("employment", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }

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
    ClearDuplicateSubmit("employment", li.getUser_id());
    return ToolUtils.succ(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "employment/get", produces = "application/json;charset=UTF-8")
  public Object getUserEmploymentInfo(@LoginInfo LoginInfoCtx li) {
    logger.info("recv request ...");
    BaseMapper<TbUserEmploymentInfoBean> mapper = tb_user_employment_info_mapper;
    Wrapper<TbUserEmploymentInfoBean> cond = new QueryWrapper<TbUserEmploymentInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserEmploymentInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return ToolUtils.succ(old_bean);
  }


  // add/update/get user bank account info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = {"bank/add", "bank/update"}, produces = "application/json;charset=UTF-8")
  public Object updateEmergencyContact(@LoginInfo LoginInfoCtx li, @RequestBody TbUserBankAccountInfoBean bean) {
    logger.info("----- recv bean: " + JSON.toJSONString(bean));
    if (!CheckDuplicateSubmit("bank", li.getUser_id())) {
      return ToolUtils.fail(1, "duplicate_submit");
    }

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
    ClearDuplicateSubmit("bank", li.getUser_id());
    return ToolUtils.succ(ImmutableMap.of("id", bean.getId(), "info_id", bean.getInfo_id()));
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "bank/get", produces = "application/json;charset=UTF-8")
  public Object getUserBankInfo(@LoginInfo LoginInfoCtx li) {
    logger.info("recv request ...");
    BaseMapper<TbUserBankAccountInfoBean> mapper = tb_user_bank_account_mapper;
    Wrapper<TbUserBankAccountInfoBean> cond = new QueryWrapper<TbUserBankAccountInfoBean>().eq("user_id", li.getUser_id()).orderByDesc("create_time").last("limit 1");
    TbUserBankAccountInfoBean old_bean = mapper.selectOne(cond);
    if (old_bean == null || old_bean.getExpire_time().getTime() < System.currentTimeMillis()) {
      return ToolUtils.succ(null);
    }
    TbApplyMaterialInfoBean apply_material = tb_apply_info_material_mapper.selectOne(new QueryWrapper<TbApplyMaterialInfoBean>().eq("info_id", old_bean.getInfo_id()).last("limit 1"));
    if (apply_material != null) {
      String new_info_id = ToolUtils.getToken();
      old_bean.setId(null);
      old_bean.setInfo_id(new_info_id);
      old_bean.setCreate_time(new Date());
      old_bean.setExpire_time(Date.from(LocalDateTime.now().plusYears(1l).atZone(ZoneId.systemDefault()).toInstant()));
      mapper.insert(old_bean);
    }
    return ToolUtils.succ(old_bean);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "info", produces = "application/json;charset=UTF-8")
  public Object TestMessage(@LoginInfo LoginInfoCtx li) {
    JSONObject parms = new JSONObject();
    parms.put("li", li);
    return ToolUtils.succ(parms);
  }
}

