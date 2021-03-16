package com.sup.paycenter.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.sup.common.bean.paycenter.*;
import com.sup.common.bean.paycenter.vo.*;
import com.sup.common.param.FunpayCallBackParam;
import com.sup.common.service.CoreService;
import com.sup.common.util.Result;
import com.sup.paycenter.bean.funpay.*;
import com.sup.paycenter.util.DateUtil;
import com.sup.paycenter.util.FunPayParamsUtil;
import com.sup.paycenter.util.GsonUtil;
import com.sup.paycenter.util.OkBang;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class FunPayController {

    @Value("${paycenter.secretKey}")
    private String secretKey;
    @Value("${paycenter.merchantId}")
    private String merchantId;
    @Value("${paycenter.businessId}")
    private String businessId;
    @Value("${paycenter.version}")
    private String version;
    @Value("${paycenter.feeId}")
    private String feeId;
    @Value("${paycenter.payReturnUrl}")
    private String payReturnUrl;
    @Value("${paycenter.repayReturnUrl}")
    private String repayReturnUrl;
    @Value("${paycenter.vcReturnUrl}")
    private String vcReturnUrl;
    @Value("${paycenter.bankType}")
    private String bankType;    // VTBreg

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
    @Value("${paycenter.method.createVC}")
    private String method_createVC;
    @Value("${paycenter.method.updateVC}")
    private String method_updateVC;
    @Value("${paycenter.method.destroyVC}")
    private String method_destroyVC;

    @Autowired
    private CoreService coreService;

    private static final int FUNPAY_SUCCESS_FLAG = 10000;

    @GetMapping(value = "getBankList")
    public Result<List<BankInfoVO>> getBankList(@RequestParam(value = "bankNo") String bankNo) {
        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis() + "");
        if (Strings.isNullOrEmpty(bankNo)) {
            m.put("bankID", "0");
        } else {
            m.put("bankID", bankNo);
        }
        m.put("version", version);
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_getBankList + "?param=" + param);
        log.info("getBankList:::result=" + result + ", param=" + param);
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
                .filter(x -> x.getStatus() == 0)
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
    public Result<VerifyVO> verifyBankInfo(@RequestBody @Valid BankInfo bankInfo) {
        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis() + "");
        m.put("version", version);
        m.put("bankNo", bankInfo.getBankNo());
        m.put("bankLocation", "vn");
        m.put("accountNo", bankInfo.getAccountNo());
        m.put("accountType", bankInfo.getAccountType() + "");
        m.put("accountName", bankInfo.getAccountName());
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_verifyBankInfo + "?param=" + param);
        log.info("verifyBankInfo:::result=" + result + ", param=" + param);
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
    public Result<PayVO> pay(@RequestBody @Valid PayInfo payInfo) {
        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("clientID", payInfo.getUserId());
        m.put("timestamp", System.currentTimeMillis() + "");
        m.put("amount", payInfo.getAmount() + "");
        m.put("currency", "VND");
        m.put("orderNo", payInfo.getOrderNo());
        m.put("returnUrl", payReturnUrl);
        m.put("accountName", payInfo.getAccountName());
        m.put("accountNo", payInfo.getAccountNo());
        m.put("accountType", payInfo.getAccountType() + "");
        m.put("bankLocation", "vn");
        m.put("bankNo", payInfo.getBankNo());
        m.put("remark", payInfo.getRemark());
        m.put("phoneNumber", payInfo.getPhone());
        m.put("IDNo", payInfo.getCidNo());
        m.put("transferTime", payInfo.getTransferTime());
        m.put("version", version);
        m.put("isAsync", "1");
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_transferMoney + "?param=" + param);
        log.info("pay:::result=" + result + ", param=" + param);
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
    public Result<RepayVO> repay(@RequestBody @Valid RepayInfo repayInfo) {
        DateTime dt = new DateTime();
        dt = dt.plusDays(7);

        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("feeID", feeId);
        m.put("clientID", repayInfo.getUserId());
        m.put("timestamp", System.currentTimeMillis() + "");
        m.put("amount", repayInfo.getAmount() + "");
        m.put("currency", "VND");
        m.put("name", "testpay");
        m.put("orderNo", repayInfo.getOrderNo());
        //还款码有效期为T+7
        m.put("expireDate", DateUtil.format(dt.toDate(), DateUtil.NO_SPLIT_FORMAT));
        m.put("returnUrl", repayReturnUrl);
        m.put("version", version);
        m.put("purchaseType", "2");
        m.put("phoneNumber", repayInfo.getPhone());
        m.put("userName", repayInfo.getName());
        m.put("IDNo", repayInfo.getCidNo());

        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_offlinePay + "?param=" + param);
        log.info("repay:::result=" + result + ", param=" + param);
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
        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis() + "");
        m.put("version", version);
        m.put("tradeNo", payStatusInfo.getTradeNo());
        m.put("orderNo", payStatusInfo.getOrderNo());
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_transferCheck + "?param=" + param);
        log.info("payStatus:::result=" + result + ", param=" + param);
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
        p.setAmount(resultBean.getResult().getAmount());
        return Result.succ(p);
    }

    @PostMapping(value = "repayStatus")
    public Result<RepayStatusVO> repayStatus(@Valid @RequestBody RepayStatusInfo repayStatusInfo) {
        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis() + "");
        m.put("version", version);
        m.put("tradeNo", repayStatusInfo.getTradeNo());
        m.put("orderNo", repayStatusInfo.getOrderNo());
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_payCheck + "?param=" + param);
        log.info("repayStatus:::result=" + result + ", param=" + param);
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
        r.setAmount(resultBean.getResult().getAmount());
        return Result.succ(r);
    }

    @PostMapping(value = "payCallBack")
    public String payCallBack(@RequestBody ReturnBean<TransferMoneyBean> bean) {
        if (bean == null || bean.getResult() == null) {
            log.error("payCallBack: param is null!");
            return "";
        }
        log.info("payCallBack:::params=" + bean);
        FunpayCallBackParam f = new FunpayCallBackParam();
        f.setAmount(bean.getResult().getAmount());
        f.setOrderNo(bean.getResult().getOrderNo());
        f.setFinishTime(DateUtil.parse(bean.getResult().getSendTime(), DateUtil.NO_SPLIT_FORMAT));
        f.setStatus(bean.getResult().getStatus());
        f.setTradeNo(bean.getResult().getTradeNo());
        coreService.payCallBack(f);
        return "";
    }

    @PostMapping(value = "repayCallBack")
    public String repayCallBack(@RequestBody ReturnBean<OfflinePayBean> bean) {
        if (bean == null || bean.getResult() == null) {
            log.error("repayCallBack: param is null!");
            return "";
        }
        log.info("repayCallBack:::params=" + bean);
        FunpayCallBackParam f = new FunpayCallBackParam();
        f.setAmount(bean.getResult().getAmount());
        f.setOrderNo(bean.getResult().getOrderNo());
        f.setFinishTime(DateUtil.parse(bean.getResult().getPurchaseTime(), DateUtil.NO_SPLIT_FORMAT));
        f.setStatus(bean.getResult().getStatus());
        f.setTradeNo(bean.getResult().getTradeNo());
        coreService.repayCallBack(f);
        return "";
    }

    @PostMapping(value = "createVC")
    public Result<CreateVCVO> createVC(@RequestBody CreateVCInfo info) {
        DateTime expDate = new DateTime().plusDays(7);

        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("feeID", feeId);
        m.put("clientID", info.getUserId());
        m.put("timestamp", System.currentTimeMillis() + "");
        m.put("version", version);
        m.put("amount", info.getAmount() + "");
        m.put("currency", "VND");
        m.put("orderNo", info.getOrderNo());
        m.put("expireDate", DateUtil.format(expDate.toDate(), DateUtil.NS_DAY_ALL_NUM_FORMAT));

        m.put("returnUrl", vcReturnUrl);
        m.put("bankType", bankType);

        m.put("accountBase", info.getMobile());
        m.put("userName", info.getUserName() + "");
        m.put("phoneNumber", info.getMobile());
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_createVC + "?param=" + param);
        log.info("createVC:::result=" + result + ", param=" + GsonUtil.toJson(m)
                + ", param(funpay)=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        ReturnBean<CreateVCBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<CreateVCBean>>() {
        }.getType());
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        CreateVCVO c = new CreateVCVO();
        c.setAccountNo(resultBean.getResult().getAccountNo());
        c.setAccountName(resultBean.getResult().getAccountName());
        c.setBankLink(resultBean.getResult().getBankLink());
        c.setBankName(resultBean.getResult().getBankName());
        c.setBranchBankName(resultBean.getResult().getBranchBankName());
        c.setServiceFee(resultBean.getResult().getServiceFee());
        c.setExpireDate(DateUtil.parse(resultBean.getResult().getExpireDate(), DateUtil.NS_DAY_ALL_NUM_FORMAT));
        return Result.succ(c);
    }

    @PostMapping(value = "vcCallBack")
    public String vcCallBack(@RequestBody ReturnBean<VCBackBean> bean) {
        if (bean == null || bean.getCode() != FUNPAY_SUCCESS_FLAG || bean.getResult() == null) {
            log.error("vcCallBack: param is null!");
            return "";
        }
        log.info("vcCallBack:::params=" + bean);
        FunpayCallBackParam f = new FunpayCallBackParam();
        f.setAmount(bean.getResult().getPurchaseAmount());
        f.setOrderNo(bean.getResult().getOrderNo());
        f.setFinishTime(DateUtil.parse(bean.getResult().getPurchaseTime(), DateUtil.NO_SPLIT_FORMAT));
        f.setStatus(0);
        f.setTradeNo(bean.getResult().getTradeNo());
        coreService.vcCallBack(f);
        return "";
    }

    @PostMapping(value = "updateVC")
    public Result<CreateVCVO> updateVC(@RequestBody UpdateVCInfo info) {
        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("feeID", feeId);
        m.put("clientID", info.getUserId());
        m.put("timestamp", System.currentTimeMillis() + "");
        m.put("amount", info.getAmount() + "");
        m.put("currency", "VND");
        m.put("orderNo", info.getOrderNo());
        m.put("bankType", bankType);
        m.put("accountNo", info.getAccountNo());
        m.put("version", version);
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_updateVC + "?param=" + param);
        log.info("updateVC:::result=" + result + ", param=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        ReturnBean<CreateVCBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<CreateVCBean>>() {
        }.getType());
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        CreateVCVO c = new CreateVCVO();
        c.setAccountNo(resultBean.getResult().getAccountNo());
        c.setAccountName(resultBean.getResult().getAccountName());
        c.setBankLink(resultBean.getResult().getBankLink());
        c.setBankName(resultBean.getResult().getBankName());
        c.setBranchBankName(resultBean.getResult().getBranchBankName());
        c.setServiceFee(resultBean.getResult().getServiceFee());
        c.setExpireDate(DateUtil.parse(resultBean.getResult().getExpireDate(), DateUtil.NS_DAY_ALL_NUM_FORMAT));
        return Result.succ(c);
    }

    @PostMapping(value = "destroyVC")
    public Result destroyVC(@RequestBody DestroyVCInfo info) {
        Map<String, String> m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("feeID", feeId);
        m.put("timestamp", System.currentTimeMillis() + "");
        m.put("orderNo", info.getOrderNo());
        m.put("bankType", bankType);
        m.put("version", version);
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_destroyVC + "?param=" + param);
        log.info("destroyVC:::result=" + result + ", param=" + GsonUtil.toJson(m) +  ", param(funpay)=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        ReturnBean resultBean = GsonUtil.fromJson(result, ReturnBean.class);
        if (resultBean.getCode() != FUNPAY_SUCCESS_FLAG) {
            return Result.fail(Result.kError, "外部服务异常");
        }
        return Result.succ();
    }

}
