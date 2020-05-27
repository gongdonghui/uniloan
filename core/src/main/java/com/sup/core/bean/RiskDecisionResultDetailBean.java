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
    private Integer rule_id;
    private Integer rule_status;
    private Integer rule_hit_type;
    private Integer decesion_id;
    private Integer user_id;
    private Integer product_id;
    private Integer apply_id;
    private Double val;
    private String variable;
    private Date apply_date;

}
