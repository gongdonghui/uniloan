package com.sup.core.service;

import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.loan.LoanFeeTypeEnum;
import com.sup.common.util.DateUtil;
import com.sup.core.mapper.ProductInfoMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

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
    private ProductInfoMapper productInfoMapper;

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
        repayPlanBean.setSeq_no(1);


        // TODO


        return null;
    }
}
