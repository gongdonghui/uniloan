package com.sup.backend.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.sup.backend.core.AuthorityInterceptor;
import com.sup.backend.core.LoginInfoArgumentResolver;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Configuration
public class WebConfigurer implements WebMvcConfigurer {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    InterceptorRegistration inter = registry.addInterceptor(AuthorityInterceptor());
    inter.addPathPatterns("/**");
    inter.excludePathPatterns("/user/login**");
    inter.excludePathPatterns("/file/upload**");
    inter.excludePathPatterns("/file/image/get**");
    inter.excludePathPatterns("/product/list**");
    inter.excludePathPatterns("/install/report**");
  }

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer config) {
    config.setTaskExecutor(MvcTaskExecutor());
    config.setDefaultTimeout(30_000);
  }

  @Bean
  public ThreadPoolTaskExecutor MvcTaskExecutor() {
    ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
    exec.setThreadNamePrefix("mvc-task-cus-");
    exec.setMaxPoolSize(20);
    return exec;
  }

  @Bean
  public HttpMessageConverters fastJsonConverter() {
    FastJsonHttpMessageConverter fast_converter = new FastJsonHttpMessageConverter();
    FastJsonConfig cfg = new FastJsonConfig();
    cfg.setSerializerFeatures(SerializerFeature.PrettyFormat);
    fast_converter.setFastJsonConfig(cfg);
    HttpMessageConverter<?> converter = fast_converter;
    return new HttpMessageConverters(converter);
  }

  @Bean
  public AuthorityInterceptor AuthorityInterceptor() {
    return new AuthorityInterceptor();
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolverList) {
    argumentResolverList.add(LoginInfoArgumentResolver());
  }

  @Bean
  public LoginInfoArgumentResolver LoginInfoArgumentResolver() {
    return new LoginInfoArgumentResolver();
  }
}
