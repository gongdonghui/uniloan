package com.sup.paycenter.bean.funpay;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/13 21:32
 */
@Data
public class VerifyBankInfoBean {
    private Integer merchantID;
    private Integer businessID;
    private Integer bankNo;
    private String bankLocation;
    private String accountNo;
    private String accountType;
    private String accountName;
    private Integer verify;
    private Integer status;
}
