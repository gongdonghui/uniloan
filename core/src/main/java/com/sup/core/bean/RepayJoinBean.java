package com.sup.core.bean;

import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2020/2/6
 */
@Data
public class RepayJoinBean {

    private Date repay_start_date;
    private Date repay_end_date;
    private Date  loan_time;
    private Integer  operator_id;
    private Integer   task_type;

}
