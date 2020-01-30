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
    private Date data_dt;       //日期
    private Integer channel_id; //渠道号
    private Integer register;       // 注册数
    private Integer apply_num;  //申请次数
    private Integer apply_user_num;     //申请用户数
    private Integer auto_pass;      //机审通过人数
    private Integer auto_deny;      //机审拒绝人数
    private Integer first_pass;     // 初审通过人数
    private Integer first_deny;     // 初审拒绝人数
    private Integer final_pass;     // 终审通过人数
    private Integer final_deny;     // 终审拒绝人数
    private Integer manual_pass;    // 人工审核通过人数(初审+终审)
    private Integer manual_deny;    // 人工审核拒绝人数(初审+终审)
    private Integer loan_num;       // 放款成功订单数
    private Integer repay;          // 应还订单数
    private Integer repay_actual;   // 实还订单数
    private Integer first_overdue;      // 首逾订单数
    private Long loan_amt;          // 放款成功合同金额
    private Long loan_inhand_amt;   // 放款成功到手金额
    private Long repay_amt;         // 应还总额
    private Long repay_actual_amt;  // 实际还款总额
    private Long first_overdue_amt; // 首逾金额
    private Double forate;          //  first overdue  rate; 首逾比例
    private Integer download;       // 下载数
    private Integer loan_failed;      //放款中订单
    private Integer loan_pending;    //放款失败订单
    private Date create_time;
}
