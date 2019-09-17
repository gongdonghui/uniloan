package com.sup.common.param;

import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  ManualRepayParam
 * <p>
 * Author: guanfeng
 * Create: 2019-09-17
 */

@Data
public class ManualRepayParam {
    private String  userId;
    private String  applyId;
    private Integer amount;     // 还款金额
    private Date    repayTime;  // 还款时间
}
