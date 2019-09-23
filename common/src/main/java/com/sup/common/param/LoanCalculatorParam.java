package com.sup.common.param;

import lombok.Data;

/**
 * Project:uniloan
 * Class:  LoanCalculatorParam
 * <p>
 * Author: guanfeng
 * Create: 2019-09-23
 */

@Data
public class LoanCalculatorParam {
    private Integer productId;      // 申请的产品id
    private Integer applyAmount;    // 申请贷款金额
    private Integer applyPeriod;    // 申请贷款期限（天）
    private Integer inhandAmount;   // 到手金额，由服务端计算
    private Integer totalAmount;    // 还款总金额，由服务端计算
}
