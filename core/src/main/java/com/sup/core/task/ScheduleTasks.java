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
import com.sup.common.util.*;
import com.sup.core.bean.CollectionRepayBean;
import com.sup.core.bean.CollectionStatBean;
import com.sup.core.bean.RepayJoinBean;
import com.sup.core.bean.RiskDecisionResultBean;
import com.sup.core.mapper.*;
import com.sup.core.param.AutoDecisionParam;
import com.sup.core.service.ApplyService;
import com.sup.core.service.LoanService;
import com.sup.core.service.RuleConfigService;
import com.sup.core.service.impl.DecisionEngineImpl;
import com.sup.core.status.DecisionEngineStatusEnum;
import com.sup.core.util.MqMessenger;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    @Autowired
    private OperationTaskMapper operationTaskMapper;

    @Autowired
    private OperationTaskJoinMapper operationTaskJoinMapper;

    @Autowired
    private CheckReportMapper checkReportMapper;

    @Autowired
    private CollectionReportMapper collectionReportMapper;

    @Autowired
    private RuleConfigService ruleConfigService;


    @Autowired
    private ReportOperatorDailyMapper reportOperatorDailyMapper;

    @Autowired
    private AuthUserMapper authUserMapper;

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
            // log.info(">>>> start apply rules...");
            RiskDecisionResultBean result = decisionEngine.applyRules(param);
            // log.info(">>>> RiskDecisionResultBean = " + GsonUtil.toJson(result));
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

            if (bean.getStatus() == ApplyStatusEnum.APPLY_AUTO_PASS.getCode()) {
                ApplyStatusEnum status = applyService.getQuickpassStatus(ApplyStatusEnum.APPLY_AUTO_PASS, bean.getUser_id());
                bean.setStatus(status.getCode());

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
            Result r = loanService.autoLoan(bean, 0);
            if (!r.isSucc()) {
                log.error("Failed to auto loan for applyId = " + bean.getId() + ", " + r.getMessage());
            }
        }
    }

    /**
     * 定时检查放款是否成功
     */
    @Scheduled(cron = "0 */10 * * * ?")
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
                psi.setOrderNo(String.valueOf(bean.getOrder_number()));
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
                    Date loanTime = null;
                    if (ps.getSendTime().indexOf("-") > 0) {
                        loanTime = DateUtil.parse(ps.getSendTime(), DateUtil.DEFAULT_DATETIME_FORMAT);
                    } else {
                        loanTime = DateUtil.parse(ps.getSendTime(), DateUtil.NO_SPLIT_FORMAT);
                    }
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
        historyWrapper.eq("repay_status", RepayHistoryStatusEnum.REPAY_STATUS_PROCESSING.getCode());
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
                log.info(">>>> repayStatus return: " + GsonUtil.toJson(rs));
                TbRepayPlanBean repayPlanBean = repayPlanMapper.selectById(bean.getRepay_plan_id());
                if (repayPlanBean == null) {
                    log.error("Invalid repayPlanId, repayHistory bean = " + GsonUtil.toJson(bean));
                }

                Date repayTime = new Date();
                String rsTime = rs.getPurchaseTime();
                if (rsTime.indexOf('-') >= 0 && rsTime.indexOf(':') >= 0) {
                    repayTime = DateUtil.parse(rsTime, DateUtil.DEFAULT_DATETIME_FORMAT);
                } else if (rsTime.indexOf('-') < 0 && rsTime.indexOf(':') < 0) {
                    repayTime = DateUtil.parse(rsTime, DateUtil.NO_SPLIT_FORMAT);
                }

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
                    bean.setRepay_status(RepayHistoryStatusEnum.REPAY_STATUS_FAILED.getCode());
                    mqMessenger.sendRepayMessage(bean);
                    if (repayHistoryMapper.updateById(bean) <= 0) {
                        log.error("checkRepayResult: Failed to update for bean = " + GsonUtil.toJson(bean));
                    }
                    if (repayPlanBean != null && repayPlanBean.getRepay_status() != RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
                        repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_ERROR.getCode());
                        if (repayPlanMapper.updateById(repayPlanBean) <= 0) {
                            log.error("checkRepayResult: Failed to update repayPlan bean = " + GsonUtil.toJson(repayPlanBean));
                        }
                    }
                }
            }
        }
    }

    /**
     * 每天查询逾期情况，并更新相应款项
     */
    @Scheduled(cron = "0 0 1 * * ?")
    // @Scheduled(cron = "0 1 * * * ?")
    public void checkOverdue() {
        // 1. 获取所有产品信息（逾期日费率）
        List<TbProductInfoBean> products = productInfoMapper.selectList(null);
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
        wrapper.orderByAsc("id");

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
                if (bean.getAct_total() + bean.getReduction_fee() >= bean.getNeed_total()) {    // 已还清
                    bean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_ALL.getCode());
                    repayPlanMapper.updateById(bean);
                    continue;
                }
                boolean isOverdue = bean.getIs_overdue() == RepayPlanOverdueEnum.PLAN_OVER_DUE.getCode();
                Integer productId = bean.getProduct_id();
                TbProductInfoBean productInfoBean = productsMap.getOrDefault(productId, null);
                if (productInfoBean == null || productInfoBean.getOverdue_rate() == null) {
                    log.error("[FATAL] No product found or rate not set for productId = " + productId);
                    continue;
                }
//                // 最后还款日期为：截止日期+宽限期
//                Date repay_end_date = DateUtil.getDate(bean.getRepay_end_date(), productInfoBean.getGrace_period());
                Date repay_end_date = bean.getRepay_end_date();
                // boolean isLate = DateUtil.compareDate(repay_end_date, now) < 0;
                boolean isLate = DateUtil.compareDay(repay_end_date, now) < 0;
                log.info("repay_end_date=" + repay_end_date + ", isLate=" + isLate);
                if (!isOverdue && !isLate) {
                    continue;
                }
                bean.setIs_overdue(RepayPlanOverdueEnum.PLAN_OVER_DUE.getCode());
                Float rate = productInfoBean.getOverdue_rate();
                int overdueDays = DateUtil.getDaysBetween(repay_end_date, now);
                Long new_penalty_interest = (long) (bean.getNeed_principal() * rate * overdueDays);
                Long ori_total = bean.getNeed_total();
                Long ori_penalty_interest = bean.getNeed_penalty_interest();
                Long new_total = ori_total + new_penalty_interest - ori_penalty_interest;

                bean.setNeed_penalty_interest(new_penalty_interest);
                bean.setNeed_total(new_total);

                Result r = loanService.updateRepayPlan(bean);
                if (!r.isSucc()) {
                    log.error("Failed to update");
                }
                // update apply_info
                TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(bean.getApply_id());
                if (applyInfoBean == null || applyInfoBean.getStatus() == ApplyStatusEnum.APPLY_OVERDUE.getCode()) {
                    continue;
                }
                // 发送MQ消息
                mqMessenger.applyStatusChange(applyInfoBean);
                applyInfoBean.setStatus(ApplyStatusEnum.APPLY_OVERDUE.getCode());
                applyInfoBean.setUpdate_time(now);
                applyInfoMapper.updateById(applyInfoBean);
            }
        }
    }

    /**
     * 每天更新还款统计表（要在更新还款计划之后）
     */
    @Scheduled(cron = "0 30 1 * * ?")
    // @Scheduled(cron = "30 0 * * * ?")
    public void statRepayInfo() {
        // TODO: 处理过程有待优化，仅处理还款中的数据

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
        log.info("Total repayStat num: " + repayStatMap.size());
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
                    statBean = loanService.statRepayPlan(applyId, repayPlanMap.get(applyId));
                    statBean.setCreate_time(now);
                    statBean.setUpdate_time(now);
                    if (repayStatMapper.insert(statBean) <= 0) {
                        log.error("Failed to insert! bean = " + GsonUtil.toJson(statBean));
                    }
                } else {
                    statBean = loanService.statRepayPlan(statBean, repayPlanMap.get(applyId));
                    statBean.setUpdate_time(now);
                    if (repayStatMapper.update(statBean,
                            new QueryWrapper<TbRepayStatBean>().eq("apply_id", applyId)) <= 0) {
                        log.error("Failed to update! bean = " + GsonUtil.toJson(statBean));
                    }
                }
            }
        }
    }


    /**
     * 每天更新用户的信用等级
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateCreditLevel() {
        //TODO   improve by use  new  cleard user;
        List<CreditLevelRuleBean> creditLevelRuleBeans = this.creditLevelRulesMapper.selectList(new QueryWrapper<CreditLevelRuleBean>().orderByDesc("level"));

        List<TbApplyInfoBean> applyInfoBeanList = this.applyInfoMapper.selectList(new QueryWrapper<TbApplyInfoBean>().eq("status", ApplyStatusEnum.APPLY_REPAY_ALL.getCode()));  //结清状态的申请单
        Map<Integer, Integer> clear_user = new HashMap<>();
        for (TbApplyInfoBean tbApplyMaterialInfoBean : applyInfoBeanList) {
            Integer user_id = tbApplyMaterialInfoBean.getUser_id();
            if (clear_user.containsKey(user_id)) {
                clear_user.put(user_id, clear_user.get(user_id) + 1);
            } else {
                clear_user.put(user_id, 1);
            }
        }
        for (Integer user_id : clear_user.keySet()) {
            int reloan_times = clear_user.get(user_id);
            this.ruleConfigService.updateCreditLevelForUser(user_id, reloan_times, creditLevelRuleBeans);
        }
    }


    /**
     * 每天更新资产等级，
     */
    @Scheduled(cron = "0 0 3 * * ?")
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
            int days = DateUtil.getDaysBetween(replay_end, date);
            Integer assetLevel = tbApplyInfoBean.getAsset_level();
            Integer applyId = tbApplyInfoBean.getId();


            for (AssetsLevelRuleBean assetsLevelRuleBean : assetsLevelRuleBeans) {
                if (days >= assetsLevelRuleBean.getBetween_paydays() && !assetLevel.equals(assetsLevelRuleBean.getLevel())) {
                    tbApplyInfoBean.setAsset_level(assetsLevelRuleBean.getLevel());
                    tbApplyInfoBean.setUpdate_time(date);
                    this.applyInfoMapper.updateById(tbApplyInfoBean);
                    if (assetLevel != null && !assetLevel.equals(assetsLevelRuleBean.getLevel())) {
                        // assert level changed
                        //applyService.cancelOperationTask(applyId, OperationTaskTypeEnum.TASK_OVERDUE, "asset level changed from " + assetLevel + " to " + assetsLevelRuleBean.getLevel());
                        applyService.addOperationTask(applyId, OperationTaskTypeEnum.TASK_OVERDUE, "");
                    }
                    break;
                }
            }
        }
    }

    //@Scheduled(cron = "0 30 1 * * ?")
    public void genDailyReport() {
        try {
            Date start = DateUtil.parseDate("2020-01-01");
            Date end = DateUtil.parseDate("2020-02-16");
            Date current = start;
            Date data_dt;
            while (current.before(end)) {
                data_dt = current;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(current);
                calendar.add(Calendar.DATE, 1);
                current = calendar.getTime();
                dailyReport(data_dt, current);
                // log.info("genDailyReport data_dt=" + DateUtil.formatDate(data_dt) + ", current=" + DateUtil.formatDate(current));
            }
        }catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }


    //@Scheduled(cron = "0 */5 * * * ?")
    @Scheduled(cron = "0 30 1 * * ?")   //T+1
    public void genDailyReportInc() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  //昨天
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, -24);
            String strDate = dateFormat.format(calendar.getTime());
            Date data_dt = dateFormat.parse(strDate);

            String str2Date = dateFormat.format(new Date());
            Date current = dateFormat.parse(str2Date);

            dailyReport(data_dt, current);
        }catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

    }

    private void dailyReport(Date data_dt, Date current) {
        class ChannelContainer {
            public Map<ApplyStatusEnum, Integer> applyStatMap = new HashMap<>();
            public Set<Integer> userSet = new HashSet<>();
            public int registNum = 0;
            public int applyNum = 0;    // 申请订单数
            public int repayNum = 0;    // 应还订单数
            public int repayActualNum = 0; // 实还订单数
            public long loanAmt = 0;    // 放款成功金额
            public long loanInhandAmt = 0;  // 到手金额
            public long repayAmt = 0;  // 应还总额
            public long repayActualAmt = 0;    // 实际还款总额
        }

        try {
            log.info("dailyReport data_dt = " + DateUtil.formatDate(data_dt) + ", current = " + DateUtil.formatDate(current));

            List<TbUserRegistInfoBean> registInfoBeans = this.userRegisterInfoMapper.selectList(new QueryWrapper<TbUserRegistInfoBean>()
                    .ge("create_time", data_dt)
                    .lt("create_time", current));

            QueryWrapper<TbApplyInfoBean> applyInfoWrapper = new QueryWrapper<>();
            applyInfoWrapper.or(w -> w.ge("create_time", data_dt).lt("create_time", current));
            applyInfoWrapper.or(w -> w.ge("update_time", data_dt).lt("update_time", current));
            log.info("[SQL] query applyInfo: " + applyInfoWrapper.getSqlSegment());
            List<TbApplyInfoBean> applyInfoBeans = this.applyInfoMapper.selectList(applyInfoWrapper);

            List<TbRepayPlanBean> repayPlanBeans = this.repayPlanMapper.selectList(new QueryWrapper<TbRepayPlanBean>()
                    .ge("repay_end_date", data_dt)
                    .lt("repay_end_date", current));

            QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<>();
            wrapper.in("status", ApplyStatusEnum.APPLY_AUTO_LOANING.getCode()
                    , ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode()
                    , ApplyStatusEnum.APPLY_LOAN_SUCC.getCode()
                    , ApplyStatusEnum.APPLY_REPAY_PART.getCode()
                    , ApplyStatusEnum.APPLY_REPAY_ALL.getCode()
                    , ApplyStatusEnum.APPLY_OVERDUE.getCode()
            );
            List<TbApplyInfoBean> infoBeans = this.applyInfoMapper.selectList(wrapper);

            /*
            channel id => {status, count}, applyNum, applyUserSet, loanAmount,...
             */
            Map<Integer, ChannelContainer> channelStatMap = new HashMap<>();
            Map<Integer, Integer> channelApplyMap = new HashMap<>();    // apply id => channel id
            // init map(apply id => channel id)
            for (TbApplyInfoBean bean : infoBeans) {
                channelApplyMap.put(bean.getId(), bean.getChannel_id());
            }

            // regist statistic
            for (TbUserRegistInfoBean registInfoBean : registInfoBeans) {
                Integer channel = registInfoBean.getChannel_id();
                if (!channelStatMap.containsKey(channel)) {
                    channelStatMap.put(channel, new ChannelContainer());
                }
                ChannelContainer container = channelStatMap.get(channel);
                container.registNum += 1;
            }

            // apply statistic
            for (TbApplyInfoBean applyInfoBean : applyInfoBeans) {
                Integer channel = applyInfoBean.getChannel_id();
                ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
                channelApplyMap.put(applyInfoBean.getId(), channel);

                if (!channelStatMap.containsKey(channel)) {
                    channelStatMap.put(channel, new ChannelContainer());
                }
                ChannelContainer container = channelStatMap.get(channel);
                Integer statusCount = container.applyStatMap.getOrDefault(status, 0);
                container.applyStatMap.put(status, statusCount + 1);
                if (DateUtil.isSameDay(applyInfoBean.getCreate_time(), data_dt)) {
                    container.userSet.add(applyInfoBean.getUser_id());
                    container.applyNum += 1;
                }
                if (status == ApplyStatusEnum.APPLY_LOAN_SUCC) {
                    container.loanAmt += applyInfoBean.getGrant_quota();
                    container.loanInhandAmt += applyInfoBean.getInhand_quota();
                }
            }

            // repay statistic
            for (TbRepayPlanBean repayPlanBean : repayPlanBeans) {
                Integer channel = channelApplyMap.get(repayPlanBean.getApply_id());
                RepayPlanStatusEnum status = RepayPlanStatusEnum.getStatusByCode(repayPlanBean.getRepay_status());

                if (!channelStatMap.containsKey(channel)) {
                    channelStatMap.put(channel, new ChannelContainer());
                }
                ChannelContainer container = channelStatMap.get(channel);
                container.repayNum += 1;
                container.repayAmt += repayPlanBean.getNeed_total();
                if (status == RepayPlanStatusEnum.PLAN_PAID_ALL) {
                    container.repayActualNum += 1;
                    container.repayActualAmt += repayPlanBean.getAct_total();
                }
            }

            // store the statistic result
            for (Map.Entry<Integer, ChannelContainer> entry : channelStatMap.entrySet()) {
                Integer channel = entry.getKey();
                ChannelContainer c = entry.getValue();
                Integer auto_deny = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_AUTO_DENY, 0);
                Integer first_deny = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_FIRST_DENY, 0);
                Integer final_deny = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_FINAL_DENY, 0);
                Integer loan_num = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_LOAN_SUCC, 0);
                Integer loan_failed = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED, 0);
                Integer loan_pending = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_AUTO_LOANING, 0);
                Integer first_ovedue = c.repayNum - c.repayActualNum;
                Integer final_pass = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_FINAL_PASS, 0) + loan_failed + loan_pending + loan_num;
                Integer first_pass = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_FIRST_PASS, 0) + final_pass + first_deny;
                Integer auto_pass = c.applyStatMap.getOrDefault(ApplyStatusEnum.APPLY_AUTO_PASS, 0) + final_pass + final_deny;
                double forate = c.repayNum > 0 ? (first_ovedue + 0.00001f) / (c.repayNum + 0.00001f) : 0;

                OperationReportBean operationReportBean = new OperationReportBean();
                operationReportBean.setData_dt(data_dt);
                operationReportBean.setChannel_id(channel);
                operationReportBean.setApply_num(c.applyNum);
                operationReportBean.setApply_user_num(c.userSet.size());
                operationReportBean.setAuto_pass(auto_pass);
                operationReportBean.setAuto_deny(auto_deny);
                operationReportBean.setFirst_pass(first_pass);
                operationReportBean.setFirst_deny(first_deny);
                operationReportBean.setFinal_pass(final_pass);
                operationReportBean.setFinal_deny(final_deny);
                operationReportBean.setManual_pass(first_pass + final_pass);
                operationReportBean.setManual_deny(first_deny + final_deny);
                operationReportBean.setLoan_num(loan_num);
                operationReportBean.setRepay(c.repayNum);
                operationReportBean.setRepay_actual(c.repayActualNum);
                operationReportBean.setFirst_overdue(first_ovedue);
                operationReportBean.setLoan_amt(c.loanAmt);
                operationReportBean.setLoan_inhand_amt(c.loanInhandAmt);
                operationReportBean.setRepay_amt(c.repayAmt);
                operationReportBean.setRepay_actual_amt(c.repayActualAmt);
                operationReportBean.setFirst_overdue_amt(c.repayAmt - c.repayActualAmt);
                operationReportBean.setLoan_failed(loan_failed);
                operationReportBean.setLoan_pending(loan_pending);
                operationReportBean.setForate(forate);
                operationReportBean.setRegister(c.registNum);
                operationReportBean.setCreate_time(new Date());
                this.operationReportMapper.insert(operationReportBean);
            }

            List<AuthUserBean> users = this.authUserMapper.selectList(new QueryWrapper<AuthUserBean>());
            Map<Integer, String> userNames = new HashMap<Integer, String>();
            for (AuthUserBean authUserBean : users) {
                userNames.put(authUserBean.getId(), authUserBean.getName());

            }

            this.doCheckReportDaily(data_dt, current, OperationTaskTypeEnum.TASK_FIRST_AUDIT.getCode(), userNames);
            this.doCheckReportDaily(data_dt, current, OperationTaskTypeEnum.TASK_FINAL_AUDIT.getCode(), userNames);

        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }

    private void doCheckReportDaily(Date data_dt, Date current, Integer taskType, Map<Integer, String> names) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = DateUtil.startOf(data_dt);
        String end = DateUtil.endOf(data_dt);
        log.info("operation task info start:"+ start+",end:"+end);

        List<OperationTaskJoinBean> operationTaskJoinBeanList = this.operationTaskJoinMapper.getOperationTaskJoinByTask(start, end, taskType);
        CheckReportBean checkReportBean = new CheckReportBean();
        int total = operationTaskJoinBeanList.size();
        checkReportBean.setTask_type(taskType);
        checkReportBean.setData_dt(data_dt);
        checkReportBean.setTotal(total);


        OperationStatBean operationStatBean = CheckStatUtil.processList(operationTaskJoinBeanList);
        log.info("operationStatBean=" + GsonUtil.toJson(operationStatBean));

        checkReportBean.setDenyed(operationStatBean.getDenyed());
        checkReportBean.setChecked(operationStatBean.getChecked());
        checkReportBean.setAllocated(operationStatBean.getAllocated());

        this.checkReportMapper.insert(checkReportBean);
        try {

            this.doCheckOpertorDaily(operationTaskJoinBeanList, data_dt, names);  // operator report
            this.updateOperatorReport(data_dt,current);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 计算每一位信审员，  截止昨天的审核情况汇总
     *
     */
    private void doCheckOpertorDaily(List<OperationTaskJoinBean> list, Date data_dt, Map<Integer, String> names) {
        Map<Integer, List<OperationTaskJoinBean>> map = new HashMap<>();
        if (list == null || list.isEmpty()) return;
        for (OperationTaskJoinBean operationTaskJoinBean : list) {
            Integer operator = operationTaskJoinBean.getOperatorId();
            if (operator == null) {
                continue;
            }
            if (!map.containsKey(operator)) {
                map.put(operator, new ArrayList<>());
            }
            map.get(operator).add(operationTaskJoinBean);
        }
        for (Integer operator : map.keySet()) {
            if(map.get(operator).isEmpty()) {
                continue;
            }
            OperationStatBean operationStatBean = CheckStatUtil.processList(map.get(operator));
            TbReportCheckOperatorDaily tbReportCheckOperatorDaily = new TbReportCheckOperatorDaily();
            tbReportCheckOperatorDaily.setData_dt(data_dt);
            tbReportCheckOperatorDaily.setOperator(operator);
            tbReportCheckOperatorDaily.setChecked(operationStatBean.getChecked());
            tbReportCheckOperatorDaily.setPassed(operationStatBean.getPassed());
            tbReportCheckOperatorDaily.setAllocated(operationStatBean.getAllocated());
            tbReportCheckOperatorDaily.setLoan_num(operationStatBean.getLoan_num());
            tbReportCheckOperatorDaily.setLoan_amt(operationStatBean.getLoan_amt());
            tbReportCheckOperatorDaily.setPass_rate(operationStatBean.getPass_rate());
            tbReportCheckOperatorDaily.setLoan_rate(operationStatBean.getLoan_rate());
            tbReportCheckOperatorDaily.setUpdate_time(new Date());
            if (names != null && names.containsKey(operator)) {
                tbReportCheckOperatorDaily.setOperator_name(names.get(operator));
            }
            this.reportOperatorDailyMapper.insert(tbReportCheckOperatorDaily);
        }
    }


    public void updateOperatorReport(Date data_dt, Date current) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.set(Calendar.HOUR_OF_DAY, -24 * 8);//last  week

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {

            String start = dateFormat.format(DateUtil.startOf(calendar.getTime()));

            String end = DateUtil.startOf(current);

            List<RepayJoinBean> repayJoinBeans = this.operationTaskJoinMapper.getRepayJoinByDate(start, end);

            Map<Integer, Map<String, Integer>> map = new HashMap<>(); //operator :  loan_date,   overdue_size;
            for (RepayJoinBean repayJoinBean : repayJoinBeans) {
                Integer operator_id = repayJoinBean.getOperator_id();
                if (!map.containsKey(operator_id)) {
                    Map<String, Integer> dis = new HashMap<>();
                    map.put(operator_id, dis);
                }

                Date loan_str = repayJoinBean.getLoan_time();
                String date_str = dayFormat.format(loan_str);
                if (!map.get(operator_id).containsKey(date_str)) {

                    map.get(operator_id).put(date_str, 0);
                }

                int val = map.get(operator_id).get(date_str);
                val++;
                map.get(operator_id).put(date_str, val);
            }
            for (Integer operator : map.keySet()) {

                Map<String, Integer> day_dis = map.get(operator);
                for (String loan_date : day_dis.keySet()) {
                    int fpd = 0, d3 = 0, d7 = 0;

                    Date loan = dateFormat.parse(loan_date);
                    int days = DateUtil.getDaysBetween(loan, current);

                    if (days == 1) {
                        fpd += day_dis.get(loan_date);
                    }
                    if (days == 4) {
                        d3 += day_dis.get(loan_date);
                    }
                    if (days == 8) {
                        d7 += day_dis.get(loan_date);
                    }
                    if (fpd > 0 || d3 > 0 || d7 > 0) {
                        log.info("need to update pd info for operator:" + operator + ", data_dt:" + loan_date + ", fpd:" + fpd + ",pd3:" + d3 + ",d7:" + d7);
                        QueryWrapper<TbReportCheckOperatorDaily> wrapper = new QueryWrapper<>();
                        wrapper.eq("data_dt", loan).eq("operator", operator);
                        List<TbReportCheckOperatorDaily> beans = this.reportOperatorDailyMapper.selectList(wrapper);
                        if (beans != null && !beans.isEmpty()) {
                            TbReportCheckOperatorDaily tbReportCheckOperatorDaily = beans.get(0);
                            int loan_num = tbReportCheckOperatorDaily.getLoan_num();
                            if (loan_num > 0) {

                                if (fpd > 0) {
                                    double val = (fpd + 0.001f) / (loan_num + 0.001f);
                                    tbReportCheckOperatorDaily.setFpd(val);
                                }
                                if (d3 > 0) {
                                    double pd3 = (d3 + 0.001f) / (loan_num + 0.001f);
                                    tbReportCheckOperatorDaily.setPd3(pd3);
                                }
                                if (d7 > 0) {
                                    double pd7 = (d7 + 0.001f) / (loan_num + 0.001f);
                                    tbReportCheckOperatorDaily.setPd7(pd7);
                                }
                                this.reportOperatorDailyMapper.updateById(tbReportCheckOperatorDaily);
                            } else {
                                log.info("loan num is invalid");
                            }
                        }

                    }

                }


            }


        } catch (Exception e) {
            log.error(e.getMessage());
        }


    }

    private void doOperationReportDaily(Date data_dt, Date current) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = dateFormat.format(data_dt);
        String end = dateFormat.format(current);

        List<CollectionStatBean> collectionStatBeans = this.operationTaskJoinMapper.getCollectionStats(end, OperationTaskTypeEnum.TASK_OVERDUE.getCode());
        Integer in_apply = collectionStatBeans.size();
        Long in_amt = 0L;
        for (CollectionStatBean collectionStatBean : collectionStatBeans) {
            in_amt += collectionStatBean.getTotal();
        }

        List<CollectionRepayBean> collectionRepayBeans = this.operationTaskJoinMapper.getCollectionRepay(start, end);

        Integer tracked = collectionRepayBeans.size();
        Long repay_amt = 0L;
        Integer repay_apply = 0;
        for (CollectionRepayBean collectionRepayBean : collectionRepayBeans) {
            Long amt = collectionRepayBean.getRepay_amt();
            if (amt > 0) {
                repay_apply++;
                repay_amt += amt;
            }
        }

        CollectionReportBean collectionReportBean = new CollectionReportBean();
        collectionReportBean.setData_dt(data_dt);
        collectionReportBean.setIn_apply(in_apply);
        collectionReportBean.setIn_amt(in_amt);
        collectionReportBean.setRepay_apply(repay_apply);
        collectionReportBean.setRepay_amt(repay_amt);
        collectionReportBean.setTracked_apply(tracked);

        this.collectionReportMapper.insert(collectionReportBean);


    }
}
