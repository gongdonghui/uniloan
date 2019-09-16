package com.sup.core.facade.impl;

import com.sup.common.bean.ApplyInfoParam;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.core.facade.ApplyFacade;
import com.sup.core.service.ApplyService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Project:uniloan
 * Class:  ApplyFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-11
 */

@Log4j
@RestController
public class ApplyFacadeImpl implements ApplyFacade {

    @Autowired
    private ApplyService applyService;

    @Override
    public Object addApplyInfo(ApplyInfoParam applyInfoParam) {
        // TODO
        // add apply info

        // add apply material info

        return null;
    }

    public Object addApplyInfo(TbApplyInfoBean bean) {
        return applyService.addApplyInfo(bean);
    }

    @Override
    public Object updateApplyInfo(TbApplyInfoBean bean) {
        return applyService.updateApplyInfo(bean);
    }
}
