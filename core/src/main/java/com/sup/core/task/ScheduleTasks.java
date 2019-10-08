package com.sup.core.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sup.common.bean.*;
import com.sup.common.bean.paycenter.PayStatusInfo;
import com.sup.common.bean.paycenter.RepayStatusInfo;
import com.sup.common.bean.paycenter.vo.PayStatusVO;
import com.sup.common.bean.paycenter.vo.RepayStatusVO;
import com.sup.common.loan.*;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.FunpayOrderUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.bean.AssetsLevelRuleBean;
import com.sup.core.bean.CreditLevelRuleBean;
import com.sup.core.bean.OverdueInfoBean;
import com.sup.core.bean.RiskDecisionResultBean;
import com.sup.core.mapper.*;
import com.sup.core.param.AutoDecisionParam;
import com.sup.core.service.ApplyService;
import com.sup.core.service.LoanService;
import com.sup.core.service.impl.DecisionEngineImpl;
import com.sup.core.status.DecisionEngineStatusEnum;
import com.sup.core.util.MqMessenger;
import com.sup.core.util.OverdueUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Project:uniloan
 * Class:  ScheduleTasks
 * <p>
 * Author: guanfeng
 * Create: 2019-09-20
 */

@Log4j
@Component
public class ScheduleTasks {
    @Value("#{new Integer('${query.page-num}')}")
    private Integer QUERY_PAGE_NUM;

    @Autowired
    private ApplyService applyService;
    @Autowired
    private DecisionEngineImpl decisionEngine;
    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private RepayPlanMapper repayPlanMapper;
    @Autowired
    private ProductInfoMapper productInfoMapper;
    @Autowired
    private RepayStatMapper repayStatMapper;
    @Autowired
    private RepayHistoryMapper repayHistoryMapper;

    @Autowired
    private UserBasicInfoMapper userBasicInfoMapper;

    @Autowired
    private AssetsLevelRulesMapper assetsLevelRulesMapper;

    @Autowired
    private CreditLevelRulesMapper creditLevelRulesMapper;

    @Autowired
    private UserRegisterInfoMapper userRegisterInfoMapper;


    @Autowired
    private LoanService loanService;
    @Autowired
    private PayCenterService funpayService;
    @Autowired
    private MqMessenger mqMessenger;

    @Autowired
    private OperationReportMapper operationReportMapper;


    @Scheduled(cron = "0 */5 * * * ?")
    public void checkApplyInfo() {
        // 1. 获取所有新提交的进件
        List<TbApplyInfoBean> applyInfoBeans = applyInfoMapper.selectList(
                new QueryWrapper<TbApplyInfoBean>().eq("status", ApplyStatusEnum.APPLY_INIT.getCode())
        );

        if (applyInfoBeans == null || applyInfoBeans.size() == 0) {
            log.info("No new apply.");
            return;
        }
        log.info("Found new apply infos, count = " + applyInfoBeans.size());
        AutoDecisionParam param = new AutoDecisionParam();
        for (TbApplyInfoBean bean : applyInfoBeans) {
            param.setApplyId(String.valueOf(bean.getId()));
            param.setProductId(String.valueOf(bean.getProduct_id()));
            param.setUserId(String.valueOf(bean.getUser_id()));
            // 2. 自动审查
            RiskDecisionResultBean result = decisionEngine.applyRules(param);
            if (result == null) {
                // Exception??
                log.error("DecisionEngine return null for param = " + GsonUtil.toJson(param));
                bean.setStatus(ApplyStatusEnum.APPLY_AUTO_DENY.getCode());
            } else if (result.getRet() == DecisionEngineStatusEnum.APPLY_DE_AUTO_PASS.getCode()) {
                bean.setStatus(ApplyStatusEnum.APPLY_AUTO_PASS.getCode());
            } else {
                bean.setStatus(ApplyStatusEnum.APPLY_AUTO_DENY.getCode());
                bean.setDeny_code(result.getRefuse_code());
            }
            // 3. 更新进件状态
            applyService.updateApplyInfo(bean);
        }
    }

    /**
     * 定时检查进件状态，终审通过则尝试自动放款
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkApplyStatus() {
        // 获取所有终审通过的进件
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<TbApplyInfoBean>()
                .eq("status", ApplyStatusEnum.APPLY_FINAL_PASS.getCode());
        // TODO: 放款状态进件较少，可不用分页处理
        List<TbApplyInfoBean> applyInfos = applyInfoMapper.selectList(wrapper);
        if (applyInfos == null || applyInfos.size() == 0) {
            log.info("No APPLY_FINAL_PASS apply info.");
            return;
        }

        for (TbApplyInfoBean bean : applyInfos) {
            Result r = loanService.autoLoan(bean);
            if (!r.isSucc()) {
                log.error("Failed to auto loan for applyId = " + bean.getId() + ", " + r.getMessage());
            }
        }
    }

    /**
     * 定时检查放款是否成功
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkLoanResult() {
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<TbApplyInfoBean>();
        wrapper.eq("status", ApplyStatusEnum.APPLY_AUTO_LOANING.getCode());

        Integer total = applyInfoMapper.selectCount(wrapper);
        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
        PayStatusInfo psi = new PayStatusInfo();
        for (int i = 1; i <= pageCount; ++i) {
            // 1. 获取放款中的进件
            Page<TbApplyInfoBean> page = new Page<>(i, QUERY_PAGE_NUM, false);
            IPage<TbApplyInfoBean> applyInfos = applyInfoMapper.selectPage(page, wrapper);
            if (applyInfos == null || applyInfos.getSize() == 0) {
                continue;
            }

            // 2. 检查放款状态
            for (TbApplyInfoBean bean : applyInfos.getRecords()) {
                if (bean.getTrade_number() == null) {
                    log.error("No trade number for applyId = " + bean.getId());
                    continue;
                }
                psi.setApplyId(String.valueOf(bean.getId()));
                psi.setTradeNo(bean.getTrade_number());
                Result<PayStatusVO> ret = funpayService.payStatus(psi);
                if (!ret.isSucc()) {
                    continue;
                }
                PayStatusVO ps = ret.getData();
                Integer status = ps.getStatus();
                FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
                if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
                    continue;
                }
                if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
                    // 放款成功，检查金额
                    Integer amount = ps.getAmount();
                    if (amount > 0 && !amount.equals(bean.getInhand_quota())) {
                        // 放款金额不一致？？？
                        log.error("########### invalid loan amount ############");
                        log.error("PayStatusVO = " + GsonUtil.toJson(ps));
                        log.error("ApplyInfo  = " + GsonUtil.toJson(bean));
                        bean.setInhand_quota(amount);
                    }
                    // 更新放款时间
                    Date loanTime = DateUtil.parse(ps.getSendTime(), DateUtil.NO_SPLIT_FORMAT);
                    bean.setLoan_time(loanTime);
                    bean.setStatus(ApplyStatusEnum.APPLY_LOAN_SUCC.getCode());
                } else {
                    log.error("Auto loan failed for applyId = " + bean.getId() +
                            ", reason: " + FunpayOrderUtil.getMessage(status)
                    );
                    bean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
                }
                Result result = applyService.updateApplyInfo(bean);
                if (!result.isSucc()) {
                    log.error("checkLoanResult: Failed to update applyId = " + bean.getId());
                }
            }
        }
    }


    /**
     * 定时检查自助还款是否成功
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkRepayResult() {
        // 检查还款明细
        QueryWrapper<TbRepayHistoryBean> historyWrapper = new QueryWrapper<>();
        historyWrapper.eq("repay_status", RepayStatusEnum.REPAY_STATUS_PROCESSING.getCode());
        Integer total = repayHistoryMapper.selectCount(historyWrapper);
        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
        RepayStatusInfo rsi = new RepayStatusInfo();
        for (int i = 1; i <= pageCount; ++i) {
            Page<TbRepayHistoryBean> page = new Page<>(i, QUERY_PAGE_NUM, false);
            IPage<TbRepayHistoryBean> repayHistoryBeans = repayHistoryMapper.selectPage(page, historyWrapper);
            if (repayHistoryBeans == null || repayHistoryBeans.getSize() == 0) {
                continue;
            }

            for (TbRepayHistoryBean bean : repayHistoryBeans.getRecords()) {
                if (bean.getTrade_number() == null) {
                    log.error("No trade number! bean = " + GsonUtil.toJson(bean));
                    continue;
                }
                rsi.setOrderNo(String.valueOf(bean.getId()));   // repay id
                rsi.setTradeNo(bean.getTrade_number());
                Result<RepayStatusVO> ret = funpayService.repayStatus(rsi);
                if (!ret.isSucc()) {
                    continue;
                }
                RepayStatusVO rs = ret.getData();
                Integer status = rs.getStatus();
                FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
                if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
                    continue;
                }
                Date repayTime = DateUtil.parse(rs.getPurchaseTime(), DateUtil.NO_SPLIT_FORMAT);
                if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
                    // 还款成功，更新还款计划
                    Long repayAmount = Long.valueOf(rs.getAmount());
                    if (repayAmount <= 0) {
                        // WTF ???
                        log.error("########### invalid repay amount ############");
                        log.error("RepayStatusVO = " + GsonUtil.toJson(rs));
                        log.error("RepayPlanBean = " + GsonUtil.toJson(bean));
                    }
                    loanService.repayAndUpdate(bean, repayAmount, repayTime, false);
                } else {
                    log.error("Auto repay failed for bean = " + GsonUtil.toJson(bean) +
                            ", reason: " + FunpayOrderUtil.getMessage(status)
                    );
                    bean.setRepay_status(RepayStatusEnum.REPAY_STATUS_FAILED.getCode());
                    mqMessenger.sendRepayMessage(bean);
                    if (repayHistoryMapper.updateById(bean) <= 0) {
                        log.error("checkRepayResult: Failed to update for bean = " + GsonUtil.toJson(bean));
                    }
                }
            }
        }
    }

    /**
     * 每天查询逾期情况，并更新相应款项
     */
    @Scheduled(cron = "0 */10 * * * ?")
    // @Scheduled(cron = "0 1 * * * ?")
    public void checkOverdue() {
        // 1. 获取所有产品信息（逾期日费率）
        List<TbProductInfoBean> products = productInfoMapper.selectList(
                new QueryWrapper<TbProductInfoBean>().eq("status", ProductStatusEnum.PRODUCT_STATUS_OFFLINE.getCode())
        );
        if (products == null || products.size() == 0) {
            // No products??
            return;
        }
        Map<Integer, TbProductInfoBean> productsMap = new HashMap<>();
        for (TbProductInfoBean bean : products) {
            productsMap.put(bean.getId(), bean);
        }

        // 2. 获取所有未还清、未核销、还款中的还款计划
        Date now = new Date();
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        wrapper.ne("repay_status", RepayPlanStatusEnum.PLAN_PAID_ALL.getCode());
        wrapper.ne("repay_status", RepayPlanStatusEnum.PLAN_PAID_WRITE_OFF.getCode());
        wrapper.le("repay_start_date", now);

        Integer total = repayPlanMapper.selectCount(wrapper);
        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
        log.info("Total repayPlan num: " + total);
        for (int i = 1; i <= pageCount; ++i) {
            Page<TbRepayPlanBean> page = new Page<>(i, QUERY_PAGE_NUM, false);
            IPage<TbRepayPlanBean> repayPlanBeans = repayPlanMapper.selectPage(page, wrapper);
            if (repayPlanBeans == null || repayPlanBeans.getSize() == 0) {
                continue;
            }

            for (TbRepayPlanBean bean : repayPlanBeans.getRecords()) {
                boolean isOverdue = bean.getIs_overdue() == RepayPlanOverdueEnum.PLAN_OVER_DUE.getCode();
                Integer productId = bean.getProduct_id();
                TbProductInfoBean productInfoBean = productsMap.getOrDefault(productId, null);
                if (productInfoBean == null || productInfoBean.getOverdue_rate() == null) {
                    log.error("[FATAL] No product found or rate not set for productId = " + productId);
                    continue;
                }
                // 最后还款日期为：截止日期+宽限期
                Date repay_end_date = DateUtil.getDate(bean.getRepay_end_date(), productInfoBean.getGrace_period());
                boolean isLate = DateUtil.compareDate(repay_end_date, now) < 0;
                if (!isOverdue || !isLate) {
                    continue;
                }
                bean.setIs_overdue(RepayPlanOverdueEnum.PLAN_OVER_DUE.getCode());
                Float rate = productInfoBean.getOverdue_rate();
                Long ori_total = bean.getNeed_total();
                Long ori_penalty_interest = bean.getNeed_penalty_interest();
                int new_penalty_interest = (int) (bean.getNeed_principal() * rate);
                bean.setNeed_penalty_interest(ori_penalty_interest + new_penalty_interest);
                bean.setNeed_total(ori_total + new_penalty_interest);
                Result r = loanService.updateRepayPlan(bean);
                if (!r.isSucc()) {
                    log.error("Failed to update");
                }
            }
        }
    }

    /**
     * 每天更新还款统计表
     */
    @Scheduled(cron = "0 */10 * * * ?")
    // @Scheduled(cron = "30 0 * * * ?")
    public void statRepayInfo() {
        // 1. 获取所有还款统计
        List<TbRepayStatBean> statBeans = repayStatMapper.selectList(
                new QueryWrapper<TbRepayStatBean>().select("apply_id", "create_time")
        );
        Map<Integer, TbRepayStatBean> repayStatMap = new HashMap<>();
        for (TbRepayStatBean statBean : statBeans) {
            repayStatMap.put(statBean.getApply_id(), statBean);
        }
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        // wrapper.ne("repay_status", RepayPlanStatusEnum.PLAN_NOT_PAID.getCode());

        Integer total = repayPlanMapper.selectCount(wrapper);
        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
        log.info("Total repayPlan num: " + total);
        for (int i = 1; i <= pageCount; ++i) {
            // 2. 获取还款计划
            Page<TbRepayPlanBean> page = new Page<>(i, QUERY_PAGE_NUM, false);
            IPage<TbRepayPlanBean> repayPlanBeans = repayPlanMapper.selectPage(page, wrapper);
            if (repayPlanBeans == null || repayPlanBeans.getSize() == 0) {
                continue;
            }
            // applyId => List<TbRepayPlanBean>
            Map<Integer, List<TbRepayPlanBean>> repayPlanMap = new HashMap<>();
            for (TbRepayPlanBean repayPlanBean : repayPlanBeans.getRecords()) {
                Integer applyId = repayPlanBean.getApply_id();
                if (!repayPlanMap.containsKey(applyId)) {
                    repayPlanMap.put(applyId, new ArrayList<>());
                }
                repayPlanMap.get(applyId).add(repayPlanBean);
            }

            // 3. 获取还款统计表中的applyId（便于识别是插入还是更新）
            for (Integer applyId : repayPlanMap.keySet()) {
                TbRepayStatBean statBean = repayStatMap.getOrDefault(applyId, null);

                Date now = new Date();
                if (statBean == null) {
                    statBean = statRepayPlan(applyId, repayPlanMap.get(applyId));
                    statBean.setCreate_time(now);
                    statBean.setUpdate_time(now);
                    if (repayStatMapper.insert(statBean) <= 0) {
                        log.error("Failed to insert! bean = " + GsonUtil.toJson(statBean));
                    }
                } else {
                    statBean = statRepayPlan(statBean, repayPlanMap.get(applyId));
                    statBean.setUpdate_time(now);
                    if (repayStatMapper.update(statBean,
                            new QueryWrapper<TbRepayStatBean>().eq("apply_id", applyId)) <= 0) {
                        log.error("Failed to update! bean = " + GsonUtil.toJson(statBean));
                    }
                }
            }
        }
    }


    protected TbRepayStatBean statRepayPlan(Integer applyId, List<TbRepayPlanBean> planBeans) {
        TbRepayStatBean statBean = new TbRepayStatBean();
        statBean.setApply_id(applyId);
        return statRepayPlan(statBean, planBeans);
    }

    protected TbRepayStatBean statRepayPlan(TbRepayStatBean statBean, List<TbRepayPlanBean> planBeans) {
        if (planBeans == null || planBeans.size() == 0) {
            return statBean;
        }
        Date now = new Date();

        int normalRepayTimes = 0;
        int overdueRepayTimes = 0;
        int overdueTimes = 0;
        int currentSeq = planBeans.size();
        for (TbRepayPlanBean planBean : planBeans) {
            Date repayStartDate = planBean.getRepay_start_date();
            if (DateUtil.compareDate(now, repayStartDate) < 0) {
                // 待还还款计划中最小期数，即为当期
                currentSeq = Math.min(currentSeq, planBean.getSeq_no());
            }
            RepayPlanOverdueEnum status = RepayPlanOverdueEnum.getStatusByCode(planBean.getRepay_status());
            boolean isOverdue = status == RepayPlanOverdueEnum.PLAN_OVER_DUE;
            boolean repayed = planBean.getAct_total() > 0;

            overdueTimes += isOverdue ? 1 : 0;
            overdueRepayTimes += isOverdue && repayed ? 1 : 0;
            normalRepayTimes += !isOverdue && repayed ? 1 : 0;
            statBean.inc(planBean);
        }

        statBean.setCurrent_seq(currentSeq);
        statBean.setNormal_repay_times(normalRepayTimes);
        statBean.setOverdue_repay_times(overdueRepayTimes);
        statBean.setOverdue_times(overdueTimes);

        return statBean;
    }

    /**
     * 每天更新用户的信用等级
     */
    @Scheduled(cron = "0 2 * * * ?")
    public void updateCreditLevel() {
        //TODO   improve by use  new  cleard user;
        List<CreditLevelRuleBean> creditLevelRuleBeans = this.creditLevelRulesMapper.selectList(new QueryWrapper<CreditLevelRuleBean>().orderByDesc("level"));

        List<TbApplyInfoBean> applyInfoBeanList = this.applyInfoMapper.selectList(new QueryWrapper<TbApplyInfoBean>().eq("status", ApplyStatusEnum.APPLY_REPAY_ALL));  //结清状态的申请单
        Map<String, Integer> clear_user = new HashMap<String, Integer>();
        for (TbApplyInfoBean tbApplyMaterialInfoBean : applyInfoBeanList) {


            String user_id = Integer.toString(tbApplyMaterialInfoBean.getUser_id());
            if (clear_user.containsKey(user_id)) {
                clear_user.put(user_id, clear_user.get(user_id) + 1);
            } else {
                clear_user.put(user_id, 1);
            }


        }
        for (String user_id : clear_user.keySet()) {
            int reloan_times = clear_user.get(user_id);

            OverdueInfoBean overdueInfoBean = OverdueUtils.getMaxOverdueDays(user_id, this.repayPlanMapper);
            for (CreditLevelRuleBean creditLevelRuleBean : creditLevelRuleBeans) {
                if (reloan_times >= creditLevelRuleBean.getReloan_times() && overdueInfoBean.getMax_days() <= creditLevelRuleBean.getMax_overdue_days()) {
                    TbUserRegistInfoBean userRegistInfoBean = this.userRegisterInfoMapper.selectById(Integer.parseInt(user_id));
                    if (userRegistInfoBean != null) {
                        userRegistInfoBean.setCredit_level(creditLevelRuleBean.getLevel());
                        this.userRegisterInfoMapper.updateById(userRegistInfoBean);
                    } else {
                        log.error("Failed to update Credit level user_id:" + user_id);
                    }
                    break;
                }

            }

        }
    }


    /**
     * 每天更新资产等级，
     */
    @Scheduled(cron = "0 3 * * * ?")
    public void updateAssertLevel() {

        List<TbApplyInfoBean> applyInfoBeanList = this.applyInfoMapper.selectList(new QueryWrapper<TbApplyInfoBean>()
                .eq("status", ApplyStatusEnum.APPLY_LOAN_SUCC.getCode())
                .or().eq("status", ApplyStatusEnum.APPLY_REPAY_PART.getCode())
                .or().eq("status", ApplyStatusEnum.APPLY_OVERDUE.getCode()));

        //TODO 结清的问题
        Date date = new Date();
        List<AssetsLevelRuleBean> assetsLevelRuleBeans = this.assetsLevelRulesMapper.selectList(new QueryWrapper<AssetsLevelRuleBean>().orderByDesc("between_paydays"));

        if (applyInfoBeanList == null || applyInfoBeanList.size() == 0) {
            log.info("Nothing to do in updating asset levels.");
            return;
        }
        for (TbApplyInfoBean tbApplyInfoBean : applyInfoBeanList) {

            TbRepayPlanBean repayPlanBean = this.repayPlanMapper.selectOne(new QueryWrapper<TbRepayPlanBean>().eq("apply_id", tbApplyInfoBean.getId()));
            Date replay_end = repayPlanBean.getRepay_end_date();
            int days = DateUtil.daysbetween(replay_end, date);

            for (AssetsLevelRuleBean assetsLevelRuleBean : assetsLevelRuleBeans) {
                if (days >= assetsLevelRuleBean.getBetween_paydays()) {
                    tbApplyInfoBean.setAsset_level(assetsLevelRuleBean.getLevel());
                    this.applyInfoMapper.updateById(tbApplyInfoBean);
                    break;
                }


            }
        }


    }

    @Scheduled(cron = "0 4 * * * ?")   //T+1
    public void dailyReport() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  //昨天
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, -24);
            String strDate = dateFormat.format(calendar.getTime());
            Date data_dt = dateFormat.parse(strDate);

            String str2Date = dateFormat.format(new Date());
            Date current = dateFormat.parse(str2Date);

            List<TbApplyInfoBean> list = this.applyInfoMapper.selectList(new QueryWrapper<TbApplyInfoBean>()
                    .ge("create_time", data_dt)
                    .lt("create_time", current));
            Integer pay = 0, pay_amt = 0, apply = 0, apply_cust = 0, auto_deny = 0, manual_deny = 0;
            HashSet<Integer> user_set = new HashSet<>();
            for (TbApplyInfoBean tbApplyInfoBean : list) {
                user_set.add(tbApplyInfoBean.getUser_id());
                if (tbApplyInfoBean.getStatus() == ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()) {
                    ++pay;
                    pay_amt += tbApplyInfoBean.getGrant_quota();

                } else if (tbApplyInfoBean.getStatus() == ApplyStatusEnum.APPLY_AUTO_DENY.getCode()) {
                    auto_deny++;
                } else if (tbApplyInfoBean.getStatus() == ApplyStatusEnum.APPLY_FIRST_DENY.getCode() ||
                        tbApplyInfoBean.getStatus() == ApplyStatusEnum.APPLY_FINAL_DENY.getCode()) {
                    manual_deny++;
                }


            }
            apply = list.size();
            apply_cust = user_set.size();
            List<TbRepayPlanBean> tbRepayPlanBeans = this.repayPlanMapper.selectList(new QueryWrapper<TbRepayPlanBean>()
                    .ge("repay_end_date", data_dt)
                    .lt("repay_end_date", current));
            Integer repay = tbRepayPlanBeans.size();

            Integer repay_actual = 0;
            for (TbRepayPlanBean repayPlanBean : tbRepayPlanBeans) {
                if (repayPlanBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
                    ++repay_actual;
                }

            }
            Integer register = this.userRegisterInfoMapper.selectCount(new QueryWrapper<TbUserRegistInfoBean>()
                    .ge("create_time", data_dt)
                    .lt("create_time", calendar));
            Integer first_ovedue = repay - repay_actual;
            double forate = (first_ovedue + 0.00001f) / (repay + 0.00001f);

            OperationReportBean operationReportBean = new OperationReportBean();
            operationReportBean.setData_dt(data_dt);
            operationReportBean.setApply(apply);
            operationReportBean.setApply_cust(apply_cust);
            operationReportBean.setAuto_deny(auto_deny);
            operationReportBean.setManual_deny(manual_deny);
            operationReportBean.setPay(pay);
            operationReportBean.setPay_amt(pay_amt);
            operationReportBean.setForate(forate);
            operationReportBean.setRepay(repay);
            operationReportBean.setRepay_actual(repay_actual);
            operationReportBean.setFirst_overdue(first_ovedue);
            operationReportBean.setRegister(register);

            this.operationReportMapper.insert(operationReportBean);
        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }

}
