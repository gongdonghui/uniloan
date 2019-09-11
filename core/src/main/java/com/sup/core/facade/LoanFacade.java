package com.sup.core.facade;

import com.sup.core.bean.RepayPlanInfoBean;
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

    // add/update/get loan plan
    @ResponseBody
    @RequestMapping(value = "plan/add", produces = "application/json;charset=UTF-8")
    Object addRepayPlan(String userId, String applyId);

    @ResponseBody
    @RequestMapping(value = "plan/update", produces = "application/json;charset=UTF-8")
    Object updateRepayPlan(@RequestBody RepayPlanInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "plan/get", produces = "application/json;charset=UTF-8")
    Object getRepayPlan(String userId, String applyId);


    /**
     * 获取支付通道还款所需信息，包括支付码和链接
     * @param userId
     * @param applyId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "repayInfo/get", produces = "application/json;charset=UTF-8")
    Object getRepayInfo(String userId, String applyId);

    /**
     * 支付通道还款回调接口
     * @param userId
     * @param applyId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "repayCallBack", produces = "application/json;charset=UTF-8")
    Object repayCallBack(String userId, String applyId);

}
