package com.sup.core.service;

import com.sup.common.bean.ApplyInfoBean;
import com.sup.core.bean.RiskDecisionVariableBean;

public interface CreditService {

    Object getCreditClass(String version, String userId, String applyId);

    Object getCreditClass(String version, String userId, ApplyInfoBean bean);


    RiskDecisionVariableBean getRiskDecisionVariable(String version, String productId, String applyId);

    RiskDecisionVariableBean getRiskDecisionVariable(String version, String productId, ApplyInfoBean bean);

}
