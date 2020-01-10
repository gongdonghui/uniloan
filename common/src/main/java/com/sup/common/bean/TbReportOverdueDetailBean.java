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
@TableName("tb_report_overdue_detail")
public class TbReportOverdueDetailBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date    data_dt;           //日期
    private Integer operator_id;
    private String  name;
    private Integer apply_id;
    private Integer status;
    private Integer grant_quota;
    private Integer need_total;
    private Integer act_total;
    private Integer normal_repay;
    private Integer overdue_amount;
    private Integer recall_amount;
    private Date    create_time;
    private Date    update_time;
}
