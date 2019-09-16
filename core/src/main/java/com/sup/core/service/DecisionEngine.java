package com.sup.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.util.DateUtil;
import com.sup.core.bean.*;
import com.sup.core.mapper.*;
import com.sup.core.util.RiskVariableConstants;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Log4j
@Service
public class DecisionEngine {
    @Autowired
    private RiskRulesMapper riskRulesMapper;

    @Autowired
    private com.sup.core.service.RedisClient redisClient;

    @Autowired
    private RepayPlanMapper repayPlanInfoMapper;

    @Autowired
    private ApplyInfoMapper applyInfoMapper;

    @Autowired
    private UserBasicInfoMapper userBasicInfoMapper;

    @Autowired
    private UserBankInfoMapper userBankInfoMapper;

    @Autowired
    private UserEmergencyContactInfoMapper userEmergencyContactInfoMapper;

    @Autowired
    private RiskDecisionResultMapper riskDecisionResultMapper;

    @Autowired
    private RiskDecisionResultDetailMapper riskDecisionResultDetailMapper;


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

    public OverdueInfoBean getMaxOverdueDays(String userId) {


        List<TbRepayPlanBean> plans = this.repayPlanInfoMapper.selectList(new QueryWrapper<TbRepayPlanBean>().eq("user_id", Integer.parseInt(userId)).orderByAsc("repay_start_date"));
        if (plans.isEmpty()) {
            return null;
        }
        int times = 0;
        int max_days = 0;
        int latest_days = -1;
        for (TbRepayPlanBean repayPlanBean : plans) {
            Date repay_date = repayPlanBean.getRepay_time();
            Date repay_end_date = repayPlanBean.getRepay_end_date();
            if (repay_date != null) {
                int days = DateUtil.daysbetween(repay_end_date, repay_date);
                if (days > 0) {
                    times++;
                    max_days = days > max_days ? days : max_days;
                    if (latest_days < 0)
                        latest_days = days;    //latest overdue days
                }
            }
        }
        OverdueInfoBean ret = new OverdueInfoBean();
        ret.setTimes(times);
        ret.setMax_days(max_days);
        ret.setLatest_days(latest_days);
        return ret;

    }

    private ContractInfo getContractInfo(String mobile) {
        UserBasicInfoBean userBasicInfoBean = this.userBasicInfoMapper.selectOne(new QueryWrapper<UserBasicInfoBean>().eq("mobile", mobile));
        if (userBasicInfoBean != null) {
            Integer userid = userBasicInfoBean.getUser_id();
            List<TbApplyInfoBean> applyInfoBeanList = this.applyInfoMapper.selectList(new QueryWrapper<TbApplyInfoBean>().eq("user_id", userid));
            int apply_times = applyInfoBeanList.size();
            OverdueInfoBean overdueInfoBean = this.getMaxOverdueDays(Integer.toString(userid));
            ContractInfo ret = new ContractInfo();
            ret.setOverdue_times(overdueInfoBean.getTimes());
            ret.setApply_times(apply_times);
            return ret;


        }
        return null;


    }


    public Map<String, Double> prepareRiskVariables(String userId, String info_id) {


        Map<String, Double> riskBean = new HashMap<String, Double>();
        UserBasicInfoBean userBasicInfoBean = userBasicInfoMapper.selectOne(new QueryWrapper<UserBasicInfoBean>().eq("info_id", info_id));
        if (userBasicInfoBean != null) {

            riskBean.put(RiskVariableConstants.AGE, Double.valueOf(userBasicInfoBean.getAge()));
        }   //age


        QueryWrapper<TbApplyInfoBean> materialWrapper = new QueryWrapper<TbApplyInfoBean>();
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectOne(
                materialWrapper.eq("user_id", userId).eq("deny_code", ""));
        if (applyInfoBean != null) {

            Date deny_date = applyInfoBean.getUpdate_time();
            int last_dey_days = DateUtil.daysbetween(deny_date, new Date());
            riskBean.put(RiskVariableConstants.DAYS_BETWEEN_LAST_REFUSE, Double.valueOf(last_dey_days));
        }   // deny  date
        OverdueInfoBean overdueInfoBean = getMaxOverdueDays(userId);
        if (overdueInfoBean != null) {

            riskBean.put(RiskVariableConstants.MAX_OVERDUE_DAYS, Double.valueOf(overdueInfoBean.getMax_days()));
            riskBean.put(RiskVariableConstants.OVERDUE_TIMES, Double.valueOf(overdueInfoBean.getTimes()));
            riskBean.put(RiskVariableConstants.LATEST_OVERDUE_DAYS, Double.valueOf(overdueInfoBean.getLatest_days()));
        }    //overdue  days

        UserBankInfoBean userBankInfoBean = this.userBankInfoMapper.selectOne(new QueryWrapper<UserBankInfoBean>().eq("info_id", info_id).orderByDesc("create_time"));
        if (userBankInfoBean != null) {

            String account_id = userBankInfoBean.getAccount_id();
            if (account_id != null && !account_id.isEmpty()) {
                List<UserBankInfoBean> sameIds = this.userBankInfoMapper.selectList(new QueryWrapper<UserBankInfoBean>().eq("account_id", account_id));
                HashSet<String> set = new HashSet<String>();
                for (UserBankInfoBean ub : sameIds) {
                    int user_id = ub.getUser_id();
                    set.add(Integer.toString(user_id));
                }
                riskBean.put(RiskVariableConstants.NUM_OF_IDS_FOR_PIN, Double.valueOf(set.size()));
            }
        }

        List<UserEmergencyContactInfoBean> emeList = this.userEmergencyContactInfoMapper.selectList(new QueryWrapper<UserEmergencyContactInfoBean>().eq("info_id", info_id));
        int max_overdue_times = 0;
        int max_apply_times = 0;
        if (!emeList.isEmpty()) {
            for (UserEmergencyContactInfoBean bean : emeList) {
                String mobile = bean.getMobile();
                ContractInfo contractInfo = this.getContractInfo(mobile);
                max_overdue_times = contractInfo.getOverdue_times() > max_overdue_times ? contractInfo.getOverdue_times() : max_overdue_times;
                max_apply_times = contractInfo.getApply_times() > max_apply_times ? contractInfo.getApply_times() : max_apply_times;
            }
            riskBean.put(RiskVariableConstants.NUM_OF_APPLY_TIMES_IN_EMMERGENCY_CONTRACT, Double.valueOf(max_apply_times));
            riskBean.put(RiskVariableConstants.NUM_OF_OVERDUE_TIMES_IN_EMMERGENCY_CONTRACT, Double.valueOf(max_overdue_times));

        }
        //TODO   get  contract  info

        return riskBean;

    }


    public RiskDecisionResultBean applyRules(String userId, String applyId, String productId, String info_id) {

        List<RiskRulesBean> rulesBeanList = riskRulesMapper.selectList(new QueryWrapper<RiskRulesBean>().eq("product_id", Integer.parseInt(productId)));

        Map<String, Double> riskBean = prepareRiskVariables(userId, info_id);
        String mobile = "";

        RiskDecisionResultBean result = new RiskDecisionResultBean();
        result.setRet(0);
        List<RiskDecisionResultDetailBean> detailBeanList = new ArrayList<>();
        for (RiskRulesBean rule : rulesBeanList) {
            RiskDecisionResultDetailBean decisionResultBean = new RiskDecisionResultDetailBean();
            decisionResultBean.setRule_id(rule.getId());
            decisionResultBean.setApply_id(Integer.parseInt(applyId));
            decisionResultBean.setUser_id(Integer.parseInt(userId));
            decisionResultBean.setApply_date(new Date());
            decisionResultBean.setRule_hit_type(rule.getHit_type());

            if (rule.getHit_type() == 1) {   //必须通过类


                boolean ret = applySingleRule(rule, mobile, riskBean);

                decisionResultBean.setRule_status(ret ? 1 : 0);
                if (!ret) {
                    int code = rule.getId();
                    result.setRefuse_code("r00" + Integer.toString(code));   // 拒贷码
                    result.setRet(1);    //1 表示拒贷  0  表示通过
                }

            } else if (rule.getHit_type() == 0) {  //提示类

                boolean ret = applySingleRule(rule, mobile, riskBean);
                decisionResultBean.setRule_status(ret ? 1 : 0);
            }
            detailBeanList.add(decisionResultBean);

        }
        serializeDecesionResult(result, detailBeanList);
        return result;
    }

    private boolean serializeDecesionResult(RiskDecisionResultBean result, List<RiskDecisionResultDetailBean> detailBeans) {

        this.riskDecisionResultMapper.insert(result);
        int did = result.getId();
        for (RiskDecisionResultDetailBean detail : detailBeans) {
            detail.setDecesion_id(did);
            this.riskDecisionResultDetailMapper.insert(detail);
        }

        return true;

    }
}
