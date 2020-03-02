package com.sup.backend.core;

import com.alibaba.fastjson.JSON;
import com.sup.backend.bean.LoginInfoCtx;
import com.sup.backend.service.RedisClient;
import com.sup.backend.util.ToolUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import static com.sup.common.util.Result.kTokenError;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Component
public class LoginInfoArgumentResolver implements HandlerMethodArgumentResolver {
  public static Logger logger = Logger.getLogger(LoginInfoArgumentResolver.class);

  @Autowired
  RedisClient rc;

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    AnnotatedElement annotatedElement = methodParameter.getAnnotatedElement();
    return methodParameter.hasParameterAnnotation(LoginInfo.class);
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
    HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
    Object r = request.getAttribute("login_info");
    if (r == null) {
      String token = request.getHeader("token");
      if (StringUtils.isEmpty(token)) {
        return null;
      }
      String login_info_str = rc.Get(token);
      if (StringUtils.isEmpty(login_info_str)) {
        return null;
      }
      r = JSON.parseObject(login_info_str, LoginInfoCtx.class);
    }
    return r;
  }
}
