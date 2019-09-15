package com.sup.core.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.core.bean.ApplyMaterialInfoBean;
import com.sup.core.bean.UserBankInfoBean;
import com.sup.core.facade.LoanFacade;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.mapper.ApplyMaterialInfoMapper;
import com.sup.core.mapper.RepayPlanMapper;
import com.sup.core.mapper.UserBankInfoMapper;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.Result;
import com.sup.core.service.ApplyService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Value("loan.auto-loan-retry-times")
    private int AUTO_LOAN_RETRY_TIMES;

    @Autowired
    private ApplyInfoMapper     applyInfoMapper;

    @Autowired
    private RepayPlanMapper repayPlanMapper;

    @Autowired
    private UserBankInfoMapper userBankInfoMapper;

    @Autowired
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;

    @Autowired
    private ApplyService applyService;


    @Override
    public Object autoLoan(String userId, String applyId) {
        // 1. get apply info and check apply status
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(applyId);
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

        // make sure there is only one bank card for current apply
        UserBankInfoBean bankInfoBean = userBankInfoMapper.selectOne(
                bankWrapper.eq("info_id", infoId).eq("user_id", userId));
        if (bankInfoBean == null) {
            log.error("No bank info for user(" + userId + "), info_id=" + infoId);
            return Result.fail("No bank info found!");
        }

        // 3. loan using funpay(need thread safe)
        boolean loanSucc = false;
        synchronized (this) {
            for (int i = 0; i < AUTO_LOAN_RETRY_TIMES; i++) {
                // TODO

                // generate BankInfo for paycenter

                // verifyBankInfo

                // generate payinfo, and loan

            }
        }

        // if loan succeeded, update apply info status
        if (loanSucc) {
            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOANING.getCode());
        } else {
            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
        }
        applyInfoBean.setUpdate_time(new Date());
        applyInfoBean.setOperator_id(0);    // system
        return applyService.updateApplyInfo(applyInfoBean);
    }

    @Override
    public Object addRepayPlan(String userId, String applyId) {
        // get apply info and check apply status
        TbApplyInfoBean bean = applyInfoMapper.selectById(applyId);
        if (bean == null) {
            log.error("addRepayPlan: invalid applyId=" + applyId);
            return Result.fail("Invalid applyId!");
        }
        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(bean.getStatus());
        if (status != ApplyStatusEnum.APPLY_LOAN_SUCC) {
            // repay plan must be added after loan
            log.error("invalid status=(" + status.getCode() + ")" + status.getCodeDesc());
            return Result.fail("Invalid status!");
        }

        if (!addRepayPlan(bean)) {
            log.error("Failed to generate repay plan for user(" + userId + "), applyId = " + applyId);
            return Result.fail("Failed to add repay plan!");
        }

        return Result.succ();
    }

    @Override
    public Object updateRepayPlan(TbRepayPlanBean bean) {
        if (bean == null) {
            return Result.fail("TbRepayPlanBean is null!");
        }
        log.info("updateRepayPlan: userId = " + bean.getUser_id() +
                ", applyId = " + bean.getApply_id());

        bean.setUpdate_time(new Date());
        if (repayPlanMapper.updateById(bean) > 0) {
            return Result.succ();
        }
        return Result.fail("update failed!");
    }

    @Override
    public Object getRepayPlan(String applyId) {
        if (applyId == null) {
            return Result.fail("Invalid applyId!");
        }
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<TbRepayPlanBean>();
        List<TbRepayPlanBean> plans = repayPlanMapper.getRepayPlan(wrapper.eq("applyId", applyId));

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
        // TODO
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
        // TODO
        return null;
    }


    /**
     * 定时检查放款是否成功
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void checkLoanResult() {
        // TODO

        // 1. 获取所有放款中的进件

        // 2. 检查放款状态

        // 3. 对放款成功的进件：更新状态、添加还款计划

    }

    /**
     * 每天查询逾期情况，并更新相应款项
     */
    @Scheduled(cron = "0 1 * * * ?")
    public void checkOverdue() {
        // TODO

        // 1. 获取所有放款中的进件

        // 2. 检查放款状态

        // 3. 对放款成功的进件：更新状态、添加还款计划

    }

    protected boolean addRepayPlan(TbApplyInfoBean applyInfoBean) {
        // generate repay plan if not exist(need thread safe)

        // TODO
        return false;
    }
}
