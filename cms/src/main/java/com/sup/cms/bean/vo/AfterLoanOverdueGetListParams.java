package com.sup.cms.bean.vo;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:49
 */
@Data
public class AfterLoanOverdueGetListParams {
    /**
     * 逾期天数
     */
    private Integer overdueDays;
    private Integer applyId;
    private Integer productId;
    private String cidNo;
    private String mobile;
    private String appName;
    /**
     * 类型
     * 0 逾期 & 未还款
     * 1 逾期 & 部分还款
     * 2 逾期 & 已还清
     */
    @Min(0)
    private Integer type;
    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
