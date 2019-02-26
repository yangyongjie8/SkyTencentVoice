package com.skyworthdigital.voice.dingdang.utils;


import android.util.Log;

public class MLog {
    private static boolean mDebug = true;

    public static void setDebugMode(boolean debugMode) {
        MLog.mDebug = debugMode;
    }

    public static void d(String tag, String text) {
        if (MLog.mDebug) {
            debug(tag, text);
        }
    }

    public static void i(String tag, String text) {
        if (MLog.mDebug) {
            Log.i(tag, text);
        }
    }

    public static void w(String tag, String text) {
        if (MLog.mDebug) {
            Log.w(tag, text);
        }
    }

    public static void e(String tag, String text) {
        if (MLog.mDebug) {
            Log.e(tag, text);
        }
    }

    public static void debug(String tag, String text) {

        final int maxLen = 3000;

        final int len = text.length();

        if (len < maxLen + 1) {

            Log.d(tag, text);

            return;

        }


        // android 无法打印超长日志，所以需要分开打印

        Log.d(tag, "max length ==begin==");

        int line = 0;

        int start = 0;

        String szline = null;

        for (start = 0; start + maxLen < len; start += maxLen, ++line) {

            szline = "line[" + line + "]: ";

            Log.d(tag, szline + text.substring(start, start + maxLen));

        }

        // print the last line, prevent substring out of index

        if (start < len) {

            szline = "line[" + line + "]: ";

            Log.d(tag, szline + text.substring(start, len));

        }

        Log.d(tag, "max length ==end==");

    }
}
