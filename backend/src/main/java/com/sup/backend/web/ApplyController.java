package com.sup.backend.web;

import com.alibaba.fastjson.JSONObject;
import com.sup.backend.bean.AppSubmitOrder;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.core.LoginInfo;
import com.sup.backend.core.LoginRequired;
import com.sup.backend.mapper.TbProductInfoMapper;
import com.sup.backend.util.ToolUtils;
import com.sup.common.param.ApplyInfoParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private TbProductInfoMapper tb_product_info_mapper;

  // add/update/get apply info
  @LoginRequired
  @ResponseBody
  @RequestMapping(value = "add", produces = "application/json;charset=UTF-8")
  public Object addApplyInfo(@LoginInfo LoginInfoCtx li, @RequestBody AppSubmitOrder order_detail) {
    JSONObject params = new JSONObject();
    DeferredResult<Object> ret = new DeferredResult<>();
    //TbProductInfoBean product = tb_product_info_mapper.selectById(order_detail.getProduct_id());
    ApplyInfoParam aip = new ApplyInfoParam();
    aip.setApp_id(null);
    aip.setChannel_id(null);
    aip.setApply_quota(order_detail.getQuota());
    aip.setPeriod(order_detail.getPeriod());
    aip.setInfoIdMap(order_detail.getMaterial_ids());
    aip.setProduct_id(order_detail.getProduct_id());
    ToolUtils.AsyncHttpPostJson(admin_uri, aip, ret, resp -> {
      JSONObject obj = JSONObject.parseObject(resp.getResponseBody());
      return obj;
    });
    return ret;
  }
}

