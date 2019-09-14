package com.sup.common.bean.paycenter.vo;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/9/14 15:50
 */
@Data
public class RepayVO {
    /**
     * 交易码 展示给用户
     */
    private String code;
    /**
     * 便利店地址
     */
    private String shopLink;
    /**
     * 交易流水号
     */
    private String tradeNo;
    /**
     * 交易码过期时间  yyyy-dd-mm hh:mm:ss
     */
    private String expireDate;
}
