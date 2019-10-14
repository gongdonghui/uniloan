package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 18:49
 */
@Data
public class LoanUnRepayInfoGetListBean {
    private Integer applyId;
    private Integer userId;
    private String mobile;
    private String productName;
    private String name;
    private String cidNo;
    private Integer loanAmount;
    private Integer shouldRepayAmount;
    /**
     * 放款日期
     */
    private Date loanDate;
    /**
     * 总到期日
     */
    private Date endDate;
    /**
     * 期次
     */
    private Integer period;
}
