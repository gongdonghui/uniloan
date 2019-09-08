package com.sup.credit.facade;

import com.sup.credit.bean.RiskDecisionVariableBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Project:uniloan
 * Class:  CreditFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-04
 */

@RequestMapping(value = "/credit")
public interface CreditFacade {

    // get user credit class
    @ResponseBody
    @RequestMapping(value = "class/get", produces = "application/json;charset=UTF-8")
    Object getCreditClass(String userId, String applyId);

    // auto audit by rules
    @ResponseBody
    @RequestMapping(value = "autoExec", produces = "application/json;charset=UTF-8")
    Object autoAudit(String version, String applyId, @RequestBody RiskDecisionVariableBean bean);


}
