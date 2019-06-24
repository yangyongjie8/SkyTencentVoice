package com.skyworthdigital.voice.alarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.alarm.AlarmHelper;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AlarmDbOperator {
    private AlarmDbHelper mDbHelper;

    public AlarmDbOperator(Context aContext) {
        mDbHelper = AlarmDbHelper.getInstance(aContext);
    }

    /*
    添加闹钟信息
     */
    public boolean insert(ContentValues values) {
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();
        try {
            String QUIRY_ALARM_ID = "SELECT _id," + AlarmDbHelper.COL_ALARM_TIME
                    + " FROM " + AlarmDbHelper.ALARM_TABLE + " WHERE "
                    + AlarmDbHelper.COL_ALARM_TIME + "= " + values.getAsLong(AlarmDbHelper.COL_ALARM_TIME);
            Cursor cursor = dbWriter.rawQuery(QUIRY_ALARM_ID, null);

            if (cursor.moveToNext()) {
                MLog.i("AlarmDbOperator", "alarm already exist");
                cursor.close();
                return false;
            }
            dbWriter.insert(AlarmDbHelper.ALARM_TABLE, null, values);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbWriter.close();
        }
        return true;
    }

    /*
    通过时间来获得存在数据库的闹钟id
     */
    public int getAlarmId(long time) {
        int id = -1;
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();
        try {
            String QUIRY_ALARM_ID = "SELECT _id," + AlarmDbHelper.COL_ALARM_TIME
                    + " FROM " + AlarmDbHelper.ALARM_TABLE + " WHERE "
                    + AlarmDbHelper.COL_ALARM_TIME + "= " + time;
            Cursor cursor = dbWriter.rawQuery(QUIRY_ALARM_ID, null);

            while (cursor.moveToNext()) {
                id = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbWriter.close();
        }

        return id;
    }

    /*public void delete(int id) {
        String[] args = new String[]{String.valueOf(id)};
        dbWriter.delete(AlarmDbHelper.ALARM_TABLE, "_id=?", args);
    }*/

    public void delete(long time) {
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();
        String[] args = new String[]{String.valueOf(time)};
        dbWriter.delete(AlarmDbHelper.ALARM_TABLE, AlarmDbHelper.COL_ALARM_TIME + "=?", args);
        dbWriter.close();
    }
    public void delete(AlarmItem item) {
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();
        String[] args = new String[]{String.valueOf(item.mTime)};
        try {
            String QUIRY_ALARM_ID = "SELECT _id," + AlarmDbHelper.COL_ALARM_TIME + "," + AlarmDbHelper.COL_EVENT_NAME
                    + " FROM " + AlarmDbHelper.ALARM_TABLE + " WHERE "
                    + AlarmDbHelper.COL_ALARM_TIME + "= " + item.mTime;
            Cursor cursor = dbWriter.rawQuery(QUIRY_ALARM_ID, null);

            while (cursor.moveToNext()) {
                AlarmHelper alarm = new AlarmHelper(VoiceApp.getInstance());
                alarm.closeAlarm(cursor.getInt(0), item.mTime, cursor.getString(2));
            }
            cursor.close();
            dbWriter.delete(AlarmDbHelper.ALARM_TABLE, AlarmDbHelper.COL_ALARM_TIME + "=?", args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbWriter.close();
        }
    }

    public void delete(String time) {
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();
        String[] args = new String[]{time};
        dbWriter.delete(AlarmDbHelper.ALARM_TABLE, AlarmDbHelper.COL_ALARM_TIME + "=?", args);
        dbWriter.close();
    }

    public void resetDb() {
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();
        try {
            long time = System.currentTimeMillis();
            String[] args = new String[]{String.valueOf(time)};
            MLog.i("AlarmDbOperator", "reset db:" + args[0]);
            dbWriter.delete(AlarmDbHelper.ALARM_TABLE, AlarmDbHelper.COL_ALARM_TIME + "<?", args);
            Cursor cursor = dbWriter.rawQuery("SELECT * FROM " + AlarmDbHelper.ALARM_TABLE, null);
            if (cursor.moveToFirst()) {
                do {
                    AlarmHelper alarm = new AlarmHelper(VoiceApp.getInstance());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(cursor.getLong(2));
                    MLog.i("AlarmDbOperator", "\n" + cursor.getInt(0) + " " + cursor.getString(2) + " " + cursor.getString(5) + "\n:" + calendar.getTime().toString());
                    alarm.addAlarm(cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex(AlarmDbHelper.COL_CONTENT_TITLE)), cursor.getString(cursor.getColumnIndex(AlarmDbHelper.COL_EVENT_NAME)),
                            cursor.getLong(cursor.getColumnIndex(AlarmDbHelper.COL_ALARM_TIME)), cursor.getString(cursor.getColumnIndex(AlarmDbHelper.COL_REPAET_MODE)), cursor.getString(cursor.getColumnIndex(AlarmDbHelper.COL_SOUND_MODE)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbWriter.close();
        }
    }


    public String getAlarmlistString() {
        StringBuilder sb = new StringBuilder();
        String eventname;
        int idx = 1;
        long time;
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();
        try {
            String QUIRY_ALARM_ID = "SELECT _id," + AlarmDbHelper.COL_ALARM_TIME + "," + AlarmDbHelper.COL_EVENT_NAME
                    + " FROM " + AlarmDbHelper.ALARM_TABLE;
            Cursor cursor = dbWriter.rawQuery(QUIRY_ALARM_ID, null);

            while (cursor.moveToNext()) {
                sb.append("\n");
                sb.append("[" + idx + "]");
                time = Long.parseLong(cursor.getString(1));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm E");
                Date date = new Date(time);
                String timestr = simpleDateFormat.format(date);
                sb.append(timestr);
                sb.append(" ");
                eventname = cursor.getString(2);
                sb.append(eventname);
                idx++;
            }
            //Log.i("wyf", "list:" + sb.toString());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbWriter.close();
        }
        String strcnt = "你一共有" + (idx - 1) + "个闹钟：";
        if (idx > 1) {
            return strcnt + sb.toString();//
        } else {
            return "";
        }
    }

    public ArrayList<AlarmItem> getAlarmlist() {
        ArrayList<AlarmItem> alarmlist = new ArrayList<>();
        SQLiteDatabase dbWriter = mDbHelper.getWritableDatabase();
        try {
            String QUIRY_ALARM_ID = "SELECT _id," + AlarmDbHelper.COL_ALARM_TIME + "," + AlarmDbHelper.COL_EVENT_NAME
                    + " FROM " + AlarmDbHelper.ALARM_TABLE;
            Cursor cursor = dbWriter.rawQuery(QUIRY_ALARM_ID, null);

            while (cursor.moveToNext()) {
                AlarmItem item = new AlarmItem();
                item.id = cursor.getInt(0);
                item.mTime = cursor.getString(1);//time;
                item.title = cursor.getString(2);
                alarmlist.add(item);
            }
            //Log.i("wyf", "list:" + sb.toString());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbWriter.close();
        }

        return alarmlist;
    }

    public class AlarmItem {
        String mTime;
        int id;
        String title;
        String repeat;
    }
}
