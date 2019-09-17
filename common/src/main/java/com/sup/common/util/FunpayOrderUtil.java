package com.sup.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Project:uniloan
 * Class:  FunpayOrderUtil
 * <p>
 * Author: guanfeng
 * Create: 2019-09-17
 */


public class FunpayOrderUtil {
    public enum Status {
        SUCCESS,
        PROCESSING,
        ERROR
    }
    private static final Map<Integer, String> codeMap = new HashMap<>();
    static {
        // funpay订单状态码表
        codeMap.put(0, "交易成功");
        codeMap.put(1, "银行拒绝交易（账户或者银行卡被锁定）");
        codeMap.put(3, "银行卡过期");
        codeMap.put(4, "不符合限定条件（OTP错误、有限期不对、CVC、CVV错误）");
        codeMap.put(5, "银行无响应");
        codeMap.put(6, "与银行通讯失败");
        codeMap.put(7, "资金不足");
        codeMap.put(8, "检验码错误");
        codeMap.put(9, "银行账户/卡号、姓名或者交易类型核对不通过");
        codeMap.put(10, "其他未定义错误");
        codeMap.put(11, "卡校验成功");
        codeMap.put(12, "交易额超出限制");
        codeMap.put(13, "用户未开通网银，请用户与发卡银行联系");
        codeMap.put(14, "OTP错误");
        codeMap.put(15, "密码错误");
        codeMap.put(16, "银行账户与持有者不对应，打款和线上支付有效");
        codeMap.put(17, "卡号错误");
        codeMap.put(18, "卡的签发日期输入错误");
        codeMap.put(19, "卡的有效日期输入错误");
        codeMap.put(20, "交易失败");
        codeMap.put(21, "OTP超时");
        codeMap.put(22, "交易校验失败，请检查集成文档");
        codeMap.put(23, "卡不被允许支付");
        codeMap.put(24, "卡交易额超过限定");
        codeMap.put(25, "卡交易额超过限定");
        codeMap.put(26, "交易处理中，如等待用户支付或者等待银行处理打款");
        codeMap.put(27, "身份验证信息错误");
        codeMap.put(28, "交易日期超过限定");
        codeMap.put(29, "交易失败，需要与银行确认原因");
        codeMap.put(30, "交易失败，交易额小于限定");
        codeMap.put(31, "未找到订单");
        codeMap.put(32, "非支付用的订单");
        codeMap.put(33, "订单重复");
        codeMap.put(34, "打款交易失败");
        codeMap.put(35, "订单已过期");
    }

    public static Status getStatus(Integer status) {
        switch (status) {
            case 0:
                return Status.SUCCESS;
            case 26:
                return Status.PROCESSING;
            default:
                return Status.ERROR;
        }
    }

    public static String getMessage(Integer status) {
        return codeMap.getOrDefault(status, "");
    }
}
