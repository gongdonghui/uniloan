package com.sup.paycenter.controller;

import com.google.common.base.Strings;
import com.sup.common.bean.paycenter.BankInfo;
import com.sup.common.bean.paycenter.PayInfo;
import com.sup.common.bean.paycenter.RepayInfo;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 21:35
 */
@RestController
@RequestMapping("/")
public class FunPayController {

    // @Value("${}")
    private String secretKey;
    // @Value("${}")
    private String merchantId;
    //  @Value("${}")
    private String businessId;
    //  @Value("${}")
    private String version;

    //  @Value("${}")
    private String funPayUrl;
    //  @Value("${}")
    private String method_getBankList;


    @GetMapping(value = "getBankList")
    public String getBankList(@RequestParam(value = "BankNo", required = false) String BankNo) {
        if (Strings.isNullOrEmpty(BankNo)) {
            return "没有填哦";
        } else {
            return BankNo;
        }
//        Map m = Maps.newHashMap();
//        m.put("merchantID", merchantId);
//        m.put("businessID", businessId);
//        m.put("timestamp", System.currentTimeMillis());
//        if (Strings.isNullOrEmpty(BankNo)) {
//            m.put("bankID", "0");
//        } else {
//            m.put("bankID", BankNo);
//        }
//        m.put("version", version);
//        String param = FunPayParamsUtil.params4Get(m, secretKey);
//        String result = OkBang.get(funPayUrl + method_getBankList + "?param=" + param);
//        if (Strings.isNullOrEmpty(result)) {
//            return "";
//        }
//        ReturnBean<BankListBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<BankListBean>>() {
//        }.getType());
//        if (resultBean.getCode() != 10000) {
//            return "";
//        }
//        resultBean.getResult().getBankList().stream();
//        return ResponseUtil.success();
    }

    @PostMapping(value = "verifyBankInfo")
    public String verifyBankInfo(@Valid BankInfo bankInfo) {
        return null;
    }

    @PostMapping(value = "pay")
    public String pay(@Valid PayInfo payInfo) {
        return null;
    }

    @PostMapping(value = "repay")
    public String repay(@Valid RepayInfo repayInfo) {
        return null;
    }

}
