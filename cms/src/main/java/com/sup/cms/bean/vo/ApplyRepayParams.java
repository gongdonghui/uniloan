package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ApplyRepayParams {
    @NotNull
    private Integer userId;

    @NotNull
    private Integer applyId;

    @NotNull
    private Integer operatorId;

    @NotNull
    private Integer repayAmount;

    /**
     * 还款日期，格式 YYYY-MM-DD hh:ss:mm
     */
    @NotNull
    private String repayDate;
}
