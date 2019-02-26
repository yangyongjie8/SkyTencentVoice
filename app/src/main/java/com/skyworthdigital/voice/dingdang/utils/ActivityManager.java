package com.skyworthdigital.voice.dingdang.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;


public class ActivityManager {
    private static List<Activity> list = new ArrayList<Activity>();

     static void onCreateActivity(Activity activity) {
        for (Activity a : list) {
            if (a.equals(activity)) {
                return;
            }
        }
        list.add(activity);
        //LogUtil.log("OnCreateActivity:" + activity.toString());
    }

    public static void removeActivity(Activity activity) {
        //LogUtil.log("RemoveActivity");
        list.remove(activity);
        /*for (Activity a : list) {
            LogUtil.log("RemoveActivity:" + a.toString());
            if (a == activity) {
                list.remove(a);
            }
        }*/
    }

     static void destroyActivities() {

        for (Activity a : list) {
            //LogUtil.log("DestroyActivities:" + a.toString());
            a.finish();
            //list.remove(a);
        }
    }

    public static List<Activity> getActivityList() {
        return list;
    }

    public static boolean isSeachActivityOn() {
        //LogUtil.log("isSeachActivityOn:"+list.size());
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
