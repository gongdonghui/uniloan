package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/9/21
 */

@Data
@TableName("tb_core_credit_level_rules")
public class CreditLevelRuleBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer max_overdue_days;
    private Integer reloan_times;
    private Integer level;
    private Date create_time;

}
