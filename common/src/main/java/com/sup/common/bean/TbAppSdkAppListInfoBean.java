package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Project:uniloan
 * Class:  TbApplyInfoBean
 * <p>
 * Author: guanfeng
 * Create: 2019-09-05
 */

@Data
@TableName("tb_app_sdk_applist_info")
public class TbAppSdkAppListInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String device_id;
  private String mobile;
  private String apk_name;
  private String apk_label;
  private Date create_time;
  private Date update_time;

  public String Signature() {
    return String.format("%s|%s|%s", device_id == null ? "": device_id, apk_name == null ? "": apk_name, apk_label == null? "": apk_label);
  }
}
