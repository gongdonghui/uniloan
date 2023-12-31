package com.sup.backend.util;

import com.alibaba.fastjson.JSON;
import com.sup.common.util.Result;
import net.coobird.thumbnailator.Thumbnails;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import org.springframework.web.context.request.async.DeferredResult;

import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

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

  public static String formatRate(Float rate) {
    DecimalFormat df = new DecimalFormat("0.00%");
    return df.format(rate);
  }

  public static Date NormTime(String p) {
    synchronized (sdf) {
      try {
        Date d = sdf.parse(p);
        if (Integer.valueOf(p.substring(0, p.indexOf('-'))) > 2020) {
          d = new Date(d.getTime()/1000l);
        }
        return d;
      } catch (Exception e) {
        return null;
      }
    }
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

  public static void AsyncHttpPostJson(String url, Object params, DeferredResult<Object> ret, Function<Response, Object> post_handler) {
    HttpClient.httpPostJsonAsync(url, JSON.toJSONString(params), new AsyncCompletionHandler<Response>() {
      @Override
      public Response onCompleted(Response response) throws Exception {
        if (post_handler != null) {
          ret.setResult(post_handler.apply(response));
        } else {
          ret.setResult(Result.succ("ok"));
        }
        return response;
      }
      @Override
      public void onThrowable(Throwable t) {
        System.out.println(ToolUtils.GetTrace(t));
        ret.setResult(Result.fail("time_out"));
      }
    });
  }

  public static byte[] getThumbnail(byte[] org, int w, int h) throws Exception {
    if (org.length < 128*1024) {
      return null;
    }
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ByteArrayInputStream bin = new ByteArrayInputStream(org);
    Thumbnails.of(bin).size(w, h).toOutputStream((bout));
    byte[] binary_thumbnail = bout.toByteArray();
    return binary_thumbnail;
  }

  public static String GetTrace(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    return sw.toString();
  }

  public static<T> Result<T> succ(T  data) {
    return Result.succ(data).setMessage(MessageUtils.get("succ"));
  }

  public static<T> Result<T> succ(T data, String key) {
    return Result.succ(data).setMessage(MessageUtils.get(key));
  }

  public static Result<String> fail(String key) {
    return Result.fail(MessageUtils.get(key));
  }

  public static Result<String> fail(Integer status, String key) {
    return Result.fail(status, MessageUtils.get(key));
  }
}
