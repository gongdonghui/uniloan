package com.sup.credit.facade;

import com.sup.credit.bean.RepayMaterialInfoBean;
import com.sup.credit.bean.RepayPlanInfoBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Project:uniloan
 * Class:  LoanFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@RequestMapping(value = "/loan")
public interface LoanFacade {

    //////////////////////////////
    // 放款接口
    //////////////////////////////

    // auto loan
    @ResponseBody
    @RequestMapping(value = "autoExec", produces = "application/json;charset=UTF-8")
    Object autoLoan(String userId, String applyId);

    // add/update/get loan info
    @ResponseBody
    @RequestMapping(value = "plan/add", produces = "application/json;charset=UTF-8")
    Object addRepayPlan(String userId, String applyId);

    @ResponseBody
    @RequestMapping(value = "plan/update", produces = "application/json;charset=UTF-8")
    Object updateRepayPlan(@RequestBody RepayPlanInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "plan/get", produces = "application/json;charset=UTF-8")
    Object getRepayPlan(String userId, String applyId);




    //////////////////////////////
    // 还款接口
    //////////////////////////////

    // get repayment link
    @ResponseBody
    @RequestMapping(value = "getRepayLink", produces = "application/json;charset=UTF-8")
    Object getRepayLink(String userId, String applyId);


    // repayment complete callback
    @ResponseBody
    @RequestMapping(value = "repayCallBack", produces = "application/json;charset=UTF-8")
    Object repayCallBack(String userId, String applyId);


    // add/update/get manual repayment material info
    @ResponseBody
    @RequestMapping(value = "repayMaterial/add", produces = "application/json;charset=UTF-8")
    Object addRepayMaterial(@RequestBody RepayMaterialInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "repayMaterial/update", produces = "application/json;charset=UTF-8")
    Object updateRepayMaterial(@RequestBody RepayMaterialInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "repayMaterial/get", produces = "application/json;charset=UTF-8")
    Object getRepayMaterial(Integer page, Integer pageSize);




    //////////////////////////////
    // 逾期管理接口
    //////////////////////////////

    // get overdue loan info
    @ResponseBody
    @RequestMapping(value = "overdue/get", produces = "application/json;charset=UTF-8")
    Object getOverdueInfo(Integer page, Integer pageSize);

    // overdue loan info manual assigned
    @ResponseBody
    @RequestMapping(value = "overdue/assign", produces = "application/json;charset=UTF-8")
    Object manualAssign(String assignTo, String operator, String applyId);

    // overdue loan info cancel assignment
    @ResponseBody
    @RequestMapping(value = "overdue/cancelAssign", produces = "application/json;charset=UTF-8")
    Object cancelAssign(String assignTo, String operator, String applyId);



}
