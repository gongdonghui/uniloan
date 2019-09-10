package com.sup.core.facade.impl;

import com.sup.core.bean.RepayPlanInfoBean;
import com.sup.core.facade.LoanFacade;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project:uniloan
 * Class:  LoanFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@RestController
public class LoanFacadeImpl implements LoanFacade {
    @Override
    public Object autoLoan(String userId, String applyId) {
        return null;
    }

    @Override
    public Object addRepayPlan(String userId, String applyId) {
        return null;
    }

    @Override
    public Object updateRepayPlan(RepayPlanInfoBean bean) {
        return null;
    }
}
