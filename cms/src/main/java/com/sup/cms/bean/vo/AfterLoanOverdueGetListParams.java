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
     * 0 逾期记录(未还)
     * 1 逾期已还记录
     */
    @Min(0)
    private Integer type;
    @Min(0)
    private Integer page;
    @Min(0)
    private Integer pageSize;
}
