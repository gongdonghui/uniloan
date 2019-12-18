package com.sup.backend.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.mapper.TbAppSdkAppListInfoMapper;
import com.sup.backend.mapper.TbAppSdkContractInfoMapper;
import com.sup.backend.mapper.TbAppSdkLocationInfoMapper;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbAppSdkAppListInfoBean;
import com.sup.common.bean.TbAppSdkContractInfoBean;
import com.sup.common.bean.TbAppSdkLocationInfoBean;
import org.apache.log4j.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
}
