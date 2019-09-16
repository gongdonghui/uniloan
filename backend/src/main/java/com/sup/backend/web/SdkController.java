package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppApplyInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbAppSdkContractInfoMapper;
import com.sup.backend.mapper.TbAppSdkLocationInfoMapper;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbAppSdkContractInfoBean;
import com.sup.common.bean.TbAppSdkLocationInfoBean;
import com.sup.common.bean.TbUserRegistInfoBean;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.vo.RepayVO;
import com.sup.common.util.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

  @ResponseBody
  @RequestMapping(value = "loc/get", produces = "application/json;charset=UTF-8")
  public Object QueryLocation(@RequestParam("mobile") String mobile) {
    QueryWrapper<TbAppSdkLocationInfoBean> query = new QueryWrapper<TbAppSdkLocationInfoBean>().eq("mobile", mobile).orderByDesc("create_time");
    TbAppSdkLocationInfoBean bean = tb_app_sdk_location_mapper.selectOne(query);
    return Result.succ(bean);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "loc/new", produces = "application/json;charset=UTF-8")
  public Object NewLocation(@LoginInfo LoginInfoCtx li, @RequestBody TbAppSdkLocationInfoBean bean) {
    TbUserRegistInfoBean reg_bean = tb_user_regist_mapper.selectById(li.getUser_id());
    bean.setMobile(reg_bean.getMobile());
    bean.setCreate_time(new Date());
    bean.setUpdate_time(new Date());
    tb_app_sdk_location_mapper.insert(bean);
    return Result.succ("ok");
  }

  @ResponseBody
  @RequestMapping(value = "contract/get", produces = "application/json;charset=UTF-8")
  public Object QueryContract(@RequestParam("mobile") String mobile) {
    QueryWrapper<TbAppSdkContractInfoBean> query = new QueryWrapper<TbAppSdkContractInfoBean>().eq("mobile", mobile).orderByDesc("create_time");
    List<TbAppSdkContractInfoBean> beans = tb_app_sdk_contract_mapper.selectList(query);
    List<TbAppSdkContractInfoBean> result = new ArrayList<>();
    if (!beans.isEmpty()) {
      Date latest_dt = beans.get(0).getCreate_time();
      beans.forEach(org_bean -> {
        if (org_bean.getCreate_time().equals(latest_dt)) {
          result.add(org_bean);
        }
      });
    }
    return Result.succ(result);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "contract/new", produces = "application/json;charset=UTF-8")
  public Object NewContract(@LoginInfo LoginInfoCtx li, @RequestBody List<TbAppSdkContractInfoBean> beans) {
    TbUserRegistInfoBean reg_bean = tb_user_regist_mapper.selectById(li.getUser_id());
    Date dt = new Date();
    beans.forEach(bean -> {
      bean.setMobile(reg_bean.getMobile());
      bean.setCreate_time(dt);
      bean.setUpdate_time(dt);
      tb_app_sdk_contract_mapper.insert(bean);
    });
    return Result.succ("ok");
  }
}

