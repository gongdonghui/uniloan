package com.sup.cms.bean.vo;

import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  ApplyQueryParams
 * <p>
 * Author: guanfeng
 * Create: 2019-10-12
 */

@Data
public class ApplyQueryParams {

    // 应还日期
    private Date repayStart;
    // 应还日期
    private Date repayEnd;

    // 实还日期
    private Date actRepayStart;
    // 实还日期
    private Date actRepayEnd;
    // 身份证号
    private String cidNo;
    // 手机号
    private String mobile;

    private String applyId;
    // 产品ID
    private String productId;
}
