package com.sup.credit.facade.impl;

import com.sup.credit.bean.RepayMaterialInfoBean;
import com.sup.credit.bean.RepayPlanInfoBean;
import com.sup.credit.facade.LoanFacade;
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

    @Override
    public Object getRepayPlan(String userId, String applyId) {
        return null;
    }

    @Override
    public Object getRepayLink(String userId, String applyId) {
        return null;
    }

    @Override
    public Object repayCallBack(String userId, String applyId) {
        return null;
    }

    @Override
    public Object addRepayMaterial(RepayMaterialInfoBean bean) {
        return null;
    }

    @Override
    public Object updateRepayMaterial(RepayMaterialInfoBean bean) {
        return null;
    }

    @Override
    public Object getRepayMaterial(Integer page, Integer pageSize) {
        return null;
    }
}
