package com.sup.common.service;

import com.sup.common.bean.paycenter.*;
import com.sup.common.bean.paycenter.vo.*;
import com.sup.common.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/9/10 21:45
 * <p>
 * 大致流程描述
 * <p>
 * 放款
 * 1.先查询银行列表(如果前端写死列表了 可以不查)
 * 2.银行卡信息鉴权(在哪步鉴权都可以 建议是提交银行卡信息的时候就可以顺便鉴权)
 * 3.调用放款接口 得到tradeNo
 * 4.等待异步回调 得到放款结果
 * 5.假如有意外情况 可以通过tradeNo调用放款状态查询接口
 * <p>
 * 还款
 * 1.调用还款接口 得到交易码和tradeNo
 * 2.用户在线下通过交易码刷pos还款
 * 3.等待回调 得到还款结果
 * 4.假如有意外情况 可以通过tradeNo调用还款状态查询接口
 */

@FeignClient(value = "service-paycenter")
public interface PayCenterService {

    /**
     * 获取银行或者支行信息
     *
     * @param bankNo 填了这参数会查询对应银行的支行信息
     * @return
     */
    @GetMapping(value = "/getBankList")
    Result<List<BankInfoVO>> getBankList(@RequestParam(value = "bankNo") String bankNo);

    /**
     * 银行卡鉴权
     *
     * @param bankInfo
     * @return
     */
    @PostMapping(value = "/verifyBankInfo")
    Result<VerifyVO> verifyBankInfo(@Valid @RequestBody BankInfo bankInfo);

    /**
     * 放款
     *
     * @param payInfo
     * @return
     */
    @PostMapping(value = "/pay")
    Result<PayVO> pay(@Valid @RequestBody PayInfo payInfo);

    /**
     * 放款状态查询
     *
     * @param payStatusInfo
     * @return
     */
    @PostMapping(value = "/payStatus")
    Result<PayStatusVO> payStatus(@Valid @RequestBody PayStatusInfo payStatusInfo);

    /**
     * 还款
     *
     * @param repayInfo
     * @return
     */
    @PostMapping(value = "/repay")
    Result<RepayVO> repay(@Valid @RequestBody RepayInfo repayInfo);

    /**
     * 还款状态查询
     *
     * @param repayStatusInfo
     * @return
     */
    @PostMapping(value = "/repayStatus")
    Result<RepayStatusVO> repayStatus(@Valid @RequestBody RepayStatusInfo repayStatusInfo);

    /**
     * 申请虚拟卡
     * @param info
     * @return
     */
    @PostMapping(value = "createVC")
    Result<CreateVCVO> createVC(@RequestBody CreateVCInfo info);

    /**
     * 更新虚拟卡
     * @param info
     * @return
     */
    @PostMapping(value = "updateVC")
    public Result<CreateVCVO> updateVC(@RequestBody UpdateVCInfo info);

    /**
     * 取消虚拟卡
     * @param info
     * @return
     */
    @PostMapping(value = "destroyVC")
    Result destroyVC(@RequestBody DestroyVCInfo info);

}