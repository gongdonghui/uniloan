package com.sup.web;

import com.sup.bean.RepayMaterialInfoBean;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */

@RestController
@RequestMapping(value = "/repay")
public class RepayController {
  public static Logger logger = Logger.getLogger(RepayController.class);

  //////////////////////////////
  // 还款接口
  //////////////////////////////

  // get repayment link
  @ResponseBody
  @RequestMapping(value = "getRepayLink", produces = "application/json;charset=UTF-8")
  public Object getRepayLink(String userId, String applyId) {
    return null;
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

