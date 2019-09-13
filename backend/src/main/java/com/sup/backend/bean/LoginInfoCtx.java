package com.sup.backend.bean;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by xidongzhou1 on 2019/9/5.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfoCtx {
  private Integer user_id;
  private String login_time;

  public String toString() {
    return JSON.toJSONString(this);
  }
}
