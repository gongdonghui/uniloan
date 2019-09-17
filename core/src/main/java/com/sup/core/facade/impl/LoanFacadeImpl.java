package com.sup.core.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.sup.common.loan.RepayPlanStatusEnum;
import com.sup.common.param.FunpayCallBackParam;
import com.sup.common.param.ManualRepayParam;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.FunpayOrderUtil;
import com.sup.common.util.GsonUtil;
import com.sup.core.facade.LoanFacade;
import com.sup.core.mapper.*;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.Result;
import com.sup.core.service.ApplyService;
import com.sup.core.service.LoanService;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Project:uniloan
 * Class:  LoanFacadeImpl
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@Log4j
@RestController
public class LoanFacadeImpl implements LoanFacade {

    @Value("loan.auto-loan-retry-times")
    private int AUTO_LOAN_RETRY_TIMES;

    @Value("query.page-num")
    private int QUERY_PAGE_NUM;

    @Autowired
    private ApplyInfoMapper     applyInfoMapper;

    @Autowired
    private UserBankInfoMapper userBankInfoMapper;

    @Autowired
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;

    @Autowired
    private RepayPlanMapper repayPlanMapper;

    @Autowired
    private RepayStatMapper repayStatMapper;

    @Autowired
    private ApplyService applyService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private PayCenterService funpayService;


    @Override
    public Result autoLoan(String userId, String applyId) {
        // get apply info and check apply status
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(applyId);
        if (applyInfoBean == null) {
            log.error("autoLoan: invalid applyId=" + applyId);
            return Result.fail("Invalid applyId!");
        }
        return autoLoan(applyInfoBean);
    }

    @Override
    public Result addRepayPlan(String userId, String applyId) {
        // get apply info and check apply status
        TbApplyInfoBean bean = applyInfoMapper.selectById(applyId);
        if (bean == null) {
            log.error("addRepayPlan: invalid applyId=" + applyId);
            return Result.fail("Invalid applyId!");
        }

        if (!loanService.addRepayPlan(bean)) {
            log.error("Failed to generate repay plan for user(" + userId + "), applyId = " + applyId);
            return Result.fail("Failed to add repay plan!");
        }

        return Result.succ();
    }

    @Override
    public Result updateRepayPlan(TbRepayPlanBean bean) {
        return loanService.updateRepayPlan(bean);
    }

    @Override
    public Result getRepayPlan(String applyId) {
        return loanService.getRepayPlan(applyId);
    }

    /**
     * 获取支付通道还款所需信息，包括支付码和链接
     *
     * @param repayInfo    还款参数
     * @return 还款所需信息，包括交易码、便利店地址、流水号、交易码过期时间
     */
    @Override
    public Result getRepayInfo(@RequestBody RepayInfo repayInfo) {
        // 检查是否已有还款信息
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(
                wrapper.eq("applyId", Integer.valueOf(repayInfo.getApplyId())).orderByDesc("create_time"));
        if (repayPlanBean == null) {
            log.error("Invalid applyId = " + repayInfo.getApplyId());
            return Result.fail("Invalid applyId!");
        }
        if (repayPlanBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
            return Result.fail("Nothing to repay.");
        }
        Date now = new Date();
        Date repayExpireTime = repayPlanBean.getExpire_time();
        String repayCode = repayPlanBean.getRepay_code();
        String repayLoc = repayPlanBean.getRepay_location();
        String tradeNo = repayPlanBean.getTrade_number();
        if (repayCode != null && repayLoc != null && tradeNo != null && repayExpireTime != null
                && DateUtil.compareDate(now, repayExpireTime) > 0) {
            RepayVO r = new RepayVO();
            r.setCode(repayCode);
            r.setShopLink(repayLoc);
            r.setExpireDate(DateUtil.formatDateTime(repayExpireTime));
            r.setTradeNo(tradeNo);
            return Result.succ(r);
        }
        Result<RepayVO> result = funpayService.repay(repayInfo);
        if (!result.isSucc()) {
            log.error("Failed to get repay info for applyId = " + repayInfo.getApplyId());
            return Result.fail("Failed to get repay info!");
        }
        RepayVO r = result.getData();
        repayPlanBean.setRepay_code(r.getCode());
        repayPlanBean.setRepay_location(r.getShopLink());
        repayPlanBean.setTrade_number(r.getTradeNo());
        repayPlanBean.setExpire_time(DateUtil.parseDateTime(r.getExpireDate()));
        repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_PROCESSING.getCode());

        Result ret = loanService.updateRepayPlan(repayPlanBean);
        if (!ret.isSucc()) {
            log.error("getRepayInfo: update repayPlan failed! RepayV0 = " + GsonUtil.toJson(r));
        }

        return Result.succ(r);
    }

    /**
     * 支付通道放款回调接口
     *
     * @param param
     * @return
     */
    @Override
    public Result payCallBack(@RequestBody FunpayCallBackParam param) {
        // update ApplyInfo status
        TbApplyInfoBean bean = applyInfoMapper.selectOne(
                new QueryWrapper<TbApplyInfoBean>().eq("applyId", param.getApplyId()).orderByDesc("create_time"));
        if (bean == null) {
            log.error("Invalid param = " + GsonUtil.toJson(param));
            return Result.fail("Invalid applyId!");
        }
        Integer status = param.getStatus();
        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
        if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
            // 处理中回调个毛线～～
            return Result.succ("");
        }
        if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
            // 放款成功，需更新放款时间
            if (param.getFinishTime() != null) {
                bean.setLoan_time(param.getFinishTime());
            } else {
                bean.setLoan_time(new Date());
            }
            // 检查放款金额与到手金额
            if (param.getAmount() != bean.getInhand_quota()) {
                log.error("########### invalid loan amount ############");
                log.error("param = " + GsonUtil.toJson(param));
                log.error("bean  = " + GsonUtil.toJson(bean));
                bean.setInhand_quota(param.getAmount());
            }
            bean.setStatus(ApplyStatusEnum.APPLY_LOAN_SUCC.getCode());
        } else {
            log.error("payCallBack: loan failed for applyId = " + bean.getId() +
                    ", reason: " + FunpayOrderUtil.getMessage(status)
            );
            bean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
        }
        return applyService.updateApplyInfo(bean);
    }

    /**
     * 支付通道还款回调接口
     *
     * @param param
     * @return
     */
    @Override
    public Result repayCallBack(@RequestBody FunpayCallBackParam param) {

        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        Integer status = param.getStatus();
        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
        if (orderStatus != FunpayOrderUtil.Status.SUCCESS) {
            // do nothing
            log.info("repayCallBack: repay failed for applyId = " + param.getApplyId() +
                    ", reason: " + FunpayOrderUtil.getMessage(status)
            );
            return Result.succ();
        }
        return repayAndUpdate(param.getApplyId(), Long.valueOf(param.getAmount()), param.getFinishTime());
    }

    /**
     * 手动还款
     *
     * @param param
     * @return
     */
    @Override
    public Result manualRepay(ManualRepayParam param) {
        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        return repayAndUpdate(param.getApplyId(), Long.valueOf(param.getAmount()), param.getRepayTime());
    }

    /**
     * 定时检查进件状态，终审通过则尝试自动放款
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void checkApplyStatus() {
        // 获取所有终审通过的进件
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<TbApplyInfoBean>()
                .eq("status", Integer.valueOf(ApplyStatusEnum.APPLY_FINAL_PASS.getCode()));
        // TODO: 应当分页处理
        List<TbApplyInfoBean> applyInfos = applyInfoMapper.selectList(wrapper);
        if (applyInfos == null || applyInfos.size() == 0) {
            log.info("No APPLY_FINAL_PASS apply info.");
            return;
        }

        for (TbApplyInfoBean bean : applyInfos) {
            Result r = autoLoan(bean);
            if (!r.isSucc()) {
                log.error("Failed to auto loan for applyId = " + bean.getId());
            }
        }
    }

    /**
     * 定时检查放款是否成功
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void checkLoanResult() {
        // 1. 获取所有放款中的进件
        QueryWrapper<TbApplyInfoBean> wrapper = new QueryWrapper<TbApplyInfoBean>()
                .eq("status", Integer.valueOf(ApplyStatusEnum.APPLY_AUTO_LOANING.getCode()));
        // TODO: 应当分页处理
        List<TbApplyInfoBean> applyInfos = applyInfoMapper.selectList(wrapper);
        if (applyInfos == null || applyInfos.size() == 0) {
            log.info("No APPLY_AUTO_LOANING apply info.");
            return;
        }

        // 2. 检查放款状态
        PayStatusInfo psi = new PayStatusInfo();
        for (TbApplyInfoBean bean : applyInfos) {
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

            Integer status = ret.getData().getStatus();
            FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
            if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
                continue;
            }
            if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
                // TODO
                // 放款成功，检查金额？

                // 更新放款时间
                Date loanTime = DateUtil.parse(ret.getData().getSendTime(), DateUtil.NO_SPLIT_FORMAT);
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


    /**
     * 定时检查自助还款是否成功
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void checkRepayResult() {
        List<TbRepayPlanBean> repayPlanBeans = repayPlanMapper.selectList(
                new QueryWrapper<TbRepayPlanBean>().eq("repay_status", RepayPlanStatusEnum.PLAN_PAID_PROCESSING.getCode())
        );
        if (repayPlanBeans == null || repayPlanBeans.size() == 0) {
            log.info("No repayPlan to check!");
            return;
        }
        // TODO: 应当分页处理
        RepayStatusInfo rsi = new RepayStatusInfo();
        for (TbRepayPlanBean bean : repayPlanBeans) {
            if (bean.getTrade_number() == null) {
                log.error("No trade number for applyId = " + bean.getId());
                continue;
            }
            rsi.setApplyId(String.valueOf(bean.getApply_id()));
            rsi.setTradeNo(bean.getTrade_number());
            Result<RepayStatusVO> ret = funpayService.repayStatus(rsi);
            if (!ret.isSucc()) {
                continue;
            }
            Integer status = ret.getData().getStatus();
            FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
            if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
                continue;
            }
            if (orderStatus == FunpayOrderUtil.Status.SUCCESS) {
                // 还款成功，更新还款计划
                Long repayAmount = 0L;
                // TODO
                // repayAmount = ret.getData().getAmount();
                Date repayTime = DateUtil.parse(ret.getData().getPurchaseTime(), DateUtil.NO_SPLIT_FORMAT);
                repayAndUpdate(bean, repayAmount, repayTime);
            } else {
                log.error("Auto repay failed for applyId = " + bean.getId() +
                        ", reason: " + FunpayOrderUtil.getMessage(status)
                );
                bean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_ERROR.getCode());
                Result result = loanService.updateRepayPlan(bean);
                if (!result.isSucc()) {
                    log.error("checkRepayResult: Failed to update repayPlan for applyId = " + bean.getApply_id());
                }
            }
        }
    }

    /**
     * 每天查询逾期情况，并更新相应款项
     */
    @Scheduled(cron = "0 1 * * * ?")
    public void checkOverdue() {
        // TODO

        // 1. 获取所有放款中的进件

        // 2. 检查放款状态

        // 3. 对放款成功的进件：更新状态、添加还款计划

    }

    /**
     * 每天更新还款统计表
     */
    @Scheduled(cron = "30 0 * * * ?")
    public void statRepayInfo() {
        // TODO: 应当分页处理
        // 1. 获取所有还款计划（不含未还）
        List<TbRepayPlanBean> repayPlanBeans = repayPlanMapper.selectList(
                new QueryWrapper<TbRepayPlanBean>().ne("repay_status", RepayPlanStatusEnum.PLAN_NOT_PAID.getCode())
        );
        if (repayPlanBeans == null || repayPlanBeans.size() == 0) {
            log.info("Nothing to statistic.");
            return;
        }
        // applyId => List<TbRepayPlanBean>
        Map<String, List<TbRepayPlanBean>> repayPlanMap = new HashMap<>();
        for (TbRepayPlanBean repayPlanBean : repayPlanBeans) {
            String applyId = String.valueOf(repayPlanBean.getApply_id());
            if (!repayPlanMap.containsKey(applyId)) {
                repayPlanMap.put(applyId, new ArrayList<>());
            }
            repayPlanMap.get(applyId).add(repayPlanBean);
        }

        List<TbRepayStatBean> statBeans = repayStatMapper.selectList(
                new QueryWrapper<TbRepayStatBean>().select("apply_id")
        );
        Set<String> statApplyIds = new HashSet<>();
        for (TbRepayStatBean statBean : statBeans) {
            statApplyIds.add(String.valueOf(statBean.getApply_id()));
        }

        // 2. 获取所有还款统计表中的applyId（便于识别是插入还是更新）
        for (String applyId : repayPlanMap.keySet()) {
            TbRepayStatBean statBean = statRepayPlan(applyId, repayPlanMap.get(applyId));
            if (statBean == null) {
                continue;
            }
            Date now = new Date();
            if (statApplyIds.contains(applyId)) {
                statBean.setUpdate_time(now);
                if (repayStatMapper.update(statBean,
                        new QueryWrapper<TbRepayStatBean>().eq("apply_id", applyId)) <= 0) {
                    log.error("Failed to update! bean = " + GsonUtil.toJson(statBean));
                }
            } else {
                statBean.setCreate_time(now);
                statBean.setUpdate_time(now);
                if (repayStatMapper.insert(statBean) <= 0) {
                    log.error("Failed to insert! bean = " + GsonUtil.toJson(statBean));
                }
            }
        }
    }



    protected Result autoLoan(TbApplyInfoBean applyInfoBean) {
        String userId = String.valueOf(applyInfoBean.getUser_id());
        String applyId = String.valueOf(applyInfoBean.getId());

        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status == null || status != ApplyStatusEnum.APPLY_FINAL_PASS) {
            log.error("autoLoan: invalid apply status=" + status.getCode() + ", " + status.getCodeDesc());
            return Result.fail("Invalid status!");
        }
        if (applyInfoBean.getInhand_quota() <= 0) {
            log.error("Invalid in-hand quota = " + applyInfoBean.getInhand_quota());
            return Result.fail("Invalid in-hand quota!");
        }

        // 2. get user bank info
        QueryWrapper<TbApplyMaterialInfoBean> materialWrapper = new QueryWrapper<>();
        TbApplyMaterialInfoBean applyMaterialInfoBean = applyMaterialInfoMapper.selectOne(materialWrapper
                .eq("applyId", applyId)
                .eq("info_type", ApplyMaterialTypeEnum.APPLY_MATERIAL_BANK.getCode()));

        if (applyMaterialInfoBean == null) {
            log.error("No apply material(bank info) found for applyId=" + applyId);
            return Result.fail("No apply material found!");
        }
        String infoId = applyMaterialInfoBean.getInfo_id();
        QueryWrapper<TbUserBankAccountInfoBean> bankWrapper = new QueryWrapper<>();

        // make sure there is only one bank card for current apply
        TbUserBankAccountInfoBean bankInfoBean = userBankInfoMapper.selectOne(
                bankWrapper.eq("info_id", infoId).eq("user_id", userId).orderByDesc("create_time"));
        if (bankInfoBean == null) {
            log.error("No bank info for user(" + userId + "), info_id=" + infoId);
            return Result.fail("No bank info found!");
        }

        // 3. loan using funpay(need thread safe)
        boolean loanSucc = false;
        synchronized (this) {
            PayInfo payInfo = new PayInfo();
            payInfo.setUserId(userId);
            payInfo.setApplyId(applyId);
            payInfo.setAmount(applyInfoBean.getInhand_quota());
            payInfo.setBankNo(String.valueOf(bankInfoBean.getBank()));
            payInfo.setAccountNo(bankInfoBean.getAccount_id());
            payInfo.setAccountType(bankInfoBean.getAccount_type());
            payInfo.setAccountName(bankInfoBean.getName());

            try {
                for (int i = 0; i < AUTO_LOAN_RETRY_TIMES; i++) {
                    Result<PayVO> result = funpayService.pay(payInfo);
                    if (result != null && result.isSucc()) {
                        loanSucc = true;
                        applyInfoBean.setTrade_number(result.getData().getTradeNo());
                        break;
                    }
                    Thread.sleep(1000);
                }
            }catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }

        // if loan succeeded, update apply info status
        if (loanSucc) {
            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOANING.getCode());
        } else {
            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_AUTO_LOAN_FAILED.getCode());
        }
        applyInfoBean.setUpdate_time(new Date());
        applyInfoBean.setOperator_id(0);    // system
        return applyService.updateApplyInfo(applyInfoBean);
    }

    protected Result repayAndUpdate(String applyId, Long repayAmount, Date repayTime) {
        // update RepayPlanInfo
        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(
                new QueryWrapper<TbRepayPlanBean>().eq("applyId", applyId).orderByDesc("create_time")
        );
        if (repayPlanBean == null) {
            log.error("Invalid applyId = " + applyId);
            return Result.fail("Invalid applyId!");
        }
        log.info("repayAndUpdate: applyId = " + applyId + ", repayAmount = " + repayAmount);
        return repayAndUpdate(repayPlanBean, repayAmount, repayTime);
    }

    protected Result repayAndUpdate(TbRepayPlanBean repayPlanBean, Long repayAmount, Date repayTime) {
        if (repayPlanBean == null) {
            return Result.fail("");
        }
        if (repayAmount <= 0) {
            return Result.succ();
        }
        if (repayTime == null) {
            repayTime = new Date();
        }
        repayPlanBean.updateActFields(repayAmount);
        repayPlanBean.setRepay_time(repayTime);
        if (repayPlanBean.getAct_total() < repayPlanBean.getNeed_total()) {
            repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_PART.getCode());
        } else {
            repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_ALL.getCode());
        }
        return loanService.updateRepayPlan(repayPlanBean);
    }

    protected TbRepayStatBean statRepayPlan(String applyId, List<TbRepayPlanBean> planBeans) {
        if (planBeans == null || planBeans.size() == 0) {
            return null;
        }
        Date now = new Date();
        TbRepayStatBean statBean = new TbRepayStatBean();
        statBean.setApply_id(Integer.valueOf(applyId));

        for (TbRepayPlanBean planBean : planBeans) {

        }
        // TODO

        return statBean;
    }
}
