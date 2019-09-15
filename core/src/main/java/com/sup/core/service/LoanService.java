package com.sup.core.service;

import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.loan.LoanFeeTypeEnum;
import com.sup.core.mapper.ProductInfoMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;

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
        TbRepayPlanBean repayPlanBean = new TbRepayPlanBean();
        repayPlanBean.setUser_id(bean.getUser_id());
        repayPlanBean.setApply_id(bean.getId());

        // TODO


        return null;
    }
}
