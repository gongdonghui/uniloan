package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppApplyInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.mapper.TbManualRepayMapper;
import com.sup.backend.mapper.TbRepayPlanMapper;
import com.sup.common.bean.TbManualRepayBean;
import com.sup.common.bean.TbMarketPlanBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.bean.TbUserRegistInfoBean;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.vo.RepayVO;
import com.sup.common.loan.MaualRepayStatusEnum;
import com.sup.common.service.CoreService;
import com.sup.common.service.PayCenterService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/repay")
public class RepayController {
  public static Logger logger = Logger.getLogger(RepayController.class);
  @Autowired
  TbUserRegistInfoMapper tb_user_regist_info_mapper;

  @Autowired
  TbManualRepayMapper tb_manual_repay_mapper;

  @Autowired
  TbRepayPlanMapper tb_repay_plan_mapper;

  @Autowired
  private CoreService core;

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "get_repay_link", produces = "application/json;charset=UTF-8")
  public Object GetRepayLink(@RequestBody AppApplyInfo apply, @LoginInfo LoginInfoCtx li) {
    logger.info("----------- begin to getrepay link -------------: " + JSON.toJSONString(apply));
    TbUserRegistInfoBean user = tb_user_regist_info_mapper.selectById(li.getUser_id());
    final String name = user.getName();
    final String phone = user.getMobile();
    final Integer amount = Integer.parseInt(apply.getCurr_amount_to_be_repaid());
    final String orderid = apply.getApply_id().toString();
    RepayInfo ri = new RepayInfo();
    ri.setAmount(amount);
    ri.setApplyId(orderid);
    ri.setName(name);
    ri.setPhone(phone);
    ri.setUserId(li.getUser_id().toString());
    return core.getRepayInfo(ri);
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "manual_repay", produces = "application/json;charset=UTF-8")
  public Object ManualRepay(@RequestBody AppApplyInfo apply, @LoginInfo LoginInfoCtx li) {
    TbRepayPlanBean repay_plan_bean = tb_repay_plan_mapper.selectById(apply.getPlan_id());
    TbManualRepayBean mb = new TbManualRepayBean();
    mb.setPlan_id(repay_plan_bean.getId());
    mb.setUser_id(repay_plan_bean.getUser_id());
    mb.setApply_id(repay_plan_bean.getApply_id());
    mb.setSeq_no(repay_plan_bean.getSeq_no());
    mb.setRepay_start_date(repay_plan_bean.getRepay_start_date());
    mb.setRepay_end_date(repay_plan_bean.getRepay_end_date());
    mb.setIs_overdue(repay_plan_bean.getIs_overdue());
    mb.setNeed_total(repay_plan_bean.getNeed_total());
    mb.setAct_total(0l);
    mb.setTrade_no("");
    mb.setStatus(MaualRepayStatusEnum.TO_BE_CONFIRM.getCode());
    mb.setRepay_image(apply.getRepay_img());
    mb.setCreate_time(new Date());
    mb.setUpdate_time(new Date());
    tb_manual_repay_mapper.insert(mb);
    return ToolUtils.succ("succ");
  }

  @ResponseBody
  @RequestMapping(value = "callBack", produces = "application/json;charset=UTF-8")
  public Object repayCallBack(String userId, String applyId) {
    return null;
  }
}

