package com.sup.paycenter.facade;

import com.sup.paycenter.bean.BankInfo;
import com.sup.paycenter.bean.PayInfo;
import com.sup.paycenter.bean.RepayInfo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: kouichi
 * @Date: 2019/9/10 21:45
 */
@RequestMapping(value = "/paycenter")
public interface FunPayFacade {

    /**
     * 获取银行或者支行信息
     *
     * @param BankNo 填了这参数会查询对应银行的支行信息
     * @return
     */
    @GetMapping(value = "/getBankList")
    String getBankList(@RequestParam(value = "BankNo", required = false) String BankNo);

    /**
     * 银行卡鉴权
     *
     * @param bankInfo
     * @return
     */
    @PostMapping(value = "/verifyBankInfo")
    String verifyBankInfo(@Valid @RequestBody BankInfo bankInfo);

    /**
     * 放款
     *
     * @param payInfo
     * @return
     */
    @PostMapping(value = "/pay")
    String pay(@Valid @RequestBody PayInfo payInfo);

    /**
     * 还款
     *
     * @param repayInfo
     * @return
     */
    @PostMapping(value = "/repay")
    String repay(@Valid @RequestBody RepayInfo repayInfo);

}