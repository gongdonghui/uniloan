package com.sup.core.facade.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.sup.common.bean.*;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.PayStatusInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.RepayStatusInfo;
import com.sup.common.bean.paycenter.vo.*;
import com.sup.common.loan.*;
import com.sup.common.param.*;
import com.sup.common.service.PayCenterService;
import com.sup.common.util.DateUtil;
import com.sup.common.util.FunpayOrderUtil;
import com.sup.common.util.GsonUtil;
import com.sup.core.facade.LoanFacade;
import com.sup.core.mapper.*;
import com.sup.common.util.Result;
import com.sup.core.service.ApplyService;
import com.sup.core.service.LoanService;
import com.sup.core.service.RuleConfigService;
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
    @Autowired
    private ApplyInfoMapper     applyInfoMapper;

    @Autowired
    private RuleConfigService ruleConfigService;

    @Autowired
    private LoanService loanService;

    private final float FLOAT_ZERO = 0.000001F;


    /**
     * 放款计算器，预估到手金额以及还款总额度
     *
     * @param param
     * @return
     */
    @Override
    public Result calculator(LoanCalculatorParam param) {
        log.info("calculator param:" + GsonUtil.toJson(param));
        Integer productId = param.getProductId();
        Integer applyAmount = param.getApplyAmount();
        Integer applyPeriod = param.getApplyPeriod();
        param = loanService.calcLoanAmount(productId, applyAmount, applyPeriod);
        if (param == null) {
            return Result.fail("System error!");
        }
        log.info("calculator result:" + GsonUtil.toJson(param));
        return Result.succ(param);
    }
//
//    @Override
//    public Result autoLoan(String userId, String applyId) {
//        // get apply info and check apply status
//        TbApplyInfoBean applyInfoBean = applyInfoMapper.selectById(applyId);
//        if (applyInfoBean == null) {
//            log.error("autoLoan: invalid applyId=" + applyId);
//            return Result.fail("Invalid applyId!");
//        }
//        return loanService.autoLoan(applyInfoBean);
//    }
//
//    @Override
//    public Result addRepayPlan(String userId, String applyId) {
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
        return loanService.getRepayInfo(repayInfo);
    }

    /**
     * 支付通道放款回调接口
     *
     * @param param
     * @return
     */
    @Override
    public Result payCallBack(@RequestBody FunpayCallBackParam param) {
        return loanService.payCallBack(param);
    }

    /**
     * 支付通道还款回调接口
     *
     * @param param
     * @return
     */
    @Override
    public Result repayCallBack(@RequestBody FunpayCallBackParam param) {
       return loanService.repayCallBack(param);
    }

    @Override
    public Result manualLoan(ManualLoanParam param) {
        return loanService.manualLoan(param);
    }

    /**
     * 手动还款
     *
     * @param param
     * @return
     */
    @Override
    public Result manualRepay(ManualRepayParam param) {
        return loanService.manualRepay(param);
    }

    /**
     * 减免费用
     *
     * @param param
     * @return
     */
    @Override
    public Result reduction(ReductionParam param) {
        return loanService.reduction(param);
    }

    /**
     * 重试线上放款
     *
     * @param param
     * @return
     */
    @Override
    public Result retryLoan(ApplyRetryLoanParam param) {
        return loanService.retryLoan(param);
    }

    /**
     * 更新用户等级
     *
     * @param userId
     * @return
     */
    @Override
    public Result updateUserCredit(Integer userId) {
        ruleConfigService.updateCreditLevelForUser(userId);
        return Result.succ();
    }

    /**
     * 获取还款使用的虚拟卡
     *
     * @param repayInfo 还款参数
     * @return 虚拟卡号、收款单位、收款银行名称、支行名称、银行地图URL、虚拟卡费用
     */
    @Override
    public Result<CreateVCVO> getVirtualCard(RepayInfo repayInfo) {
        return loanService.getVirtualCard(repayInfo);
    }

    /**
     * 虚拟卡还款回调接口
     *
     * @param param
     * @return
     */
    @Override
    public Result vcCallBack(FunpayCallBackParam param) {
        return loanService.vcCallBack(param);
    }
}
