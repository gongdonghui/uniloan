package com.sup.common.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  ManualLoanParam
 * <p>
 * Author: guanfeng
 * Create: 2019-09-17
 */

@Data
public class ManualLoanParam {
    private String  userId;
    private String  applyId;
    private String  operatorId; // 操作人ID
    private Integer amount;     // 放款金额

    private String  loanTime;  // 放款时间 yyyy-MM-dd HH:mm:ss
}
