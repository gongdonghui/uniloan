package com.sup.kalapa.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * @author kouichi
 * @date 2018/10/3
 */
public class GsonUtil {

    public static Gson gson = new GsonBuilder()
            .setDateFormat(DateUtil.DEFAULT_DATETIME_FORMAT)
            .disableHtmlEscaping()
            .create();

    public static Gson gson_yyyymmdd = new GsonBuilder()
            .setDateFormat(DateUtil.DEFAULT_YEAR_MONTH_DAY_FORMAT)
            .disableHtmlEscaping()
            .create();

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T beanCopy(Object o, Class<T> classOfT) {
        return gson.fromJson(gson.toJson(o), classOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type classOfT) {
        return gson.fromJson(json, classOfT);
    }

}
