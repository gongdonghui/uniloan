package com.sup.paycenter.facade.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.sup.paycenter.bean.BankInfo;
import com.sup.paycenter.bean.PayInfo;
import com.sup.paycenter.bean.RepayInfo;
import com.sup.paycenter.bean.funpay.BankListBean;
import com.sup.paycenter.bean.funpay.ReturnBean;
import com.sup.paycenter.facade.FunPayFacade;
import com.sup.paycenter.util.FunPayParamsUtil;
import com.sup.paycenter.util.GsonUtil;
import com.sup.paycenter.util.OkBang;
import com.sup.paycenter.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * @Author: kouichi
 * @Date: 2019/9/11 21:35
 */
@RestController
public class FunPayFacadeImpl implements FunPayFacade {

    @Value("${}")
    private String secretKey;
    @Value("${}")
    private String merchantId;
    @Value("${}")
    private String businessId;
    @Value("${}")
    private String version;

    @Value("${}")
    private String funPayUrl;
    @Value("${}")
    private String method_getBankList;


    @Override
    public String getBankList(String BankNo) {
        Map m = Maps.newHashMap();
        m.put("merchantID", merchantId);
        m.put("businessID", businessId);
        m.put("timestamp", System.currentTimeMillis());
        if (Strings.isNullOrEmpty(BankNo)) {
            m.put("bankID", "0");
        } else {
            m.put("bankID", BankNo);
        }
        m.put("version", version);
        String param = FunPayParamsUtil.params4Get(m, secretKey);
        String result = OkBang.get(funPayUrl + method_getBankList + "?param=" + param);
        if (Strings.isNullOrEmpty(result)) {
            return "";
        }
        ReturnBean<BankListBean> resultBean = GsonUtil.fromJson(result, new TypeToken<ReturnBean<BankListBean>>() {
        }.getType());
        if (resultBean.getCode() != 10000) {
            return "";
        }
        resultBean.getResult().getBankList().stream();
        return ResponseUtil.success();
    }

    @Override
    public String verifyBankInfo(@Valid BankInfo bankInfo) {
        return null;
    }

    @Override
    public String pay(@Valid PayInfo payInfo) {
        return null;
    }

    @Override
    public String repay(@Valid RepayInfo repayInfo) {
        return null;
    }

}
