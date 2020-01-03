package com.sup.common.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author kouichi
 * @data 2018/10/9
 */
@Slf4j
public class ResponseUtil {

    public static String success() {
        return write(Response.CODE_SUCCESS, Response.MSG_SUCCESS, null);
    }

    public static String success(Object data) {
        return write(Response.CODE_SUCCESS, Response.MSG_SUCCESS, data);
    }

    public static String failed() {
        return write(Response.CODE_SYSTEM_ERROR, Response.MSG_SYSTEM_ERROR, null);
    }

    public static String failed(String msg) {
        return write(Response.CODE_SYSTEM_ERROR, msg, null);
    }

    public static String failed(int code, String msg) {
        return write(code, msg, null);
    }

    public static String failed(int code, String msg, Object o) {
        return write(code, msg, o);
    }

    private static String write(int code, String msg, Object data) {
        Map map = Maps.newHashMap();
        map.put("code", code);
        map.put("msg", msg);
        map.put("data", data);
        String result = GsonUtil.toJson(map);
        if (result.length() > 1024) {
            log.info(result.substring(0, 1024) + " ...");
        } else {
            log.info(result);
        }
        return result;
    }

}
