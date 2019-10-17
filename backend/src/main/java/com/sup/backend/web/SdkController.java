package com.sup.backend.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppSdkAppListInfo;
import com.sup.backend.bean.AppSdkContactInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbAppSdkAppListInfoMapper;
import com.sup.backend.mapper.TbAppSdkContractInfoMapper;
import com.sup.backend.mapper.TbAppSdkLocationInfoMapper;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbAppSdkAppListInfoBean;
import com.sup.common.bean.TbAppSdkContractInfoBean;
import com.sup.common.bean.TbAppSdkLocationInfoBean;
import com.sup.common.bean.TbUserRegistInfoBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/sdk")
public class SdkController {
  public static Logger logger = Logger.getLogger(SdkController.class);

  @Autowired
  TbAppSdkLocationInfoMapper tb_app_sdk_location_mapper;

  @Autowired
  TbAppSdkContractInfoMapper tb_app_sdk_contract_mapper;

  @Autowired
  TbUserRegistInfoMapper tb_user_regist_mapper;

  @Autowired
  TbAppSdkAppListInfoMapper  tb_app_sdk_app_list_info_mapper;

  @ResponseBody
  @RequestMapping(value = "loc/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object QueryLocation(@RequestParam("mobile") String mobile) {
    QueryWrapper<TbAppSdkLocationInfoBean> query = new QueryWrapper<TbAppSdkLocationInfoBean>().eq("mobile", mobile).orderByDesc("id").last("limit 1");
    TbAppSdkLocationInfoBean bean = tb_app_sdk_location_mapper.selectOne(query);
    return ToolUtils.succ(bean);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "loc/new", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object NewLocation(@LoginInfo LoginInfoCtx li, @RequestBody TbAppSdkLocationInfoBean bean) {
    TbUserRegistInfoBean reg_bean = tb_user_regist_mapper.selectById(li.getUser_id());
    bean.setMobile(reg_bean.getMobile());
    Date dt = new Date();
    bean.setCreate_time(dt);
    bean.setUpdate_time(dt);
    tb_app_sdk_location_mapper.insert(bean);
    return ToolUtils.succ(null);
  }

  @ResponseBody
  @RequestMapping(value = "contact/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object QueryContract(@RequestParam("mobile") String mobile) {
    List<TbAppSdkContractInfoBean> result = new ArrayList<>();
    TbAppSdkContractInfoBean first_bean = tb_app_sdk_contract_mapper.selectOne(new QueryWrapper<TbAppSdkContractInfoBean>().eq("mobile", mobile).orderByDesc("id").last("limit 1"));
    if (first_bean == null) {
      return ToolUtils.succ(result);
    }
    QueryWrapper<TbAppSdkContractInfoBean> query = new QueryWrapper<TbAppSdkContractInfoBean>().eq("mobile", mobile).eq("create_time", first_bean.getCreate_time());
    List<TbAppSdkContractInfoBean> beans = tb_app_sdk_contract_mapper.selectList(query);
    return ToolUtils.succ(beans);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "contact/new", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object NewContract(@LoginInfo LoginInfoCtx li, @RequestBody AppSdkContactInfo sdk_contacts) {
    // reorder to our-format !!
    if (sdk_contacts == null || sdk_contacts.getContacts() == null || sdk_contacts.getContacts().isEmpty()) {
      return ToolUtils.fail(1, "no_valid_items");
    }
    List<TbAppSdkContractInfoBean> beans = new ArrayList<>();
    for (AppSdkContactInfo.SingleItem item : sdk_contacts.getContacts()) {
      TbAppSdkContractInfoBean bean = new TbAppSdkContractInfoBean();
      bean.setMobile(sdk_contacts.getMobile());
      bean.setDevice_id(sdk_contacts.getDevice_id());
      bean.setContract_name(item.getName());
      bean.setContract_info(String.join(",", item.getMobiles()));
      bean.setContract_memo("");
      beans.add(bean);
    }

    String login_mobile = tb_user_regist_mapper.selectById(li.getUser_id()).getMobile();
    Date dt = new Date();
    beans.forEach(bean -> {
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      tb_app_sdk_contract_mapper.insert(bean);
    });
    return ToolUtils.succ(null);
  }

  @ResponseBody
  @RequestMapping(value = "applist/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object QueryApplist(@RequestParam("mobile") String mobile) {
    List<TbAppSdkAppListInfoBean> result = new ArrayList<>();
    TbAppSdkAppListInfoBean first_bean = tb_app_sdk_app_list_info_mapper.selectOne(new QueryWrapper<TbAppSdkAppListInfoBean>().eq("mobile", mobile).orderByDesc("id").last("limit 1"));
    if (first_bean == null) {
      return ToolUtils.succ(result);
    }
    QueryWrapper<TbAppSdkAppListInfoBean> query = new QueryWrapper<TbAppSdkAppListInfoBean>().eq("mobile", mobile).eq("create_time", first_bean.getCreate_time());
    List<TbAppSdkAppListInfoBean> beans = tb_app_sdk_app_list_info_mapper.selectList(query);
    return ToolUtils.succ(beans);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "applist/new", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object NewApplist(@LoginInfo LoginInfoCtx li, @RequestBody AppSdkAppListInfo app_list_info) {
    // reorder to our-format !!
    if (app_list_info == null || app_list_info.getApps() == null || app_list_info.getApps().isEmpty()) {
      return ToolUtils.fail(1, "no_valid_items");
    }

    List<TbAppSdkAppListInfoBean> beans = new ArrayList<>();
    for (AppSdkAppListInfo.SingleItem item : app_list_info.getApps()) {
      TbAppSdkAppListInfoBean bean = new TbAppSdkAppListInfoBean();
      bean.setMobile(app_list_info.getMobile());
      bean.setDevice_id(app_list_info.getDevice_id());
      bean.setApk_name(item.getApk_name());
      bean.setApk_label(item.getApk_label());
      beans.add(bean);
    }

    String login_mobile = tb_user_regist_mapper.selectById(li.getUser_id()).getMobile();
    Date dt = new Date();
    beans.forEach(bean -> {
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      tb_app_sdk_app_list_info_mapper.insert(bean);
    });
    return ToolUtils.succ(null);
  }

}

