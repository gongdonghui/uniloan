package com.sup.core.service;

        import com.sup.core.bean.RiskDecisionResultBean;
        import com.sup.core.param.AutoDecisionParam;

/**
 * gongshuai
 * <p>
 * 2019/9/16
 */
public interface DecesionEngine {
    public RiskDecisionResultBean applyRules(String userId, String applyId, String productId, String info_id);

    public RiskDecisionResultBean applyRules(AutoDecisionParam param);

}
