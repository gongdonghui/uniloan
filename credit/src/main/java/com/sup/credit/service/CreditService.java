package com.sup.credit.service;

import com.sup.credit.bean.ApplyInfoBean;
import com.sup.credit.bean.RiskDecisionVariableBean;

public interface CreditService {

    Object getCreditClass(String version, String userId, String applyId);

    Object getCreditClass(String version, String userId, ApplyInfoBean bean);


    RiskDecisionVariableBean getRiskDecisionVariable(String version, String applyId);

    RiskDecisionVariableBean getRiskDecisionVariable(String version, ApplyInfoBean bean);

}
