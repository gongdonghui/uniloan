package com.sup.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Configuration
public class LocaleConfig {
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    localeResolver.setDefaultLocale(Locale.getDefault());
    return localeResolver;
  }

  public static class HeaderLocaleInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws ServletException {
      LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
      String lang = request.getHeader("lang");
      if (StringUtils.isEmpty(lang)) {
        lang = "en";
      }

      if (lang.startsWith("vi")) {
        lang = "vi_VN";
      } else if (lang.startsWith("zh")) {
        lang = "zh_CN";
      } else {
        lang = "en_US";
      }
      localeResolver.setLocale(request, response, StringUtils.parseLocale(lang));

      return true;
    }
  };

  @Bean
  public WebMvcConfigurer localeInterceptor() {
    return new WebMvcConfigurer() {
      @Override
      public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HeaderLocaleInterceptor());
      }
    };
  }
}
