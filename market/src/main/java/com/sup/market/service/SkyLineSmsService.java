package com.sup.market.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sup.market.util.HttpClient;
import com.sup.market.util.ToolUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
public class   SkyLineSmsService {
  public static Logger logger = Logger.getLogger(SkyLineSmsService.class);

  @Value("${sms.skyline.uri}")
  private String uri;

  @Value("${sms.skyline.account}")
  private String account;

  @Value("${sms.skyline.password}")
  private String password;

  @Value("${sms.skyline.vip-account}")
  private String vip_account;

  @Value("${sms.skyline.vip-password}")
  private String vip_password;


  public String genSign(String account, String password, String time_str) {
    return ToolUtils.Md5Sum(String.format("%s%s%s", account, password, time_str));
  }

  public boolean send(List<String> mobiles, String content, String account, String password, String senderid) throws Exception {
    Map<String, String> params = new HashMap<>();
    String time_str = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
    StringBuffer sb = new StringBuffer();
    sb.append(uri)
      .append("?account=").append(account)
      .append("&sign=").append(genSign(account, password, time_str))
      .append("&numbers=").append(String.join(",", mobiles))
      .append("&datetime=").append(time_str)
      .append("&content=").append(URLEncoder.encode(content, "UTF-8"));
    if (StringUtils.isNotEmpty(senderid)) {
      sb.append("&senderid=").append("Verify");
    }
    String url = sb.toString();
    logger.info("sms_to_send => " + url);
    String resp = HttpClient.httpGet(url, null);
    JSONObject resp_obj = JSON.parseObject(resp);
    logger.info("resp => " + JSON.toJSONString(resp_obj));
    return (resp_obj.getIntValue("status") == 0);
  }

  public boolean SendSmsVip(List<String> mobiles, String content) throws Exception {
    return send(mobiles, content, vip_account, vip_password, "Verify");
  }

  public boolean SendSms(List<String> mobiles, String content) throws Exception {
    return send(mobiles, content, account, password, "");
  }
}
