package com.sup.backend.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/6.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AppTbUserCitizenIdentityCardInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String info_id;
  private Integer user_id;
  @NotEmpty(message = "error_param")
  private String name;
  @NotEmpty(message = "error_param")
  private String cid_no;
  @NotNull(message = "error_param")
  private Integer gender;
  @NotEmpty(message = "error_param")
  private String pic_1;
  @NotEmpty(message = "error_param")
  private String pic_2;
  @NotEmpty(message = "error_param")
  private String pic_3;
  private Date birthday;
  private Integer age;
  private String pic_4;
  Date create_time;
  Date expire_time;
}
