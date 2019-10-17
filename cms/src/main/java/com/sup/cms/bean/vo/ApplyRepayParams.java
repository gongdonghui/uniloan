package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class ApplyRepayParams {
    @NotNull
    private Integer userId;

    @NotNull
    private Integer applyId;

    @NotNull
    private Integer operatorId;

    private Integer repayAmount;

    /**
     * 手动还款确认？
     * 0：还款失败
     * 1：还款成功
     */
    @NotNull
    private Integer confirm;

    @NotNull
    private List<Integer> repayInfoIds;     // id of tb_manual_repay

    /**
     * 还款流水号
     */
    private List<String> tradeNos;

    /**
     * 还款日期，格式 YYYY-MM-DD hh:ss:mm
     */
    private String repayDate;
}
