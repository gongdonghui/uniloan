package com.sup.core.service;

import com.sun.org.apache.regexp.internal.RE;
import com.sup.common.bean.ApplyInfoBean;
import com.sup.common.bean.ApplyInfoHistoryBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.Result;
import com.sup.core.mapper.ApplyInfoHistoryMapper;
import com.sup.core.mapper.ApplyInfoMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Project:uniloan
 * Class:  LoanService
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@Log4j
public class ApplyService {

    @Autowired
    private ApplyInfoMapper applyInfoMapper;

    @Autowired
    private ApplyInfoHistoryMapper applyInfoHistoryMapper;

    @Value("apply.expire-days")
    private int APPLY_EXPIRE_DAYS;

    public Object addApplyInfo(ApplyInfoBean bean) {
        log.info("addApplyInfo: userId=" + bean.getUser_id()
                + ", appId=" + bean.getApp_id()
                + ", channelId=" + bean.getChannel_id()
                + ", productId=" + bean.getProduct_id());
        Calendar c = new GregorianCalendar();
        Date now = new Date();
        c.setTime(now);
        c.add(Calendar.DATE, APPLY_EXPIRE_DAYS);
        Date expireTime = c.getTime();

        bean.setApply_time(now);
        bean.setCreate_time(now);
        bean.setUpdate_time(now);
        bean.setExpire_time(expireTime);
        bean.setStatus(ApplyStatusEnum.APPLY_INIT.getCode());


        if (applyInfoMapper.insert(bean) <= 0) {
            return Result.fail("insert into ApplyInfo failed!");
        }
        // apply id will be set after insert succeeded.
        log.info("insert into ApplyInfo succ, applyId=" + bean.getId());
        ApplyInfoHistoryBean applyInfoHistoryBean = new ApplyInfoHistoryBean(bean);
        applyInfoHistoryBean.setCreate_time(now);
        if (applyInfoHistoryMapper.insert(applyInfoHistoryBean) <= 0) {
            return Result.fail("insert into ApplyInfoHistory failed!");
        }
        return Result.succ();
    }

    public Object updateApplyInfo(ApplyInfoBean bean) {
        ApplyStatusEnum newState = ApplyStatusEnum.getStatusByCode(bean.getStatus());
        if (newState == null) {
            log.error("updateApplyInfo: invalid status=" + bean.getStatus()
                    + ", operator = " + bean.getOperator_id()
                    + ", applyId = " + bean.getId()
            );
            return Result.fail("invalid status!");
        }
        log.info("updateApplyInfo: operator=" + bean.getOperator_id() +
                ", applyId = " + bean.getId() +
                ", new status = (" + bean.getStatus() +
                ")" + newState.getCodeDesc()
        );
        // TODO: should check the status order here to avoid invalid operation

        Date now = new Date();
        bean.setUpdate_time(now);
        if (applyInfoMapper.updateById(bean) <= 0) {
            return Result.fail("update ApplyInfo failed!");
        }
        ApplyInfoHistoryBean applyInfoHistoryBean = new ApplyInfoHistoryBean(bean);
        applyInfoHistoryBean.setCreate_time(now);
        if (applyInfoHistoryMapper.insert(applyInfoHistoryBean) <= 0) {
            return Result.fail("insert into ApplyInfoHistory failed!");
        }
        return Result.succ();
    }
}
