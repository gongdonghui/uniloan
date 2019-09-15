package com.sup.core.service;

import com.sup.common.bean.TbApplyInfoBean;
import com.sup.core.bean.RiskDecisionVariableBean;

public interface CreditService {

    Object getCreditClass(String version, String userId, String applyId);

    Object getCreditClass(String version, String userId, TbApplyInfoBean bean);


    RiskDecisionVariableBean getRiskDecisionVariable(String version, String productId, String applyId);

    RiskDecisionVariableBean getRiskDecisionVariable(String version, String productId, TbApplyInfoBean bean);

}
