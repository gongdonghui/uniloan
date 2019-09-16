package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigInteger;
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
public class TbRepayPlanBean {
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
    private BigInteger need_principal = BigInteger.ZERO;
    private BigInteger act_principal = BigInteger.ZERO;
    private BigInteger need_interest = BigInteger.ZERO;
    private BigInteger act_interest = BigInteger.ZERO;
    private BigInteger need_penalty_interest = BigInteger.ZERO;
    private BigInteger act_penalty_interest = BigInteger.ZERO;
    private BigInteger need_management_fee = BigInteger.ZERO;
    private BigInteger act_management_fee = BigInteger.ZERO;
    private BigInteger need_late_payment_fee = BigInteger.ZERO;
    private BigInteger act_late_payment_fee = BigInteger.ZERO;
    private BigInteger need_breach_fee = BigInteger.ZERO;
    private BigInteger act_breach_fee = BigInteger.ZERO;
    private BigInteger need_other = BigInteger.ZERO;
    private BigInteger act_other = BigInteger.ZERO;
    private BigInteger need_total = BigInteger.ZERO;
    private BigInteger act_total = BigInteger.ZERO;
    private Integer operator_id = 0;
    private String  repay_code;     // 自动还款交易码
    private String  repay_location; // 自动还款地址
    private String  trade_number;   // 自动还款流水号
    private Date expire_time;
    private Date create_time;
    private Date update_time;
}
