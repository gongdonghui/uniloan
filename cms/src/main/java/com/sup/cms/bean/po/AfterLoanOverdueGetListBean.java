package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:52
 */
@Data
public class AfterLoanOverdueGetListBean {
    private Integer applyId;
    private String appName;
    private String productName;
    private String name;
    private String mobile;
    private String cidNo;
    private Integer loanAmount;
    private Integer shouldRepayAmount;
    private Integer overdueAmount;
    private Date    loanDate;   // 放款日期
    private Date    endDate;    // 还款截止日期
    private Date    repayDate;  // 还款日期
    private Integer period;
    /**
     * 催收次数
     */
    private Integer collectionTimes;
}
