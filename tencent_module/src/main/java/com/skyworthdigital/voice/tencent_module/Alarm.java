package com.skyworthdigital.voice.tencent_module;

import android.util.Log;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.alarm.AlarmHelper;
import com.skyworthdigital.voice.alarm.database.AlarmDbHelper;
import com.skyworthdigital.voice.tencent_module.model.Slot;
import com.skyworthdigital.voice.tencent_module.model.ValueItem;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Ives 2019/6/14
 */
public class Alarm {

    private Calendar mCalendar = Calendar.getInstance();
    public final static int ERROR_EXIST = 1;
    public final static int ERROR_OTHER = 3;
    public final static int SUCCESS = 4;

    public int addClock(List<Slot> slots) {
        if (slots == null) {
            return Alarm.ERROR_OTHER;
        }
        boolean valid = false;
        String eventTitle = "";

        try {
            long cur = System.currentTimeMillis();
            mCalendar.setTimeInMillis(cur);
            Log.i("wyf", "cur:" + mCalendar.get(Calendar.YEAR) + mCalendar.get(Calendar.MONTH) + " " + mCalendar.get(Calendar.DATE) + " " + mCalendar.get(Calendar.HOUR) + ":" + mCalendar.get(Calendar.MINUTE));

            for (Slot tmp : slots) {
                switch (tmp.mName) {
                    case "time":
                    case "date":
                        ValueItem.DateTime datetime = tmp.mValueList.get(0).mDateTime;
                        if (datetime.mYear >= 0 || datetime.mMon >= 0 || datetime.mDay >= 0 || datetime.mHour >= 0 || datetime.mMin >= 0) {
                            if (datetime.mYear >= 0 && datetime.mYear > 2017) {
                                mCalendar.set(Calendar.YEAR, datetime.mYear);
                            }
                            if (datetime.mMon >= 0 && datetime.mMon < 13) {
                                mCalendar.set(Calendar.MONTH, datetime.mMon - 1);
                            }
                            if (datetime.mDay >= 0 && datetime.mDay < 31) {
                                mCalendar.set(Calendar.DATE, datetime.mDay);
                            }
                            if (datetime.mHour >= 0 && datetime.mHour < 24) {
                                mCalendar.set(Calendar.HOUR_OF_DAY, datetime.mHour);
                                valid = true;
                            }

                            if (datetime.mMin >= 0 && datetime.mMin < 61) {
                                mCalendar.set(Calendar.MINUTE, datetime.mMin);
                            }

                            if (datetime.mSec >= 0) {
                                mCalendar.set(Calendar.SECOND, datetime.mSec);
                            }
                            Log.i("wyf", "datetime:" + mCalendar.get(Calendar.YEAR) + "/" + mCalendar.get(Calendar.MONTH) + "/" + mCalendar.get(Calendar.DATE) + " or " + datetime.mYear + " " + datetime.mMon + " " + datetime.mDay + " " + datetime.mHour + " " + datetime.mMin + " cur:" + cur);
                        }
                        ValueItem.Repeat.Interval.IntervalTime starttime = tmp.mValueList.get(0).mRepeat.mInterval.mStart;
                        if (starttime.mYear >= 0 || starttime.mMon >= 0 || starttime.mDay >= 0 || starttime.mHour >= 0 || starttime.mMin >= 0) {
                            if (starttime.mYear >= 0 && starttime.mYear > 2017) {
                                mCalendar.set(Calendar.YEAR, starttime.mYear);
                            }
                            if (starttime.mMon >= 0 && starttime.mMon < 13) {
                                mCalendar.set(Calendar.MONTH, starttime.mMon - 1);
                            }
                            if (starttime.mDay >= 0 && starttime.mDay < 31) {
                                mCalendar.set(Calendar.DATE, starttime.mDay);
                            }
                            if (starttime.mHour >= 0 && starttime.mHour < 24) {
                                mCalendar.set(Calendar.HOUR_OF_DAY, starttime.mHour);
                                valid = true;
                            }

                            if (starttime.mMin >= 0 && starttime.mMin < 61) {
                                mCalendar.set(Calendar.MINUTE, starttime.mMin);
                            }

                            if (starttime.mSec >= 0) {
                                mCalendar.set(Calendar.SECOND, starttime.mSec);
                            }
                            Log.i("wyf", "mStart:" + mCalendar.get(Calendar.YEAR) + "/" + mCalendar.get(Calendar.MONTH) + "/" + mCalendar.get(Calendar.DATE) + " or " + starttime.mYear + " " + starttime.mMon + " " + starttime.mDay + " " + starttime.mHour + " " + starttime.mMin);
                        }
                        break;
                    case "note":
                        if (tmp.mValueList.size() > 0) {
                            eventTitle = tmp.mValueList.get(0).mOriginalText;
                        }
                        break;
                }
            }
            if (valid) {
                AlarmHelper alarm = new AlarmHelper(VoiceApp.getInstance());
                Log.i("wyf", "time cur:" + cur + " " + mCalendar.getTimeInMillis());
                mCalendar.set(Calendar.MILLISECOND, 0);
                mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                if (alarm.saveAlarm(eventTitle, eventTitle, mCalendar.getTimeInMillis(), "once", AlarmDbHelper.VALUE_SOUND_MODE_SOUND)) {
                    return SUCCESS;
                } else {
                    return ERROR_EXIST;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_OTHER;
        }
        return ERROR_OTHER;
    }


    @Override
    public String toString() {
        return "";
    }
}
