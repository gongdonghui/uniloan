package com.sup.core.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sup.common.bean.TbApplyInfoBean;
import com.sup.common.bean.TbApplyMaterialInfoBean;
import com.sup.common.bean.TbRepayPlanBean;
import com.sup.common.bean.TbUserBankAccountInfoBean;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.vo.PayVO;
import com.sup.common.loan.ApplyMaterialTypeEnum;
import com.sup.common.service.PayCenterService;
import com.sup.core.facade.LoanFacade;
import com.sup.core.mapper.ApplyInfoMapper;
import com.sup.core.mapper.ApplyMaterialInfoMapper;
import com.sup.core.mapper.RepayPlanMapper;
import com.sup.core.mapper.UserBankInfoMapper;
import com.sup.common.loan.ApplyStatusEnum;
import com.sup.common.util.Result;
import com.sup.core.service.ApplyService;
import com.sup.core.service.LoanService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

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

    @Autowired
    private ApplyInfoMapper     applyInfoMapper;

    @Autowired
    private UserBankInfoMapper userBankInfoMapper;

    @Autowired
    private ApplyMaterialInfoMapper applyMaterialInfoMapper;

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
     * @param userId  用户id
     * @param applyId 进件id
     * @param amount  还款金额
     * @return 还款所需信息，包括交易码、便利店地址、流水号、交易码过期时间
     */
    @Override
    public Result getRepayInfo(String userId, String applyId, Integer amount) {
        // 检查是否已有还款信息


        // 重新获取还款信息

        return null;
    }

    /**
     * 支付通道放款回调接口
     *
     * @param userId
     * @param applyId
     * @param tradeNo
     * @return
     */
    @Override
    public Result payCallBack(String userId, String applyId, String tradeNo) {
        // TODO
        return null;
    }

    /**
     * 支付通道还款回调接口
     *
     * @param userId
     * @param applyId
     * @param tradeNo
     * @return
     */
    @Override
    public Result repayCallBack(String userId, String applyId, String tradeNo) {
        // TODO
        return null;
    }

    /**
     * 定时检查进件状态，终审通过则尝试自动放款
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void checkApplyStatus() {
        // TODO

        // 1. 获取所有终审通过的进件

        // 2.

        // 3. 对放款成功的进件：更新状态、添加还款计划

    }

    /**
     * 定时检查放款是否成功
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void checkLoanResult() {
        // TODO

        // 1. 获取所有放款中的进件

        // 2. 检查放款状态

        // 3. 对放款成功的进件：更新状态、添加还款计划

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



    protected Result autoLoan(TbApplyInfoBean applyInfoBean) {
        String userId = String.valueOf(applyInfoBean.getUser_id());
        String applyId = String.valueOf(applyInfoBean.getId());

        ApplyStatusEnum status = ApplyStatusEnum.getStatusByCode(applyInfoBean.getStatus());
        if (status != ApplyStatusEnum.APPLY_FINAL_PASS) {
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
}
