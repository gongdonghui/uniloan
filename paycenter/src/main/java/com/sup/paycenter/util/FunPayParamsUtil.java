package com.sup.paycenter.util;

import com.google.gson.Gson;
import org.springframework.util.DigestUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: kouichi
 * @Date: 2019/9/10 23:08
 */
public class FunPayParamsUtil {

    public static String params4Get(Map<String, String> m, String secretKey) {
        String sign = sign(m, secretKey);
        m.put("sign", sign);
        String s = map2String(m);
        return Base64.getUrlEncoder().encodeToString(s.getBytes());
    }

    public static String params4Post(Map<String, String> m, String secretKey) {
        String sign = sign(m, secretKey);
        m.put("sign", sign);
        return GsonUtil.toJson(m);
    }

    private static String sign(Map<String, String> m, String secretKey) {
        TreeMap<String, String> params = new TreeMap<>();
        m.forEach((k, v) -> params.put(k, v));
        String s = map2String(params);
        s = s + secretKey;
        return DigestUtils.md5DigestAsHex(s.getBytes()).toUpperCase();
    }

    private static String map2String(Map<String, String> m) {
        StringBuilder sb = new StringBuilder();
        for (String key : m.keySet()) {
            sb.append(key).append("=").append(m.get(key)).append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    public static void main(String[] args) {
        Map<String, String> m = new HashMap<>();
        String secretKey = "pUoZtgB8mtG7mLPmh9RY4XtHJN1e4cN0092IPOPi";
        m.put("merchantID", "1758");
        m.put("businessID", "506");
        // m.put("feeID", "486");
        m.put("timestamp", System.currentTimeMillis() + "");
        // m.put("orderNo", "49305");
        m.put("accountNo", "116002730866");
        m.put("version", "1.3");
        m.put("bankType", "VTB");
        m.put("pageSize", "10");
        String param = params4Get(m, secretKey);
        System.out.print(param);

    }
}
