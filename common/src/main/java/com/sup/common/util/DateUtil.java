package com.sup.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    public static Date getDate(Date d, int daysAfter) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        c.add(Calendar.DATE, daysAfter);
        return c.getTime();
    }
}
