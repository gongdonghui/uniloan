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
@TableName("tb_user_documentary_image")
public class TbUserDocumentaryImageBean {
  @TableId(type = IdType.AUTO)
  private Integer id;
  private String info_id;
  private Integer user_id;
  private String image_key;
  private Integer image_category;
  private Integer image_object;
  private String image_rel_id;
  private Integer upload_type;
  private String upload_user;
  private String ext;
  private Date create_time;
  private Date expire_time;
}
