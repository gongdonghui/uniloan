package com.sup.core.service;

        import com.sup.core.bean.RiskDecisionResultBean;
        import com.sup.core.param.AutoDecisionParam;

/**
 * gongshuai
 * <p>
 * 2019/9/16
 */
public interface DecesionEngine {

    public RiskDecisionResultBean applyRules(AutoDecisionParam param);

}
