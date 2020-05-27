package com.sup.common.bean.paycenter.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2020/5/17 16:35
 */
@Data
public class CreateVCVO {
    /**
     * 需要展示给用户 虚拟银行账号
     */
    private String accountNo;
    /**
     * 需要展示给用户 银行户主名称
     */
    private String accountName;
    /**
     * 需要展示给用户 银行名称
     */
    private String bankName;
    /**
     * 需要展示给用户 支行名称
     */
    private String branchBankName;
    /**
     * 需要展示给用户 银行地图信息
     */
    private String bankLink;
    /**
     * 需要展示给用户 用户服务费 用户每次存钱都会扣取这个服务费
     */
    private Integer serviceFee;

    private Date    expireDate;  // yyyyMMdd
}
