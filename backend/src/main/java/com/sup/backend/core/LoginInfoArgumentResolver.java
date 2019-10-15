package com.sup.backend.core;

import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Component
public class LoginInfoArgumentResolver implements HandlerMethodArgumentResolver {
  public static Logger logger = Logger.getLogger(LoginInfoArgumentResolver.class);

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    AnnotatedElement annotatedElement = methodParameter.getAnnotatedElement();
    return methodParameter.hasParameterAnnotation(LoginInfo.class);
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
    HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
    return request.getAttribute("login_info");
  }
}
