package com.sup.paycenter.bean.funpay;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/14 15:45
 */
@Data
public class OfflinePayBean {
    private Integer merchantID;
    private Integer businessID;
    private Integer feeID;
    private Integer amount;
    private String currency;
    private String orderNo;
    private Integer status;
    private String purchaseTime;
    private String tradeNo;
    private String name;

    private String code;
    private String shopLink;
}
