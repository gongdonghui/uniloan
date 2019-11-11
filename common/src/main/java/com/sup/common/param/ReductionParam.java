package com.sup.common.param;

import lombok.Data;

/**
 * Project:uniloan
 * Class:  ReductionParam
 * <p>
 * Author: guanfeng
 * Create: 2019-09-17
 */

@Data
public class ReductionParam {
    private Integer  applyId;
    private Integer  planId;
    private Integer  operatorId;
    private Integer amount;     // 减免金额
    private String  comment;    // 减免理由
}
