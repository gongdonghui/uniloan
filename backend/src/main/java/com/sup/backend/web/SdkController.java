package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netflix.discovery.converters.Auto;
import com.sup.backend.bean.AppSdkAppListInfo;
import com.sup.backend.bean.AppSdkContactInfo;
import com.sup.backend.bean.AppTbInstallClickInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.ApplicationContextHelper;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.*;
import com.sup.backend.service.BackgroundService;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collections;
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

  @Autowired
  BackgroundService background_service;

  @Autowired
  TbInstallClickInfoMapper tb_install_click_mapper;

  @ResponseBody
  @RequestMapping(value = "install/report", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object ReportInstall(@RequestBody AppTbInstallClickInfo param) {
    logger.info("install_record: " + JSON.toJSONStringWithDateFormat(param, ToolUtils.DEFAULT_DATE_FORMAT));
    TbInstallClickInfoBean install = new TbInstallClickInfoBean();
    install.setDeviceid(param.getDevice_id());
    install.setMobile(param.getMobile());
    install.setInstall_referrer(param.getInstall_referrer());
    if (param.getInstall_begin_date() != null) {
      install.setInstall_begin_date(new Date(param.getInstall_begin_date()*1000l));
    }
    if (param.getReferrer_click_date() != null) {
      install.setReferrer_click_date(new Date(param.getReferrer_click_date()*1000l));
    }
    tb_install_click_mapper.insert(install);
    return ToolUtils.succ(null);
  }

  @ResponseBody
  @RequestMapping(value = "loc/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object QueryLocation(@RequestParam("info_id") String info_id) {
    if (StringUtils.isNotEmpty(info_id)) {
      QueryWrapper<TbAppSdkLocationInfoBean> query = new QueryWrapper<TbAppSdkLocationInfoBean>().eq("info_id", info_id).orderByDesc("id").last("limit 1");
      TbAppSdkLocationInfoBean bean = tb_app_sdk_location_mapper.selectOne(query);
      return ToolUtils.succ(bean);
    } else {
      return ToolUtils.succ(null);
    }
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "loc/new", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object NewLocation(@LoginInfo LoginInfoCtx li, @RequestBody TbAppSdkLocationInfoBean bean) {
    if (StringUtils.isEmpty(bean.getApply_lat()) || StringUtils.isEmpty(bean.getApply_long())) {
      return ToolUtils.succ(null);
    }

    String mobile = bean.getMobile();
    if (mobile.length() == 9) {
      mobile = "0" + mobile;
      logger.info("upload_sdk_by_old_style_phone: " + mobile);
    }
    bean.setMobile(mobile);

    Date dt = new Date();
    bean.setCreate_time(dt);
    bean.setUpdate_time(dt);
    bean.setInfo_id("");
    background_service.InsertLocationAsync(bean.getMobile(), bean);
    return ToolUtils.succ(null);
  }

  @ResponseBody
  @RequestMapping(value = "contact/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object QueryContract(@RequestParam("info_id") String info_id) {
    if (StringUtils.isNotEmpty(info_id)) {
      QueryWrapper<TbAppSdkContractInfoBean> query = new QueryWrapper<TbAppSdkContractInfoBean>().eq("info_id", info_id).orderByDesc("id");
      return ToolUtils.succ(tb_app_sdk_contract_mapper.selectList(query));
    } else {
      return ToolUtils.succ(null);
    }
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "contact/new", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object NewContract(@LoginInfo LoginInfoCtx li, @RequestBody AppSdkContactInfo sdk_contacts) {
    // reorder to our-format !!
    if (sdk_contacts == null || StringUtils.isEmpty(sdk_contacts.getDevice_id()) || sdk_contacts.getContacts() == null || sdk_contacts.getContacts().isEmpty()) {
      return ToolUtils.succ("no_valid_items");
    }

    String mobile = sdk_contacts.getMobile();
    if (mobile.length() == 9) {
      mobile = "0" + mobile;
      logger.info("upload_sdk_by_old_style_phone: " + mobile);
    }
    sdk_contacts.setMobile(mobile);

    List<TbAppSdkContractInfoBean> beans = new ArrayList<>();
    Date dt = new Date();
    for (AppSdkContactInfo.SingleItem item : sdk_contacts.getContacts()) {
      TbAppSdkContractInfoBean bean = new TbAppSdkContractInfoBean();
      bean.setMobile(sdk_contacts.getMobile());
      bean.setDevice_id(sdk_contacts.getDevice_id());
      bean.setContract_name(item.getName());
      bean.setInfo_id("");
      if (item.getMobiles() == null) {
        item.setMobiles(new ArrayList<>());
      } else {
        Collections.sort(item.getMobiles());
      }
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      bean.setContract_info(String.join(",", item.getMobiles()));
      bean.setContract_memo("");
      bean.setSignature(bean.calcSignature());
      beans.add(bean);
    }

    background_service.InsertContactAsync(sdk_contacts.getMobile(), beans);
    return ToolUtils.succ(null);
  }

  @ResponseBody
  @RequestMapping(value = "applist/get", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object QueryApplist(@RequestParam("info_id") String info_id) {
    if (StringUtils.isNotEmpty(info_id)) {
      QueryWrapper<TbAppSdkAppListInfoBean> query = new QueryWrapper<TbAppSdkAppListInfoBean>().eq("info_id", info_id).orderByDesc("id");
      return ToolUtils.succ(tb_app_sdk_app_list_info_mapper.selectList(query));
    } else {
      return ToolUtils.succ(null);
    }
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "applist/new", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Object NewApplist(@LoginInfo LoginInfoCtx li, @RequestBody AppSdkAppListInfo app_list_info) {
    // reorder to our-format !!
    if (app_list_info == null || StringUtils.isEmpty(app_list_info.getDevice_id()) || app_list_info.getApps() == null || app_list_info.getApps().isEmpty()) {
      return ToolUtils.succ("no_valid_items");
    }

    String mobile = app_list_info.getMobile();
    if (mobile.length() == 9) {
      mobile = "0" + mobile;
      logger.info("upload_sdk_by_old_style_phone: " + mobile);
    }
    app_list_info.setMobile(mobile);

    List<TbAppSdkAppListInfoBean> beans = new ArrayList<>();
    Date dt = new Date();
    for (AppSdkAppListInfo.SingleItem item : app_list_info.getApps()) {
      TbAppSdkAppListInfoBean bean = new TbAppSdkAppListInfoBean();
      bean.setInfo_id("");
      bean.setMobile(app_list_info.getMobile());
      bean.setDevice_id(app_list_info.getDevice_id());
      bean.setApk_name(item.getApk_name());
      bean.setInstall_time(new Date(item.getInstall_time()*1000l));
      bean.setApk_label(item.getApk_label());
      bean.setSignature(bean.calcSignature());
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      beans.add(bean);
    }

    background_service.InsertApplistsync(app_list_info.getMobile(), beans);
    return ToolUtils.succ(null);
  }

}

