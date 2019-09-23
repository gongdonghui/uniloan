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
import com.sup.common.param.ManualRepayParam;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.FunpayOrderUtil;
import com.sup.common.util.GsonUtil;
import com.sup.common.util.Result;
import com.sup.core.mapper.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
    private ApplyService applyService;
    @Autowired
    private PayCenterService funpayService;


    public Result autoLoan(TbApplyInfoBean applyInfoBean) {
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

        Result ret = updateRepayPlan(repayPlanBean);
        if (!ret.isSucc()) {
            log.error("getRepayInfo: update repayPlan failed! RepayV0 = " + GsonUtil.toJson(r));
        }

        return Result.succ(r);
    }

    public boolean addRepayPlan(TbApplyInfoBean applyInfoBean) {

        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status != ApplyStatusEnum.APPLY_LOAN_SUCC) {
            // repay plan must be added after loan
            log.error("addRepayPlan: invalid status=(" + status.getCode() + ")" + status.getCodeDesc());
            return false;
        }
        // generate repay plan if not exist(need thread safe)
        TbRepayPlanBean repayPlanBean = repayPlanMapper.getByApplyId(applyInfoBean.getId());
        if (repayPlanBean != null) {
            log.error("RepayPlan already exists for applyId = " + applyInfoBean.getId());
            return false;
        }

        synchronized (this) {
            repayPlanBean = genRepayPlan(applyInfoBean);
            if (repayPlanBean == null) {
                log.error("Failed to generate repay plan for applyId = " + applyInfoBean.getId());
                return false;
            }

            if (repayPlanMapper.insert(repayPlanBean) > 0) {
                return true;
            }
        }
        return false;
    }



    public Result updateRepayPlan(TbRepayPlanBean bean) {
        if (bean == null) {
            return Result.fail("TbRepayPlanBean is null!");
        }

        log.debug("updateRepayPlan: bean = " + GsonUtil.toJson(bean));

        bean.setUpdate_time(new Date());
        if (repayPlanMapper.updateById(bean) > 0) {
            return Result.succ();
        }

        log.error("updateRepayPlan: bean = " + GsonUtil.toJson(bean));

        return Result.fail("");
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
        List<TbRepayPlanBean> plans = repayPlanMapper.getRepayPlan(wrapper.eq("applyId", applyId));
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
            log.error("Invalid amount = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        Integer status = param.getStatus();
        FunpayOrderUtil.Status orderStatus = FunpayOrderUtil.getStatus(status);
        if (orderStatus != FunpayOrderUtil.Status.SUCCESS) {
            // do nothing
            log.info("repayCallBack: nothing to do for applyId = " + param.getApplyId() +
                    ", reason: " + FunpayOrderUtil.getMessage(status)
            );
            return Result.succ();
        }
        if (param.getAmount() <= 0) {
            // WTF???
            log.error("########### invalid repay amount ############");
            log.error("param = " + GsonUtil.toJson(param));
        }
        return repayAndUpdate(param.getApplyId(), Long.valueOf(param.getAmount()), param.getFinishTime());
    }

    public Result manualRepay(ManualRepayParam param) {
        if (param.getAmount() == null || param.getAmount() <= 0) {
            log.error("Invalid amount = " + GsonUtil.toJson(param));
            return Result.fail("");
        }
        return repayAndUpdate(param.getApplyId(), Long.valueOf(param.getAmount()), param.getRepayTime());
    }

    public Result repayAndUpdate(String applyId, Long repayAmount, Date repayTime) {
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

    public Result repayAndUpdate(TbRepayPlanBean repayPlanBean, Long repayAmount, Date repayTime) {
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
        return updateRepayPlan(repayPlanBean);
    }

    public LoanCalculatorParam calcLoanAmount(TbApplyInfoBean applyInfoBean) {
        return calcLoanAmount(applyInfoBean.getProduct_id(), applyInfoBean.getGrant_quota(), applyInfoBean.getPeriod());
    }

    public LoanCalculatorParam calcLoanAmount(Integer productId, Integer applyAmount, Integer applyPeriod) {
        // TODO
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
        return param;
    }

    protected TbRepayPlanBean genRepayPlan(TbApplyInfoBean bean) {

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

        switch (feeType) {
            case LOAN_PRE_FEE:
                principalToRepay = loanAmount;
                feeToRepay = 0;
                interestToRepay = interestTotal;
                break;
            case LOAN_PRE_FEE_PRE_INTEREST:
                principalToRepay = loanAmount;
                feeToRepay = 0;
                interestToRepay = 0;
                break;
            case LOAN_POST_FEE_POST_INTEREST:
                principalToRepay = loanAmount;
                feeToRepay = feeTotal;
                interestToRepay = interestTotal;
                break;
            default:
                break;
        }
        int totalToRepay = principalToRepay + feeToRepay + interestToRepay;
        Date repayStartTime = bean.getLoan_time();
        Date repayEndTime = DateUtil.getDate(repayStartTime, bean.getPeriod());

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
        repayPlanBean.setNeed_interest(Long.valueOf(interestToRepay));
        repayPlanBean.setNeed_management_fee(Long.valueOf(feeToRepay));
        repayPlanBean.setNeed_total(Long.valueOf(totalToRepay));

        return repayPlanBean;
    }
}
