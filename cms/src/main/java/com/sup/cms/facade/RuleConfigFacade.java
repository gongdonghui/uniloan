package com.sup.cms.facade;

import com.sup.common.bean.RiskVariableBean;
import com.sup.common.param.AssetLevelPraram;
import com.sup.common.param.CreditLevelParam;
import com.sup.common.param.DelRuleParam;
import com.sup.common.param.RiskRuleParams;
import com.sup.common.util.Result;
import com.sup.common.bean.AssetsLevelRuleBean;
import com.sup.common.bean.CreditLevelRuleBean;
import com.sup.common.bean.RiskRulesBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * gongshuai
 * <p>
 * 2019/10/5
 */

@RequestMapping(value = "/ruleConfig")
public interface RuleConfigFacade {


    @ResponseBody
    @RequestMapping(value = "addCdLevel", produces = "application/json;charset=UTF-8")
    Result addCreditLevel(@RequestBody CreditLevelParam param);

    @ResponseBody
    @RequestMapping(value = "delCdLevel", produces = "application/json;charset=UTF-8")
    Result delCreditLevel(@RequestBody DelRuleParam param);

    @ResponseBody
    @RequestMapping(value = "addAsLevel", produces = "application/json;charset=UTF-8")
    Result addAssetLevel(@RequestBody AssetLevelPraram param);

    @ResponseBody
    @RequestMapping(value = "delAsLevel", produces = "application/json;charset=UTF-8")
    Result delAssetLevels(@RequestBody DelRuleParam param);


    @ResponseBody
    @RequestMapping(value = "addRiskRule", produces = "application/json;charset=UTF-8")
    Result addRiskRule(@RequestBody RiskRuleParams param);


    @ResponseBody
    @RequestMapping(value = "delRiskRule", produces = "application/json;charset=UTF-8")
    Result delRiskRule(@RequestBody DelRuleParam param);

    @ResponseBody
    @RequestMapping(value = "getListRiskRules", produces = "application/json;charset=UTF-8")
    Result<List<RiskRulesBean>> getLisRisktRules();

    @ResponseBody
    @RequestMapping(value = "getListCdRules", produces = "application/json;charset=UTF-8")
    Result<List<CreditLevelRuleBean>> getLisCdRules();

    @ResponseBody
    @RequestMapping(value = "getListAsRules", produces = "application/json;charset=UTF-8")
    Result<List<AssetsLevelRuleBean>> getListAsRules();

    @ResponseBody
    @RequestMapping(value = "getRiskVariables", produces = "application/json;charset=UTF-8")
    Result<List<RiskVariableBean>> getRiskVariables();
}
