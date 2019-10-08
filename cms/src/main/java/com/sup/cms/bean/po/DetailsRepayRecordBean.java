package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * 还款记录
 *
 * @Author: kouichi
 * @Date: 2019/10/7 18:07
 */
@Data
public class DetailsRepayRecordBean {
    //    实收时间
    private Date repayTime;
    //            实收金额
    private Integer actTotal;
    //    剩余应收金额
    private Integer remainTotal;
    //            实收本金
    private Integer actPrincipal;
    //    实收利息
    private Integer actInterest;
    //    实收罚息
    private Integer actPenaltyInterest;
    //            其他费用
    private Integer remainOther;
}
