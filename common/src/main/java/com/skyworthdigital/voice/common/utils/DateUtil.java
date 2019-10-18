package com.skyworthdigital.voice.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ives 2019/7/25
 */
public class DateUtil {

    public static String getCurrentTimestamp(){
        return getTimestamp(new Date());
    }
    public static String getTimestamp(Date date){
        // 例如 "2012-12-03 18:09:22 +08"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss "+getZoneOffset(), Locale.getDefault());
        return sdf.format(date);
    }
    public static Date getDate(String timestamp) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss "+getZoneOffset(), Locale.getDefault());
        return sdf.parse(timestamp);
    }

    public static Date getDateOneWeekLater(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        return c.getTime();
    }
    private static String getZoneOffset(){
        Calendar c = Calendar.getInstance();
        int offset = c.get(Calendar.ZONE_OFFSET)/3600000;
        int absOffset = Math.abs(offset);
        String offsetStr = absOffset<10?("0"+offset):String.valueOf(offset);
        return offset>0?("+"+offsetStr):("-"+offsetStr);
    }
}
