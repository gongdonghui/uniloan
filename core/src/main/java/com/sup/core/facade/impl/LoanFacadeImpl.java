package com.sup.core.facade.impl;

import com.sup.core.Result;
import com.sup.core.bean.RepayPlanInfoBean;
import com.sup.core.facade.LoanFacade;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.mapper.RepayPlanInfoMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  LoanFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@Log4j
@RestController
public class LoanFacadeImpl implements LoanFacade {
    @Autowired
    private ApplyInfoMapper     applyInfoMapper;

    @Autowired
    private RepayPlanInfoMapper repayPlanInfoMapper;


    @Override
    public Object autoLoan(String userId, String applyId) {
        // 1. get apply info and check apply status

        // 2. get user bank info

        // 3. loan using funpay(need thread safe)

        return null;
    }

    @Override
    public Object addRepayPlan(String userId, String applyId) {
        // 1. get apply info and check apply status

        // 2. generate repay plan if not exist(need thread safe)

        // 3.
        return null;
    }

    @Override
    public Object updateRepayPlan(RepayPlanInfoBean bean) {
        if (bean == null) {
            return Result.fail("RepayPlanInfoBean is null!");
        }
        log.info("updateRepayPlan: userId = " + bean.getUser_id() +
                ", applyId = " + bean.getApply_id());

        bean.setUpdate_time(new Date());
        if (repayPlanInfoMapper.updateById(bean) > 0) {
            return Result.succ("OK");
        }
        return Result.fail("update failed!");
    }

    @Override
    public Object getRepayPlan(String userId, String applyId) {
        return null;
    }

    /**
     * 获取支付通道还款所需信息，包括支付码和链接
     *
     * @param userId
     * @param applyId
     * @return
     */
    @Override
    public Object getRepayInfo(String userId, String applyId) {
        return null;
    }

    /**
     * 支付通道还款回调接口
     *
     * @param userId
     * @param applyId
     * @return
     */
    @Override
    public Object repayCallBack(String userId, String applyId) {
        return null;
    }
}
