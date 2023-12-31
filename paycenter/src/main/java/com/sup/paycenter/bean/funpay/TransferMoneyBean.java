package com.sup.paycenter.bean.funpay;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/13 22:11
 */
@Data
public class TransferMoneyBean {
    private Integer merchantID;
    private Integer businessID;
    private Integer amount;
    private String currency;
    private String orderNo;
    private String accountNo;
    private String accountType;
    private String accountName;
    private String bankLocation;
    private Integer bankNo;
    private Integer bankBranchNo;
    private String sendTime;

    private Integer status;

    private String tradeNo;

}
