package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppApplyInfo;
import com.sup.backend.bean.AppApplyOverView;
import com.sup.backend.bean.AppSubmitOrder;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.ApplyInfoMapper;
import com.sup.backend.mapper.TbApplyMaterialInfoMapper;
import com.sup.backend.mapper.TbProductInfoMapper;
import com.sup.backend.mapper.TbRepayPlanMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.RepayPlanStatusEnum;
import com.sup.common.param.ApplyInfoParam;
import com.sup.common.service.CoreService;
import com.sup.common.util.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/apply")
public class ApplyController {
  public static Logger logger = Logger.getLogger(ApplyController.class);
  @Autowired
  TbApplyMaterialInfoMapper tb_apply_info_material_mapper;
  @Autowired
  ApplyInfoMapper apply_info_mapper;
  @Autowired
  TbRepayPlanMapper tb_repay_plan_mapper;
  //@Autowired
  //private CoreService apply_service;

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "list_pending_applies", produces = "application/json;charset=UTF-8")
  public Object listPending(@LoginInfo LoginInfoCtx li) {
    QueryWrapper<TbApplyInfoBean> query = new QueryWrapper<TbApplyInfoBean>().eq("user_id", li.getUser_id());
    List<TbApplyInfoBean> beans = apply_info_mapper.selectList(query);
    Set<Integer> pending_status = new HashSet<Integer>(){{
      add(ApplyStatusEnum.APPLY_INIT.getCode());
      add(ApplyStatusEnum.APPLY_AUTO_PASS.getCode());
      add(ApplyStatusEnum.APPLY_SECOND_PASS.getCode());
      add(ApplyStatusEnum.APPLY_FIRST_PASS.getCode());
      add(ApplyStatusEnum.APPLY_FINAL_PASS.getCode());
      add(ApplyStatusEnum.APPLY_AUTO_LOANING.getCode());
    }};
    List<AppApplyOverView> ret_app_beans = new ArrayList<>();
    for (TbApplyInfoBean bean : beans) {
      if (!pending_status.contains(bean.getStatus())) {
        continue;
      }
      AppApplyOverView ov = new AppApplyOverView();
      ov.setStatus(bean.getStatus());
      ov.setAmount(bean.getApply_quota().toString());
      ov.setApply_id(bean.getId());
      ov.setPeriod(bean.getPeriod().toString());
      ov.setApply_time(ToolUtils.NormTime(bean.getCreate_time()));
      ret_app_beans.add(ov);
    }
    return Result.succ(ret_app_beans);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "list_repay_applies", produces = "application/json;charset=UTF-8")
  public Object listApply(@LoginInfo LoginInfoCtx li) {
    QueryWrapper<TbApplyInfoBean> query = new QueryWrapper<TbApplyInfoBean>().eq("user_id", li.getUser_id());
    List<TbApplyInfoBean> beans = apply_info_mapper.selectList(query);
    List<AppApplyInfo> ret_app_beans = new ArrayList<>();
    for (TbApplyInfoBean bean : beans) {
      if (!bean.getStatus().equals(ApplyStatusEnum.APPLY_LOAN_SUCC.getCode())) {
        continue;
      }

      List<TbRepayPlanBean> plans = tb_repay_plan_mapper.selectList(new QueryWrapper<TbRepayPlanBean>().eq("apply_id", bean.getId()).orderByAsc("repay_start_date"));
      for (TbRepayPlanBean plan : plans) {
        if (plan.getRepay_status().equals(RepayPlanStatusEnum.PLAN_PAID_ALL.getCode())) {
          continue;
        }

        if (plan.getRepay_start_date().getTime() <= System.currentTimeMillis()) {
          AppApplyInfo ai = new AppApplyInfo();
          ai.setCurr_amount_to_be_repaid(plan.getNeed_total().toString());
          ai.setTotal_amount_to_be_repaid(plan.getNeed_total().toString());
          ai.setDest_account_no("xx");
          ai.setContract_amount(bean.getApply_quota());
          ai.setTotal_period(bean.getPeriod().toString());
          ai.setIs_overdue(plan.getRepay_end_date().getTime() < System.currentTimeMillis() ? 1: 0);
          ai.setApply_id(bean.getId());
          ai.setPlan_id(plan.getId());
          ai.setPeriod(plan.getSeq_no().toString());
          ai.setLatest_repay_date(ToolUtils.NormTime(plan.getRepay_end_date()).substring(0, 10));
          ret_app_beans.add(ai);
        }
      }
    }
    return Result.succ(ret_app_beans);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "new", produces = "application/json;charset=UTF-8")
  public Object addApplyInfo(@LoginInfo LoginInfoCtx li, @RequestBody AppSubmitOrder order_detail) {
    logger.info("apply_obj: " + JSON.toJSONString(order_detail));
    ApplyInfoParam aip = new ApplyInfoParam();
    aip.setUser_id(li.getUser_id());
    aip.setApp_id(-1);
    aip.setApply_quota(order_detail.getQuota());
    aip.setProduct_id(order_detail.getProduct_id());
    aip.setChannel_id(-1);
    aip.setPeriod(order_detail.getPeriod());
    aip.setInfoIdMap(order_detail.getMaterial_ids());
    return Result.succ(aip);
    //Result r = apply_service.addApplyInfo(aip);
    //return r;
  }
}

