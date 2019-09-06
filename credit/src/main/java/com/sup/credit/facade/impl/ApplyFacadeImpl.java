package com.sup.credit.facade.impl;

import com.sup.credit.bean.ApplyInfoBean;
import com.sup.credit.facade.ApplyFacade;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project:uniloan
 * Class:  ApplyFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@RestController
public class ApplyFacadeImpl implements ApplyFacade {
    @Override
    public Object addApplyInfo(String userId, String productId, String channelId, String appId) {
        return null;
    }

    @Override
    public Object updateApplyInfo(ApplyInfoBean bean) {
        return null;
    }

    @Override
    public Object getApplyInfo(String userId) {
        return null;
    }
}
