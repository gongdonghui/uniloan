package com.sup.common.mq;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by xidongzhou1 on 2019/9/16.
 */
@Data
public class UserStateMessage implements Serializable {
  private Integer user_id;
  private Integer rel_id;
  private String state;
  private String create_time;
  private String ext;
}
