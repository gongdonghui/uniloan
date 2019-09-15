package com.sup.core.service;

import com.sup.core.bean.RiskDecisionResultBean;
import com.sup.core.bean.RiskRulesBean;
import com.sup.core.mapper.RiskRulesMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Log4j
@Service
public class DecisionEngin {
    @Autowired
    private RiskRulesMapper riskRulesMapper;

    @Autowired
    private com.sup.core.service.RedisClient redisClient;

    private boolean applySingleRule(RiskRulesBean rule, String mobile, Map<String, Double> riskBean) {

        if (rule.getValue_type() == 1) {  //名单类
            String key = rule.getVariable_name() + mobile;
            boolean exist = redisClient.Exist(key);
            return exist && rule.getIs_in() == 1;   //is_in:1 表示在名单 （白名单），0  表示不能在名单（黑名单）


        } else {
            String variable_name = rule.getVariable_name();
            double val = riskBean.get(variable_name);
            boolean ret = false;
            if (rule.getRange_left() == 2) {
                ret = val > rule.getVal_left();
            } else {
                ret = val >= rule.getVal_left();
            }
            boolean ret_right = false;
            if (rule.getRange_right() == 1) {
                ret_right = val > rule.getVal_right();
            } else {
                ret_right = val >= rule.getVal_left();
            }
            return ret && ret_right;
        }
    }
    public   Map<String,Double>   prepareRiskVariables() {


        Map<String, Double> riskBean = new HashMap<String, Double>();

        return  riskBean;

    }


    public RiskDecisionResultBean applyRules(String userId, String applyId, String productId) {

        List<RiskRulesBean> rulesBeanList = riskRulesMapper.loadRulesByProduct(Integer.parseInt(productId));
        //TODO prepareRiskVariables();
        Map<String, Double> riskBean =  prepareRiskVariables();
        String mobile = "";

        RiskDecisionResultBean result = new RiskDecisionResultBean();
        Map<String, Integer> force_ret = new HashMap<String, Integer>();
        Map<String, Integer> option_ret = new HashMap<String, Integer>();
        result.setRet(0);

        for (RiskRulesBean rule : rulesBeanList) {

            if (rule.getHit_type() == 1) {   //必须通过类


                boolean ret = applySingleRule(rule, mobile, riskBean);
                force_ret.put(rule.getVariable_name(), (ret ? 0 : 1));
                if (!ret) {
                    int code = rule.getId();
                    result.setRefuse_code("r00" + Integer.toString(code));   // 拒贷码
                    result.setRet(1);
                }

            } else if (rule.getHit_type() == 0) {  //提示类

                boolean ret = applySingleRule(rule, mobile, riskBean);
                option_ret.put(rule.getVariable_name(), (ret ? 0 : 1));

            }
        }
        result.setOption_rules(option_ret);
        result.setForce_rules(force_ret);
        return result;
    }
}
