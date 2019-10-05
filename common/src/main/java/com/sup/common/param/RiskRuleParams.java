package com.sup.common.param;

import lombok.Data;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@Data
public class RiskRuleParams {
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
