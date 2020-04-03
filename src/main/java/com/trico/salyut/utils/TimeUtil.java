package com.trico.salyut.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtil {
    public static String getTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", Locale.CHINA);
        TimeZone TZ = TimeZone.getTimeZone("Asia/Chongqing");
        sdf.setTimeZone(TZ);
        return sdf.format(date);
    }
}
