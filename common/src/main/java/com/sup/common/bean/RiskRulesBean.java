package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_core_risk_rules")
public class RiskRulesBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer product_id;
    private Integer hit_type;
    private Integer value_type;
    private Integer is_in;
    private Integer range_left;
    private Integer range_right;
    private String variable_name;
    private Integer credit_level;
    private float val_left;
    private float val_right;
    private Date create_time;
}
