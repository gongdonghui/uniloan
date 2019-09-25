package com.sup.backend.bean;

import lombok.Data;

import java.util.List;

/**
 * Project:uniloan
 * Class:  TbApplyInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
public class AppSdkContactInfo {
  @Data
  public static class SingleItem {
    private String name;
    private List<String> mobiles;
  }
  private String device_id;
  private String mobile;
  private List<SingleItem> contacts;
}
