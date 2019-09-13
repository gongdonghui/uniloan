package com.sup.backend.config;

import com.sup.backend.core.AuthorityInterceptor;
import com.sup.backend.core.LoginInfoArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
