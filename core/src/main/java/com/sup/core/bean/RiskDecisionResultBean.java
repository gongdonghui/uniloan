package com.sup.core.bean;


import lombok.Data;

import java.util.Map;

@Data
public class RiskDecisionResultBean {
    private int ret;
    private String refuse_code;
    private Map<String, Integer>    option_rules;
    private Map<String, Integer>    force_rules;

}
