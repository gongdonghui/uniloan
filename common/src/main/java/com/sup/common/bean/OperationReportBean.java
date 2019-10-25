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
    private Date data_dt;   //日期
    private Integer apply;   //申请次数
    private Integer apply_cust;   //申请用户数
    private Integer auto_pass;    //机审通过人数
    private Integer auto_deny;   //机审拒绝人数
    private Integer manual_pass;  //人工审核通过人数
    private Integer manual_deny;    //人工审核拒绝人数
    private Integer pay;        //放款成功订单数
    private Integer pay_amt;   //放款成功金额
    private Integer first_overdue;   //首逾订单数
    private Integer repay;    //应还总额
    private Integer repay_actual;    //实际还款总额
    private Double forate; //  first overdue  rate; 首逾比例
    private Integer register;    //注册数
    private Integer download;    //下载数
    private Integer channel_id;    //渠道号
    private Date create_time;
}
