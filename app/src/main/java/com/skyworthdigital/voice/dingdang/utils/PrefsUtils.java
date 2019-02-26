package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.forest.bigdatasdk.util.LogUtil;


public class PrefsUtils {

    private static final String GUIDE_SHOW_PROPERTY = "guide_show";
    private static final String SP_FM_GUIDE_SHOW = "fm_guide_show";
    private static final String SP_MUSIC_GUIDE_SHOW = "music_guide_show";
    private static final String SP_MOVIE_GUIDE_SHOW = "movie_guide_show";
    private static final String SP_TVLIVE_GUIDE_SHOW = "tvlive_guide_show";
    private static final String SP_POEM_GUIDE_SHOW = "poem_guide_show";
    private static final String SP_ALARM_RING = "alarm_ring";

    public static void setVideoGuideShow(Context context, boolean show) {
        LogUtil.log("setVideoGuideShow:" + show);
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_MOVIE_GUIDE_SHOW, show).apply();
    }

    public static boolean getVideoGuideShow(Context context) {
        LogUtil.log("getVideoGuideShow:");
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SP_MOVIE_GUIDE_SHOW, false);
    }

    public static void setFmGuideShow(Context context, boolean show) {
        LogUtil.log("setLoginOut:" + show);
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_FM_GUIDE_SHOW, show).apply();
    }

    public static boolean getFmGuideShow(Context context) {
        LogUtil.log("getFmGuideShow:");
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SP_FM_GUIDE_SHOW, false);
    }

    public static void setMusicGuideShow(Context context, boolean show) {
        LogUtil.log("setLoginOut:" + show);
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_MUSIC_GUIDE_SHOW, show).apply();
    }

    public static boolean getMusicGuideShow(Context context) {
        LogUtil.log("getFmGuideShow:");
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SP_MUSIC_GUIDE_SHOW, false);
    }

    public static void setPoemGuideShow(Context context, boolean show) {
        LogUtil.log("setPoemGuideShow:" + show);
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_POEM_GUIDE_SHOW, show).apply();
    }

    public static boolean getPoemGuideShow(Context context) {
        LogUtil.log("getFmGuideShow:");
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SP_POEM_GUIDE_SHOW, false);
    }

    public static void setTvliveGuideShow(Context context, boolean show) {
        LogUtil.log("setTvliveGuideShow:" + show);
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SP_TVLIVE_GUIDE_SHOW, show).apply();
    }

    public static boolean getTvliveGuideShow(Context context) {
        LogUtil.log("getFmGuideShow:");
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SP_TVLIVE_GUIDE_SHOW, false);
    }

    public static void setAlarmRing(Context context, String key) {
        LogUtil.log("setAlarmRing:" + key);
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(SP_ALARM_RING, key).apply();
    }

    public static String getAlarmRing(Context context) {
        LogUtil.log("getAlarmRing:");
        SharedPreferences sharedPreferences = context.
                getSharedPreferences(GUIDE_SHOW_PROPERTY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SP_ALARM_RING, "");
    }
}
