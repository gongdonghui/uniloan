package com.sup.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.LoanFeeTypeEnum;
import com.sup.common.loan.RepayPlanOverdueEnum;
import com.sup.common.loan.RepayPlanStatusEnum;
import com.sup.common.util.DateUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.mapper.RepayPlanMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Project:uniloan
 * Class:  LoanService
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@Log4j
public class LoanService {

    @Autowired
    private RepayPlanMapper repayPlanMapper;

    public boolean addRepayPlan(TbApplyInfoBean applyInfoBean) {

        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status != ApplyStatusEnum.APPLY_LOAN_SUCC) {
            // repay plan must be added after loan
            log.error("addRepayPlan: invalid status=(" + status.getCode() + ")" + status.getCodeDesc());
            return false;
        }
        // generate repay plan if not exist(need thread safe)
        TbRepayPlanBean repayPlanBean = repayPlanMapper.getByApplyId(applyInfoBean.getId());
        if (repayPlanBean != null) {
            log.error("RepayPlan already exists for applyId = " + applyInfoBean.getId());
            return false;
        }

        synchronized (this) {
            repayPlanBean = genRepayPlan(applyInfoBean);
            if (repayPlanBean == null) {
                log.error("Failed to generate repay plan for applyId = " + applyInfoBean.getId());
                return false;
            }

            if (repayPlanMapper.insert(repayPlanBean) > 0) {
                return true;
            }
        }
        return false;
    }

    public TbRepayPlanBean genRepayPlan(TbApplyInfoBean bean) {

        LoanFeeTypeEnum feeType = LoanFeeTypeEnum.getStatusByCode(bean.getFee_type());
        if (feeType == null) {
            log.error("genRepayPlan: invalid feeType = " + bean.getFee_type()
                    + ", applyId = " + bean.getId()
            );
            return null;
        }
        int loanAmount = bean.getGrant_quota();
        int feeTotal = (int)(loanAmount * bean.getFee());    // service fee
        int interestTotal = (int)(loanAmount * bean.getRate() * bean.getPeriod());

        int principalToRepay = loanAmount;
        int feeToRepay = feeTotal;
        int interestToRepay = interestTotal;

        switch (feeType) {
            case LOAN_PRE_FEE:
                principalToRepay = loanAmount;
                feeToRepay = 0;
                interestToRepay = interestTotal;
                break;
            case LOAN_PRE_FEE_PRE_INTEREST:
                principalToRepay = loanAmount;
                feeToRepay = 0;
                interestToRepay = 0;
                break;
            case LOAN_POST_FEE_POST_INTEREST:
                principalToRepay = loanAmount;
                feeToRepay = feeTotal;
                interestToRepay = interestTotal;
                break;
            default:
                break;
        }
        int totalToRepay = principalToRepay + feeToRepay + interestToRepay;
        Date repayStartTime = bean.getLoan_time();
        Date repayEndTime = DateUtil.getDate(repayStartTime, bean.getPeriod());

        TbRepayPlanBean repayPlanBean = new TbRepayPlanBean();
        repayPlanBean.setUser_id(bean.getUser_id());
        repayPlanBean.setApply_id(bean.getId());
        repayPlanBean.setProduct_id(bean.getProduct_id());
        repayPlanBean.setSeq_no(1);
        repayPlanBean.setRepay_start_date(repayStartTime);
        repayPlanBean.setRepay_end_date(repayEndTime);
        repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_NOT_PAID.getCode());
        repayPlanBean.setIs_overdue(RepayPlanOverdueEnum.PLAN_NOT_OVER_DUE.getCode());
        repayPlanBean.setNeed_principal(Long.valueOf(principalToRepay));
        repayPlanBean.setNeed_interest(Long.valueOf(interestToRepay));
        repayPlanBean.setNeed_management_fee(Long.valueOf(feeToRepay));
        repayPlanBean.setNeed_total(Long.valueOf(totalToRepay));

        return repayPlanBean;
    }

    public Result updateRepayPlan(TbRepayPlanBean bean) {
        if (bean == null) {
            return Result.fail("TbRepayPlanBean is null!");
        }

        log.debug("updateRepayPlan: bean = " + GsonUtil.toJson(bean));

        bean.setUpdate_time(new Date());
        if (repayPlanMapper.updateById(bean) > 0) {
            return Result.succ();
        }

        log.error("updateRepayPlan: bean = " + GsonUtil.toJson(bean));

        return Result.fail("");
    }

    public Result getRepayPlan(String applyId) {
        if (applyId == null) {
            return Result.fail("Invalid applyId!");
        }
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        List<TbRepayPlanBean> plans = repayPlanMapper.getRepayPlan(wrapper.eq("applyId", applyId));

        return Result.succ(plans);
    }

    public boolean writeOffRepayPlan(Integer applyId) {
        // write off all the repay plan
        if (applyId == null) {
            return false;
        }
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        List<TbRepayPlanBean> plans = repayPlanMapper.getRepayPlan(wrapper.eq("applyId", applyId));
        int status = RepayPlanStatusEnum.PLAN_PAID_WRITE_OFF.getCode();
        Date now = new Date();
        for (TbRepayPlanBean bean : plans) {
            bean.setRepay_status(status);
            bean.setUpdate_time(now);
            repayPlanMapper.updateById(bean);
        }
        return true;
    }
}
