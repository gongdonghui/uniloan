package com.sup.cms.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @Author: kouichi
 * @Date: 2019/10/13 19:52
 */
@Data
public class ReportCollectorBean {
    private Date    allocDate;      // 分配时间
    private Integer operatorId;     // 催收员id
    private Integer groupId;        // 催收员分组id
    private Integer taskNum;        // 分单数
    private Long    taskAmount;     // 分单金额，即 应还总额-正常还款总额（分配前）
    private Integer collectNum;     // 已催回订单数
    private Long    collectAmt;     // 已催回订单金额
    private Integer partialCollectNum;  // 部分催回订单数
    private Long    partialCollectAmt;  // 部分催回订单金额
    private Integer noCollectNum;   // 未催回订单数
    private Float   collectRate;    // 催回率
}
