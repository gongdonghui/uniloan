package com.sup.core.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.ApplyInfoBean;
import com.sup.core.bean.ApplyMaterialInfoBean;
import com.sup.core.bean.RepayPlanInfoBean;
import com.sup.core.bean.UserBankInfoBean;
import com.sup.core.facade.LoanFacade;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.mapper.ApplyMaterialInfoMapper;
import com.sup.core.mapper.RepayPlanInfoMapper;
import com.sup.core.mapper.UserBankInfoMapper;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.Result;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

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

    @Autowired
    private UserBankInfoMapper userBankInfoMapper;

    @Autowired
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;


    @Override
    public Object autoLoan(String userId, String applyId) {
        // 1. get apply info and check apply status
        ApplyInfoBean applyInfoBean = applyInfoMapper.selectById(applyId);
        if (applyInfoBean == null) {
            log.error("autoLoan: invalid applyId=" + applyId);
            return Result.fail("Invalid applyId!");
        }
        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status != ApplyStatusEnum.APPLY_FINAL_PASS) {
            log.error("autoLoan: invalid apply status=" + status.getCode() + ", " + status.getCodeDesc());
            return Result.fail("Invalid status!");
        }

        // 2. get user bank info
        QueryWrapper<ApplyMaterialInfoBean> materialWrapper = new QueryWrapper<ApplyMaterialInfoBean>();
        ApplyMaterialInfoBean applyMaterialInfoBean = applyMaterialInfoMapper.selectOne(
                materialWrapper.eq("applyId", applyId).eq("info_type", 4));
        if (applyMaterialInfoBean == null) {
            log.error("No apply material(bank info) found for applyId=" + applyId);
            return Result.fail("No apply material found!");
        }
        String infoId = applyMaterialInfoBean.getInfo_id();
        QueryWrapper<UserBankInfoBean> bankWrapper = new QueryWrapper<>();
        UserBankInfoBean bankInfoBean = userBankInfoMapper.selectOne(
                bankWrapper.eq("info_id", infoId).eq("user_id", userId));
        if (bankInfoBean == null) {
            log.error("No bank info for user(" + userId + "), info_id=" + infoId);
            return Result.fail("No bank info found!");
        }

        // 3. loan using funpay(need thread safe)
        boolean loanSucc = false;
        synchronized (this) {
            {
                // TODO
                // generate BankInfo for paycenter

                // verifyBankInfo

                // generate payinfo, and loan

            }
        }

        // if loan succeeded, generate repay plan
        if (loanSucc) {
            if (!addRepayPlan(applyInfoBean)) {
                log.error("Failed to generate repay plan for user(" + userId + "), applyId = " + applyId);
                //
            }
        }


        return Result.succ();
    }

    @Override
    public Object addRepayPlan(String userId, String applyId) {
        // TODO
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
            return Result.succ();
        }
        return Result.fail("update failed!");
    }

    @Override
    public Object getRepayPlan(String applyId) {
        if (applyId == null) {
            return Result.fail("Invalid applyId!");
        }
        QueryWrapper<RepayPlanInfoBean> wrapper = new QueryWrapper<RepayPlanInfoBean>();
        List<RepayPlanInfoBean> plans = repayPlanInfoMapper.getRepayPlan(wrapper.eq("applyId", applyId));

        return Result.succ(plans);
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


    protected boolean addRepayPlan(ApplyInfoBean applyInfoBean) {

        return false;
    }
}
