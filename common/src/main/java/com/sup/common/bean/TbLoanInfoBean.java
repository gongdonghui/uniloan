package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sup.common.loan.RepayPlanOverdueEnum;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  TbRepayStatBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("tb_loan_info")
public class TbLoanInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer apply_id;
    private Integer user_id;
    private Integer product_id;
    private Integer channel_id;
    private Integer app_id;
    private Integer current_seq;    // 当前还款期数
    private Integer status;         // 订单状态，详见tb_apply_info.status
    private Integer is_overdue;
    private Integer max_overdue_days;
    private Date    regist_time;
    private Date    apply_time;
    private Date    loan_time;
    private Date    repay_end_date; // 应还款日期
    private Date    repay_time;     // 最近还款日期
    private Long    contract_amount;
    private Long    inhand_amount;
    private Long    need_principal = 0L;    // 应还本金
    private Long    act_principal = 0L;     // 已还本金
    private Long    need_total = 0L;
    private Long    act_total = 0L;
    private Long    normal_repay = 0L;     // 正常还款总额，逾期后还款不计入内
    private Long    reduction_fee = 0L;
    private Long    need_penalty_interest = 0L;
    private Long    act_penalty_interest = 0L;
    private Long    overdue_amount = 0L;        // 逾期金额
    private Long    remain_overdue_amount = 0L; // 剩余逾期金额

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date create_time;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
    private Date update_time;

}
