package com.sup.backend.service;

import com.alibaba.fastjson.JSON;
import com.sup.backend.mapper.TbAppSdkAppListInfoMapper;
import com.sup.backend.mapper.TbAppSdkContractInfoMapper;
import com.sup.backend.mapper.TbAppSdkLocationInfoMapper;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbAppSdkAppListInfoBean;
import com.sup.common.bean.TbAppSdkContractInfoBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

  @Async
  public void InsertContactAsync(String mobile, List<TbAppSdkContractInfoBean> beans, List<TbAppSdkContractInfoBean> exists) {
    Collections.sort(beans, (x, y) -> x.getContract_info().compareTo(y.getContract_info()));
    logger.info(String.format("\n [new]: %s \n [exist]:%s ", JSON.toJSONStringWithDateFormat(beans, ToolUtils.DEFAULT_DATE_FORMAT), JSON.toJSONStringWithDateFormat(exists, ToolUtils.DEFAULT_DATE_FORMAT)));
    if (exists != null && (!exists.isEmpty()) && exists.size() == beans.size()) {
      boolean dup = true;
      for (int i = 0; i < beans.size(); ++i) {
        if (!beans.get(i).Signature().equals(exists.get(i).Signature())) {
          dup = false;
          break;
        }
      }
      if (dup == true) {
        logger.info("dup_records ..");
        return;
      }
    }
    logger.info("new_records ..");
    Date dt = new Date();
    beans.forEach(bean -> {
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      tb_app_sdk_contract_mapper.insert(bean);
    });
  }

  @Async
  public void InsertApplistsync(String mobile, List<TbAppSdkAppListInfoBean> beans, List<TbAppSdkAppListInfoBean> exists) {
    Collections.sort(beans, (x, y) -> x.getApk_name().compareTo(y.getApk_name()));
    logger.info(String.format("\n [new]: %s \n [exist]:%s ", JSON.toJSONStringWithDateFormat(beans, ToolUtils.DEFAULT_DATE_FORMAT), JSON.toJSONStringWithDateFormat(exists, ToolUtils.DEFAULT_DATE_FORMAT)));
    if (exists != null && (!exists.isEmpty()) && exists.size() == beans.size()) {
      boolean dup = true;
      for (int i = 0; i < beans.size(); ++i) {
        if (!beans.get(i).Signature().equals(exists.get(i).Signature())) {
          dup = false;
          break;
        }
      }
      if (dup == true) {
        logger.info("dup_records ..");
        return;
      }
    }
    logger.info("new_records ..");
    Date dt = new Date();
    beans.forEach(bean -> {
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      tb_app_sdk_app_list_info_mapper.insert(bean);
    });
  }
}
