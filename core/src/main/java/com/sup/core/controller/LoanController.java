package com.sup.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sup.common.bean.*;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.PayStatusInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.RepayStatusInfo;
import com.sup.common.bean.paycenter.vo.PayStatusVO;
import com.sup.common.bean.paycenter.vo.PayVO;
import com.sup.common.bean.paycenter.vo.RepayStatusVO;
import com.sup.common.bean.paycenter.vo.RepayVO;
import com.sup.common.loan.ApplyMaterialTypeEnum;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.loan.RepayPlanOverdueEnum;
import com.sup.common.loan.RepayPlanStatusEnum;
import com.sup.common.param.FunpayCallBackParam;
import com.sup.common.param.ManualRepayParam;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.FunpayOrderUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.mapper.*;
import com.sup.core.service.ApplyService;
import com.sup.core.service.LoanService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Project:uniloan
 * Class:  LoanFacade
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Log4j
//@RestController
//@RequestMapping(value = "/loan")
public class LoanController {
//    @Value("#{new Integer('${loan.auto-loan-retry-times}')}")
//    private Integer AUTO_LOAN_RETRY_TIMES;
//
//    @Value("#{new Integer('${query.page-num}')}")
//    private Integer QUERY_PAGE_NUM;
//
//    @Autowired
//    private ApplyInfoMapper applyInfoMapper;
//
//    @Autowired
//    private UserBankInfoMapper userBankInfoMapper;
//
//    @Autowired
//    private ApplyMaterialInfoMapper applyMaterialInfoMapper;
//
//    @Autowired
//    private RepayPlanMapper repayPlanMapper;
//
//    @Autowired
//    private RepayStatMapper repayStatMapper;
//
//    @Autowired
//    private ProductInfoMapper   productInfoMapper;
//
//    @Autowired
//    private ApplyService applyService;
//
//    @Autowired
//    private LoanService loanService;
//
//    @Autowired
//    private PayCenterService funpayService;
//
//    private final float FLOAT_ZERO = 0.000001F;
//
//    //////////////////////////////
//    // 放款接口
//    //////////////////////////////
//
//    // auto loan
//    @ResponseBody
//    @RequestMapping(value = "autoExec", produces = "application/json;charset=UTF-8")
//    Result autoLoan(String userId, String applyId) {
//        // get apply info and check apply status
//        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(applyId);
//        if (applyInfoBean == null) {
//            log.error("autoLoan: invalid applyId=" + applyId);
//            return Result.fail("Invalid applyId!");
//        }
//        return autoLoan(applyInfoBean);
//    }
//
//    // add/update/get loan plan
//    @ResponseBody
//    @RequestMapping(value = "plan/add", produces = "application/json;charset=UTF-8")
//    Result addRepayPlan(String userId, String applyId) {
//        // get apply info and check apply status
//        TbApplyInfoBean bean = applyInfoMapper.selectById(applyId);
//        if (bean == null) {
//            log.error("addRepayPlan: invalid applyId=" + applyId);
//            return Result.fail("Invalid applyId!");
//        }
//
//        if (!loanService.addRepayPlan(bean)) {
//            log.error("Failed to generate repay plan for user(" + userId + "), applyId = " + applyId);
//            return Result.fail("Failed to add repay plan!");
//        }
//
//        return Result.succ();
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "plan/update", produces = "application/json;charset=UTF-8")
//    Result updateRepayPlan(@RequestBody TbRepayPlanBean bean) {
//        return loanService.updateRepayPlan(bean);
//    }
//
//    /**
//     *
//     * @param applyId
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "plan/get", produces = "application/json;charset=UTF-8")
//    Result getRepayPlan(String applyId) {
//        return loanService.getRepayPlan(applyId);
//    }
//
//    /**
//     * 手动还款
//     * @param param
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "manualRepay", produces = "application/json;charset=UTF-8")
//    Result manualRepay(@RequestBody ManualRepayParam param) {
//        if (param.getAmount() == null || param.getAmount() <= 0) {
//            log.error("Invalid amount = " + GsonUtil.toJson(param));
//            return Result.fail("");
//        }
//        return repayAndUpdate(param.getApplyId(), Long.valueOf(param.getAmount()), param.getRepayTime());
//    }
//
//
//
//    /**
//     * 获取支付通道还款所需信息，包括支付码和链接
//     * @param repayInfo    还款参数
//     * @return  还款所需信息，包括交易码、便利店地址、流水号、交易码过期时间
//     */
//    @ResponseBody
//    @RequestMapping(value = "repayInfo/get", produces = "application/json;charset=UTF-8")
//    Result getRepayInfo(@RequestBody RepayInfo repayInfo) {
//        // 检查是否已有还款信息
//        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
//        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(
//                wrapper.eq("applyId", Integer.valueOf(repayInfo.getApplyId())).orderByDesc("create_time"));
//        if (repayPlanBean == null) {
//            log.error("Invalid applyId = " + repayInfo.getApplyId());
//            return Result.fail("Invalid applyId!");
//        }
//        if (repayPlanBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
//            return Result.fail("Nothing to repay.");
//        }
//        Date now = new Date();
//        Date repayExpireTime = repayPlanBean.getExpire_time();
//        String repayCode = repayPlanBean.getRepay_code();
//        String repayLoc = repayPlanBean.getRepay_location();
//        String tradeNo = repayPlanBean.getTrade_number();
//        if (repayCode != null && repayLoc != null && tradeNo != null && repayExpireTime != null
//                && DateUtil.compareDate(now, repayExpireTime) > 0) {
//            RepayVO r = new RepayVO();
//            r.setCode(repayCode);
//            r.setShopLink(repayLoc);
//            r.setExpireDate(DateUtil.formatDateTime(repayExpireTime));
//            r.setTradeNo(tradeNo);
//            return Result.succ(r);
//        }
//        Result<RepayVO> result = funpayService.repay(repayInfo);
//        if (!result.isSucc()) {
//            log.error("Failed to get repay info for applyId = " + repayInfo.getApplyId());
//            return Result.fail("Failed to get repay info!");
//        }
//        RepayVO r = result.getData();
//        repayPlanBean.setRepay_code(r.getCode());
//        repayPlanBean.setRepay_location(r.getShopLink());
//        repayPlanBean.setTrade_number(r.getTradeNo());
//        repayPlanBean.setExpire_time(DateUtil.parseDateTime(r.getExpireDate()));
//        repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_PROCESSING.getCode());
//
//        Result ret = loanService.updateRepayPlan(repayPlanBean);
//        if (!ret.isSucc()) {
//            log.error("getRepayInfo: update repayPlan failed! RepayV0 = " + GsonUtil.toJson(r));
//        }
//
//        return Result.succ(r);
//    }
//
//    /**
//     * 支付通道放款回调接口
//     * @param param
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "payCallBack", produces = "application/json;charset=UTF-8")
//    Result payCallBack(@RequestBody FunpayCallBackParam param) {
//        // update ApplyInfo status
//        TbApplyInfoBean bean = applyInfoMapper.selectOne(
//                new QueryWrapper<TbApplyInfoBean>().eq("applyId", param.getApplyId()).orderByDesc("create_time"));
//        if (bean == null) {
//            log.error("Invalid param = " + GsonUtil.toJson(param));
//            return Result.fail("Invalid applyId!");
//        }
//        Integer status = param.getStatus();
//        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
//        if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
//            // 处理中回调个毛线～～
//            return Result.succ("");
//        }
//        if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
//            // 放款成功，需更新放款时间
//            if (param.getFinishTime() != null) {
//                bean.setLoan_time(param.getFinishTime());
//            } else {
//                bean.setLoan_time(new Date());
//            }
//            // 检查放款金额与到手金额
//            if (param.getAmount() != bean.getInhand_quota()) {
//                log.error("########### invalid loan amount ############");
//                log.error("param = " + GsonUtil.toJson(param));
//                bean.setInhand_quota(param.getAmount());
//            }
//            bean.setStatus(ApplyStatusEnum.APPLY_LOAN_SUCC.getCode());
//        } else {
//            log.error("payCallBack: loan failed for applyId = " + bean.getId() +
//                    ", reason: " + FunpayOrderUtil.getMessage(status)
//            );
//            bean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
//        }
//        return applyService.updateApplyInfo(bean);
//    }
//
//
//    /**
//     * 支付通道还款回调接口
//     * @param param
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "repayCallBack", produces = "application/json;charset=UTF-8")
//    Result repayCallBack(@RequestBody FunpayCallBackParam param) {
//        if (param.getAmount() == null || param.getAmount() <= 0) {
//            log.error("Invalid amount = " + GsonUtil.toJson(param));
//            return Result.fail("");
//        }
//        Integer status = param.getStatus();
//        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
//        if (orderStatus != FunpayOrderUtil.Status.SUCCESS) {
//            // do nothing
//            log.info("repayCallBack: nothing to do for applyId = " + param.getApplyId() +
//                    ", reason: " + FunpayOrderUtil.getMessage(status)
//            );
//            return Result.succ();
//        }
//        if (param.getAmount() <= 0) {
//            // WTF???
//            log.error("########### invalid repay amount ############");
//            log.error("param = " + GsonUtil.toJson(param));
//        }
//        return repayAndUpdate(param.getApplyId(), Long.valueOf(param.getAmount()), param.getFinishTime());
//    }
//
//    ///////////////////////////////////////////////
//    /**
//     * 定时检查进件状态，终审通过则尝试自动放款
//     */
//    @Scheduled(cron = "0 */15 * * * ?")
//    public void checkApplyStatus() {
//        // 获取所有终审通过的进件
//        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<TbApplyInfoBean>()
//                .eq("status", ApplyStatusEnum.APPLY_FINAL_PASS.getCode());
//        // TODO: 放款状态进件较少，可不用分页处理
//        List<TbApplyInfoBean> applyInfos = applyInfoMapper.selectList(wrapper);
//        if (applyInfos == null || applyInfos.size() == 0) {
//            log.info("No APPLY_FINAL_PASS apply info.");
//            return;
//        }
//
//        for (TbApplyInfoBean bean : applyInfos) {
//            Result r = autoLoan(bean);
//            if (!r.isSucc()) {
//                log.error("Failed to auto loan for applyId = " + bean.getId());
//            }
//        }
//    }
//
//    /**
//     * 定时检查放款是否成功
//     */
//    @Scheduled(cron = "0 */10 * * * ?")
//    public void checkLoanResult() {
//        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<TbApplyInfoBean>();
//        wrapper.eq("status", ApplyStatusEnum.APPLY_AUTO_LOANING.getCode());
//
//        Integer total = applyInfoMapper.selectCount(wrapper);
//        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
//        PayStatusInfo psi = new PayStatusInfo();
//        for (int i = 1; i <= pageCount; ++i) {
//            // 1. 获取放款中的进件
//            Page<TbApplyInfoBean> page = new Page<>(i, QUERY_PAGE_NUM, false);
//            IPage<TbApplyInfoBean> applyInfos = applyInfoMapper.selectPage(page, wrapper);
//            if (applyInfos == null || applyInfos.getSize() == 0) {
//                continue;
//            }
//
//            // 2. 检查放款状态
//            for (TbApplyInfoBean bean : applyInfos.getRecords()) {
//                if (bean.getTrade_number() == null) {
//                    log.error("No trade number for applyId = " + bean.getId());
//                    continue;
//                }
//                psi.setApplyId(String.valueOf(bean.getId()));
//                psi.setTradeNo(bean.getTrade_number());
//                Result<PayStatusVO> ret = funpayService.payStatus(psi);
//                if (!ret.isSucc()) {
//                    continue;
//                }
//                PayStatusVO ps = ret.getData();
//                Integer status = ps.getStatus();
//                FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
//                if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
//                    continue;
//                }
//                if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
//                    // 放款成功，检查金额
//                    Integer amount = ps.getAmount();
//                    if (amount > 0 && amount != bean.getInhand_quota()) {
//                        // 放款金额不一致？？？
//                        log.error("########### invalid loan amount ############");
//                        log.error("PayStatusVO = " + GsonUtil.toJson(ps));
//                        log.error("ApplyInfo  = " + GsonUtil.toJson(bean));
//                        bean.setInhand_quota(amount);
//                    }
//                    // 更新放款时间
//                    Date loanTime = DateUtil.parse(ps.getSendTime(), DateUtil.NO_SPLIT_FORMAT);
//                    bean.setLoan_time(loanTime);
//                    bean.setStatus(ApplyStatusEnum.APPLY_LOAN_SUCC.getCode());
//                } else {
//                    log.error("Auto loan failed for applyId = " + bean.getId() +
//                            ", reason: " + FunpayOrderUtil.getMessage(status)
//                    );
//                    bean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
//                }
//                Result result = applyService.updateApplyInfo(bean);
//                if (!result.isSucc()) {
//                    log.error("checkLoanResult: Failed to update applyId = " + bean.getId());
//                }
//            }
//        }
//    }
//
//
//    /**
//     * 定时检查自助还款是否成功
//     */
//    @Scheduled(cron = "0 */10 * * * ?")
//    public void checkRepayResult() {
//        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
//        wrapper.eq("repay_status", RepayPlanStatusEnum.PLAN_PAID_PROCESSING.getCode());
//
//        Integer total = repayPlanMapper.selectCount(wrapper);
//        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
//        RepayStatusInfo rsi = new RepayStatusInfo();
//        for (int i = 1; i <= pageCount; ++i) {
//            Page<TbRepayPlanBean> page = new Page<>(i, QUERY_PAGE_NUM, false);
//            IPage<TbRepayPlanBean> repayPlanBeans = repayPlanMapper.selectPage(page, wrapper);
//            if (repayPlanBeans == null || repayPlanBeans.getSize() == 0) {
//                continue;
//            }
//
//            for (TbRepayPlanBean bean : repayPlanBeans.getRecords()) {
//                if (bean.getTrade_number() == null) {
//                    log.error("No trade number for applyId = " + bean.getId());
//                    continue;
//                }
//                rsi.setApplyId(String.valueOf(bean.getApply_id()));
//                rsi.setTradeNo(bean.getTrade_number());
//                Result<RepayStatusVO> ret = funpayService.repayStatus(rsi);
//                if (!ret.isSucc()) {
//                    continue;
//                }
//                RepayStatusVO rs = ret.getData();
//                Integer status = rs.getStatus();
//                FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
//                if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
//                    continue;
//                }
//                if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
//                    // 还款成功，更新还款计划
//                    Long repayAmount = Long.valueOf(rs.getAmount());
//                    if (repayAmount <= 0) {
//                        // WTF ???
//                        log.error("########### invalid repay amount ############");
//                        log.error("RepayStatusVO = " + GsonUtil.toJson(rs));
//                        log.error("RepayPlanBean = " + GsonUtil.toJson(bean));
//                    }
//                    Date repayTime = DateUtil.parse(rs.getPurchaseTime(), DateUtil.NO_SPLIT_FORMAT);
//                    repayAndUpdate(bean, repayAmount, repayTime);
//                } else {
//                    log.error("Auto repay failed for applyId = " + bean.getId() +
//                            ", reason: " + FunpayOrderUtil.getMessage(status)
//                    );
//                    bean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_ERROR.getCode());
//                    Result result = loanService.updateRepayPlan(bean);
//                    if (!result.isSucc()) {
//                        log.error("checkRepayResult: Failed to update repayPlan for applyId = " + bean.getApply_id());
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 每天查询逾期情况，并更新相应款项
//     */
//    @Scheduled(cron = "0 1 * * * ?")
//    public void checkOverdue() {
//        // 1. 获取所有产品信息（逾期日费率）
//        List<TbProductInfoBean> products = productInfoMapper.selectList(
//                new QueryWrapper<TbProductInfoBean>().select("id", "overdueRate", "gracePeriod")
//        );
//        if (products == null || products.size() == 0) {
//            // No products??
//            return;
//        }
//        Map<Integer, TbProductInfoBean> productsMap = new HashMap<>();
//        for (TbProductInfoBean bean : products) {
//            productsMap.put(bean.getId(), bean);
//        }
//
//        // 2. 获取所有未还清、未核销、还款中的还款计划
//        Date now = new Date();
//        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
//        wrapper.ne("repay_status", RepayPlanStatusEnum.PLAN_PAID_ALL.getCode());
//        wrapper.ne("repay_status", RepayPlanStatusEnum.PLAN_PAID_WRITE_OFF.getCode());
//        wrapper.le("repay_start_date", now);
//
//        Integer total = repayPlanMapper.selectCount(wrapper);
//        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
//        for (int i = 1; i <= pageCount; ++i) {
//            Page<TbRepayPlanBean> page = new Page<>(i, QUERY_PAGE_NUM, false);
//            IPage<TbRepayPlanBean> repayPlanBeans = repayPlanMapper.selectPage(page, wrapper);
//            if (repayPlanBeans == null || repayPlanBeans.getSize() == 0) {
//                continue;
//            }
//
//            for (TbRepayPlanBean bean : repayPlanBeans.getRecords()) {
//                boolean isOverdue = bean.getIs_overdue() == RepayPlanOverdueEnum.PLAN_OVER_DUE.getCode();
//                Integer productId = bean.getProduct_id();
//                TbProductInfoBean productInfoBean = productsMap.getOrDefault(productId, null);
//                if (productInfoBean == null || productInfoBean.getOverdue_rate() == null) {
//                    log.error("[FATAL] No product found or rate not set for productId = " + productId);
//                    continue;
//                }
//                // 最后还款日期为：截止日期+宽限期
//                Date repay_end_date = DateUtil.getDate(bean.getRepay_end_date(), productInfoBean.getGrace_period());
//                boolean isLate = DateUtil.compareDate(repay_end_date, now) < 0;
//                if (!isOverdue || !isLate) {
//                    continue;
//                }
//                bean.setIs_overdue(RepayPlanOverdueEnum.PLAN_OVER_DUE.getCode());
//                Float rate = productInfoBean.getOverdue_rate();
//                Long ori_total = bean.getNeed_total();
//                Long ori_penalty_interest = bean.getNeed_penalty_interest();
//                int  new_penalty_interest = (int) (bean.getNeed_principal() * rate);
//                bean.setNeed_penalty_interest(ori_penalty_interest + new_penalty_interest);
//                bean.setNeed_total(ori_total + new_penalty_interest);
//                Result r = loanService.updateRepayPlan(bean);
//                if (!r.isSucc()) {
//                    log.error("Failed to update");
//                }
//            }
//        }
//    }
//
//    /**
//     * 每天更新还款统计表
//     */
//    @Scheduled(cron = "30 0 * * * ?")
//    public void statRepayInfo() {
//        // 1. 获取所有还款统计
//        List<TbRepayStatBean> statBeans = repayStatMapper.selectList(
//                new QueryWrapper<TbRepayStatBean>().select("apply_id", "create_time")
//        );
//        Map<Integer, TbRepayStatBean> repayStatMap = new HashMap<>();
//        for (TbRepayStatBean statBean : statBeans) {
//            repayStatMap.put(statBean.getApply_id(), statBean);
//        }
//        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
//        wrapper.ne("repay_status", RepayPlanStatusEnum.PLAN_NOT_PAID.getCode());
//
//        Integer total = repayPlanMapper.selectCount(wrapper);
//        Integer pageCount = (total + QUERY_PAGE_NUM - 1) / QUERY_PAGE_NUM;
//        for (int i = 1; i <= pageCount; ++i) {
//            // 2. 获取还款计划（不含未还）
//            Page<TbRepayPlanBean> page = new Page<>(i, QUERY_PAGE_NUM, false);
//            IPage<TbRepayPlanBean> repayPlanBeans = repayPlanMapper.selectPage(page, wrapper);
//            if (repayPlanBeans == null || repayPlanBeans.getSize() == 0) {
//                continue;
//            }
//            // applyId => List<TbRepayPlanBean>
//            Map<Integer, List<TbRepayPlanBean>> repayPlanMap = new HashMap<>();
//            for (TbRepayPlanBean repayPlanBean : repayPlanBeans.getRecords()) {
//                Integer applyId = repayPlanBean.getApply_id();
//                if (!repayPlanMap.containsKey(applyId)) {
//                    repayPlanMap.put(applyId, new ArrayList<>());
//                }
//                repayPlanMap.get(applyId).add(repayPlanBean);
//            }
//
//            // 3. 获取还款统计表中的applyId（便于识别是插入还是更新）
//            for (Integer applyId : repayPlanMap.keySet()) {
//                TbRepayStatBean statBean = repayStatMap.getOrDefault(applyId, null);
//
//                Date now = new Date();
//                if (statBean == null) {
//                    statBean = statRepayPlan(applyId, repayPlanMap.get(applyId));
//                    statBean.setCreate_time(now);
//                    statBean.setUpdate_time(now);
//                    if (repayStatMapper.insert(statBean) <= 0) {
//                        log.error("Failed to insert! bean = " + GsonUtil.toJson(statBean));
//                    }
//                } else {
//                    statBean = statRepayPlan(statBean, repayPlanMap.get(applyId));
//                    statBean.setUpdate_time(now);
//                    if (repayStatMapper.update(statBean,
//                            new QueryWrapper<TbRepayStatBean>().eq("apply_id", applyId)) <= 0) {
//                        log.error("Failed to update! bean = " + GsonUtil.toJson(statBean));
//                    }
//                }
//            }
//        }
//    }
//
//    ///////////////////////////////////////////////
//    protected Result autoLoan(TbApplyInfoBean applyInfoBean) {
//        String userId = String.valueOf(applyInfoBean.getUser_id());
//        String applyId = String.valueOf(applyInfoBean.getId());
//
//        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
//        if (status == null || status != ApplyStatusEnum.APPLY_FINAL_PASS) {
//            log.error("autoLoan: invalid apply status=" + status.getCode() + ", " + status.getCodeDesc());
//            return Result.fail("Invalid status!");
//        }
//        if (applyInfoBean.getInhand_quota() <= 0) {
//            log.error("Invalid in-hand quota = " + applyInfoBean.getInhand_quota());
//            return Result.fail("Invalid in-hand quota!");
//        }
//
//        // 2. get user bank info
//        QueryWrapper<TbApplyMaterialInfoBean> materialWrapper = new QueryWrapper<>();
//        TbApplyMaterialInfoBean applyMaterialInfoBean = applyMaterialInfoMapper.selectOne(materialWrapper
//                .eq("applyId", applyId)
//                .eq("info_type", ApplyMaterialTypeEnum.APPLY_MATERIAL_BANK.getCode()));
//
//        if (applyMaterialInfoBean == null) {
//            log.error("No apply material(bank info) found for applyId=" + applyId);
//            return Result.fail("No apply material found!");
//        }
//        String infoId = applyMaterialInfoBean.getInfo_id();
//        QueryWrapper<TbUserBankAccountInfoBean> bankWrapper = new QueryWrapper<>();
//
//        // make sure there is only one bank card for current apply
//        TbUserBankAccountInfoBean bankInfoBean = userBankInfoMapper.selectOne(
//                bankWrapper.eq("info_id", infoId).eq("user_id", userId).orderByDesc("create_time"));
//        if (bankInfoBean == null) {
//            log.error("No bank info for user(" + userId + "), info_id=" + infoId);
//            return Result.fail("No bank info found!");
//        }
//
//        // 3. loan using funpay(need thread safe)
//        boolean loanSucc = false;
//        synchronized (this) {
//            PayInfo payInfo = new PayInfo();
//            payInfo.setUserId(userId);
//            payInfo.setApplyId(applyId);
//            payInfo.setAmount(applyInfoBean.getInhand_quota());
//            payInfo.setBankNo(String.valueOf(bankInfoBean.getBank()));
//            payInfo.setAccountNo(bankInfoBean.getAccount_id());
//            payInfo.setAccountType(bankInfoBean.getAccount_type());
//            payInfo.setAccountName(bankInfoBean.getName());
//
//            try {
//                for (int i = 0; i < AUTO_LOAN_RETRY_TIMES; i++) {
//                    Result<PayVO> result = funpayService.pay(payInfo);
//                    if (result != null && result.isSucc()) {
//                        loanSucc = true;
//                        applyInfoBean.setTrade_number(result.getData().getTradeNo());
//                        break;
//                    }
//                    Thread.sleep(1000);
//                }
//            }catch (Exception e) {
//                e.printStackTrace();
//                log.error(e.getMessage());
//            }
//        }
//
//        // if loan succeeded, update apply info status
//        if (loanSucc) {
//            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOANING.getCode());
//        } else {
//            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
//        }
//        applyInfoBean.setUpdate_time(new Date());
//        applyInfoBean.setOperator_id(0);    // system
//        return applyService.updateApplyInfo(applyInfoBean);
//    }
//
//    protected Result repayAndUpdate(String applyId, Long repayAmount, Date repayTime) {
//        // update RepayPlanInfo
//        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(
//                new QueryWrapper<TbRepayPlanBean>().eq("applyId", applyId).orderByDesc("create_time")
//        );
//        if (repayPlanBean == null) {
//            log.error("Invalid applyId = " + applyId);
//            return Result.fail("Invalid applyId!");
//        }
//        log.info("repayAndUpdate: applyId = " + applyId + ", repayAmount = " + repayAmount);
//        return repayAndUpdate(repayPlanBean, repayAmount, repayTime);
//    }
//
//    protected Result repayAndUpdate(TbRepayPlanBean repayPlanBean, Long repayAmount, Date repayTime) {
//        if (repayPlanBean == null) {
//            return Result.fail("");
//        }
//        if (repayAmount <= 0) {
//            return Result.succ();
//        }
//        if (repayTime == null) {
//            repayTime = new Date();
//        }
//        repayPlanBean.updateActFields(repayAmount);
//        repayPlanBean.setRepay_time(repayTime);
//        if (repayPlanBean.getAct_total() < repayPlanBean.getNeed_total()) {
//            repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_PART.getCode());
//        } else {
//            repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_ALL.getCode());
//        }
//        return loanService.updateRepayPlan(repayPlanBean);
//    }
//
//    protected TbRepayStatBean statRepayPlan(Integer applyId, List<TbRepayPlanBean> planBeans) {
//        TbRepayStatBean statBean = new TbRepayStatBean();
//        statBean.setApply_id(applyId);
//        return statRepayPlan(statBean, planBeans);
//    }
//
//    protected TbRepayStatBean statRepayPlan(TbRepayStatBean statBean, List<TbRepayPlanBean> planBeans) {
//        if (planBeans == null || planBeans.size() == 0) {
//            return statBean;
//        }
//        Date now = new Date();
//
//        int normalRepayTimes = 0;
//        int overdueRepayTimes = 0;
//        int overdueTimes = 0;
//        int currentSeq = planBeans.size();
//        for (TbRepayPlanBean planBean : planBeans) {
//            Date repayStartDate = planBean.getRepay_start_date();
//            if (DateUtil.compareDate(now, repayStartDate) < 0) {
//                // 待还还款计划中最小期数，即为当期
//                currentSeq = Math.min(currentSeq, planBean.getSeq_no());
//            }
//            RepayPlanOverdueEnum status = RepayPlanOverdueEnum.getStatusByCode(planBean.getRepay_status());
//            boolean isOverdue = status == RepayPlanOverdueEnum.PLAN_OVER_DUE;
//            boolean repayed = planBean.getAct_total() > 0;
//
//            overdueTimes += isOverdue ? 1 : 0;
//            overdueRepayTimes += isOverdue && repayed ? 1 : 0;
//            normalRepayTimes  += !isOverdue && repayed ? 1 : 0;
//            statBean.inc(planBean);
//        }
//
//        statBean.setCurrent_seq(currentSeq);
//        statBean.setNormal_repay_times(normalRepayTimes);
//        statBean.setOverdue_repay_times(overdueRepayTimes);
//        statBean.setOverdue_times(overdueTimes);
//
//        return statBean;
//    }

}
