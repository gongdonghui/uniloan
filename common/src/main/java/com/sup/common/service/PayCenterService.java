package com.sup.common.service;

import com.sup.common.bean.paycenter.BankInfo;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.vo.BankInfoVO;
import com.sup.common.bean.paycenter.vo.VerifyVO;
import com.sup.common.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: kouichi
 * @Date: 2019/9/10 21:45
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
    Result pay(@Valid @RequestBody PayInfo payInfo);

    /**
     * 还款
     *
     * @param repayInfo
     * @return
     */
    @PostMapping(value = "/repay")
    String repay(@Valid @RequestBody RepayInfo repayInfo);

}
