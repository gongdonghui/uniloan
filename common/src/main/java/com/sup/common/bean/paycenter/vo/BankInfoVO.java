package com.sup.common.bean.paycenter.vo;

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
    private Integer version;
    private Integer accountSupport;
    private Integer cardSupport;
}
