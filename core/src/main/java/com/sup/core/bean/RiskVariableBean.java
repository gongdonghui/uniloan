package com.sup.core.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * gongshuai
 * <p>
 * 2020/3/29
 */
@Data
@TableName("tb_core_riskvaraiables")
public class RiskVariableBean {

    private Integer  apply_id;
    private String   variables;
}
