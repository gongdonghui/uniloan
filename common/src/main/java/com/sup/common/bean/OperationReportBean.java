package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@Data
@TableName("tb_report_operation_daily")
public class OperationReportBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date data_dt;
    private Integer apply;
    private Integer apply_cust;
    private Integer auto_pass;
    private Integer auto_deny;
    private Integer manual_pass;
    private Integer manual_deny;
    private Integer pay;
    private Integer pay_amt;
    private Integer first_overdue;
    private Integer repay;
    private Integer repay_actual;
    private Double forate; //  first overdue  rate;
    private Integer register;
    private Integer download;
    private Integer channel_id;
    private Date create_time;
}
