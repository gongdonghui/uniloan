package com.sup.backend.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.backend.bean.AppApplyInfo;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.common.bean.RepayMaterialInfoBean;
import com.sup.common.bean.TbUserRegistInfoBean;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.core.Result;
import com.sup.backend.mapper.TbUserRegistInfoMapper;
import com.sup.backend.util.HttpClient;
import com.sup.backend.util.ToolUtils;
import org.apache.log4j.Logger;
import org.asynchttpclient.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  @Autowired
  TbUserRegistInfoMapper tb_user_regist_info_mapper;

  //////////////////////////////
  // 还款接口
  //////////////////////////////
  @RequestMapping(value = "getRepayLink1", produces = "application/json;charset=UTF-8")
  public DeferredResult<Result> test() {
    logger.info("test_start....");
    DeferredResult<Result> ret = new DeferredResult<>();
    HttpClient.HttpGetAsync("http://www.google.com", null, new AsyncCompletionHandler<Response>() {
      @Override
      public Response onCompleted(Response response) throws Exception {
        ret.setResult(Result.succ("ok"));
        logger.info("async comple: " + response.getResponseBody().substring(0, 10));
        return response;
      }

      @Override
      public void onThrowable(Throwable t) {
        System.out.println(ToolUtils.GetTrace(t));
        ret.setResult(Result.fail("time_out"));
      }
    });
    logger.info("test_end...");
    return ret;
  }

  // get repayment link
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "getRepayLink", produces = "application/json;charset=UTF-8")
  public Object getRepayLink(HttpServletRequest request, HttpServletResponse resp, @RequestBody AppApplyInfo apply, @LoginInfo LoginInfoCtx li) {
    logger.info("----------- begin to getrepay link -------------");
    TbUserRegistInfoBean user = tb_user_regist_info_mapper.selectOne(new QueryWrapper<TbUserRegistInfoBean>().eq("id", li.getUser_id()));
    final String name = user.getName();
    final String phone = user.getMobile();
    final Integer amount = Integer.parseInt(apply.getCurr_amount_to_be_repaid());
    final String orderid = apply.getPeriod_id().toString();
    final String expireDate = (new SimpleDateFormat(ToolUtils.COMPACT_DATE_FORMAT)).format(Date.from(LocalDateTime.now().plusMinutes(30l).atZone(ZoneId.systemDefault()).toInstant()));
    return "succ";
  }

  // repayment complete callback
  @ResponseBody
  @RequestMapping(value = "callBack", produces = "application/json;charset=UTF-8")
  public Object repayCallBack(String userId, String applyId) {
    return null;
  }

  // add/update/get manual repayment material info
  @ResponseBody
  @RequestMapping(value = "material/add", produces = "application/json;charset=UTF-8")
  public Object addRepayMaterial(@RequestBody RepayMaterialInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "material/update", produces = "application/json;charset=UTF-8")
  public Object updateRepayMaterial(@RequestBody RepayMaterialInfoBean bean) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "material/get", produces = "application/json;charset=UTF-8")
  public Object getRepayMaterial(Integer page, Integer pageSize) {
    return null;
  }
}

