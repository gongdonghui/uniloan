package com.sup.common.param;

import lombok.Data;

/**
 * Project:uniloan
 * Class:  FunpayCallBackParam
 * <p>
 * Author: guanfeng
 * Create: 2019-09-17
 */

@Data
public class FunpayCallBackParam {
    private String userId;
    private String applyId;
    private String tradeNo;
    private Integer status;     // see code map in FunpayOrderUtil
}
