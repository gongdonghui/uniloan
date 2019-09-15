package com.sup.backend.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
public class ToolUtils {
  public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static String COMPACT_DATE_FORMAT = "yyyyMMddHHmmss";
  public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static String getToken() {
    UUID uuid = UUID.randomUUID();
    return uuid.toString().replace("-", "");
  }

  public static String NormTime(Date d) {
    synchronized (sdf) {
      if (d != null) {
        return sdf.format(d);
      }
      return "1900-01-01 00:00:00";
    }
  }

  public static String Md5Sum(String str) {
    try {
      if (str == null) {
        return "deadbeef";
      }
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(str.getBytes());
      return new BigInteger(1, md.digest()).toString(16);
    } catch (Exception e) {
      return "deadbeef";
    }
  }

  public static String GetTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }
}