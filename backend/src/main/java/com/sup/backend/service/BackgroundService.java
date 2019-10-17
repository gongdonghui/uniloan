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
    Collections.sort(beans, (x, y) -> x.getSignature().compareTo(y.getSignature()));
    if (exists != null && (!exists.isEmpty()) && exists.size() == beans.size()) {
      boolean dup = true;
      for (int i = 0; i < beans.size(); ++i) {
        if (!beans.get(i).getSignature().equals(exists.get(i).getSignature())) {
          dup = false;
          logger.info(String.format("[diff_item]:[%s] => [%s]", JSON.toJSONStringWithDateFormat(beans.get(i), ToolUtils.DEFAULT_DATE_FORMAT), JSON.toJSONStringWithDateFormat(exists.get(i), ToolUtils.DEFAULT_DATE_FORMAT)));
          break;
        }
      }
      if (dup == true) {
        return;
      }
    }

    Date dt = new Date();
    beans.forEach(bean -> {
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      tb_app_sdk_contract_mapper.insert(bean);
    });
    logger.info(String.format("[new_contact]: mobile [%s] len [%d], sig [%s]", mobile, beans.size(), ToolUtils.NormTime(dt)));
  }

  @Async
  public void InsertApplistsync(String mobile, List<TbAppSdkAppListInfoBean> beans, List<TbAppSdkAppListInfoBean> exists) {
    exists.forEach(v -> v.setSignature(v.calcSignature()));
    Collections.sort(beans, (x, y) -> x.getSignature().compareTo(y.getSignature()));
    if (exists != null && (!exists.isEmpty()) && exists.size() == beans.size()) {
      boolean dup = true;
      for (int i = 0; i < beans.size(); ++i) {
        if (!beans.get(i).getSignature().equals(exists.get(i).getSignature())) {
          dup = false;
          logger.info(String.format("[diff_item]:[%s] => [%s]", JSON.toJSONStringWithDateFormat(beans.get(i), ToolUtils.DEFAULT_DATE_FORMAT), JSON.toJSONStringWithDateFormat(exists.get(i), ToolUtils.DEFAULT_DATE_FORMAT)));
          break;
        }
      }
      if (dup == true) {
        return;
      }
    }
    Date dt = new Date();
    beans.forEach(bean -> {
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      tb_app_sdk_app_list_info_mapper.insert(bean);
    });
    logger.info(String.format("[new_applist]: mobile [%s] len [%d], sig [%s]", mobile, beans.size(), ToolUtils.NormTime(dt)));
  }
}
