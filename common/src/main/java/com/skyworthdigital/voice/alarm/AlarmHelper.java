package com.skyworthdigital.voice.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.alarm.database.AlarmDbHelper;
import com.skyworthdigital.voice.alarm.database.AlarmDbOperator;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.util.ArrayList;

public class AlarmHelper {
    private Context mContext;
    private AlarmManager mAlarmManager;

    public AlarmHelper(Context c) {
        this.mContext = c;
        mAlarmManager = (AlarmManager) c
                .getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * 添加节目单提醒
     */
    public void addMovieProgramAlarm(){
        VoiceApp.getInstance().registerReceiver(new AlarmReceiver(), new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void addAlarm(int id, String contentTitle, String content, long time, String repeat, String sound) {
        Intent intent = new Intent(VoiceApp.getInstance(), AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ACTION_REMIND_BYUSER);
        intent.putExtra("_id", id);
        intent.putExtra("content", content);
        intent.putExtra("contentTitle", contentTitle);
        intent.putExtra("repeat", repeat);
        intent.putExtra("time", time);
        intent.putExtra("sound", sound);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (TextUtils.equals(repeat, "once") || TextUtils.equals(repeat, "year")) {
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, time, pi);
        } else if (TextUtils.equals(repeat, "day") || TextUtils.equals(repeat, "weekday")) {
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pi);
        }
    }
    public boolean saveAlarm(String contentTitle, String content, long time, String repeat, String sound) {
        if (time < System.currentTimeMillis() && TextUtils.equals(repeat, "once")) {
            MLog.i("AlarmHelper", "time expire");
            return false;
        }

        ContentValues values = new ContentValues(5);
        values.put(AlarmDbHelper.COL_EVENT_NAME, content);
        values.put(AlarmDbHelper.COL_CONTENT_TITLE, contentTitle);
        values.put(AlarmDbHelper.COL_SWITCH_STATUS, 1);
        values.put(AlarmDbHelper.COL_ALARM_TIME, time);
        values.put(AlarmDbHelper.COL_REPAET_MODE, repeat);
        values.put(AlarmDbHelper.COL_SOUND_MODE, sound);
        AlarmDbOperator dbOperator = new AlarmDbOperator(VoiceApp.getInstance());
        dbOperator.insert(values);
        int id = dbOperator.getAlarmId(time);

        addAlarm(id, contentTitle, content, time, repeat, sound);
        MLog.i("AlarmHelper", "add alarm id:" + id);
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
