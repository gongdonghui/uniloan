package com.sup.backend.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Created by xidongzhou1 on 2019/10/10.
 */
@Configuration
public class FeignClientConfig {
  @Bean
  public RequestInterceptor headInterceptor() {
    return requestTemplate -> {
      String lang = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("lang");
      requestTemplate.header("lang", lang);
    };
  }
}
