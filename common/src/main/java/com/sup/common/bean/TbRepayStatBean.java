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

    public void add(TbRepayPlanBean bean) {
        synchronized (this) {
            need_principal += bean.getNeed_principal();
            act_principal  += bean.getAct_principal();
            need_interest += bean.getNeed_interest();
            act_interest  += bean.getAct_interest();
            need_management_fee += bean.getNeed_management_fee();
            act_management_fee  += bean.getAct_management_fee();
            need_penalty_interest += bean.getNeed_penalty_interest();
            act_penalty_interest  += bean.getAct_penalty_interest();
            need_late_payment_fee += bean.getNeed_late_payment_fee();
            act_late_payment_fee  += bean.getAct_late_payment_fee();
            need_breach_fee += bean.getNeed_breach_fee();
            act_breach_fee  += bean.getAct_breach_fee();
            need_other += bean.getNeed_other();
            act_other  += bean.getAct_other();

            need_total += bean.getNeed_total();
            act_total  += bean.getAct_total();
        }
    }
}
