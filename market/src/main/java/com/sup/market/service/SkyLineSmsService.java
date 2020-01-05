package com.sup.market.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sup.market.bean.SkySmsResponse;
import com.sup.market.util.HttpClient;
import com.sup.market.util.ToolUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Component
public class   SkyLineSmsService {
  public static Logger logger = Logger.getLogger(SkyLineSmsService.class);

  @Value("${sms.skyline.uri}")
  private String uri;

  @Value("${sms.skyline.retry}")
  private Integer retry_count;

  @Value("${sms.skyline.mock}")
  private Boolean mock;

  @Value("${sms.skyline.account}")
  private String account;

  @Value("${sms.skyline.password}")
  private String password;

  @Value("${sms.skyline.vip-account}")
  private String vip_account;

  @Value("${sms.skyline.vip-password}")
  private String vip_password;

  @Value("${sms.skyline.area-code}")
  private String area_code;

  @Value("${sms.skyline.pattern}")
  private String pattern_str;

  public static Pattern pat = null;

  public String genSign(String account, String password, String time_str) {
    return ToolUtils.Md5Sum(String.format("%s%s%s", account, password, time_str));
  }

  public String toAreaNumber(String mobile) {
    if (pat == null) {
      synchronized (SkyLineSmsService.class) {
        if (pat == null) {
          System.out.println("pattern_str: " + pattern_str);
          pat = Pattern.compile(pattern_str);
        }
      }
    }

    Matcher m = pat.matcher(mobile);
    if (!m.find()) {
      return mobile;
    }

    return String.format("%s%s", area_code, m.group(m.groupCount()));
  }

  public boolean send(List<String> mobiles, String content, String account, String password, String senderid) throws Exception {
    if (mock) {
      return true;
    }

    List<String> norm_numbers = mobiles.stream().map(x -> toAreaNumber(x)).collect(Collectors.toList());

    Map<String, String> params = new HashMap<>();
    String time_str = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
    StringBuffer sb = new StringBuffer();
    sb.append(uri)
      .append("?account=").append(account)
      .append("&sign=").append(genSign(account, password, time_str))
      .append("&numbers=").append(String.join(",", norm_numbers))
      .append("&datetime=").append(time_str)
      .append("&content=").append(URLEncoder.encode(content, "UTF-8"));
    if (StringUtils.isNotEmpty(senderid)) {
      sb.append("&senderid=").append("Verify");
    }
    String url = sb.toString();
    logger.info("sms_to_send => " + url);
    String resp = HttpClient.httpGet(url, null);
    SkySmsResponse resp_obj = JSON.parseObject(resp, SkySmsResponse.class);
    logger.info("resp => " + JSON.toJSONString(resp_obj));
    return resp_obj.getStatus().equals(0);
  }

  public boolean send_retry(List<String> mobiles, String content, String account, String password, String senderid) throws Exception {
    int retry_most = (retry_count == null || retry_count == 0) ? 1: Math.max(1, retry_count);
    int retry = 1;
    boolean succ;
    while (true) {
      succ = send(mobiles, content, account, password, senderid);
      logger.info(String.format("try (%d) of (%d), result: (%b)", retry, retry_most, succ));
      if (succ) {
        break;
      }
      retry++;
      if (retry > retry_most) {
        break;
      }
      Thread.sleep(5*1000l);
    }
    return succ;
  }

  public boolean SendSmsVip(List<String> mobiles, String content) throws Exception {
    return send_retry(mobiles, content, vip_account, vip_password, "Verify");
  }

  public boolean SendSms(List<String> mobiles, String content) throws Exception {
    return send_retry(mobiles, content, account, password, "");
  }
}
