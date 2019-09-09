package com.sup.credit.facade;

import com.sup.credit.bean.ApplyInfoBean;
import com.sup.credit.bean.CreditClassBean;
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

    // auto audit by rules
    @ResponseBody
    @RequestMapping(value = "autoAudit", produces = "application/json;charset=UTF-8")
    Object autoAudit(String version, String applyId, @RequestBody RiskDecisionVariableBean bean);

    //////////////////////////////
    // 进件CRUD接口
    //////////////////////////////

    // update/get user credit class
    @ResponseBody
    @RequestMapping(value = "class/add", produces = "application/json;charset=UTF-8")
    Object addCreditClass(@RequestBody CreditClassBean bean);

    @ResponseBody
    @RequestMapping(value = "class/update", produces = "application/json;charset=UTF-8")
    Object updateCreditClass(@RequestBody CreditClassBean bean);

//    // get user credit class
//    @ResponseBody
//    @RequestMapping(value = "class/get", produces = "application/json;charset=UTF-8")
//    Object getCreditClass(String userId, String applyId);


    // add/update/get apply info
    @ResponseBody
    @RequestMapping(value = "apply/add", produces = "application/json;charset=UTF-8")
    Object addApplyInfo(String userId, String productId, String channelId, String appId);

    @ResponseBody
    @RequestMapping(value = "apply/update", produces = "application/json;charset=UTF-8")
    Object updateApplyInfo(@RequestBody ApplyInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "apply/get", produces = "application/json;charset=UTF-8")
    Object getApplyInfo(String userId);

    // TODO: 当前有效进件
}
