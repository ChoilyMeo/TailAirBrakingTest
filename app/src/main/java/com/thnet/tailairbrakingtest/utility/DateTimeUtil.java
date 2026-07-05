package com.thnet.tailairbrakingtest.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

    /**
     *  G    Era    标志符            Text    公元
     y    年                    Year    1996; 96
     M    年中的月份                Month    July; Jul; 07
     w    年中的周数                Number    27
     W    月份中的周数            Number    2
     D    年中的天数                Number    189
     d    月份中的天数            Number    10
     F    月份中的星期            Number    2
     E    星期中的天数            Text    Tuesday; Tue
     a    am/pm 标记            Text    PM
     H    一天中的小时数（0-23）    Number    0
     k    一天中的小时数（1-24）    Number    24
     K    am/pm 中的小时数（0-11）    Number    0
     h    am/pm 中的小时数（1-12）    Number    12
     m    小时中的分钟数            Number    30
     s    分钟中的秒数            Number    55
     S    毫秒数                Number    978
     z    时区    General time zone    Pacific Standard Time; PST; GMT-08:00
     Z    时区    RFC 822 time zone    -0800
     */

    /**
     * 把时间格式化成如：2002-08-03 08:26:16 格式的字符串
     */
    public final static String FMT_yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    /**
     * 把时间格式化成如：2002-07-05 格式的字符串
     */
    public final static String FMT_yyyyMMdd = "yyyy-MM-dd";

    /**
     * 把时间格式化成如：22:04:45 格式的字符串
     */
    public final static String FMT_HHmmss = "HH:mm:ss";


    /**
     * 私有化构造器，使得不能产生该类对象，类中所有的方法均为静态方法
     */
    private DateTimeUtil() {
    }

    /**
     * 根据给出的Date值和格式串采用操作系统的默认所在的国家风格来格式化时间，并返回相应的字符串
     *
     * @param date
     *            日期对象
     * @param formatStr
     *            日期格式
     * @return 如果为null，返回字符串""
     */
    public static String formatDateTimetoString(Date date, String formatStr) {
        String reStr = "";
        if (date == null || formatStr == null || formatStr.trim().length() < 1) {
            return reStr;
        }
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(formatStr);
        reStr = sdf.format(date);
        return reStr == null ? "" : reStr;
    }

    /**
     * 按指定的格式和操作系统默认国家的风格把给定的日期字符串格式化为一个Date型日期
     *
     * @param dateTimeStr
     *            日期毫秒字符串
     * @param formatStr
     *            日期格式
     * @return java.util.Date类型对象
     * @throws Exception
     *             可能抛出的异常
     */
    public static Date parseToDate(String dateTimeStr, String formatStr) throws Exception {
        if (dateTimeStr == null || formatStr == null || dateTimeStr.trim().length() < 1
                || formatStr.trim().length() < 1) {
            throw new IllegalArgumentException("参数dateTimeStr、formatStr不能是null或空格串！");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        try {
            return sdf.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new Exception(e);
        }
    }

    /**
     * 把给定的时间加上指定的时间值，可以为负
     *
     * @param d
     *            需要设定的日期对象
     * @param times
     *            时间值
     * @param type
     *            类型，Calendar.MILLISECOND，毫秒<BR>
     *            Calendar.SECOND，秒<BR>
     *            Calendar.MINUTE，分钟<BR>
     *            Calendar.HOUR，小时<BR>
     *            Calendar.DATE，日<BR>
     * @return 如果d为null，返回null
     */
    public static Date addTime(Date d, int times, int type) {
        if (d == null) {
            throw new IllegalArgumentException("参数d不能是null对象！");
        }
        switch (type) {
            case Calendar.MILLISECOND:
                break;
            case Calendar.SECOND:
                break;
            case Calendar.MINUTE:
                break;
            case Calendar.HOUR:
                break;
            case Calendar.DATE:
                break;
            default:
                throw new RuntimeException("时间类型不正确!type＝" + type);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(type, times);
        return calendar.getTime();
    }
}
