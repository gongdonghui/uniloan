package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:52
 */
@Data
public class OverdueGetListBean {
    private Integer applyId;
    private Date    shouldRepayDate;    // 应还日期
    private Date    repayDate;          // 实际还款日期
    private String  name;
    private Integer gender;
    private Integer age;
    private String  mobile;
    private String  cidNo;
    private String  appName;
    private String  productName;
    private Integer remainAmount;   // 待收回金额
    private Integer callBackAmount; // 已收回金额
    private Integer loanAmount;     // 借款金额
    private Integer shouldRepayAmount;  // 到期应还金额
    private Integer overdueAmount;  // 逾期金额
    private Integer penaltyInterest;    // 逾期罚息
    private Integer overdueDays;    // 逾期天数
    private Integer taskStatus;     // 催收状态
    private Date loanDate;          // 借款时间
    private Date taskDate;          // 催收时间
    private Integer operatorId;     // 催收员id
    private String  operatorName;   // 催收员姓名
    private Date   latestDate;//最后催收时间
}
