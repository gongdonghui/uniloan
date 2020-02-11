package com.sup.common.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
public class DateUtil {

    /**
     * 日期
     */
    public static final String DEFAULT_YEAR_MONTH_DAY_FORMAT = "yyyy-MM-dd";
    /**
     * 时间
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    /**
     * 日期+时间
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 各种神奇格式
     */
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    public static final String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String NO_SPLIT_FORMAT = "yyyyMMddHHmmss";
    public static final String ONE_MILLION_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    public static final String MILLIONS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'+0800'";
    public static final String NO_SPLIT_MILLIONS_FORMAT = "yyyyMMddHHmmssSSS";
    public static final String NS_YEAR_MONTH_FORMAT = "yyyy年MM月";
    public static final String NS_YEAR_MONTH_DAY_FORMAT = "yyyy年MM月dd日";
    public static final String NS_TIME_FORMAT = "yyyy.MM.dd HH:mm:ss";
    public static final String NS_TIME2_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static final String NS_MINUTE_FORMAT = "yyyy.MM.dd HH:mm";
    public static final String NS_MINUTE2_FORMAT = "yyyy年MM月dd日 HH:mm";
    public static final String NS_DAY_ALL_NUM_FORMAT = "yyyyMMdd";

    /**
     * 获取间隔时间单位
     */
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final String SECOND = "second";

    public static SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 将日期对象格式化成yyyy-mm-dd类型的字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatDate(Date date) {
        return formatDate2String(date, DEFAULT_YEAR_MONTH_DAY_FORMAT);
    }

    /**
     * 将日期对象格式化成HH:mm:ss类型的字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatTime(Date date) {
        return formatDate2String(date, DEFAULT_TIME_FORMAT);
    }

    /**
     * 将日期对象格式化成yyyy-MM-dd HH:mm:ss类型的字符串
     *
     * @param date 日期对象
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    public static String formatDateTime(Date date) {
        return formatDate2String(date, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 将日期格式化为指定格式
     *
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        return formatDate2String(date, format);
    }

    /**
     * 将yyyy-MM-dd格式的字符串转换为日期对象
     *
     * @param date yyyy-MM-dd格式字符串
     * @return 转换后的日期对象，无法转换时返回null
     */
    public static Date parseDate(String date) {
        return parseString2Date(date, DEFAULT_YEAR_MONTH_DAY_FORMAT);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的字符串转换为日期对象
     *
     * @param datetime yyyy-MM-dd HH:mm:ss格式字符串
     * @return 转换后的日期对象，无法转换时返回null
     */
    public static Date parseDateTime(String datetime) {
        return parseString2Date(datetime, DEFAULT_DATETIME_FORMAT);
    }

    /**
     * 将指定格式的字符串对象转换为日期对象
     *
     * @param str     字符串
     * @param pattern 指定的格式
     * @return 转换后的日期，无法转换时返回null
     */
    public static Date parse(String str, String pattern) {
        return parseString2Date(str, pattern);
    }

    /**
     * 解析非标准时间
     *
     * @param date
     * @return
     */
    public static Date parseNonStandardDate(String date) {
        try {
            if (date.contains("-")) {
                if (!date.contains(":")) {
                    date = date + " 00:00:00";
                }
                String[] size = date.split(":");
                if (size.length == 2) {
                    date = date + ":00";
                }
                return parseString2Date(date, DEFAULT_DATETIME_FORMAT);
            }
            if (date.contains("年") && date.contains("月") && date.contains("日") && date.contains(":")) {
                return parseString2Date(date, NS_MINUTE2_FORMAT);
            }
            if (date.contains("年") && date.contains("月") && date.contains("日")) {
                return parseString2Date(date, NS_YEAR_MONTH_DAY_FORMAT);
            }
            if (date.contains("年") && date.contains("月")) {
                return parseString2Date(date, NS_YEAR_MONTH_FORMAT);
            }
            if (date.contains("/")) {
                if (!date.contains(":")) {
                    date = date + " 00:00:00";
                }
                return parseString2Date(date, NS_TIME2_FORMAT);
            }
            if (date.contains(".") && date.contains(":")) {
                String[] size = date.split(":");
                if (size.length == 2) {
                    date = date + ":00";
                }
                return parseString2Date(date, NS_TIME_FORMAT);
            }

            if (date.length() == 8) {
                //20130220
                return parseString2Date(date, NS_DAY_ALL_NUM_FORMAT);
            }
        } catch (Exception e) {
            log.error("[DateUtil.Error]parseNonStandardDate异常:" + date, e);
        }
        return null;
    }

    /**
     * 获取两个时间的间隔时间
     *
     * @param d1
     * @param d2
     * @param method
     * @return
     */
    public static int getPeriod(Date d1, Date d2, String method) {
        DateTime start = new DateTime(d1);
        DateTime end = new DateTime(d2);
        Period period = new Period(start, end);
        if (method.equals(YEAR)) {
            return period.getYears();
        }
        if (method.equals(MONTH)) {
            return period.getMonths();
        }
        if (method.equals(DAY)) {
            return period.getDays();
        }
        if (method.equals(HOUR)) {
            return period.getHours();
        }
        if (method.equals(MINUTE)) {
            return period.getMinutes();
        }
        if (method.equals(SECOND)) {
            return period.getSeconds();
        }
        return -1;
    }

    /**
     * 比较两个时间先后
     * 大于0 说明data1晚
     * 小于0 说明date1早
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compareDate(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return 0;
        } else if (date1 == null) {
            return -1;
        } else if (date2 == null) {
            return 1;
        } else {
            return date1.compareTo(date2);
        }
    }

    /**
     * 根据指定的格式格式将传入字符串转化为日期对象
     *
     * @param date   传入字符串
     * @param format 指定格式
     * @return 格式化后日期对象
     */
    private static Date parseString2Date(String date, String format) {
        try {
            return DateTimeFormat.forPattern(format).parseLocalDateTime(date).toDate();
        } catch (Exception e) {
            log.error("[DateUtil.Error]parseString2Date异常-date" + date + ",format:" + format, e);
            return null;
        }
    }

    /**
     * 将日期对象格式化成指定的格式字符串
     *
     * @param date   日期对象
     * @param format 格式
     * @return 格式化后的字符串，无法格式化时，返回null
     */
    private static String formatDate2String(Date date, String format) {
        if (date == null || Strings.isNullOrEmpty(format)) {
            return null;
        }
        try {
            return DateTimeFormat.forPattern(format).print(date.getTime());
        } catch (Exception e) {
            log.error("[DateUtil.Error]formatDate2String异常-date" + date + ",format:" + format, e);
            return null;
        }
    }

    /**
     * 当前时间与给定日期的相差日 与给定限制做对比
     * 0两者差值小于限定
     * 1两者差值相等
     * 2两者差值大于限定
     *
     * @param date
     * @param limit
     * @return
     */
    public static int isExpire(Date date, int limit) {
        DateTime dt1 = new DateTime(date);
        DateTime dt2 = new DateTime(new Date());
        int between = Days.daysBetween(dt1, dt2).getDays();
        if (between < limit) {
            return 0;
        } else if (between == limit) {
            return 1;
        } else {
            return 2;
        }
    }

    public static Date getDate(Date d, int daysInterval) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        c.add(Calendar.DATE, daysInterval);
        return c.getTime();
    }

    public static int getDaysBetween(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {  // 不是同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
                    timeDistance += 366;
                } else {  //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        }
        return day2 - day1;
    }

//    public  static  int  daysbetween(Date start, Date end) {
//        int days = (int) ((end.getTime() - start.getTime()) / (1000*3600*24));
//        return days;
//    }

    /**
     * 比较两个日期，仅比较年月日，忽略时分秒
     * -1 d1早于d2
     * 0 d1=d2
     * 1 d1晚于d2
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int compareDay(Date d1, Date d2) {
        if (d1 == null && d2 == null) {
            return 0;
        } else if (d1 == null) {
            return -1;
        } else if (d2 == null) {
            return 1;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);


        return cal1.compareTo(cal2);
    }

    public static boolean isSameDay(Date d1, Date d2) {

        if (d1 == null && d2 == null) {
            return true;
        }
        if (d1 == null || d2 == null) {
            return false;
        }
        return DEFAULT_FORMAT.format(d1).equals(DEFAULT_FORMAT.format(d2));
    }

    /**
     * yyyy-MM-dd 00:00:00
     * @param date
     * @return
     */
    public static String startOf(Date date) {
        String dt = formatDate2String(date, DEFAULT_YEAR_MONTH_DAY_FORMAT);
        return dt == null ? null : dt + " 00:00:00";
    }

    /**
     * yyyy-MM-dd 23:59:59
     * @param date
     * @return
     */
    public static String endOf(Date date) {
        String dt = formatDate2String(date, DEFAULT_YEAR_MONTH_DAY_FORMAT);
        return dt == null ? null : dt + " 23:59:59";
    }

    public static void main(String args[]) {
        Date start  = parseDateTime("2020-02-06 09:00:00");
        Date end    = new Date();
        //System.out.println("daysbetween: " + daysbetween(start, end));
        System.out.println("getDaysBetween: " + getDaysBetween(start, end));
        Date dt = new Date();
        Date now = new Date();
        int ret = compareDay(dt, now);
        System.out.println("ret=" + ret);
    }
}
