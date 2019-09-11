package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  RepayPlanInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-06
 */

@Data
@TableName("tb_repay_plan")
public class RepayPlanInfoBean {
    @TableId(type = IdType.AUTO)
    private Integer id;         // repay plan id
    private Integer user_id;
    private Integer apply_id;
    private Integer seq_no;
    private Date repay_start_date;
    private Date repay_end_date;
    private Date repay_time;
    private Integer repay_status;
    private Integer is_overdue;
    private Float need_principal;
    private Float act_principal;
    private Float need_interest;
    private Float act_interest;
    private Float need_penalty_interest;
    private Float act_penalty_interest;
    private Float need_management_fee;
    private Float act_management_fee;
    private Float need_late_payment_fee;
    private Float act_late_payment_fee;
    private Float need_breach_fee;
    private Float act_breach_fee;
    private Float need_other;
    private Float act_other;
    private Float need_total;
    private Float act_total;
    private Date create_time;
    private Date update_time;
}
