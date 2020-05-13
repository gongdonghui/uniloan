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
public class AppSdkAppListInfo {
  @Data
  public static class SingleItem {
    private String apk_name;
    private String apk_label;
    private Long install_time;
  }
  private String device_id;
  private String mobile;
  private List<SingleItem> apps;
}
