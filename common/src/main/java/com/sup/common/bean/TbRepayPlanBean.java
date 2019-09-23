package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TbRepayPlanBean {
    @TableId(type = IdType.AUTO)
    private Integer id;         // repay plan id
    private Integer user_id;
    private Integer apply_id;
    private Integer product_id;
    private Integer seq_no;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date repay_start_date;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date repay_end_date;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date repay_time;

    private Integer repay_status;   // 还款状态 0|未还  1|未还清 2|已还清 3|自助还款处理中 4|自助还款处理失败 5|核销
    private Integer is_overdue;
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
    private Integer operator_id = 0;
    private String  repay_code;     // 自动还款交易码
    private String  repay_location; // 自动还款地址
    private String  trade_number;   // 自动还款流水号

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date expire_time;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date update_time;

    // 根据实际还款金额，仅更新实际还款各个字段，状态仍需在调用出处理
    public void updateActFields(Long actRepayAmount) {
        Long[] needAmounts = {
                need_principal, need_interest, need_management_fee, need_penalty_interest,
                need_late_payment_fee, need_breach_fee, need_other
        };
        Long[] actAmounts  = {
                act_principal, act_interest, act_management_fee, act_penalty_interest,
                act_late_payment_fee, act_breach_fee, act_other
        };

        synchronized (this) {
            Long gap = 0L;
            for (int i = 0; i < needAmounts.length; ++i) {
                if (actRepayAmount <= 0) {
                    break;
                }
                gap = needAmounts[i] - actAmounts[i];   // fill the gap
                actAmounts[i] = Math.min(actAmounts[i] + actRepayAmount, needAmounts[i]);
                actRepayAmount -= gap;
            }
            act_principal = actAmounts[0];
            act_interest  = actAmounts[1];
            act_management_fee = actAmounts[2];
            act_penalty_interest = actAmounts[3];
            act_late_payment_fee = actAmounts[4];
            act_breach_fee = actAmounts[5];
            act_other = actAmounts[6];

            act_total = 0L;
            for (int i = 0; i < actAmounts.length; ++i) {
                act_total += actAmounts[i];
            }
        }
    }
}
