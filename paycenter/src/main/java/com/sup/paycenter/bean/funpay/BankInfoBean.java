package com.sup.paycenter.bean.funpay;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 22:14
 */
@Data
public class BankInfoBean {
    private String bankName;
    private int bankID;
    private int version;
    private int status;
    private String abbreviation;
    private int accountSupport;
    private int cardSupport;
}
