package com.skyworthdigital.voice.dingdang.domains.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.WindowManager;

import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.control.tts.MyTTS;
import com.skyworthdigital.voice.dingdang.domains.alarm.database.AlarmDbOperator;
import com.skyworthdigital.voice.dingdang.domains.music.utils.QQMusicUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.PrefsUtils;


import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String mContent = context.getString(R.string.str_alarm_note);
        String repeat = "once";
        Calendar calendar = Calendar.getInstance();
        if (intent.hasExtra("content")) {
            mContent = intent.getStringExtra("content");
            MLog.d("alarm", "onReceiver" + mContent);
        }

        if (intent.hasExtra("repeat")) {
            repeat = intent.getStringExtra("repeat");
            MLog.d("alarm", "onReceiver repeat:" + repeat);
        }

        if (intent.hasExtra("time")) {
            String time = intent.getStringExtra("time");
            calendar.setTimeInMillis(System.currentTimeMillis());
            MLog.d("alarm", "onReceiver time:" + time + " cur:" + System.currentTimeMillis());
            if (TextUtils.equals(repeat, "once")) {
                AlarmDbOperator dbOperator = new AlarmDbOperator(VoiceApp.getInstance());
                dbOperator.delete(time);
            } else if (TextUtils.equals(repeat, "weekday")) {
                boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
                int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                //若一周第一天为星期天，则-1
                if (isFirstSunday) {
                    weekDay = weekDay - 1;
                    if (weekDay == 0) {
                        weekDay = 7;
                    }
                }
                MLog.d("alarm", "weekday:" + weekDay);
            }
            if (System.currentTimeMillis() > 60000 + Long.parseLong(time)) {
                MLog.d("alarm", "outime alarm");
                return;
            }
        }


        AlarmDialog dialog = new AlarmDialog(context);
        dialog.setContentText(mContent);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        if (intent.hasExtra("content")) {
            MyTTS.getInstance(null).talkWithoutDisplay(context.getString(R.string.str_alarm_note) + intent.getStringExtra("content"));
        } else {
            MyTTS.getInstance(null).talkWithoutDisplay(context.getString(R.string.str_alarm_note));
        }
        String key = PrefsUtils.getAlarmRing(VoiceApp.getInstance());
        if (!TextUtils.isEmpty(key)) {
            QQMusicUtils.musicSearchAction(VoiceApp.getInstance(), key);
        }
    }
}
