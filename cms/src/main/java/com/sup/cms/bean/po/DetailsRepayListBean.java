package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 21:48
 */
@Data
public class DetailsRepayListBean {
    private Integer planId;     // 还款计划id
    private Integer seqNo;
    private Integer shouldRepayAmount;          // 应还金额
    private Integer remainShouldRepayAmount;    // 剩余还款金额
    private Integer remainPrincipal;            // 剩余本金
    private Integer remainInterest;             // 剩余利息
    private Integer actRepayAmount;             // 实还金额
    private Date    shouldRepayDate;            // 应还日期
    private Date    actRepayDate;               // 还款日期
    private Integer remainPenaltyInterestAmount;// 剩余罚息
    private Integer remainBreachFeeAmount;      // 剩余违约金
    private Integer status;
}
