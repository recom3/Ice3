package com.recom3.snow3.mobilesdk.utils;

import android.util.Log;

import org.w3c.dom.DOMException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class DateUtils {
    private static SimpleDateFormat dateFormat;

    public static SimpleDateFormat fileInfoFormat;

    public static SimpleDateFormat jsonFormat;

    private static SimpleDateFormat shortTimeFormat;

    private static SimpleDateFormat timeFormat;

    public static TimeZone utc = TimeZone.getTimeZone("UTC");

    static {
        timeFormat = new SimpleDateFormat("h:mm:ss aaa");
        shortTimeFormat = new SimpleDateFormat("h:mm aa");
        dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
        fileInfoFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        jsonFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static long epochTimeMillis() {
        return System.currentTimeMillis();
    }

    public static int epochTimeSecs() {
        return (int)(System.currentTimeMillis() / 1000L);
    }

    public static long fileDateStringToDate(String paramString) {
        fileInfoFormat.setTimeZone(utc);
        try {
            return fileInfoFormat.parse(paramString).getTime();
        } catch (DOMException dOMException) {
            dOMException.printStackTrace();
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        return 0L;
    }

    public static String fileDateToString(long paramLong) {
        fileInfoFormat.setTimeZone(utc);
        return fileInfoFormat.format(Long.valueOf(paramLong));
    }

    public static String getDateAndTimeString(long paramLong, TimeZone paramTimeZone) {
        return getDateString(paramLong, jsonFormat, paramTimeZone);
    }

    public static String getDateString(long paramLong) {
        return getDateString(paramLong, dateFormat, TimeZone.getDefault());
    }

    public static String getDateString(long paramLong, SimpleDateFormat paramSimpleDateFormat, TimeZone paramTimeZone) {
        Date date = new Date(paramLong);
        paramSimpleDateFormat.setTimeZone(paramTimeZone);
        return paramSimpleDateFormat.format(date);
    }

    public static String getDurationString(int paramInt) {
        return (paramInt > 7260) ? ((paramInt / 3600) + " hrs, " + (paramInt % 3600 / 60) + " min(s)") : ((paramInt > 7200) ? ((paramInt / 3600) + " hrs") : ((paramInt > 3660) ? ((paramInt / 3600) + " hr, " + (paramInt % 3600 / 60) + " min(s)") : ((paramInt > 3600) ? ((paramInt / 3600) + " hr") : ((paramInt > 60) ? ((paramInt / 60) + " min(s), " + (paramInt % 60) + " sec(s)") : (paramInt + " sec(s)")))));
    }

    public static String getShortTimeString(long paramLong) {
        return getDateString(paramLong, shortTimeFormat, TimeZone.getDefault());
    }

    public static String getTimeString(long paramLong) {
        return getDateString(paramLong, timeFormat, TimeZone.getDefault());
    }

    public static long jsonStringToDate(String paramString) {
        long l;
        jsonFormat.setTimeZone(utc);
        try {
            l = jsonFormat.parse(paramString).getTime();
        } catch (ParseException parseException) {
            Log.i("DateUtils", "failed to parse json date string", parseException);
            l = 0L;
        }
        return l;
    }
}
