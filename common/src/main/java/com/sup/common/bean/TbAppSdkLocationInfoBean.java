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
@TableName("tb_app_sdk_location_info")
public class TbAppSdkLocationInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String info_id;
  private String apply_long;
  private String apply_lat;
  private String device_id;
  private String mobile;
  private Date create_time;
  private Date update_time;
}
