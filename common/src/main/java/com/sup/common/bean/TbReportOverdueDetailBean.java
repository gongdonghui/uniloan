package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sup.common.util.DateUtil;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@Data
@TableName("tb_report_overdue_detail")
public class TbReportOverdueDetailBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date    data_dt;           //日期
    private Integer task_id;
    private Integer operator_id;
    private String  name;
    private Integer apply_id;
    private Integer product_id;
    private Integer status;
    private Integer is_overdue;
    private Integer overdue_days;
    private Integer grant_quota;
    private Integer need_total;
    private Integer act_total;
    private Integer normal_repay;   // 任务分配前，正常还款金额
    private Integer overdue_amount;
    private Integer recall_amount;
    private Date    repay_end_date;
    private Date    create_time;
    private Date    update_time;

    public boolean eaquals(TbReportOverdueDetailBean bean) {
        // 忽略normal_repay
        return DateUtil.isSameDay(data_dt, bean.data_dt)
                && task_id.equals(bean.task_id)
                && operator_id.equals(bean.operator_id)
                && apply_id.equals(bean.apply_id)
                && product_id.equals(bean.product_id)
                && status.equals(bean.status)
                && is_overdue.equals(bean.is_overdue)
                && overdue_days.equals(bean.overdue_days)
                && grant_quota.equals(bean.grant_quota)
                && need_total.equals(bean.need_total)
                && act_total.equals(bean.act_total)
                && overdue_amount.equals(bean.overdue_amount)
                && recall_amount.equals(bean.recall_amount)
                && DateUtil.isSameDay(repay_end_date, bean.repay_end_date)
                ;
    }
}
