package com.sup.common.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

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
    private Integer amount;     // 交易金额（放款或者还款）

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date    finishTime; // 交易完成时间
}
