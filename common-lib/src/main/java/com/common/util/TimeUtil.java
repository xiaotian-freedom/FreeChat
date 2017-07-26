package com.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLS_IN_DAY = 1000L * SECONDS_IN_DAY;

    /**
     * 将格式为Format的字符串转为时间戳
     *
     * @param formatTime String 格式化的时间字符串
     * @param strFormat  String 日期格式
     * @return long 时间戳
     */
    public static long getLongTimeFromFormat(String formatTime, String strFormat) {
        long timestamp = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat, Locale.getDefault());
        try {
            Date d = sdf.parse(formatTime);
            timestamp = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    /**
     * 将时间戳(long型)转为字符串
     *
     * @param timestamp     long  时间戳
     * @param strDateFormat String 日期格式
     * @return String 格式化后 的字符串
     */
    public static String getFormatTimeFromLong(long timestamp, String strDateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat, Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    /**
     * 将时间戳转为日期型
     *
     * @param timestamp     long
     * @param strDateFormat String 日期格式
     * @return Date
     */
    public static Date getDateFromTimeStemp(long timestamp, String strDateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat, Locale.getDefault());
        String d = sdf.format(timestamp);
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取两个时间戳之间相差的月份
     *
     * @param startTime 起始时间
     * @param endTime   终止时间
     * @return 相差月份数
     */
    public static int getMonthGapBetweenTowStamp(long startTime, long endTime) {
        Calendar cldStart = Calendar.getInstance();
        cldStart.setTimeInMillis(startTime);
        Calendar cldEnd = Calendar.getInstance();
        cldEnd.setTimeInMillis(endTime);
        int gap = 0;
        while (cldStart.before(cldEnd)) {
            cldStart.add(Calendar.MONTH, 1);
            gap += 1;
        }
        return gap;
    }

    /**
     * 根据日期取得星期
     *
     * @param timestamp long
     * @return String
     */
    public static String getWeek(long timestamp) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(timestamp));
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 数字转汉字
     *
     * @param c        char
     * @param sBuilder StringBuilder
     * @return String
     */
    public static String getChinese(char c, StringBuilder sBuilder) {
        switch (c) {
            case '0':
                sBuilder.append("〇");
                break;
            case '1':
                sBuilder.append("一");
                break;
            case '2':
                sBuilder.append("二");
                break;
            case '3':
                sBuilder.append("三");
                break;
            case '4':
                sBuilder.append("四");
                break;
            case '5':
                sBuilder.append("五");
                break;
            case '6':
                sBuilder.append("六");
                break;
            case '7':
                sBuilder.append("七");
                break;
            case '8':
                sBuilder.append("八");
                break;
            case '9':
                sBuilder.append("九");
                break;
            default:
                sBuilder.append(c);
                break;
        }
        return sBuilder.toString();
    }

    /**
     * @param str
     * @return
     */
    public static String getChinese(String str) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '0':
                    sBuilder.append("〇");
                    break;
                case '1':
                    sBuilder.append("一");
                    break;
                case '2':
                    sBuilder.append("二");
                    break;
                case '3':
                    sBuilder.append("三");
                    break;
                case '4':
                    sBuilder.append("四");
                    break;
                case '5':
                    sBuilder.append("五");
                    break;
                case '6':
                    sBuilder.append("六");
                    break;
                case '7':
                    sBuilder.append("七");
                    break;
                case '8':
                    sBuilder.append("八");
                    break;
                case '9':
                    sBuilder.append("九");
                    break;
                default:
                    sBuilder.append(c);
                    break;
            }
        }
        return sBuilder.toString();
    }

    /**
     * 是否是今天
     *
     * @param timestamp long
     * @return boolean
     */
    public static boolean isToday(long timestamp) {
        long currentMillis = System.currentTimeMillis();// 系统当前时间
        long currentStart = currentMillis - (currentMillis + 8 * 3600 * 1000) %
                (24 * 60 * 60 * 1000);
        long timeGap = (currentStart - timestamp) / 1000;// 与当前凌晨时间相差秒数
        return timeGap <= 0;
    }

    /**
     * 获取当前系统时间
     *
     * @return
     */
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间(自定义时间格式)
     *
     * @param format String
     * @return String
     */
    public static String getCurrentTimeString(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        long currentMillis = System.currentTimeMillis();
        return sdf.format(new Date(currentMillis));
    }

    /**
     * 将时间戳(long型)与当前时间作比较
     *
     * @param timestamp 时间戳
     * @return 格式化的字符串
     */
    public static String converTime(long timestamp) {
        long currentSeconds = System.currentTimeMillis();
        long timeGap = (currentSeconds - timestamp) / 1000;// 与现在时间相差秒数
        String timeStr;
        if (timeGap > 365 * 24 * 60 * 60) {// 1年以前
            timeStr = getFormatTimeFromLong(timestamp, "yyyy年M月dd日 HH:mm");
        } else if (timeGap > 24 * 60 * 60) {// 1天以上
            timeStr = timeGap / (24 * 60 * 60) + "天前";
        } else if (timeGap > 60 * 60) {// 1小时-24小时
            timeStr = timeGap / (60 * 60) + "小时前";
        } else if (timeGap > 60) {// 1分钟-59分钟
            timeStr = timeGap / 60 + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * 是否在一分钟内
     *
     * @param timestamp
     * @return
     */
    public static boolean isInOneMinute(long timestamp) {
        long currentSeconds = System.currentTimeMillis();
        long timeGap = (currentSeconds - timestamp) / 1000;// 与现在时间相差秒数
        return timeGap < 60;
    }

    /**
     * 剩余十秒钟
     * @param timestamp
     * @return
     */
    public static boolean remainTenSeconds(long timestamp) {
        long currentSeconds = System.currentTimeMillis();
        long timeGap = (currentSeconds - timestamp) / 1000;// 与现在时间相差秒数
        return timeGap == 49;
    }

    /**
     * 格式化时间显示为今天、昨天、前天样式
     *
     * @param timestamp long
     * @param isDetail  boolean
     * @return String
     */
    public static String displayTime(long timestamp, boolean isDetail) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdfkkmm = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdfyyMdd = new SimpleDateFormat("yy-M-dd", Locale.getDefault());
        SimpleDateFormat sdfyyyyMddkkmm = new SimpleDateFormat("yyyy-M-dd", Locale.getDefault());
        long currentMillis = System.currentTimeMillis();// 系统当前时间
        long currentStart = currentMillis - (currentMillis + 8 * 3600 * 1000) % (24 * 60 * 60 * 1000);
        long timeGap = (currentStart - timestamp) / 1000;// 与当前凌晨时间相差秒数

        // 今天
        if (timeGap <= 0) {
            sb.append(sdfkkmm.format(timestamp));
        }// 昨天
        else if (timeGap > 0 && timeGap <= 24 * 60 * 60) {
            sb.append("昨天");
            if (isDetail) {
                sb.append(sdfkkmm.format(timestamp));
            }
        }// 前天
        else if (timeGap > 24 * 60 * 60 && timeGap <= 2 * 24 * 60 * 60) {
            sb.append("前天");
            if (isDetail) {
                sb.append(sdfkkmm.format(timestamp));
            }
        } else {
            if (!isDetail) {
                sb.append(sdfyyMdd.format(timestamp));
            } else {
                sb.append(sdfyyyyMddkkmm.format(timestamp));
            }

        }

        return sb.toString();
    }

    /**
     * 格式化聊天时间
     *
     * @param timestamp
     * @return
     */
    public static String formatChatDate(long timestamp) {
        long currentMillis = System.currentTimeMillis();// 系统当前时间
        long currentStart = currentMillis - (currentMillis + 8 * 3600 * 1000) %
                (24 * 60 * 60 * 1000);
        long timeGap = (currentStart - timestamp) / 1000;// 与当前凌晨时间相差秒数
        String timeStr = "";
        if (timeGap > 365 * 24 * 3600) {// 1年以前
            timeStr = getFormatTimeFromLong(timestamp, "yy-MM-dd HH:mm");
        } else if (timeGap > 7 * 24 * 3600) {//今年之内 7天以前 显示月份
            timeStr = getFormatTimeFromLong(timestamp, "MM-dd HH:mm");
        } else if (timeGap > 24 * 3600) {//昨天之前 7天之内 显示星期
            timeStr = getWeek(timestamp) + " " + getFormatTimeFromLong(timestamp, "HH:mm");
        } else if (timeGap > 0 && timeGap <= 24 * 60 * 60) {
            timeStr = "昨天" + " " + getFormatTimeFromLong(timestamp, "HH:mm");
        } else if (timeGap <= 0) {
            timeStr = getFormatTimeFromLong(timestamp, "HH:mm");
        }

        return timeStr;
    }

    /**
     * 格式为秒
     *
     * @param timestamp
     * @return
     */
    public static long convertToSec(long timestamp) {
        return new Date(timestamp).getTime() / 1000;
    }

}
