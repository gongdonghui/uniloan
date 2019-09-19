package com.sup.common.bean.paycenter.vo;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/14 16:17
 */
@Data
public class RepayStatusVO {
    private Integer amount;
    private Integer status;
    private String purchaseTime;    // yyyyMMddHHmmss
}
