package com.sup.backend.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sup.backend.bean.AppSubmitOrder;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.core.Result;
import com.sup.backend.util.HttpClient;
import com.sup.backend.util.ToolUtils;
import org.apache.log4j.Logger;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/apply")
public class ApplyController {
  public static Logger logger = Logger.getLogger(ApplyController.class);

  @Value("admin.uri")
  private String admin_uri;

  // add/update/get apply info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "add", produces = "application/json;charset=UTF-8")
  public Object addApplyInfo(@LoginInfo LoginInfoCtx li, @RequestBody AppSubmitOrder order_detail) {
    JSONObject params = new JSONObject();
    DeferredResult<Object> ret = new DeferredResult<>();
    ToolUtils.AsyncHttpPostJson(admin_uri, params, ret, resp -> {
      JSONObject obj = JSONObject.parseObject(resp.getResponseBody());
      return obj;
    });
    return ret;
  }
}

