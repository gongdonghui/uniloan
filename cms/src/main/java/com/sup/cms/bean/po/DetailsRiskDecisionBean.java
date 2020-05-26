package com.sup.cms.bean.po;

import lombok.Data;

/**
 * @Author: kouichi
 * @Date: 2019/10/8 21:31
 */
@Data
public class DetailsRiskDecisionBean {
    private Integer ruleStatus;
    private Integer ruleHitType;
    private String  variableName;
    private Double  val;
}
