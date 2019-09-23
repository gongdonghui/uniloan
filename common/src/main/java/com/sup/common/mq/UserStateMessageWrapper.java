package com.sup.common.mq;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by xidongzhou1 on 2019/9/16.
 */
@Data
public class UserStateMessageWrapper extends UserStateMessage implements Serializable {
  protected String topic;
  protected String tag;
  protected String msg_id;
}
