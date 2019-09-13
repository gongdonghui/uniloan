package com.sup.backend.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sup.backend.util.HttpClient;
import com.sup.backend.util.ToolUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Component
public class SkyLineSmsService {
  @Value("${sms.skyline.uri")
  private String uri;

  @Value("${sms.skyline.account")
  private String account;

  @Value("${sms.skyline.password")
  private String password;


  public String genSign(String account, String password, String time_str) {
    return ToolUtils.Md5Sum(String.format("%s%s%s", account, password, time_str));
  }

  public boolean SendSms(List<String> mobiles, String content) throws Exception {
    Map<String, String> params = new HashMap<>();
    String time_str = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
    params.put("account", account);
    params.put("sign", genSign(account, password, time_str));
    params.put("datetime", time_str);
    params.put("numbers", String.join(",", mobiles));
    params.put("content", URLEncoder.encode(content, "UTF-8"));
    String resp = HttpClient.httpGet(uri, params);
    JSONObject resp_obj = JSON.parseObject(resp);
    return (resp_obj.getIntValue("status") == 0);
  }
}
