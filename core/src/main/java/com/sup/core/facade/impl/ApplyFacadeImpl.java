package com.sup.core.facade.impl;

import com.sup.core.Result;
import com.sup.core.bean.ApplyInfoBean;
import com.sup.core.facade.ApplyFacade;
import com.sup.core.mapper.ApplyInfoMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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
    private ApplyInfoMapper applyInfoMapper;

    @Override
    public Object updateApplyInfo(ApplyInfoBean bean) {
        log.info("updateApplyInfo: operator=" + bean.getOperator_id() +
                        ", applyId=" + bean.getApp_id() +
                        ", applyStatus=" + bean.getStatus());

        bean.setUpdate_time(new Date());
        if (applyInfoMapper.updateById(bean) > 0) {
            return Result.succ("succ");
        }
        log.error("updateApplyInfo failed!");
        return Result.fail("");
    }
}
