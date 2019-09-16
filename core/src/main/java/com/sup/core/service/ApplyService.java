package com.sup.core.service;

import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbApplyInfoHistoryBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.DateUtil;
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

    public boolean addApplyInfo(TbApplyInfoBean bean) {
        log.info("addApplyInfo: userId=" + bean.getUser_id()
                + ", appId=" + bean.getApp_id()
                + ", channelId=" + bean.getChannel_id()
                + ", productId=" + bean.getProduct_id());

        if (applyInfoMapper.insert(bean) <= 0) {
            log.error("insert into ApplyInfo failed!");
            return false;
        }
        // apply id will be set after insert succeeded.
        log.info("insert into ApplyInfo succ, applyId=" + bean.getId());
        TbApplyInfoHistoryBean applyInfoHistoryBean = new TbApplyInfoHistoryBean(bean);
        applyInfoHistoryBean.setCreate_time(bean.getCreate_time());
        if (applyInfoHistoryMapper.insert(applyInfoHistoryBean) <= 0) {
            log.error("insert into ApplyInfoHistory failed!");
            return false;
        }
        return true;
    }

    public Object updateApplyInfo(TbApplyInfoBean bean) {
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
        if (newState == ApplyStatusEnum.APPLY_INIT) {
            bean.setCreate_time(now);
        } else if (newState == ApplyStatusEnum.APPLY_FINAL_PASS) {
            bean.setPass_time(now);
        } else if (newState == ApplyStatusEnum.APPLY_LOAN_SUCC) {
            bean.setLoan_time(now);
        }
        if (applyInfoMapper.updateById(bean) <= 0) {
            return Result.fail("update ApplyInfo failed!");
        }
        TbApplyInfoHistoryBean applyInfoHistoryBean = new TbApplyInfoHistoryBean(bean);
        applyInfoHistoryBean.setCreate_time(now);
        if (applyInfoHistoryMapper.insert(applyInfoHistoryBean) <= 0) {
            return Result.fail("insert into ApplyInfoHistory failed!");
        }
        return Result.succ();
    }
}
