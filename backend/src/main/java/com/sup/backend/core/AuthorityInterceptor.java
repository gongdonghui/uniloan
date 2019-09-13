package com.sup.backend.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.service.RedisClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter {
  public static Logger logger = Logger.getLogger(AuthorityInterceptor.class);
  @Autowired
  RedisClient rc;


  public static void WriteResult(HttpServletResponse resp, Result ret) throws Exception {
    resp.setContentType("application/json;charset=utf-8");
    resp.getWriter().print(JSON.toJSONString(ret, SerializerFeature.WriteMapNullValue));
    resp.getWriter().close();
    resp.flushBuffer();
    return;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse resp, Object handler) throws Exception {
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    Method method = handlerMethod.getMethod();
    LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
    if (methodAnnotation != null) {
      String token = request.getHeader("token");
      if (StringUtils.isEmpty(token)) {
        token = request.getParameter("token");
      }

      if (StringUtils.isEmpty(token)) {
        logger.warn("invalid request, token is null");
        WriteResult(resp, Result.fail("no_token"));
        return false;
      }

      String login_info_str = rc.Get(token);
      if (StringUtils.isEmpty(login_info_str)) {
        WriteResult(resp, Result.fail("expire_token"));
        return false;
      }

      LoginInfoCtx login_info = JSON.parseObject(rc.Get(token), LoginInfoCtx.class);
      request.setAttribute("login_info", login_info);
      return true;
    }
    return true;
  }
}
