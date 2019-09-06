package com.sup.credit.facade;

import com.sup.credit.bean.ApplyInfoBean;
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

    // apply info manual assigned

    // apply info cancel assignment

}
