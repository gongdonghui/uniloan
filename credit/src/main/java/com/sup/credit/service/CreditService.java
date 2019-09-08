package com.sup.credit.service;

import com.sup.credit.bean.ApplyInfoBean;
import com.sup.credit.bean.RiskDecisionVariableBean;

public interface CreditService {

    RiskDecisionVariableBean getRiskDecisionVariable(String version, String applyId);

    RiskDecisionVariableBean getRiskDecisionVariable(String version, ApplyInfoBean bean);

}
