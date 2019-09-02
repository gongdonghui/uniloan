package com.sup.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.ibatis.annotations.ConstructorArgs;

/**
 * Created by xidongzhou1 on 2019/9/2.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Result {
  public static Integer kSuccess = 0;
  public static Integer kError = 1;

  private Integer status;
  private String message;
  private Object data;

  public static Result of(Integer status, Object data) {
    return new Result().setStatus(status).setData(data).setMessage("");
  }

  public static Result of(Integer status, Object data, String message) {
    return new Result().setStatus(status).setData(data).setMessage(message);
  }
}
