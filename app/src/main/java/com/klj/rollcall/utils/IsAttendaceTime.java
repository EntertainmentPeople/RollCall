package com.klj.rollcall.utils;


import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.text.TextUtils;

import java.util.Date;

/**
 * Created by 娱乐人物 on 2017/5/23.
 */

public class IsAttendaceTime {
    private long dayTime = 0;
    private static int attendTime = 90;

    /*
    8-8:45,8:55-9:40,10;10-10:55;11:05-11:50
     */
    public static boolean isAttendTime(int weekday, int start, long aTime) {
        if (!TextUtils.isEmpty(getAttendType(weekday, start, aTime)) && !ConstantUtil.IS_NOT_ATTENDAN_TIME.equals(getAttendType(weekday, start, aTime))) {
            return true;
        }
        return false;
    }

    public static long getCurrntTime() {
        return (System.currentTimeMillis() / 1000);
    }

    public static String getAttendType(int weekday, int start, long aTime) {
        String msg = "";
        if (weekday == getWeekDay(aTime)) {
            switch (start) {
                case 1:
                    msg = judgeType(aTime, 8, 0);
                    break;
                case 3:
                    msg = judgeType(aTime, 10, 10);
                    break;
                case 5:
                    msg = judgeType(aTime, 15, 0);
                    break;
                case 7:
                    msg = judgeType(aTime, 17, 0);
                    break;
                case 9:
                    msg = judgeType(aTime, 19, 40);
                    break;
                default:
                    msg = ConstantUtil.IS_NOT_ATTENDAN_TIME;
            }
        } else {
            msg = ConstantUtil.IS_NOT_ATTENDAN_TIME;
        }
        return msg;
    }

    private static String judgeType(long aTime, int hour, int minute) {
        String msg = "";
        int newMinute = minute;
        int newHour = hour;
        if (minute - 10 < 0) {
            newMinute = 60 - minute;
            newHour -= 1;
        } else {
            newMinute -= 10;
        }
        long startTime = getSpecified(newHour, newMinute);
        long endTime = getSpecified(hour, minute + attendTime + 10);
        String tostartTime = Utils.toTransferTime(String.valueOf(startTime));
        String toendTime = Utils.toTransferTime(String.valueOf(endTime));
        if (aTime >= startTime && aTime <= endTime) {
            if (aTime <= getSpecified(hour, minute) || aTime >= getSpecified(hour, minute + attendTime)) {
                msg = "正常";
            } else if (aTime <= getSpecified(hour, minute + 10)) {
                msg = "迟到";
            } else if (aTime > getSpecified(hour, minute + attendTime - 10)) {
                msg = "早退";
            } else {
                msg = "旷课";
            }
        }
        return msg;
    }

    /**
     * 获取当天的秒数
     *
     * @return
     */
    public static long getCurrentDay() {
        Date date = new Date();
        long l = 24 * 60 * 60; //每天的秒数
        return (date.getTime() / 1000 - (date.getTime() % l));
    }

    /**
     * 获取上课时间的秒数
     *
     * @param hour
     * @param minute
     * @return
     */
    public static long getSpecified(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        Date date = new Date(System.currentTimeMillis());
        c.set(2017, 4, 28, hour, minute, 0);
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return c.getTimeInMillis() / 1000;


        /*Date date = new Date();
        long l = 0;
        if (minute == 0) {
            l = hour * 60 * 60;
        } else {
            l = (hour * 60 + minute) * 60;
        }
        return (date.getTime() / 1000 - (date.getTime() % l));*/
    }

    public static int getWeekDay(long aTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(aTime * 1000));
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int week = c.get(java.util.Calendar.DAY_OF_WEEK);
        if (week != 1) {
            return week - 1;
        }
        return 7;
    }
}
