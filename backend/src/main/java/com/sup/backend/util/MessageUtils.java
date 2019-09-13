package com.sup.backend.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Component
public class  MessageUtils {
  private static MessageSource messageSource;

  public MessageUtils(MessageSource messageSource) {
    MessageUtils.messageSource = messageSource;
  }

  public static String get(String key) {
    try {
      return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    } catch (Exception e) {
      return key;
    }
  }
}
