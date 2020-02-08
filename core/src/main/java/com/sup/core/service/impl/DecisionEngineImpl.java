package com.sup.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.*;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.DateUtil;
import com.sup.core.bean.*;
import com.sup.core.mapper.*;
import com.sup.core.param.AutoDecisionParam;
import com.sup.core.service.DecesionEngine;
import com.sup.core.service.ThirdPartyService;
import com.sup.core.util.OverdueUtils;
import com.sup.common.util.RiskVariableConstants;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Log4j
@Service
public class DecisionEngineImpl implements DecesionEngine {
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

    @Autowired
    private TbAppSdkContractInfoMapper tbAppSdkContractInfoMapper;

    @Autowired
    private TbUserRegistInfoMapper tbUserRegistInfoMapper;


    @Autowired
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private UserIdCardInfoMapper userIdCardInfoMapper;


    private boolean applySingleRule(RiskRulesBean rule, String mobile, String cid, String applyId, Map<String, Double> riskBean) {


        String variable_name = rule.getVariable_name();
        log.info("apply rule for:" + variable_name);
        if (rule.getValue_type() == 1) {  //名单类
            if (variable_name.equals(RiskVariableConstants.BLACKLIST_JR)) {
                boolean exists = this.thirdPartyService.checkBlackListInJirong(cid, "", mobile, applyId);
                return !exists;   // 黑名单命中 表示规则不通过
            } else if (variable_name.equals(RiskVariableConstants.BLACKLIST_XT)) {
                boolean exists = this.thirdPartyService.checkBlackListInXingtan(cid, "", mobile, applyId);
                return !exists;

            } else if (variable_name.equals(RiskVariableConstants.BLACKLIST_INNER)) {
                //内部上传黑名单
                //String key = variable_name + mobile;
                //boolean exist = redisClient.Exist(key);
                //return exist && rule.getIs_in() == 1;   //is_in:1 表示在名单 （白名单），0  表示不能在名单（黑名单）
                boolean exists = this.thirdPartyService.checkInnerBlackList(cid, "", mobile, applyId);
                return !exists;
            } else {
                return true;
            }


        } else {   //数值类变量
            if (riskBean.containsKey(variable_name)) {
                double val = riskBean.get(variable_name);
                boolean ret = false;
                if (rule.getRange_left() == 2) {
                    ret = val > rule.getVal_left();
                } else {
                    ret = val >= rule.getVal_left();
                }
                boolean ret_right = false;
                if (rule.getRange_right() == 1) {
                    ret_right = val < rule.getVal_right();
                } else {
                    ret_right = val <= rule.getVal_left();
                }
                return ret && ret_right;
            } else {
                return true;   //不存在变量时，规则通过
            }
        }
    }


    private ContractInfo getContractInfo(String mobile) {
        TbUserRegistInfoBean userRegistInfoBean = this.tbUserRegistInfoMapper.selectOne(new QueryWrapper<TbUserRegistInfoBean>().eq("mobile", mobile));
        if (userRegistInfoBean != null) {
            Integer userid = userRegistInfoBean.getId();
            List<TbApplyInfoBean> applyInfoBeanList = this.applyInfoMapper.selectList(new QueryWrapper<TbApplyInfoBean>().eq("user_id", userid));
            int apply_times = applyInfoBeanList.size();
            OverdueInfoBean overdueInfoBean = OverdueUtils.getMaxOverdueDays(userid, this.repayPlanInfoMapper);
            if (overdueInfoBean == null) {
                return null;
            }
            ContractInfo ret = new ContractInfo();
            ret.setOverdue_times(overdueInfoBean.getTimes());
            ret.setApply_times(apply_times);
            return ret;
        }
        return null;


    }


    private Map<String, Double> prepareRiskVariables(String userId, String user_mobile, String basic_info_id, String bank_info_id, String eme_info_id) {


        Map<String, Double> riskBean = new HashMap<>();
        riskBean.put(RiskVariableConstants.AGE, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.DAYS_BETWEEN_LAST_REFUSE, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.MAX_OVERDUE_DAYS, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.OVERDUE_TIMES, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.LATEST_OVERDUE_DAYS, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.NUM_OF_IDS_FOR_PIN, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.NUM_OF_APPLY_TIMES_IN_EMMERGENCY_CONTRACT, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.NUM_OF_OVERDUE_TIMES_IN_EMMERGENCY_CONTRACT, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.NUM_OF_APPLY_IN_CONTRACT, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.NUM_OF_OVDUE_IN_CONTRACT, Double.valueOf(0));
        riskBean.put(RiskVariableConstants.NUM_OF_CONTRACT, Double.valueOf(0));


        // log.info(">>>> prepareRiskVariables.basic_info_id...");
        if (!basic_info_id.isEmpty()) {
            TbUserBasicInfoBean userBasicInfoBean = userBasicInfoMapper.selectOne(new QueryWrapper<TbUserBasicInfoBean>().eq("info_id", basic_info_id));
            if (userBasicInfoBean != null) {

                riskBean.put(RiskVariableConstants.AGE, Double.valueOf(userBasicInfoBean.getAge()));
            }//age

            QueryWrapper<TbApplyInfoBean> materialWrapper = new QueryWrapper<TbApplyInfoBean>();
            List<Integer>     denyStatus =  new ArrayList<>();
            denyStatus.add(ApplyStatusEnum.APPLY_AUTO_DENY.getCode());
            denyStatus.add(ApplyStatusEnum.APPLY_FINAL_DENY.getCode());
            denyStatus.add(ApplyStatusEnum.APPLY_FIRST_DENY.getCode());

            List<TbApplyInfoBean> applyInfoBean = applyInfoMapper.selectList(
                    materialWrapper.eq("user_id", userId).in("status", denyStatus).
                            orderByDesc("update_time"));  // deny  date
            if (applyInfoBean != null &&  !  applyInfoBean.isEmpty()) {

                Date deny_date = applyInfoBean.get(0).getUpdate_time();
                int last_dey_days = DateUtil.getDaysBetween(deny_date, new Date());
                riskBean.put(RiskVariableConstants.DAYS_BETWEEN_LAST_REFUSE, Double.valueOf(last_dey_days));
            }

            OverdueInfoBean overdueInfoBean = OverdueUtils.getMaxOverdueDays(Integer.parseInt(userId), this.repayPlanInfoMapper);
            if (overdueInfoBean != null) { //overdue  days

                riskBean.put(RiskVariableConstants.MAX_OVERDUE_DAYS, Double.valueOf(overdueInfoBean.getMax_days()));
                riskBean.put(RiskVariableConstants.OVERDUE_TIMES, Double.valueOf(overdueInfoBean.getTimes()));
                riskBean.put(RiskVariableConstants.LATEST_OVERDUE_DAYS, Double.valueOf(overdueInfoBean.getLatest_days()));
            }
        }
        // log.info(">>>> prepareRiskVariables.bank_info_id...");
        if (!bank_info_id.isEmpty()) {
            TbUserBankAccountInfoBean userBankInfoBean = this.userBankInfoMapper.selectOne(new QueryWrapper<TbUserBankAccountInfoBean>().eq("info_id", bank_info_id).orderByDesc("create_time"));
            if (userBankInfoBean != null) {

                String account_id = userBankInfoBean.getAccount_id();
                if (account_id != null && !account_id.isEmpty()) {
                    List<TbUserBankAccountInfoBean> sameIds = this.userBankInfoMapper.selectList(new QueryWrapper<TbUserBankAccountInfoBean>().eq("account_id", account_id));
                    HashSet<String> set = new HashSet<String>();
                    for (TbUserBankAccountInfoBean ub : sameIds) {
                        int user_id = ub.getUser_id();
                        set.add(Integer.toString(user_id));
                    }
                    riskBean.put(RiskVariableConstants.NUM_OF_IDS_FOR_PIN, Double.valueOf(set.size()));
                }
            }
        }

        // log.info(">>>> prepareRiskVariables.eme_info_id...");
        if (!eme_info_id.isEmpty()) {

            List<UserEmergencyContactInfoBean> emeList = this.userEmergencyContactInfoMapper.selectList(new QueryWrapper<UserEmergencyContactInfoBean>().eq("info_id", eme_info_id));
            if (!emeList.isEmpty()) {
                int max_overdue_times = 0;
                int max_apply_times = 0;
                for (UserEmergencyContactInfoBean bean : emeList) {
                    String mobile = bean.getMobile();
                    if (!mobile.isEmpty()) {
                        ContractInfo contractInfo = this.getContractInfo(mobile);
                        if (contractInfo != null) {
                            max_overdue_times = contractInfo.getOverdue_times() > max_overdue_times ? contractInfo.getOverdue_times() : max_overdue_times;
                            max_apply_times = contractInfo.getApply_times() > max_apply_times ? contractInfo.getApply_times() : max_apply_times;
                        }
                    }
                }
                riskBean.put(RiskVariableConstants.NUM_OF_APPLY_TIMES_IN_EMMERGENCY_CONTRACT, Double.valueOf(max_apply_times));
                riskBean.put(RiskVariableConstants.NUM_OF_OVERDUE_TIMES_IN_EMMERGENCY_CONTRACT, Double.valueOf(max_overdue_times));

            }
        }


        List<TbAppSdkContractInfoBean> contractInfoBeans = this.queryLatestContact(user_mobile);
        int size_of_contract = contractInfoBeans.size();
        riskBean.put(RiskVariableConstants.NUM_OF_CONTRACT, Double.valueOf(size_of_contract));
        // log.info(">>>> prepareRiskVariables.contractInfoBeans...");
        if (!contractInfoBeans.isEmpty()) {
            // log.info(">>>> prepareRiskVariables.contractInfoBeans.size=" + contractInfoBeans.size());
            int max_overdue_times = 0;
            int max_apply_times = 0;
            for (TbAppSdkContractInfoBean appSdkContractInfoBean : contractInfoBeans) {
                String mobile = appSdkContractInfoBean.getContract_info();
                if (!mobile.isEmpty()) {
                    ContractInfo contractInfo = this.getContractInfo(mobile);
                    if (contractInfo != null) {
                        max_overdue_times = contractInfo.getOverdue_times() > max_overdue_times ? contractInfo.getOverdue_times() : max_overdue_times;
                        max_apply_times = contractInfo.getApply_times() > max_apply_times ? contractInfo.getApply_times() : max_apply_times;
                    }
                }

            }
            riskBean.put(RiskVariableConstants.NUM_OF_APPLY_IN_CONTRACT, Double.valueOf(max_apply_times));
            riskBean.put(RiskVariableConstants.NUM_OF_OVDUE_IN_CONTRACT, Double.valueOf(max_overdue_times));
        }


        return riskBean;

    }


    private List<TbAppSdkContractInfoBean> queryLatestContact(String mobile) {
        List<TbAppSdkContractInfoBean> result = new ArrayList<>();
        TbAppSdkContractInfoBean first_bean = this.tbAppSdkContractInfoMapper.selectOne(new QueryWrapper<TbAppSdkContractInfoBean>().eq("mobile", mobile).orderByDesc("id").last("limit 1"));
        if (first_bean == null) {
            return result;
        }
        QueryWrapper<TbAppSdkContractInfoBean> query = new QueryWrapper<TbAppSdkContractInfoBean>().eq("mobile", mobile).eq("create_time", first_bean.getCreate_time()).orderByAsc("id");
        result = this.tbAppSdkContractInfoMapper.selectList(query);
        result.forEach(v -> v.setSignature(v.calcSignature()));
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

    private String getUserIdentity(String id_info_id) {
        if (!id_info_id.isEmpty()) {
            UserIdCardInfoBean userIdCardInfoBean = this.userIdCardInfoMapper.selectOne(new QueryWrapper<UserIdCardInfoBean>().eq("info_id", id_info_id));
            if (this.userIdCardInfoMapper != null) {
                String cid = userIdCardInfoBean.getCid_no();
                return cid;
            }

        }
        return "";
    }


    @Override
    public RiskDecisionResultBean applyRules(AutoDecisionParam param) {
        TbUserRegistInfoBean userRegistInfoBean = this.tbUserRegistInfoMapper.selectById(Integer.parseInt(param.getUserId()));
        if (userRegistInfoBean == null) {
            log.info("user register info is null ");
            return null;
        }
        //user id  not exist
        String user_mobile = userRegistInfoBean.getMobile();
        if (user_mobile.isEmpty()) {
            log.info("user mobile is empty");
            return null;
        }
        //user  register mobie  is empty

        // log.info(">>>> prepareRiskVariables...");

        List<TbApplyMaterialInfoBean> applyMaterialInfoBeans = this.applyMaterialInfoMapper.
                selectList(new QueryWrapper<TbApplyMaterialInfoBean>().eq("apply_id", Integer.parseInt(param.getApplyId())));
        String basic_info_id = "", eme_info_id = "", bank_info_id = "", id_info_id = "";

        //0|身份证信息  1|基本信息 2|紧急联系人 3|职业信息 4|银行卡信息
        for (TbApplyMaterialInfoBean materialInfoBean : applyMaterialInfoBeans) {
            int infoType = materialInfoBean.getInfo_type();
            switch (infoType) {
                case 0:
                    id_info_id = materialInfoBean.getInfo_id();
                    break;
                case 1:
                    basic_info_id = materialInfoBean.getInfo_id();
                    break;
                case 2:
                    eme_info_id = materialInfoBean.getInfo_id();
                    break;
                case 4:
                    bank_info_id = materialInfoBean.getInfo_id();
                    break;

            }

        }

        Map<String, Double> riskBean = prepareRiskVariables(param.getUserId(), user_mobile, basic_info_id, bank_info_id, eme_info_id);

        String cid = this.getUserIdentity(id_info_id);
        if (cid.isEmpty()) {
            log.info("cid is null");
            return null;
        }

        RiskDecisionResultBean result = new RiskDecisionResultBean();
        result.setRet(0);
        result.setApply_id(Integer.parseInt(param.getApplyId()));
        result.setUser_id(Integer.parseInt(param.getUserId()));
        result.setApply_time(new Date());

        List<RiskRulesBean> rulesBeanList = riskRulesMapper.selectList(new QueryWrapper<RiskRulesBean>().eq("product_id", Integer.parseInt(param.getProductId())));
        List<RiskDecisionResultDetailBean> detailBeanList = new ArrayList<>();
        for (RiskRulesBean rule : rulesBeanList) {
            RiskDecisionResultDetailBean decisionResultBean = new RiskDecisionResultDetailBean();
            decisionResultBean.setRule_id(rule.getId());
            decisionResultBean.setApply_id(Integer.parseInt(param.getApplyId()));
            decisionResultBean.setUser_id(Integer.parseInt(param.getUserId()));
            decisionResultBean.setApply_date(new Date());
            decisionResultBean.setRule_hit_type(rule.getHit_type());

            if (rule.getHit_type() == 1) {   //必须通过类

                // log.info(">>>> applySingleRule...");
                boolean ret = applySingleRule(rule, user_mobile, cid, param.getApplyId(), riskBean);
                log.info("apply rule  for:" + rule.getVariable_name() + ", ret:" + ret);
                decisionResultBean.setRule_status(ret ? 1 : 0);
                if (!ret) {
                    int code = rule.getId();
                    result.setRefuse_code("r00" + Integer.toString(code));   // 拒贷码
                    result.setRet(1);    //1 表示拒贷  0  表示通过
                }

            } else if (rule.getHit_type() == 0) {  //提示类

                boolean ret = applySingleRule(rule, user_mobile, cid, param.getApplyId(), riskBean);
                decisionResultBean.setRule_status(ret ? 1 : 0);
            }
            detailBeanList.add(decisionResultBean);

        }
        // log.info(">>>> serializeDecesionResult...");
        serializeDecesionResult(result, detailBeanList);
        return result;

    }
}
