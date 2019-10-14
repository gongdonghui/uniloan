package com.sup.cms.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.cms.facade.RuleConfigFacade;
import com.sup.common.bean.RiskVariableBean;
import com.sup.common.param.AssetLevelPraram;
import com.sup.common.param.CreditLevelParam;
import com.sup.common.param.DelRuleParam;
import com.sup.common.param.RiskRuleParams;
import com.sup.common.util.Result;
import com.sup.common.bean.AssetsLevelRuleBean;
import com.sup.common.bean.CreditLevelRuleBean;
import com.sup.common.bean.RiskRulesBean;
import com.sup.cms.mapper.AssetsLevelRulesMapper;
import com.sup.cms.mapper.CreditLevelRulesMapper;
import com.sup.cms.mapper.RiskRulesMapper;
import com.sup.common.util.RiskVariableConstants;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */
@RestController
@Log4j
public class RuleConfigImplFacade implements RuleConfigFacade {

    @Autowired
    private CreditLevelRulesMapper creditLevelRulesMapper;
    @Autowired
    private AssetsLevelRulesMapper assetsLevelRulesMapper;
    @Autowired
    private RiskRulesMapper riskRulesMapper;

    @Override
    public Result addCreditLevel(CreditLevelParam param) {
        if (param == null) {
            return Result.fail("input  param is null");
        }
        CreditLevelRuleBean creditLevelRuleBean = new CreditLevelRuleBean();
        creditLevelRuleBean.setMax_overdue_days(param.getMax_overdue_days());
        creditLevelRuleBean.setReloan_times(param.getReloan_times());
        creditLevelRuleBean.setLevel(param.getLevel());
        creditLevelRuleBean.setCreate_time(new Date());

        creditLevelRulesMapper.insert(creditLevelRuleBean);


        return Result.succ();
    }

    @Override
    public Result delCreditLevel(DelRuleParam param) {

        if (param == null) {
            return Result.fail("input param is  null");
        }
        Integer id = param.getRule_id();
        creditLevelRulesMapper.deleteById(id);
        return Result.succ();
    }

    @Override
    public Result addAssetLevel(AssetLevelPraram param) {

        if (param == null) {
            return Result.fail("input  param is null");
        }
        AssetsLevelRuleBean bean = new AssetsLevelRuleBean();
        bean.setLevel_name(param.getLevel_name());
        bean.setLevel(param.getLevel());
        bean.setBetween_paydays(param.getBetween_days());
        bean.setCreate_time(new Date());
        this.assetsLevelRulesMapper.insert(bean);
        return Result.succ();
    }

    @Override
    public Result delAssetLevels(DelRuleParam param) {
        if (param == null) {
            return Result.fail("input  param is  null");

        }
        Integer rule_id = param.getRule_id();
        this.assetsLevelRulesMapper.deleteById(rule_id);
        return Result.succ();
    }

    @Override
    public Result addRiskRule(RiskRuleParams param) {
        if (param == null) {
            return Result.fail("input param is null");
        }

        RiskRulesBean riskRulesBean = new RiskRulesBean();
        riskRulesBean.setVariable_name(param.getVariable_name());
        riskRulesBean.setValue_type(param.getValue_type());
        riskRulesBean.setHit_type(param.getHit_type());
        riskRulesBean.setIs_in(param.getIs_in());
        riskRulesBean.setRange_left(param.getRange_left());
        riskRulesBean.setRange_right(param.getRange_right());
        riskRulesBean.setVal_left(param.getVal_left());
        riskRulesBean.setVal_right(param.getVal_right());
        riskRulesBean.setCredit_level(param.getCredit_level());
        riskRulesBean.setProduct_id(param.getProduct_id());
        riskRulesBean.setCreate_time(new  Date());
        this.riskRulesMapper.insert(riskRulesBean);
        return   Result.succ();
    }

    @Override
    public Result delRiskRule(DelRuleParam param) {
        if (param == null) {
            return Result.fail("input  param is  null");

        }
        Integer rule_id = param.getRule_id();
        this.riskRulesMapper.deleteById(rule_id);
        return Result.succ();
    }

    @Override
    public Result<List<RiskRulesBean>> getLisRisktRules() {
        List<RiskRulesBean> list = this.riskRulesMapper.selectList(new QueryWrapper<>());
        if (list.isEmpty()) {
            log.info("risk rule is empty");
        }
        return Result.succ(list);
    }

    @Override
    public Result<List<CreditLevelRuleBean>> getLisCdRules() {

        List<CreditLevelRuleBean> list = this.creditLevelRulesMapper.selectList(new QueryWrapper<>());
        if (list.isEmpty()) {
            log.info("credit level rule  is empty");
        }
        return Result.succ(list);
    }

    @Override
    public Result<List<AssetsLevelRuleBean>> getListAsRules() {

        List<AssetsLevelRuleBean> list = this.assetsLevelRulesMapper.selectList(new QueryWrapper<>());
        if (list.isEmpty()) {
            log.info("asset level rule is empty");
        }

        return Result.succ(list);
    }

    @Override
    public Result<List<RiskVariableBean>> getRiskVariables() {

        List<String> names = RiskVariableConstants.getVariableList();
        List<RiskVariableBean> ret = new ArrayList<RiskVariableBean>();
        for (String variable : names) {
            RiskVariableBean bean = new RiskVariableBean();
            bean.setName(variable);
            Integer value_type = 0;
            if (variable.endsWith("list")) value_type = 1;
            bean.setValue_type(value_type);
            ret.add(bean);

        }

        return Result.succ(ret);
    }


}
