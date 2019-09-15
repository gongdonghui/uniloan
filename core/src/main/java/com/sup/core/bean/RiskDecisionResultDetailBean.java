package com.sup.core.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_core_risk_decesion_result_detail")
public class RiskDecisionResultDetailBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private int rule_id;
    private int rule_status;
    private int rule_hit_type;
    private int decesion_id;
    private int user_id;
    private int product_id;
    private int apply_id;
    private Date apply_time;

}
