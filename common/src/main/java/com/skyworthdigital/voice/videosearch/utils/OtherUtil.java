package com.skyworthdigital.voice.videosearch.utils;

import android.content.Context;
import android.view.View;

import com.skyworthdigital.voice.common.R;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class OtherUtil {
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        if (str.trim().length() <= 0) {
            return true;
        }
        return false;
    }

    public static int getDrawableIdByName(Context context, String name) {
        try {
            return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getTimeSpacing(Context context, long screenTime) {
        long currentTime = System.currentTimeMillis();
        long timeSpacing = currentTime - screenTime;
        return getInfoByIntervalTime(context, timeSpacing);
    }

    private static String getInfoByIntervalTime(Context context, long timeSpacing) {
        String strTimeInfo = "";
        if (timeSpacing <= 60 * 1000) {
            strTimeInfo = "1" + context.getString(R.string.str_minutes) + context.getString(R.string.str_before);
        } else if (timeSpacing <= 60 * 60 * 1000) {
            strTimeInfo =
                    (timeSpacing / (60 * 1000))
                            + context.getString(R.string.str_minutes)
                            + context.getString(R.string.str_before);
        } else if (timeSpacing <= 24 * 60 * 60 * 1000) {
            strTimeInfo =
                    (timeSpacing / (60 * 60 * 1000))
                            + context.getString(R.string.str_hour)
                            + context.getString(R.string.str_before);
        } else {
            long currentTime = System.currentTimeMillis();
            long screenTime = currentTime - timeSpacing;
            if (screenTime > 0) {
                Date date = new Date(screenTime);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                strTimeInfo = df.format(date);
            }
        }
        return strTimeInfo;
    }

    public static String getDurationInfoBySecond(int timeLength) {
        String strTimeInfo = "";
        try {
            if (timeLength >= 3600) {
                Date dt = new Date((timeLength + 3600 * 16) * 1000);
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                strTimeInfo = df.format(dt);
            } else if (timeLength <= 3600 * 24) {
                Date dt = new Date((timeLength + 3600 * 16) * 1000);
                DateFormat df = new SimpleDateFormat("mm:ss");
                strTimeInfo = df.format(dt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strTimeInfo;
    }

    public static void setViewVisibility(View view, int state) {
        try {
            if (state != view.getVisibility()) {
                view.setVisibility(state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
