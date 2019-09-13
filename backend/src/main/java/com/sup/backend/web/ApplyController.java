package com.sup.backend.web;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/apply")
public class ApplyController {
  public static Logger logger = Logger.getLogger(ApplyController.class);

  // add/update/get apply info
  @ResponseBody
  @RequestMapping(value = "add", produces = "application/json;charset=UTF-8")
  public Object addApplyInfo(String userId, String productId, String channelId, String appId) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "list", produces = "application/json;charset=UTF-8")
  public Object getApplyInfoList(String userId) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "current", produces = "application/json;charset=UTF-8")
  public Object getCurrentApplyInfo(String userId) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "detail", produces = "application/json;charset=UTF-8")
  public Object getApplyInfo(String applyId) {
    return null;
  }

  @ResponseBody
  @RequestMapping(value = "repayPlan/get", produces = "application/json;charset=UTF-8")
  public Object getRepayPlan(String userId, String applyId) {
    return null;
  }
}

