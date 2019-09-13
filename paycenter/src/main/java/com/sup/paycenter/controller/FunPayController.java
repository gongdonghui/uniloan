package com.sup.paycenter.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.sup.common.bean.paycenter.BankInfo;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import com.sup.common.bean.paycenter.vo.BankInfoVO;
import com.sup.common.bean.paycenter.vo.VerifyVO;
import com.sup.common.util.Result;
import com.sup.paycenter.bean.funpay.BankListBean;
import com.sup.paycenter.bean.funpay.ReturnBean;
import com.sup.paycenter.bean.funpay.VerifyBankInfoBean;
import com.sup.paycenter.util.FunPayParamsUtil;
import com.sup.paycenter.util.GsonUtil;
import com.sup.paycenter.util.OkBang;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 21:35
 */
@RestController
@RequestMapping("/")
public class FunPayController {

    @Value("${paycenter.secretKey}")
    private String secretKey;
    @Value("${paycenter.}")
    private String merchantId;
    @Value("${paycenter.merchantId}")
    private String businessId;
    @Value("${paycenter.businessId}")
    private String version;

    @Value("${paycenter.url}")
    private String funPayUrl;
    @Value("${paycenter.method.getBankList}")
    private String method_getBankList;
    @Value("${paycenter.method.verifyBankInfo}")
    private String method_verifyBankInfo;
    @Value("${paycenter.method.transferMoney}")
    private String method_transferMoney;

    private static final int FUNPAY_SUCCESS_FLAG = 10000;

    @GetMapping(value = "getBankList")
    public Result<BankInfoVO> getBankList(@RequestParam(value = "bankNo") String bankNo) {
        Map m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis());
        if (Strings.isNullOrEmpty(bankNo)) {
            m.put("bankID", "0");
        } else {
            m.put("bankID", bankNo);
        }
        m.put("version", version);
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_getBankList + "?param=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        ReturnBean<BankListBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<BankListBean>>() {
        }.getType());
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        List<BankInfoVO> l = Lists.newArrayList();
        resultBean.getResult().getBankList().stream()
                .filter(x -> x.getStatus() == 1)
                .forEach(x -> {
                    BankInfoVO i = new BankInfoVO();
                    i.setBankName(x.getBankName());
                    i.setAbbreviation(x.getAbbreviation());
                    i.setBankNo(x.getBankID() + "");
                    i.setVersion(x.getVersion());
                    i.setAccountSupport(x.getAccountSupport());
                    i.setCardSupport(x.getCardSupport());
                    l.add(i);
                });
        return Result.succ(l);
    }

    @PostMapping(value = "verifyBankInfo")
    public Result<VerifyVO> verifyBankInfo(@Valid BankInfo bankInfo) {
        Map m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis());
        m.put("version", version);
        m.put("bankNo", bankInfo.getBankNo());
        m.put("bankLocation", "vn");
        m.put("accountNo", bankInfo.getAccountNo());
        m.put("accountType", bankInfo.getAccountType());
        m.put("accountName", bankInfo.getAccountName());
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_verifyBankInfo + "?param=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        ReturnBean<VerifyBankInfoBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<VerifyBankInfoBean>>() {
        }.getType());
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        VerifyVO v = new VerifyVO();
        v.setRealName(resultBean.getResult().getAccountName());
        v.setVerify(resultBean.getResult().getVerify());
        v.setStatus(resultBean.getResult().getStatus());
        return Result.succ(v);
    }

    @PostMapping(value = "pay")
    public Result pay(@Valid PayInfo payInfo) {
        Map m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis());
        m.put("version", version);
        m.put("clientID", payInfo.getUserId());
        //todo  单位转换
        m.put("amount", payInfo.getAmount());
        m.put("currency", "VND");
        m.put("orderNo", payInfo.getApplyId());
        m.put("bankLocation", "vn");
        m.put("accountNo", payInfo.getAccountNo());
        m.put("accountType", payInfo.getAccountType());
        m.put("accountName", payInfo.getAccountName());
        m.put("bankNo", payInfo.getBankNo());
        m.put("bankBranchNo", "");
        m.put("remark", payInfo.getRemark());
        m.put("transferTime", payInfo.getTransferTime());
        m.put("isAsync", "1");
        //todo
        m.put("returnUrl", "xxxxxxxx");
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_transferMoney + "?param=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }

        //todo 放款流程设计
        //  1.查询银行列表
        //  2.银行卡信息鉴权
        //  3.调用放款接口 得到交易id (放弃回调功能 这样可以不用mq 减少复杂度)
        //  4.定时轮询查交易状态
        //  5.根据查询到的结果进行相应的后续处理
        return null;
    }

    @PostMapping(value = "repay")
    public String repay(@Valid RepayInfo repayInfo) {
        return null;
    }

}
