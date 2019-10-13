package com.sup.backend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tb_user_basic_info")
public class AppTbUserBasicInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String info_id;
  private Integer user_id;
  private Integer education;
  private Integer marriage;
  private Integer children_count;
  private Integer residence_province;
  private Integer residence_city;
  private Integer residence_country;
  @NotEmpty(message = "error_param")
  private String residence_addr;
  private Integer residence_duration;
  private Integer purpose;
  private String purpose_other;
  private String zalo_id;
  private Integer age;
  private Date create_time;
  private Date expire_time;
}
