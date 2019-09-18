package com.sup.common.mq;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplyStateMessage implements Serializable {
    private Integer user_id;
    private Integer product_id;
    private Integer channel_id;
    private Integer app_id;
    private Integer status;     // 进件状态:  0:待审核, 1:自动审核通过, 2:初审通过, 3:复审通过, 4:终审通过,
    //           5:自动审核拒绝, 6:初审拒绝, 7:复审拒绝, 8:终审拒绝, 9:取消或异常
    //           10:自动放款中，11:自动放款失败，12:已放款/还款中，13:未还清，14:已还清，
    //           15:逾期，16:核销

    private String  deny_code;      // 拒贷码
    private String  create_time;    // 申请时间 yyyy-MM-dd HH:mm:ss
}
