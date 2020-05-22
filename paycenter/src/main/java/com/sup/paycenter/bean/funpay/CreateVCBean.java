package com.sup.paycenter.bean.funpay;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2020/5/17 16:32
 */
@Data
public class CreateVCBean {
    private Integer merchantID;
    private Integer businessID;
    private Integer feeID;
    private String orderNo;
    private String expireDate;  // yyyyMMdd
    private Integer amount;
    private String currency;
    private String accountNo;
    private String accountName;
    private String bankName;
    private String branchBankName;
    private String bankLink;
    private Integer serviceFee;
    private String sign;
}
