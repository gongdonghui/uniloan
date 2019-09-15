package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppApplyInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.common.bean.TbUserRegistInfoBean;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.ToolUtils;
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
import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/repay")
public class RepayController {
  public static Logger logger = Logger.getLogger(RepayController.class);

  @Value("paycenter.uri")
  private String paycenter_url;

  @Value("admin.uri")
  private String admin_uri;

  @Autowired
  TbUserRegistInfoMapper tb_user_regist_info_mapper;

  //////////////////////////////
  // 还款接口
  //////////////////////////////
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "getRepayLink", produces = "application/json;charset=UTF-8")
  public Object GetRepayLink(@RequestBody AppApplyInfo apply, @LoginInfo LoginInfoCtx li) {
    logger.info("----------- begin to getrepay link -------------");
    TbUserRegistInfoBean user = tb_user_regist_info_mapper.selectOne(new QueryWrapper<TbUserRegistInfoBean>().eq("id", li.getUser_id()));
    final String name = user.getName();
    final String phone = user.getMobile();
    final Integer amount = Integer.parseInt(apply.getCurr_amount_to_be_repaid());
    final String orderid = apply.getPlan_id().toString();
    final String expireDate = (new SimpleDateFormat(ToolUtils.COMPACT_DATE_FORMAT)).format(Date.from(LocalDateTime.now().plusMinutes(30l).atZone(ZoneId.systemDefault()).toInstant()));

    RepayInfo ri = new RepayInfo();
    ri.setAmount(amount);
    ri.setApplyId(orderid);
    ri.setName(name);
    ri.setPhone(phone);
    ri.setUserId(li.getUser_id().toString());
    DeferredResult<Object> ret = new DeferredResult<>();
    ToolUtils.AsyncHttpPostJson(paycenter_url + "/repay", ri, ret, resp -> {
      Result<RepayVO> obj = JSON.parseObject(resp.getResponseBody(), new TypeReference<Result<RepayVO>>(){});
      return obj;
    });
    logger.info("test_end...");
    return ret;
  }

  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "manual_repay", produces = "application/json;charset=UTF-8")
  public Object ManualRepay(@RequestBody AppApplyInfo apply, @LoginInfo LoginInfoCtx li) {
    DeferredResult<Object> ret = new DeferredResult<>();
    ToolUtils.AsyncHttpPostJson(admin_uri, apply, ret, resp -> {
      JSONObject obj = JSON.parseObject(resp.getResponseBody());
      return obj;
    });
    return ret;
  }


  // repayment complete callback
  @ResponseBody
  @RequestMapping(value = "callBack", produces = "application/json;charset=UTF-8")
  public Object repayCallBack(String userId, String applyId) {
    return null;
  }
}

