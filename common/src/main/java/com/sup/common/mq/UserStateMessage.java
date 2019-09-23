package com.sup.common.mq;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by xidongzhou1 on 2019/9/16.
 */
@Data
public class UserStateMessage implements Serializable {
  protected Integer user_id;
  protected String mobile;
  protected Integer rel_id;
  protected String state;
  protected String create_time;
  protected String ext;
}
