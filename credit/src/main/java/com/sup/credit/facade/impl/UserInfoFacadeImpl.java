package com.sup.credit.facade.impl;

import com.sup.credit.bean.UserBasicInfoBean;
import com.sup.credit.bean.UserContactInfoBean;
import com.sup.credit.bean.UserEmploymentInfoBean;
import com.sup.credit.bean.UserIdCardInfoBean;
import com.sup.credit.facade.UserInfoFacade;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project:uniloan
 * Class:  UserInfoFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@RestController
public class UserInfoFacadeImpl implements UserInfoFacade {
    @Override
    public Object addUserIdCardInfo(UserIdCardInfoBean bean) {
        return null;
    }

    @Override
    public Object updateUserIdCardInfo(UserIdCardInfoBean bean) {
        return null;
    }

    @Override
    public Object getUserIdCardInfo(String userId) {
        return null;
    }

    @Override
    public Object addUserBasicInfo(UserBasicInfoBean bean) {
        return null;
    }

    @Override
    public Object updateUserBasicInfo(UserBasicInfoBean bean) {
        return null;
    }

    @Override
    public Object getUserBasicInfo(String userId) {
        return null;
    }

    @Override
    public Object addUserContactInfo(UserContactInfoBean bean) {
        return null;
    }

    @Override
    public Object updateUserContactInfo(UserContactInfoBean bean) {
        return null;
    }

    @Override
    public Object getUserContactInfo(String userId) {
        return null;
    }

    @Override
    public Object addUserEmploymentInfo(UserEmploymentInfoBean bean) {
        return null;
    }

    @Override
    public Object updateUserEmploymentInfo(UserEmploymentInfoBean bean) {
        return null;
    }

    @Override
    public Object getUserEmploymentInfo(String userId) {
        return null;
    }

    @Override
    public Object addUserBankInfo(UserBasicInfoBean bean) {
        return null;
    }

    @Override
    public Object updateUserBankInfo(UserBasicInfoBean bean) {
        return null;
    }

    @Override
    public Object getUserBankInfo(String userId) {
        return null;
    }
}
