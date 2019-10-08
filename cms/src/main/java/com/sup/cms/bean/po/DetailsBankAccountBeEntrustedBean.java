package com.sup.cms.bean.po;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/10/7 23:36
 */
@Data
public class DetailsBankAccountBeEntrustedBean {
    /**
     * 开户名
     */
    private String name;
    /**
     * 银行卡号
     */
    private String account;
    /**
     * 所属银行
     */
    private Integer bank;
}
