package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tb_repay_stat")
public class TbRepayStatBean {
    private Integer apply_id;
    private Long need_principal = 0L;
    private Long act_principal = 0L;
    private Long need_interest = 0L;
    private Long act_interest = 0L;
    private Long need_management_fee = 0L;
    private Long act_management_fee = 0L;
    private Long need_penalty_interest = 0L;
    private Long act_penalty_interest = 0L;
    private Long need_late_payment_fee = 0L;
    private Long act_late_payment_fee = 0L;
    private Long need_breach_fee = 0L;
    private Long act_breach_fee = 0L;
    private Long need_other = 0L;
    private Long act_other = 0L;
    private Long need_total = 0L;
    private Long act_total = 0L;
    private Integer current_seq;
    private Integer normal_repay_times;
    private Integer overdue_repay_times;
    private Integer overdue_times;
    private Date create_time;
    private Date update_time;

}
