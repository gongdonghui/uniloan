package com.sup.backend.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by xidongzhou1 on 2019/10/17.
 */
@Component
public class ApplicationContextHelper implements ApplicationContextAware {
  private static ApplicationContext applicationContext;

  public ApplicationContextHelper() {
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    ApplicationContextHelper.applicationContext = applicationContext;
  }

  public static<T> T getBean(Class<T> needType) {
    return (T)applicationContext.getBean(needType);
  }

}
