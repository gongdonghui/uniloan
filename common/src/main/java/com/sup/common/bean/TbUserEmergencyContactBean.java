package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tb_user_emergency_contact")
public class TbUserEmergencyContactBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String info_id;
  private Integer user_id;
  private Integer relationship;
  private String name;
  private String mobile;
  private Date create_time;
  private Date expire_time;
}
