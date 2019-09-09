package com.sup.credit.facade;

import com.sup.credit.bean.UserBasicInfoBean;
import com.sup.credit.bean.UserContactInfoBean;
import com.sup.credit.bean.UserEmploymentInfoBean;
import com.sup.credit.bean.UserIdCardInfoBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Project:uniloan
 * Class:  ApplyFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@RequestMapping(value = "/userInfo")
public interface UserInfoFacade {

    //////////////////////////////
    // 申请资料CRUD接口
    //////////////////////////////

    // add/update/get user IDCard info
    @ResponseBody
    @RequestMapping(value = "idCard/add", produces = "application/json;charset=UTF-8")
    Object addUserIdCardInfo(@RequestBody UserIdCardInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "idCard/update", produces = "application/json;charset=UTF-8")
    Object updateUserIdCardInfo(@RequestBody UserIdCardInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "idCard/get", produces = "application/json;charset=UTF-8")
    Object getUserIdCardInfo(@RequestParam(value = "userId") String userId);

    // add/update/get user basic info
    @ResponseBody
    @RequestMapping(value = "basic/add", produces = "application/json;charset=UTF-8")
    Object addUserBasicInfo(@RequestBody UserBasicInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "basic/update", produces = "application/json;charset=UTF-8")
    Object updateUserBasicInfo(@RequestBody UserBasicInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "basic/get", produces = "application/json;charset=UTF-8")
    Object getUserBasicInfo(@RequestParam(value = "userId") String userId);


    // add/update/get user contact info
    @ResponseBody
    @RequestMapping(value = "contact/add", produces = "application/json;charset=UTF-8")
    Object addUserContactInfo(@RequestBody UserContactInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "contact/update", produces = "application/json;charset=UTF-8")
    Object updateUserContactInfo(@RequestBody UserContactInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "contact/get", produces = "application/json;charset=UTF-8")
    Object getUserContactInfo(@RequestParam(value = "userId") String userId);


    // add/update/get user employment info
    @ResponseBody
    @RequestMapping(value = "employment/add", produces = "application/json;charset=UTF-8")
    Object addUserEmploymentInfo(@RequestBody UserEmploymentInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "employment/update", produces = "application/json;charset=UTF-8")
    Object updateUserEmploymentInfo(@RequestBody UserEmploymentInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "employment/get", produces = "application/json;charset=UTF-8")
    Object getUserEmploymentInfo(@RequestParam(value = "userId") String userId);


    // add/update/get user bank account info
    @ResponseBody
    @RequestMapping(value = "bank/add", produces = "application/json;charset=UTF-8")
    Object addUserBankInfo(@RequestBody UserBasicInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "bank/update", produces = "application/json;charset=UTF-8")
    Object updateUserBankInfo(@RequestBody UserBasicInfoBean bean);

    @ResponseBody
    @RequestMapping(value = "bank/get", produces = "application/json;charset=UTF-8")
    Object getUserBankInfo(@RequestParam(value = "userId") String userId);


}
