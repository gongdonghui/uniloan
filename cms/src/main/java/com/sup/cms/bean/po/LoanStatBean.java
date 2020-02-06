package com.sup.cms.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  LoanStatBean
 * <p>
 * Author: guanfeng
 * Create: 2019-10-12
 */

@Data
public class LoanStatBean {

    private Date    dt;
    private Integer loanNum;                // 放款数
    private Long    principal;              // 放款本金
    private Long    contractAmt;            // 合同金额
    private Long    normalRepayAmt;         // 正常还款金额
    private Integer normalRepayNum;         // 正常还款次数
    private Integer firstOverdueNum;        // 首逾笔数
    private Long    firstOverdueAmt;        // 首逾金额
    private Long    overduedContractAmt;    // 逾期已还合同金额
    private Long    overduedPenaltyInterest;// 逾期已还罚息
    private Long    overduingContractAmt;   // 逾期中合同金额
    private Long    repayTotal;             // 总回款
    private Float   foRate;                 // 首逾率
    private Integer overdueNum;             // 当前逾期笔数
    private Float   overdueRate;            // 当前逾期率
    private Float   principalRate;          // 本金回收率

}
