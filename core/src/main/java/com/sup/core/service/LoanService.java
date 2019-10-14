package com.sup.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.*;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.vo.PayVO;
import com.sup.common.bean.paycenter.vo.RepayVO;
import com.sup.common.loan.*;
import com.sup.common.param.FunpayCallBackParam;
import com.sup.common.param.LoanCalculatorParam;
import com.sup.common.param.ManualLoanParam;
import com.sup.common.param.ManualRepayParam;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.FunpayOrderUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.mapper.*;
import com.sup.core.util.MqMessenger;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Project:uniloan
 * Class:  LoanService
 * <p>
 * Author: guanfeng
 * Create: 2019-09-09
 */

@Log4j
@Service
public class LoanService {

    @Value("#{new Integer('${loan.auto-loan-retry-times}')}")
    private Integer AUTO_LOAN_RETRY_TIMES;

    @Autowired
    private RepayPlanMapper repayPlanMapper;
    @Autowired
    private UserBankInfoMapper userBankInfoMapper;
    @Autowired
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;
    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private ProductInfoMapper productInfoMapper;
    @Autowired
    private RepayHistoryMapper  repayHistoryMapper;

    @Autowired
    private ApplyService applyService;
    @Autowired
    private PayCenterService funpayService;
    @Autowired
    private MqMessenger mqMessenger;

    public Result autoLoan(TbApplyInfoBean applyInfoBean) {
        String userId = String.valueOf(applyInfoBean.getUser_id());
        String applyId = String.valueOf(applyInfoBean.getId());

        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status == null || status != ApplyStatusEnum.APPLY_FINAL_PASS) {
            return Result.fail("Not APPLY_FINAL_PASS status!");
        }
        if (applyInfoBean.getInhand_quota() <= 0) {
            log.error("Invalid in-hand quota = " + applyInfoBean.getInhand_quota());
            return Result.fail("Invalid in-hand quota!");
        }

        // 2. get user bank info
        QueryWrapper<TbApplyMaterialInfoBean> materialWrapper = new QueryWrapper<>();
        TbApplyMaterialInfoBean applyMaterialInfoBean = applyMaterialInfoMapper.selectOne(materialWrapper
                .eq("apply_id", applyId)
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
            payInfo.setRemark(applyId);

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

    public Result getRepayInfo(@RequestBody RepayInfo repayInfo) {
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectOne(
                wrapper.eq("apply_id", Integer.valueOf(repayInfo.getApplyId())).orderByDesc("create_time"));
        if (repayPlanBean == null) {
            log.error("Invalid applyId = " + repayInfo.getApplyId());
            return Result.fail("Invalid applyId!");
        }
        if (repayPlanBean.getRepay_status() == RepayPlanStatusEnum.PLAN_PAID_ALL.getCode()) {
            return Result.fail("Nothing to repay.");
        }
        Date now = new Date();
        // 检查还款记录表，还款处理中的记录
        QueryWrapper<TbRepayHistoryBean> historyWrapper = new QueryWrapper<>();
        historyWrapper.eq("repay_status", RepayStatusEnum.REPAY_STATUS_PROCESSING.getCode());
        historyWrapper.ge("expire_time", now);

        TbRepayHistoryBean repayHistoryBean = repayHistoryMapper.selectOne(historyWrapper);
        if (repayHistoryBean != null) {
            RepayVO r = new RepayVO();
            r.setCode(repayHistoryBean.getRepay_code());
            r.setShopLink(repayHistoryBean.getRepay_location());
            r.setExpireDate(DateUtil.formatDateTime(repayHistoryBean.getExpire_time()));
            r.setTradeNo(repayHistoryBean.getTrade_number());
            return Result.succ(r);
        }

        // 创建还款记录
        repayHistoryBean = new TbRepayHistoryBean();
        repayHistoryBean.setUser_id(Integer.valueOf(repayInfo.getUserId()));
        repayHistoryBean.setApply_id(Integer.valueOf(repayInfo.getApplyId()));
        repayHistoryBean.setRepay_plan_id(repayPlanBean.getId());
        repayHistoryBean.setRepay_amount(Long.valueOf(repayInfo.getAmount()));
        repayHistoryBean.setCreate_time(now);
        repayHistoryBean.setUpdate_time(now);
        if (repayHistoryMapper.insert(repayHistoryBean) <= 0) {
            log.error("Fail to add repay detail for bean: " + GsonUtil.toJson(repayInfo));
            return Result.fail("");
        }
        repayInfo.setOrderNo(String.valueOf(repayHistoryBean.getId()));
        try {
            Result<RepayVO> result = funpayService.repay(repayInfo);
            if (!result.isSucc()) {
                log.error("Failed to get repay info for applyId = " + repayInfo.getApplyId() + ", msg = " + result.getMessage());
                return Result.fail("Failed to get repay info!");
            }
            RepayVO r = result.getData();
            repayHistoryBean.setRepay_code(r.getCode());
            repayHistoryBean.setRepay_location(r.getShopLink());
            repayHistoryBean.setTrade_number(r.getTradeNo());
            repayHistoryBean.setExpire_time(DateUtil.parseDateTime(r.getExpireDate()));
            repayHistoryBean.setRepay_status(RepayStatusEnum.REPAY_STATUS_PROCESSING.getCode());

            if (repayHistoryMapper.updateById(repayHistoryBean) <= 0) {
                log.error("Failed to update repayHistory bean = " + GsonUtil.toJson(repayHistoryBean));
                // return Result.fail("");
            }
            repayPlanBean.setRepay_code(r.getCode());
            repayPlanBean.setRepay_location(r.getShopLink());
            repayPlanBean.setTrade_number(r.getTradeNo());
            repayPlanBean.setExpire_time(DateUtil.parseDateTime(r.getExpireDate()));
            repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_PAID_PROCESSING.getCode());
            repayPlanBean.setUpdate_time(now);
            if (repayPlanMapper.updateById(repayPlanBean) <= 0) {
                log.error("Failed to update repayPlan bean = " + GsonUtil.toJson(repayPlanBean));
            }
            return Result.succ(r);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Paycenter error: " + e.getMessage());
        }
        return Result.fail("System error!");
    }

    public boolean addRepayPlan(TbApplyInfoBean applyInfoBean) {

        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status != ApplyStatusEnum.APPLY_LOAN_SUCC) {
            // repay plan must be added after loan
            log.error("addRepayPlan: invalid status = " + GsonUtil.toJson(status));
            return false;
        }
        // generate repay plan if not exist(need thread safe)
        TbRepayPlanBean repayPlanBean = repayPlanMapper.getByApplyId(applyInfoBean.getId());
        if (repayPlanBean != null) {
            log.error("RepayPlan already exists for applyId = " + applyInfoBean.getId());
            return false;
        }

        List<TbRepayPlanBean> plans = genRepayPlan(applyInfoBean);
        if (plans == null || plans.size() == 0) {
            return false;
        }
        boolean ret = true;
        synchronized (this) {
            for (TbRepayPlanBean bean : plans) {
                if (repayPlanMapper.insert(bean) <= 0) {
                    ret = false;
                    log.error("Failed to add repay plan: " + GsonUtil.toJson(bean));
                }
            }
        }
        return ret;
    }



    public Result updateRepayPlan(TbRepayPlanBean bean) {
        if (bean == null) {
            return Result.fail("TbRepayPlanBean is null!");
        }

        log.debug("updateRepayPlan: bean = " + GsonUtil.toJson(bean));

        bean.setUpdate_time(new Date());
        if (repayPlanMapper.updateById(bean) <= 0) {
            log.error("updateRepayPlan failed! bean = " + GsonUtil.toJson(bean));
            return Result.fail("");
        }

        // update ApplyInfo
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(bean.getApply_id());
        if (applyInfoBean == null) {
            log.error("Invalid applyId! bean = " + GsonUtil.toJson(bean));
            return Result.succ();
        }
        List<TbRepayPlanBean> planBeans = repayPlanMapper.selectList(
                new QueryWrapper<TbRepayPlanBean>().eq("apply_id", bean.getApply_id())
        );
        assert(planBeans != null && planBeans.size() > 0);
        TbRepayStatBean repayStatBean = new TbRepayStatBean();
        for (TbRepayPlanBean planBean : planBeans) {
            repayStatBean.inc(planBean);
        }
        if (repayStatBean.getAct_total().longValue() + repayStatBean.getReduction_fee() >= repayStatBean.getNeed_total().longValue()) {
            applyInfoBean.setStatus(ApplyStatusEnum.APPLY_REPAY_ALL.getCode());
            mqMessenger.applyStatusChange(applyInfoBean);
            applyInfoMapper.updateById(applyInfoBean);
        }

        return Result.succ();
    }

    public Result getRepayPlan(String applyId) {
        if (applyId == null) {
            return Result.fail("Invalid applyId!");
        }
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        List<TbRepayPlanBean> plans = repayPlanMapper.getRepayPlan(wrapper.eq("applyId", applyId));

        return Result.succ(plans);
    }

    public boolean writeOffRepayPlan(Integer applyId) {
        // write off all the repay plan
        if (applyId == null) {
            return false;
        }
        QueryWrapper<TbRepayPlanBean> wrapper = new QueryWrapper<>();
        List<TbRepayPlanBean> plans = repayPlanMapper.getRepayPlan(wrapper.eq("apply_id", applyId));
        int status = RepayPlanStatusEnum.PLAN_PAID_WRITE_OFF.getCode();
        Date now = new Date();
        for (TbRepayPlanBean bean : plans) {
            bean.setRepay_status(status);
            bean.setUpdate_time(now);
            repayPlanMapper.updateById(bean);
        }
        return true;
    }

    public Result payCallBack(@RequestBody FunpayCallBackParam param) {
        // update ApplyInfo status
        TbApplyInfoBean bean = applyInfoMapper.selectOne(
                new QueryWrapper<TbApplyInfoBean>().eq("apply_id", param.getOrderNo()).orderByDesc("create_time"));
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
            if (!param.getAmount().equals(bean.getInhand_quota())) {
                log.error("########### invalid loan amount ############");
                log.error("param = " + GsonUtil.toJson(param));
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

    public Result repayCallBack(@RequestBody FunpayCallBackParam param) {

        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount. param = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        Integer status = param.getStatus();
        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
        if (orderStatus == FunpayOrderUtil.Status.PROCESSING) {
            // 处理中，返回即可
            return Result.succ();
        }
        QueryWrapper<TbRepayHistoryBean> wrapper = new QueryWrapper<>();
        TbRepayHistoryBean repayHistoryBean = repayHistoryMapper.selectById(param.getOrderNo());
        if (repayHistoryBean == null) {
            log.error("Invalid repayHistory id. param = " + GsonUtil.toJson(param));
            return Result.fail("Invalid id!");
        }
        if (orderStatus == FunpayOrderUtil.Status.ERROR) {
            // 还款失败
            repayHistoryBean.setRepay_status(RepayStatusEnum.REPAY_STATUS_FAILED.getCode());
            repayHistoryMapper.updateById(repayHistoryBean);
            mqMessenger.sendRepayMessage(repayHistoryBean);
            return Result.fail("Repay failed!");
        }

        return repayAndUpdate(param.getOrderNo(), Long.valueOf(param.getAmount()), param.getFinishTime(), false);
    }

    public Result manualLoan(ManualLoanParam param) {
        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount = " + GsonUtil.toJson(param));
            return Result.fail("Invalid param!");
        }
        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(param.getApplyId());
        if (applyInfoBean == null) {
            log.error("Invalid param = " + GsonUtil.toJson(param));
            return Result.fail("Invalid param!");
        }
        if (!applyInfoBean.getInhand_quota().equals(param.getAmount())) {
            log.error("########### invalid loan amount ############");
            log.error("param = " + GsonUtil.toJson(param));
            log.error("bean  = " + GsonUtil.toJson(applyInfoBean));
        }
        Date loanTime = DateUtil.parse(param.getLoanTime(), DateUtil.DEFAULT_DATETIME_FORMAT);
        applyInfoBean.setStatus(ApplyStatusEnum.APPLY_LOAN_SUCC.getCode());
        applyInfoBean.setOperator_id(Integer.valueOf(param.getOperatorId()));
        applyInfoBean.setLoan_time(loanTime);

        return applyService.updateApplyInfo(applyInfoBean);
    }

    public Result manualRepay(ManualRepayParam param) {
        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        // 创建还款记录
        Date now = new Date();
        Date repayTime = DateUtil.parse(param.getRepayTime(), DateUtil.DEFAULT_DATETIME_FORMAT);
        TbRepayHistoryBean repayHistoryBean = new TbRepayHistoryBean();
        repayHistoryBean.setUser_id(Integer.valueOf(param.getUserId()));
        repayHistoryBean.setApply_id(Integer.valueOf(param.getApplyId()));
        repayHistoryBean.setOperator_id(Integer.valueOf(param.getOperatorId()));
        repayHistoryBean.setRepay_amount(Long.valueOf(param.getAmount()));
        repayHistoryBean.setRepay_time(repayTime);
        repayHistoryBean.setRepay_status(RepayStatusEnum.REPAY_STATUS_PROCESSING.getCode());
        repayHistoryBean.setCreate_time(now);
        repayHistoryBean.setUpdate_time(now);
        if (repayHistoryMapper.insert(repayHistoryBean) <= 0) {
            log.error("Failed to add new repayHistory! bean = " + GsonUtil.toJson(repayHistoryBean));
            return Result.fail("Failed to add new repayHistory!");
        }

        return repayAndUpdate(repayHistoryBean, Long.valueOf(param.getAmount()), repayTime, true);
    }

    protected Result repayAndUpdate(String repayHistoryId, Long repayAmount, Date repayTime, boolean isManual) {
        TbRepayHistoryBean repayHistoryBean = repayHistoryMapper.selectById(repayHistoryId);
        if (repayHistoryBean == null) {
            log.error("Invalid repayHistory id = " + repayHistoryId);
            return Result.fail("Invalid id!");
        }
        return repayAndUpdate(repayHistoryBean, repayAmount, repayTime, isManual);
    }

    public Result repayAndUpdate(TbRepayHistoryBean repayHistoryBean, Long repayAmount, Date repayTime, boolean isManual) {

        if (repayHistoryBean == null) {
            return Result.fail("");
        }
        if (repayAmount <= 0) {
            return Result.succ();
        }
        if (repayTime == null) {
            repayTime = new Date();
        }
        // 更新还款记录
        repayHistoryBean.setRepay_status(RepayStatusEnum.REPAY_STATUS_SUCCEED.getCode());
        repayHistoryBean.setRepay_amount(repayAmount);
        repayHistoryBean.setRepay_time(repayTime);
        repayHistoryBean.setUpdate_time(new Date());
        if (repayHistoryMapper.updateById(repayHistoryBean) <= 0) {
            log.error("Failed to update bean: " + GsonUtil.toJson(repayHistoryBean));
        }

        // 更新还款计划
        // TODO: 根据plan_id查找当期还款计划
        // TbRepayPlanBean repayPlanBean = repayPlanMapper.getByApplyId(repayHistoryBean.getApply_id());
        TbRepayPlanBean repayPlanBean = repayPlanMapper.selectById(repayHistoryBean.getRepay_plan_id());
        if (repayPlanBean == null) {
            log.error("Invalid apply id! bean = " + GsonUtil.toJson(repayHistoryBean));
            return Result.fail("Invalid apply id!");
        }

        repayPlanBean.updateActFields(repayAmount);
        repayPlanBean.setRepay_time(repayTime);
        RepayPlanStatusEnum repayStatus;

        if (repayPlanBean.getAct_total() + repayPlanBean.getReduction_fee() < repayPlanBean.getNeed_total()) {
            repayStatus = RepayPlanStatusEnum.PLAN_PAID_PART;
        } else {
            repayStatus = RepayPlanStatusEnum.PLAN_PAID_ALL;
        }
        repayPlanBean.setRepay_status(repayStatus.getCode());
        // sendRepayMessage(repayPlanBean.getUser_id(), repayHistoryBean.getId(), repayStatus, repayAmount, repayTime);
        mqMessenger.sendRepayMessage(repayHistoryBean);
        return updateRepayPlan(repayPlanBean);
    }

    public LoanCalculatorParam calcLoanAmount(TbApplyInfoBean applyInfoBean) {
        return calcLoanAmount(applyInfoBean.getProduct_id(), applyInfoBean.getGrant_quota(), applyInfoBean.getPeriod());
    }

    public LoanCalculatorParam calcLoanAmount(Integer productId, Integer applyAmount, Integer applyPeriod) {
        QueryWrapper<TbProductInfoBean> wrapper = new QueryWrapper<>();
        wrapper.eq("id", productId);
        TbProductInfoBean productInfoBean = productInfoMapper.selectOne(wrapper);
        if (productInfoBean == null) {
            log.error("Invalid product id: " + productId);
            return null;
        }
        LoanFeeTypeEnum feeType = LoanFeeTypeEnum.getStatusByCode(productInfoBean.getFee_type());
        if (feeType == null) {
            log.error("Invalid feeType! product bean = " + GsonUtil.toJson(productInfoBean));
            return null;
        }

        int loanAmount = applyAmount;
        int feeTotal = (int)(loanAmount * productInfoBean.getFee());
        int interestTotal = (int) (loanAmount * productInfoBean.getRate() * applyPeriod);

        int quotaInhand = 0;
        switch (feeType) {
            case LOAN_PRE_FEE:
                quotaInhand = loanAmount - feeTotal;
                break;
            case LOAN_PRE_FEE_PRE_INTEREST:
                quotaInhand = loanAmount - feeTotal - interestTotal;
                break;
            case LOAN_POST_FEE_POST_INTEREST:
                quotaInhand = loanAmount;
                break;
            default:
                break;
        }
        LoanCalculatorParam param = new LoanCalculatorParam();
        param.setProductId(productId);
        param.setApplyAmount(applyAmount);
        param.setApplyPeriod(applyPeriod);
        param.setInhandAmount(quotaInhand);
        param.setTotalAmount(loanAmount + feeTotal + interestTotal);
        // log.info("Product bean: " + GsonUtil.toJson(productInfoBean));
        // log.info("Return bran: " + GsonUtil.toJson(param));
        return param;
    }


    protected List<TbRepayPlanBean> genRepayPlan(TbApplyInfoBean bean) {

        LoanFeeTypeEnum feeType = LoanFeeTypeEnum.getStatusByCode(bean.getFee_type());
        if (feeType == null) {
            log.error("genRepayPlan: invalid feeType = " + bean.getFee_type()
                    + ", applyId = " + bean.getId()
            );
            return null;
        }
        int loanAmount = bean.getGrant_quota();
        int feeTotal = (int)(loanAmount * bean.getFee());    // service fee
        int interestTotal = (int)(loanAmount * bean.getRate() * bean.getPeriod());

        int principalToRepay = loanAmount;
        int feeToRepay = feeTotal;
        int interestToRepay = interestTotal;
        int total = loanAmount + feeTotal + interestTotal;

        switch (feeType) {
            case LOAN_PRE_FEE:
                feeToRepay = 0;
                interestToRepay = interestTotal;
                break;
            case LOAN_PRE_FEE_PRE_INTEREST:
                feeToRepay = 0;
                interestToRepay = 0;
                break;
            case LOAN_POST_FEE_POST_INTEREST:
                feeToRepay = feeTotal;
                interestToRepay = interestTotal;
                break;
            default:
                break;
        }
        List<TbRepayPlanBean> plans = new ArrayList<>();

        int totalToRepay = principalToRepay + feeToRepay + interestToRepay;
        Date repayStartTime = bean.getLoan_time();
        Date repayEndTime = DateUtil.getDate(repayStartTime, bean.getPeriod());

        Date now = new Date();
        // TODO: 根据期数生成多个还款计划

        TbRepayPlanBean repayPlanBean = new TbRepayPlanBean();
        repayPlanBean.setUser_id(bean.getUser_id());
        repayPlanBean.setApply_id(bean.getId());
        repayPlanBean.setProduct_id(bean.getProduct_id());
        repayPlanBean.setSeq_no(1);
        repayPlanBean.setRepay_start_date(repayStartTime);
        repayPlanBean.setRepay_end_date(repayEndTime);
        repayPlanBean.setRepay_status(RepayPlanStatusEnum.PLAN_NOT_PAID.getCode());
        repayPlanBean.setIs_overdue(RepayPlanOverdueEnum.PLAN_NOT_OVER_DUE.getCode());
        repayPlanBean.setNeed_principal(Long.valueOf(principalToRepay));
        repayPlanBean.setNeed_interest(Long.valueOf(interestTotal));
        repayPlanBean.setNeed_management_fee(Long.valueOf(feeTotal));
        repayPlanBean.setNeed_total(Long.valueOf(total));
        // 实际已还款项，含已扣除费用（服务费、利息等）
        repayPlanBean.setAct_interest(Long.valueOf(interestTotal - interestToRepay));
        repayPlanBean.setAct_management_fee(Long.valueOf(feeTotal - feeToRepay));
        repayPlanBean.setAct_total(Long.valueOf(total - totalToRepay));
        repayPlanBean.setCreate_time(now);
        repayPlanBean.setUpdate_time(now);

        plans.add(repayPlanBean);

        return plans;
    }
}
