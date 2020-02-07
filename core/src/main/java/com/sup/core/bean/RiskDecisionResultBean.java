package com.sup.core.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("tb_core_risk_decesion_result")
public class RiskDecisionResultBean {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private int user_id;
    private int product_id;
    private int apply_id;
    private int ret;
    private String refuse_code;
    private Date apply_time;


}
