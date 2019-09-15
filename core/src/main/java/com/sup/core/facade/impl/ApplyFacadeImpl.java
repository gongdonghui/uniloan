package com.sup.core.facade.impl;

import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.Result;
import com.sup.common.bean.ApplyInfoBean;
import com.sup.core.facade.ApplyFacade;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.service.ApplyService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    public Object addApplyInfo(ApplyInfoBean bean) {
        return applyService.addApplyInfo(bean);
    }

    @Override
    public Object updateApplyInfo(ApplyInfoBean bean) {
        return applyService.updateApplyInfo(bean);
    }
}
