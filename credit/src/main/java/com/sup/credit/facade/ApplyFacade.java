package com.sup.credit.facade;

import com.sup.credit.bean.ApplyInfoBean;
import com.sup.credit.bean.CreditClassBean;
import com.sup.credit.bean.UserIdCardInfoBean;
import org.springframework.web.bind.annotation.*;

/**
 * Project:uniloan
 * Class:  ApplyFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@RequestMapping(value = "/credit")
public interface ApplyFacade {

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

    @ResponseBody
    @RequestMapping(value = "class/get", produces = "application/json;charset=UTF-8")
    Object getCreditClass();


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

    //////////////////////////////
    // 进件指派接口
    //////////////////////////////

    // apply info auto assigned
    @ResponseBody
    @RequestMapping(value = "apply/autoAssign", produces = "application/json;charset=UTF-8")
    Object autoAssign(@RequestBody ApplyInfoBean bean);

    // apply info manual assigned
    @ResponseBody
    @RequestMapping(value = "apply/assign", produces = "application/json;charset=UTF-8")
    Object manualAssign(String assignTo, String operator, String applyId);

    // apply info cancel assignment
    @ResponseBody
    @RequestMapping(value = "apply/cancelAssign", produces = "application/json;charset=UTF-8")
    Object cancelAssign(String assignTo, String operator, String applyId);


}
