package com.skyworthdigital.voice.alarm;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.alarm.database.AlarmDbHelper;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;


public class Alarm {
    private String TAG = Alarm.class.getSimpleName();

    private Calendar mCalendar = Calendar.getInstance();
    public final static int ERROR_NOT_SUPPORT_REPEAT = 1;
    public final static int ERROR_NOT_END = 2;
    public final static int ERROR_OTHER = 3;
    //public final static int ERROR_TIMEEXPIRE = 5;
    public final static int SUCCESS = 4;

    public int addClock(String result) {
        AlarmTime alarmTime;
        String eventTitle;
        int hourOfDay = 255, minute = 255;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject resultObj = jsonObject.getJSONObject("result");
            JSONObject slot = resultObj.getJSONObject("nlu").getJSONObject("slots");
            if (resultObj.has("should_end_session")) {
                boolean end = resultObj.getBoolean("should_end_session");
                MLog.i(TAG, "end:" + end);
                if (!end) {
                    MLog.i(TAG, "alarm set not over");
                    return ERROR_NOT_END;
                }
            } else {
                MLog.i(TAG, "no should_end_session");
            }
            if (slot.has("alarm_time")) {
                String alarm_time = slot.getString("alarm_time");
                Log.i("wyf", "alarm_time:" + alarm_time);
                Gson gson = new Gson();
                alarmTime = gson.fromJson(alarm_time, AlarmTime.class);
                if (slot.has("event_title")) {
                    eventTitle = slot.getString("event_title");
                } else {
                    eventTitle = "";
                }
                if (TextUtils.isEmpty(alarmTime.getRepeat())) {
                    alarmTime.setRepeat("once");
                    //return ERROR_NOT_SUPPORT_REPEAT;
                } else if (!TextUtils.equals(alarmTime.getRepeat(), "year")) {
                    return ERROR_NOT_SUPPORT_REPEAT;
                }/* else if (!(TextUtils.equals(mAlarmTime.getRepeat(), "day")
                        || TextUtils.equals(mAlarmTime.getRepeat(), "week")
                        || TextUtils.equals(mAlarmTime.getRepeat(), "year")
                        || TextUtils.equals(mAlarmTime.getRepeat(), "weekday"))) {
                    return ERROR_NOT_SUPPORT_REPEAT;
                }*/
                //Log.i("wyf", "event_title:" + mEventTitle + " " + mAlarmTime.getApm() + mAlarmTime.getDay() + " " + mCalendar.getTimeInMillis());

                mCalendar.setTimeInMillis(System
                        .currentTimeMillis());
                if (!TextUtils.isEmpty(alarmTime.getDay())) {
                    if (alarmTime.getDay().startsWith("+")) {
                        Log.i("wyf", "day:" + alarmTime.getDay().substring(1));
                        mCalendar.add(Calendar.DATE, Integer.parseInt(alarmTime.getDay().substring(1)));
                    } else if (TextUtils.equals(alarmTime.getDay(), "next")) {
                        mCalendar.add(Calendar.DATE, 1);
                    } else {
                        mCalendar.set(Calendar.DATE, Integer.parseInt(alarmTime.getDay()));
                    }
                }
                if (!TextUtils.isEmpty(alarmTime.getHour())) {
                    if (alarmTime.getHour().startsWith("+")) {
                        Log.i("wyf", "hour:" + alarmTime.getHour().substring(1));
                        mCalendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmTime.getHour().substring(1)));
                    } else {
                        hourOfDay = Integer.parseInt(alarmTime.getHour());
                        if (TextUtils.equals(alarmTime.getApm(), "pm")) {
                            if (hourOfDay < 12) {
                                hourOfDay += 12;
                            }
                        }
                        if (hourOfDay > 24) {
                            return ERROR_OTHER;
                        }
                        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        mCalendar.set(Calendar.SECOND, 0);
                    }
                }
                if (!TextUtils.isEmpty(alarmTime.getMonth())) {
                    if (TextUtils.equals(alarmTime.getMonth(), "next")) {
                        mCalendar.add(Calendar.MONTH, 1);
                    } else if (alarmTime.getMonth().startsWith("+")) {
                        Log.i("wyf", "month:" + alarmTime.getMonth().substring(1));
                        mCalendar.add(Calendar.MONTH, Integer.parseInt(alarmTime.getMonth().substring(1)));
                    } else {
                        mCalendar.set(Calendar.MONTH, Integer.parseInt(alarmTime.getMonth()));
                    }
                }

                if (!TextUtils.isEmpty(alarmTime.getMinute())) {
                    if (alarmTime.getMinute().startsWith("+")) {
                        Log.i("wyf", "MINUTE:" + alarmTime.getMinute().substring(1));
                        mCalendar.add(Calendar.MINUTE, Integer.parseInt(alarmTime.getMinute().substring(1)));
                    } else {
                        mCalendar.set(Calendar.SECOND, 0);
                        minute = Integer.parseInt(alarmTime.getMinute());
                        if (minute < 60) {
                            mCalendar.set(Calendar.MINUTE, minute);
                        } else {
                            return ERROR_OTHER;
                        }
                    }
                } else if (hourOfDay != 255) {
                    mCalendar.set(Calendar.MINUTE, 0);
                    mCalendar.set(Calendar.SECOND, 0);
                }
                mCalendar.set(Calendar.MILLISECOND, 0);
                mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                Log.i("wyf", "hour:" + hourOfDay + " min:" + minute + " " + mCalendar.getTimeInMillis());
                AlarmHelper alarm = new AlarmHelper(VoiceApp.getInstance());
                alarm.saveAlarm(VoiceApp.getInstance().getString(R.string.str_alarm_note), eventTitle, mCalendar.getTimeInMillis(), alarmTime.getRepeat(), AlarmDbHelper.VALUE_SOUND_MODE_SOUND);
            } else {
                return ERROR_OTHER;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_OTHER;
        }
        return SUCCESS;
    }

    @Override
    public String toString() {
        return "";
    }
}