package com.sup.paycenter.bean.funpay;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 22:14
 */
@Data
public class BankInfoBean {
    private String bankName;
    private Integer bankID;
    private Integer version;
    private Integer status;
    private String abbreviation;
    private Integer accountSupport;
    private Integer cardSupport;
}
