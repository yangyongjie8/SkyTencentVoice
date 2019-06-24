package com.skyworthdigital.voice.baidu_module.util;

import android.app.Activity;
import android.text.TextUtils;

import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.util.ArrayList;
import java.util.List;


public class ActivityManager {
    private static String TAG = ActivityManager.class.getSimpleName();
    private static List<Activity> list = new ArrayList<Activity>();

    public static void onCreateActivity(Activity activity) {
        if (list == null) {
            list = new ArrayList<>();
        }

        for (Activity a : list) {
            if (a.equals(activity)) {
                return;
            }
        }
        for (Activity a : list) {
            if (TextUtils.equals(a.getClass().getName(), activity.getClass().getName())
                    || (a.getClass().getName().contains("com.skyworthdigital.voiceassistant.videosearch") && activity.getClass().getName().contains("com.skyworthdigital.voiceassistant.videosearch"))) {
                a.finish();
                list.remove(a);
                list.add(activity);
                MLog.i(TAG, "OnCreateActivity111:" + activity.toString() + " " + activity.getClass().getName());
                return;
            }
        }
        list.add(activity);
        MLog.i(TAG, "OnCreateActivity:" + activity.toString() + " " + activity.getClass().getName());
    }

    public static void removeActivity(Activity activity) {
        MLog.i(TAG, "RemoveActivity");
        list.remove(activity);
        /*for (Activity a : list) {
            LogUtil.log("RemoveActivity:" + a.toString());
            if (a == activity) {
                list.remove(a);
            }
        }*/
    }

    public static void destroyActivities() {
        for (Activity a : list) {
            MLog.i(TAG, "DestroyActivities:" + a.toString());
            a.finish();
        }
        list.clear();
    }

    public static List<Activity> getActivityList() {
        return list;
    }

    public static boolean isSeachActivityOn() {
        MLog.i(TAG, "isSeachActivityOn:"+list.size());
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
