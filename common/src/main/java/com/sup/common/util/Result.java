package com.sup.common.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by xidongzhou1 on 2019/9/2.
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Result<T> {
    public static Integer kSuccess = 0;
    public static Integer kError = 1;
    public static Integer kTokenError = 2;

    private Integer status;
    private String message;
    private T data;

    public  boolean isSucc() {
        return status.equals(kSuccess);
    }

    public static <T> Result of(Integer status, T data) {
        return new Result<>().setStatus(status).setData(data).setMessage("");
    }

    public static <T> Result of(Integer status, T data, String message) {
        return new Result<>().setStatus(status).setData(data).setMessage(message);
    }

    public static Result succ() {
        return new Result<Object>().setStatus(kSuccess).setMessage("succ");
    }

    public static <T> Result succ(T data) {
        return new Result<T>().setStatus(kSuccess).setMessage("succ").setData(data);
    }

    public static Result fail(String key) {
        return new Result<Object>().setStatus(kError).setMessage(key);
    }

    public static Result fail(Integer status, String msg) {
        return new Result<Object>().setStatus(status).setMessage(msg);
    }

}
