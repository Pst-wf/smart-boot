package com.smart.common.utils;


import java.util.Date;

/**
 * 时间计算工具类
 *
 * @author wf
 * @version 2022-01-01 17:28:23
 */
public class TimeUtil {

    /**
     * 将毫秒数转换为：xx天，xx时，xx分，xx秒
     */
    public static String formatDateAgo(long millisecond) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;
        long day = millisecond / dd;
        long hour = (millisecond - day * dd) / hh;
        long minute = (millisecond - day * dd - hour * hh) / mi;
        long second = (millisecond - day * dd - hour * hh - minute * mi) / ss;
        StringBuilder sb = new StringBuilder();
        if (millisecond >= 0 && millisecond < 1000) {
            sb.append(millisecond).append("毫秒");
        } else {
            if (day > 0) {
                sb.append(day).append("天");
            }
            if (hour > 0) {
                sb.append(hour).append("时");
            }
            if (minute > 0) {
                sb.append(minute).append("分");
            }
            if (second > 0) {
                sb.append(second).append("秒");
            }
        }
        return sb.toString();
    }

    /**
     * 将过去的时间转为为，刚刚，xx秒，xx分钟，xx小时前、xx天前，大于3天的显示日期
     */
    public static String formatTimeAgo(String dateTime) {
        return formatTimeAgo(DateUtil.parseDate(dateTime));
    }

    /**
     * 将过去的时间转为为，刚刚，xx秒，xx分钟，xx小时前、xx天前，大于3天的显示日期
     */
    public static String formatTimeAgo(Date dateTime) {
        String interval;
        // 得出的时间间隔是毫秒
        long time = System.currentTimeMillis() - dateTime.getTime();
        // 如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
        if (time / 1000 < 10 && time / 1000 >= 0) {
            interval = "刚刚";
        }
        // 如果时间间隔大于24小时则显示多少天前
        else if (time / 3600000 < 24 * 4 && time / 3600000 >= 24) {
            // 得出的时间间隔的单位是天
            int d = (int) (time / (3600000 * 24));
            interval = d + "天前";
        }
        // 如果时间间隔小于24小时则显示多少小时前
        else if (time / 3600000 < 24 && time / 3600000 >= 1) {
            // 得出的时间间隔的单位是小时
            int h = (int) (time / 3600000);
            interval = h + "小时前";
        }
        // 如果时间间隔小于60分钟则显示多少分钟前
        else if (time / 60000 < 60 && time / 60000 >= 1) {
            // 得出的时间间隔的单位是分钟
            int m = (int) ((time % 3600000) / 60000);
            interval = m + "分钟前";
        }
        // 如果时间间隔小于60秒则显示多少秒前
        else if (time / 1000 < 60 && time / 1000 >= 10) {
            int se = (int) ((time % 60000) / 1000);
            interval = se + "秒前";
        }
        // 大于3天的，则显示正常的时间，但是不显示秒
        else {
            interval = DateUtil.formatDate(dateTime, "yyyy-MM-dd HH:mm:ss");
        }
        return interval;
    }

    /**
     * 日期格式编号生成器
     *
     * @param date   日期
     * @param pos    补位数量
     * @param number 数字
     * @return String
     */
    public static String createNo(Date date, int pos, long number) {
        if (date == null) {
            throw new RuntimeException("日期不能为空！");
        }
        return DateUtil.formatDate(date, "yyyyMMdd") + String.format("%0" + pos + "d", number);
    }
}