package com.sup.common.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by xidongzhou1 on 2019/9/6.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("tb_user_citizen_identity_card_info")
public class TbUserCitizenIdentityCardInfoBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String info_id;
  private Integer user_id;
  private String name;
  private String cid_no;
  private Integer gender;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date birthday;
  private Integer age;
  private String pic_1;
  private String pic_2;
  private String pic_3;
  private String pic_4;
  Date create_time;
  Date expire_time;
}
