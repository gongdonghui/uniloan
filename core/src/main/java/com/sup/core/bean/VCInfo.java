package com.sup.core.bean;

import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/9/15
 */
@Data
public class VCInfo {
    private Integer id;
    private String  accountName;    // bank account name
    private String  bankName;
    private String  branchName;
    private String  bankLink;
    private Integer serviceFee;
    private String  accountNo;      // virtual card no
    private String  applyId;
    private String  orderNo;        // 订单号，还款历史id
    private Integer status;
    private Date    expireTime;
}
