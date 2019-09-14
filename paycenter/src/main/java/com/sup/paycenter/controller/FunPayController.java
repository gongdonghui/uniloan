package com.sup.paycenter.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.sup.common.bean.paycenter.*;
import com.sup.common.bean.paycenter.vo.*;
import com.sup.common.util.Result;
import com.sup.paycenter.bean.funpay.*;
import com.sup.paycenter.util.DateUtil;
import com.sup.paycenter.util.FunPayParamsUtil;
import com.sup.paycenter.util.GsonUtil;
import com.sup.paycenter.util.OkBang;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
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
    @Value("${paycenter.feeId}")
    private String feeId;

    @Value("${paycenter.url}")
    private String funPayUrl;
    @Value("${paycenter.method.getBankList}")
    private String method_getBankList;
    @Value("${paycenter.method.verifyBankInfo}")
    private String method_verifyBankInfo;
    @Value("${paycenter.method.transferMoney}")
    private String method_transferMoney;
    @Value("${paycenter.method.offlinePay}")
    private String method_offlinePay;
    @Value("${paycenter.method.transferCheck}")
    private String method_transferCheck;
    @Value("${paycenter.method.payCheck}")
    private String method_payCheck;


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
    public Result<PayVO> pay(@Valid PayInfo payInfo) {
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
        ReturnBean<TransferMoneyBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<TransferMoneyBean>>() {
        }.getType());
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        PayVO p = new PayVO();
        p.setTradeNo(resultBean.getResult().getTradeNo());
        return Result.succ(p);
    }

    @PostMapping(value = "repay")
    public Result<RepayVO> repay(@Valid RepayInfo repayInfo) {
        Map m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("feeID", feeId);
        m.put("timestamp", System.currentTimeMillis());
        m.put("version", version);
        m.put("clientID", repayInfo.getUserId());
        //todo 单位转换
        m.put("amount", repayInfo.getAmount());
        //todo 不知道计费点名称哪里来
        m.put("name", "xxx");
        m.put("orderNo", repayInfo.getApplyId());
        //还款码有效期为T+7
        DateTime dt = new DateTime();
        dt.plusDays(7);
        m.put("expireDate", DateUtil.format(dt.toDate(), DateUtil.NO_SPLIT_FORMAT));
        m.put("returnUrl", "xxx");
        m.put("purchaseType", "2");
        m.put("phoneNumber", repayInfo.getPhone());
        m.put("userName", repayInfo.getName());
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_offlinePay + "?param=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        ReturnBean<OfflinePayBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<OfflinePayBean>>() {
        }.getType());
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        RepayVO r = new RepayVO();
        r.setCode(resultBean.getResult().getCode());
        r.setShopLink(resultBean.getResult().getShopLink());
        r.setTradeNo(resultBean.getResult().getTradeNo());
        r.setExpireDate(DateUtil.formatDateTime(dt.toDate()));
        return Result.succ(r);
    }

    @PostMapping(value = "payStatus")
    public Result<PayStatusVO> payStatus(@Valid @RequestBody PayStatusInfo payStatusInfo) {
        Map m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis());
        m.put("version", version);
        m.put("tradeNo", payStatusInfo.getTradeNo());
        m.put("orderNo", payStatusInfo.getApplyId());
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_transferCheck + "?param=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        ReturnBean<TransferMoneyBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<TransferMoneyBean>>() {
        }.getType());
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        PayStatusVO p = new PayStatusVO();
        p.setStatus(resultBean.getResult().getStatus());
        if (!Strings.isNullOrEmpty(resultBean.getResult().getSendTime())) {
            Date dt = DateUtil.parse(resultBean.getResult().getSendTime(), DateUtil.NO_SPLIT_FORMAT);
            p.setSendTime(DateUtil.formatDateTime(dt));
        }
        return Result.succ(p);
    }

    @PostMapping(value = "repayStatus")
    public Result<RepayStatusVO> repayStatus(@Valid @RequestBody RepayStatusInfo repayStatusInfo) {
        Map m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis());
        m.put("version", version);
        m.put("tradeNo", repayStatusInfo.getTradeNo());
        m.put("orderNo", repayStatusInfo.getApplyId());
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_payCheck + "?param=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        ReturnBean<OfflinePayBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<OfflinePayBean>>() {
        }.getType());
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        RepayStatusVO r = new RepayStatusVO();
        r.setStatus(resultBean.getResult().getStatus());
        if (!Strings.isNullOrEmpty(resultBean.getResult().getPurchaseTime())) {
            Date dt = DateUtil.parse(resultBean.getResult().getPurchaseTime(), DateUtil.NO_SPLIT_FORMAT);
            r.setPurchaseTime(DateUtil.formatDateTime(dt));
        }
        return Result.succ(r);
    }

}
