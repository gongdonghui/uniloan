package com.sup.paycenter.bean.funpay;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2020/5/17 16:41
 */
@Data
public class VCBackBean {
    private Integer merchantID;
    private Integer businessID;
    private Integer feeID;
    private String orderNo;
    private String tradeNo;
    private String expireDate;
    private Integer amount;
    private String currency;
    private String accountNo;
    private String accountName;
    private String bankName;
    private String branchBankName;
    private String bankLink;
    private Integer purchaseAmount;
    private String purchaseCurrency;
    private String purchaseTime;
    private String remark;
    private Integer serviceFee;
    private String sign;
}
