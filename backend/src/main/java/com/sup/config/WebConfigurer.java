package com.sup.config;

import com.sup.core.AuthorityInterceptor;
import com.sup.core.LoginInfoArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
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
