package com.sup.credit.facade.impl;

import com.sup.credit.bean.ApplyInfoBean;
import com.sup.credit.bean.CreditClassBean;
import com.sup.credit.bean.RiskDecisionVariableBean;
import com.sup.credit.facade.CreditFacade;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project:uniloan
 * Class:  CreditFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@RestController
public class CreditFacadeImpl implements CreditFacade {
    @Override
    public Object autoAudit(String version, String applyId, RiskDecisionVariableBean bean) {
        return null;
    }

    @Override
    public Object addCreditClass(CreditClassBean bean) {
        return null;
    }

    @Override
    public Object updateCreditClass(CreditClassBean bean) {
        return null;
    }

    @Override
    public Object getCreditClass(String userId, String applyId) {
        return null;
    }

    @Override
    public Object addApplyInfo(String userId, String productId, String channelId, String appId) {
        return null;
    }

    @Override
    public Object updateApplyInfo(ApplyInfoBean bean) {
        return null;
    }

    @Override
    public Object getApplyInfo(String userId) {
        return null;
    }
}
