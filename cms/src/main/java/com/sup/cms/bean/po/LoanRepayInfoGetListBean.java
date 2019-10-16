package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 17:50
 */
@Data
public class LoanRepayInfoGetListBean {
    private Integer applyId;
    private Integer userId;
    private String mobile;
    private String productName;
    private String name;
    private String cidNo;
    private Integer writeOffAmount;
    /**
     * 放款金额
     */
    private Integer loanAmount;
    private Integer shouldRepayAmount;
    private Integer repayAmount;

    /**
     * 手动还款待确认
     * 0 无手动还款
     * 1 已手动还款，待确认
     */
    private Integer repayNeedConfirm;
    /**
     * 实放时间
     */
    private Date loanDate;
    /**
     * 总到期日
     */
    private Date endDate;
    /**
     * 最新还款日期
     */
    private Date repayDate;
    /**
     * 操作时间
     */
    private Date updateTime;
    /**
     * 贷款期次
     */
    private Integer period;
    /**
     * 已还期次
     */
    private Integer repayPeriod;
}
