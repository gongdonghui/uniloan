package com.sup.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    public static Date getDate(Date d, int daysInterval) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        c.add(Calendar.DATE, daysInterval);
        return c.getTime();
    }

    public  static  int  daysbetween(Date start, Date end) {
        int days = (int) ((end.getTime() - start.getTime()) / (1000*3600*24));
        return days;

    }
}
