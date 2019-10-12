package com.sup.cms.bean.po;

import lombok.Data;

/**
 * Project:uniloan
 * Class:  LoanInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-10-12
 */

@Data
public class LoanInfoBean {

    // 订单号
    private Integer applyId;

    // 手机号
    private String mobile;

    // 还款状态
    private Integer status;

    // 产品名称
    private String productName;

    // 名称
    private String name;

    // 身份证号
    private String cidNo;
}
