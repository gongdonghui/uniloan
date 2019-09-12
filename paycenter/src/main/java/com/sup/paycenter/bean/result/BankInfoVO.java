package com.sup.paycenter.bean.result;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 22:46
 */
@Data
public class BankInfoVO {
    private String bankName;
    private String abbreviation;
    private String bankNo;
    private int version;
    private int accountSupport;
    private int cardSupport;
}
