package com.sup.core.service;

        import com.sup.core.bean.RiskDecisionResultBean;

/**
 * gongshuai
 * <p>
 * 2019/9/16
 */
public interface DecesionEngine {
    public RiskDecisionResultBean applyRules(String userId, String applyId, String productId, String info_id);
}
