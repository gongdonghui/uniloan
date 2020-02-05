package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  TbApplyInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
@TableName("tb_apply_quickpass_rules")
public class TbApplyQuickpassRulesBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer stage_from;     // 参考ApplyStatusEnum
    private Integer stage_skip_to;  // 参考ApplyStatusEnum
    private Integer apply_count;
    private Integer overdue_count;
    private Long    apply_amount;
    private Integer last_apply_days;
    private Integer overdue_days;
    private Integer max_overdue_days;
    private Integer operator_id;
    private String  comment;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date    create_time;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date    update_time;
}
