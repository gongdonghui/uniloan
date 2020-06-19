package com.sup.backend.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sup.backend.bean.*;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.mapper.*;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.*;
import com.sup.common.util.Result;
import org.apache.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by xidongzhou1 on 2019/10/17.
 */
@Service
public class BackgroundService {
  private static Logger logger = Logger.getLogger(BackgroundService.class);

  @Autowired
  TbAppSdkContractInfoMapper tb_app_sdk_contract_mapper;

  @Autowired
  TbAppSdkAppListInfoMapper tb_app_sdk_app_list_info_mapper;

  @Autowired
  TbAppSdkLocationInfoMapper tb_app_sdk_location_info_mapper;

  @Autowired
  TbSdkDialHistoryMapper tb_sdk_dial_history_mapper;

  @Autowired
  TbSdkSmsHistoryMapper tb_sdk_sms_history_mapper;

  @Autowired
  RedissonClient red_client;

  @Async
  public void InsertLocationAsync(String mobile, TbAppSdkLocationInfoBean bean) {
    RLock lock = red_client.getLock(mobile);
    try {
      lock.lock();
      QueryWrapper<TbAppSdkLocationInfoBean> query = new QueryWrapper<TbAppSdkLocationInfoBean>().eq("mobile", mobile).eq("info_id", "");
      List<TbAppSdkLocationInfoBean> exists = tb_app_sdk_location_info_mapper.selectList(query);
      exists.forEach(b -> tb_app_sdk_location_info_mapper.deleteById(b.getId()));
      tb_app_sdk_location_info_mapper.insert(bean);
    } finally {
      lock.unlock();
    }
  }

  @Async
  public void InsertContactAsync(String mobile, List<TbAppSdkContractInfoBean> beans) {
    RLock lock = red_client.getLock(mobile);
    try {
      lock.lock();
      QueryWrapper<TbAppSdkContractInfoBean> query = new QueryWrapper<TbAppSdkContractInfoBean>().eq("mobile", mobile).eq("info_id", "");
      List<TbAppSdkContractInfoBean> exists = tb_app_sdk_contract_mapper.selectList(query);
      logger.info(String.format("[%s] ------ new: %d, old: %d ------", mobile, beans.size(), exists.size()));
      exists.forEach(bean -> tb_app_sdk_contract_mapper.deleteById(bean.getId()));
      beans.forEach(bean -> tb_app_sdk_contract_mapper.insert(bean));
    } finally {
      lock.unlock();
    }
  }

  @Async
  public void InsertApplistsync(String mobile, List<TbAppSdkAppListInfoBean> beans) {
    RLock lock = red_client.getLock(mobile);
    try {
      lock.lock();
      QueryWrapper<TbAppSdkAppListInfoBean> query = new QueryWrapper<TbAppSdkAppListInfoBean>().eq("mobile", mobile).eq("info_id", "");
      List<TbAppSdkAppListInfoBean> exists = tb_app_sdk_app_list_info_mapper.selectList(query);
      logger.info(String.format("[%s] ------ new: %d, old: %d ------", mobile, beans.size(), exists.size()));
      exists.forEach(bean -> tb_app_sdk_app_list_info_mapper.deleteById(bean.getId()));
      beans.forEach(bean -> tb_app_sdk_app_list_info_mapper.insert(bean));
    } finally {
      lock.unlock();
    }
  }

  @Async
  public void UploadDialRecord(LoginInfoCtx li, AppDialRecordWrapper datas) {
    try {
      List<AppDialRecord> records = datas.getRecords();
      if (!records.isEmpty()) {
        Integer user_id = li.getUser_id();
        TbSdkDialHistoryBean prev_one = tb_sdk_dial_history_mapper.selectOne(Wrappers.<TbSdkDialHistoryBean>lambdaQuery().eq(TbSdkDialHistoryBean::getUser_id, user_id).orderByDesc(TbSdkDialHistoryBean::getCall_time).last("limit 1"));
        Date prev_dt = (prev_one != null ? prev_one.getCall_time() : ToolUtils.NormTime("2000-01-01 00:00:00"));
        for (AppDialRecord e : records) {
          Date dt = ToolUtils.NormTime(e.getDate());
          if (dt.compareTo(prev_dt) <= 0) {
            continue;
          }
          TbSdkDialHistoryBean td = new TbSdkDialHistoryBean();
          td.setCall_time(dt);
          td.setUser_id(user_id);
          td.setName(e.getName());
          td.setCall_type(Integer.valueOf(e.getType()));
          td.setDuration(Integer.valueOf(e.getDuration()));
          td.setCounterpart_number(e.getFormattedNumber());
          td.setLocation(e.getLocation());
          td.setCreate_time(new Date());
          tb_sdk_dial_history_mapper.insert(td);
        }
      }
    } catch (Exception e) {
      logger.error(ToolUtils.GetTrace(e));
    }
  }

  @Async
  public void UploadSmsRecord(LoginInfoCtx li, AppSmsRecordWrapper datas) {
    try {
      List<AppSmsRecord> records = datas.getRecords();
      if (!records.isEmpty()) {
        Integer user_id = li.getUser_id();
        TbSdkSmsHistoryBean prev_one = tb_sdk_sms_history_mapper.selectOne(Wrappers.<TbSdkSmsHistoryBean>lambdaQuery().eq(TbSdkSmsHistoryBean::getUser_id, user_id).orderByDesc(TbSdkSmsHistoryBean::getSms_time).last("limit 1"));
        Date prev_dt = (prev_one != null ? prev_one.getSms_time(): ToolUtils.NormTime("2000-01-01 00:00:00"));
        for (AppSmsRecord e : records) {
          Date dt = ToolUtils.NormTime(e.getDate());
          if (dt.compareTo(prev_dt) <= 0) {
            continue;
          }
          TbSdkSmsHistoryBean td = new TbSdkSmsHistoryBean();
          td.setSms_time(dt);
          td.setUser_id(user_id);
          td.setStatus(Integer.valueOf(e.getStatus()));
          td.setAddress(e.getAddress());
          td.setBody(e.getBody());
          td.setType(Integer.valueOf(e.getType()));
          td.setReaded(Integer.valueOf(e.getRead()));
          td.setName(e.getName());
          td.setCreate_time(new Date());
          tb_sdk_sms_history_mapper.insert(td);
        }
      }
    } catch (Exception e) {
      logger.error(ToolUtils.GetTrace(e));
    }
  }
}
