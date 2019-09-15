package com.sup.core.util;

import java.util.Date;

/**
 * gongshuai
 * <p>
 * 2019/9/15
 */
public class DateUtil {
    public  static  int  daysbetween(Date start, Date end) {
        int days = (int) ((end.getTime() - start.getTime()) / (1000*3600*24));
        return days;

    }
}
