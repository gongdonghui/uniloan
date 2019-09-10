package com.sup.core.facade;

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



    // auto audit by rules
    @ResponseBody
    @RequestMapping(value = "autoAudit", produces = "application/json;charset=UTF-8")
    Object autoAudit(String version, String applyId);

}
