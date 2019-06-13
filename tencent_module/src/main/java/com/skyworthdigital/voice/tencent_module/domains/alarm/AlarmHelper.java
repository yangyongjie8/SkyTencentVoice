package com.skyworthdigital.voice.tencent_module.domains.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.domains.alarm.database.AlarmDbHelper;
import com.skyworthdigital.voice.tencent_module.domains.alarm.database.AlarmDbOperator;

import java.util.ArrayList;

public class AlarmHelper {
    private Context mContext;
    private AlarmManager mAlarmManager;

    public AlarmHelper(Context c) {
        this.mContext = c;
        mAlarmManager = (AlarmManager) c
                .getSystemService(Context.ALARM_SERVICE);
    }

    public void addAlarm(int id, String content, String time, String repeat) {
        Intent intent = new Intent(VoiceApp.getInstance(), AlarmReceiver.class);
        intent.putExtra("_id", id);
        intent.putExtra("content", content);
        intent.putExtra("repeat", repeat);
        intent.putExtra("time", time);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (TextUtils.equals(repeat, "once") || TextUtils.equals(repeat, "year")) {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(time), pi);
        } else if (TextUtils.equals(repeat, "day") || TextUtils.equals(repeat, "weekday")) {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Long.parseLong(time), AlarmManager.INTERVAL_DAY, pi);
        }
    }

    public boolean saveAlarm(String content, String time, String repeat) {
        if (Long.parseLong(time) < System.currentTimeMillis() && TextUtils.equals(repeat, "once")) {
            MLog.d("alarm", "time expire");
            return false;
        }
        MLog.d("alarm", "saveAlarm:"+time);
        ContentValues values = new ContentValues(4);
        values.put(AlarmDbHelper.COL_EVENT_NAME, content);
        values.put(AlarmDbHelper.COL_SWITCH_STATUS, 1);
        values.put(AlarmDbHelper.COL_ALARM_TIME, time);
        values.put(AlarmDbHelper.COL_REPAET_MODE, repeat);
        AlarmDbOperator dbOperator = new AlarmDbOperator(VoiceApp.getInstance());
        if(!dbOperator.insert(values)) {
            return false;
        }
        int id = dbOperator.getAlarmId(time);

        addAlarm(id, content, time, repeat);
        MLog.d("alarm", "add alarm id:" + id);
        return true;
    }

    public void deleteAlram(AlarmDbOperator.AlarmItem alarm) {
        AlarmDbOperator dbOperator = new AlarmDbOperator(VoiceApp.getInstance());
        dbOperator.delete(alarm);
    }

    public ArrayList<AlarmDbOperator.AlarmItem> getAlarmlists() {
        AlarmDbOperator dbOperator = new AlarmDbOperator(VoiceApp.getInstance());
        return dbOperator.getAlarmlist();
    }

    public String getAlarmlistsString() {
        AlarmDbOperator dbOperator = new AlarmDbOperator(VoiceApp.getInstance());
        return dbOperator.getAlarmlistString();
    }


    public void closeAlarm(int id, String time, String content) {
        Intent intent = new Intent();
        intent.putExtra("_id", id);
        //intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("time", time);
        intent.setClass(mContext, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, id, intent, 0);
        mAlarmManager.cancel(pi);
    }
}
