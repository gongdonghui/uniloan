package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppApplyInfo;
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
import com.sup.common.param.ApplyInfoParam;
import com.sup.common.util.Result;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/apply")
public class ApplyController {
  public static Logger logger = Logger.getLogger(ApplyController.class);

  @Value("admin.uri")
  private String admin_uri;
  @Autowired
  TbApplyMaterialInfoMapper tb_apply_info_material_mapper;
  @Autowired
  ApplyInfoMapper apply_info_mapper;
  @Autowired
  TbRepayPlanMapper tb_repay_plan_mapper;
  //@Autowired
  //private ApplyService apply_service;

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
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
          ai.setTotal_period(bean.getPeriod().toString());
          ai.setIs_overdue(plan.getRepay_end_date().getTime() > System.currentTimeMillis() ? 1: 0);
          ai.setApply_id(bean.getApp_id());
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
    //Result r = apply_service.addApplyInfo(aip);
    return Result.succ("ok");
  }
}

