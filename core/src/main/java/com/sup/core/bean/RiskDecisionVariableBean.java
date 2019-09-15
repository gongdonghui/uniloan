package com.sup.core.bean;

/**
 * Project:uniloan
 * Class:  RiskDecisionVariableBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-08
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_core_risk_variables")
public class RiskDecisionVariableBean {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String value_type;

}
