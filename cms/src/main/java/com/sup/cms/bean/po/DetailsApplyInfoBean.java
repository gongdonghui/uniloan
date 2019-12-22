package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * 进件信息
 *
 * @Author: kouichi
 * @Date: 2019/10/7 17:27
 */
@Data
public class DetailsApplyInfoBean {
    /**
     * 进件状态
     */
    private Integer status;
    /**
     * 订单号
     */
    private Integer applyId;
    /**
     * 申请用户id
     */
    private Integer userId;
    /**
     * 申请时间
     */
    private Date createTime;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 申请金额
     */
    private Integer applyQuota;
    /**
     * 申请利率
     */
    private Double applyRate;
    /**
     * 还款方式
     */
    private Integer feeType;
    /**
     * 审批金额
     */
    private Integer grantQuota;
    /**
     * 审批利率
     */
    private Double rate;
    /**
     * 贷款编号 和applyId一样
     */
    private Integer loanId;
    /**
     * 借款用途
     */
    private String purpose;
    /**
     * 预授信金额
     */
    private Integer quota;
    /**
     * app名称
     */
    private String appName;
    /**
     * 信用等级
     */
    private String creditLevel;
    /**
     * 渠道标识
     */
    private String channel;

}
