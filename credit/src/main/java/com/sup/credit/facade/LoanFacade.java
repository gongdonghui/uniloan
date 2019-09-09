package com.sup.credit.facade;

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
}
