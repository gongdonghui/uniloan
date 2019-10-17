package com.sup.common.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
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
    @NotNull
    private String  userId;
    @NotNull
    private String  applyId;
    @NotNull
    private String  operatorId;     // 操作人ID
    @NotNull
    private Integer amount;         // 放款金额
    @NotNull
    private String  tradeNumber;    // 手动放款流水号
    @NotNull
    private String  loanTime;       // 放款时间 yyyy-MM-dd HH:mm:ss
}
