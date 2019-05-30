package com.skyworthdigital.voice.dingdang.domains.tvlive.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONArray;
import org.json.JSONException;


public class DbUtils {
    private MyDatabaseHelper mMyDBHelper;
    private static final int DATABASE_VERSION = 6;


    /**
     * DbUtils类需要实例化数据库Help类,只有得到帮助类的对象我们才可以实例化 SQLiteDatabase
     */
    public DbUtils(Context context) {
        mMyDBHelper = new MyDatabaseHelper(context, DataTools.getBasebasePath(), DATABASE_VERSION);
    }

    public boolean updateDbFromNetwork(JSONArray xunmaTvList) {
        if (xunmaTvList == null) {
            return false;
        }
        SQLiteDatabase sqLiteDatabase = mMyDBHelper.getWritableDatabase();
        Cursor cursor = null, cursor_id = null;
        try {
            for (int i = 0; i < xunmaTvList.length(); i++) {
                String id = xunmaTvList.getJSONObject(i).getString("id");
                int number = xunmaTvList.getJSONObject(i).getInt("number");
                //LogUtil.log(i + ":\n" + "id:" + id);
                String type = xunmaTvList.getJSONObject(i).getString("type");
                String channel = xunmaTvList.getJSONObject(i).getString("channel");

                String all_sql_sel = "SELECT * FROM " + DataTools.TABLE_TVLIST + " WHERE " +
                        "id=" + "'" + id + "'" +
                        " AND number=" + "'" + number + "'" +
                        " AND channel=" + "'" + channel + "'" +
                        " AND type=" + "'" + type + "'";
                String id_sql_sel = "SELECT * FROM " + DataTools.TABLE_TVLIST + " WHERE " +
                        "id=" + "'" + id + "'";
                cursor = sqLiteDatabase.rawQuery(all_sql_sel, null);

                if (!cursor.moveToNext()) {
                    cursor_id = sqLiteDatabase.rawQuery(id_sql_sel, null);
                    if (cursor_id.moveToNext()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id", id);
                        contentValues.put("number", number);
                        contentValues.put("type", type);
                        contentValues.put("channel", channel);
                        MLog.d("tvlive", "update item:" + " " + all_sql_sel);
                        sqLiteDatabase.update(DataTools.TABLE_TVLIST, contentValues, "id=?", new String[]{id});
                    } else {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("id", id);
                        contentValues.put("number", number);
                        contentValues.put("type", type);
                        contentValues.put("channel", channel);
                        contentValues.put("channel_name", "NULL");
                        contentValues.put("channel_code", "NULL");
                        MLog.d("tvlive", "updateDbFromNetwork:" + " " + all_sql_sel);
                        //LogUtil.log("add item:" + " " + all_sql_sel);
                        sqLiteDatabase.insert(DataTools.TABLE_TVLIST, null, contentValues);
                    }
                }
                //if (cursor != null) {
                cursor.close(); // 记得关闭 corsor
                //}
                if (cursor_id != null) {
                    cursor_id.close(); // 记得关闭 corsor
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // 记得关闭 corsor
            }
            if (cursor_id != null) {
                cursor_id.close(); // 记得关闭 corsor
            }
            sqLiteDatabase.close();
        }
        return true;
    }

    public String searchItem(String channelname, String channelcode, String slotname, String speechname) {
        if (TextUtils.isEmpty(channelname) && TextUtils.isEmpty(channelcode) && TextUtils.isEmpty(speechname) && TextUtils.isEmpty(slotname)) {
            return null;
        }
        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();

        try {
            // 查询比较特别,涉及到 cursor
            String current_sql_sel = "SELECT  * FROM " + DataTools.TABLE_TVLIST + " WHERE type!='Test' AND (";
            if (!TextUtils.isEmpty(channelname) && TextUtils.isEmpty(channelcode)) {
                current_sql_sel += "channel_name=" + "'" + channelname + "高清'" + " or channel_name=" + "'" + channelname + "'";
            } else if (TextUtils.isEmpty(channelname) && !TextUtils.isEmpty(channelcode)) {
                current_sql_sel += "channel_code=" + "'" + channelcode + "'";
            } else if (!TextUtils.isEmpty(channelname) && !TextUtils.isEmpty(channelcode)) {
                current_sql_sel += "channel_name=" + "'" + channelname + "高清'" + " or channel_name=" + "'" + channelname + "'" + " or channel_code=" + "'" + channelcode + "'";
            }

            if (!TextUtils.isEmpty(slotname)) {
                if (!TextUtils.isEmpty(channelname) || !TextUtils.isEmpty(channelcode)) {
                    current_sql_sel += " or channel" + " like '%" + slotname + "%'";
                } else {
                    current_sql_sel += "channel" + " like '%" + slotname + "%'";
                }
            }

            if (!TextUtils.isEmpty(speechname)) {
                if (!TextUtils.isEmpty(channelname) || !TextUtils.isEmpty(channelcode) || !TextUtils.isEmpty(slotname)) {
                    current_sql_sel += " or channel" + " like '%" + speechname + "%'";
                } else {
                    current_sql_sel += "channel" + " like '%" + speechname + "%'";
                }
            }
            current_sql_sel += ")";
            MLog.d("TvLiveControl", "searchItem:" + " " + current_sql_sel);
            Cursor cursor = readableDatabase.rawQuery(current_sql_sel, null);
            if (cursor.moveToNext()) {
                //LogUtil.log("matched and get first item.");
                String ret = cursor.getString(cursor.getColumnIndex("id"));
                cursor.close(); // 记得关闭 corsor
                return ret;
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readableDatabase.close(); // 关闭数据库
        }
        return null;
    }

    public String searchByType(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }

        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();
        try {
            String current_sql_sel = "SELECT  * FROM " + DataTools.TABLE_TVLIST + " WHERE type=" + "'" + type + "'";
            Cursor cursor = readableDatabase.rawQuery(current_sql_sel, null);
            if (cursor.moveToNext()) {
                //LogUtil.log("searchByType matched");
                String ret = cursor.getString(cursor.getColumnIndex("id"));
                cursor.close(); // 记得关闭 corsor
                return ret;
            } else {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readableDatabase.close(); // 关闭数据库
        }
        return null;
    }


    /**
     * 功能：用于数据库生成,后续更新数据库调试会用到,勿删
     */
    /*public long addItem(String id, int number, String type, String channel, String channel_name, String channel_code) {
        SQLiteDatabase sqLiteDatabase = mMyDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("number", idx);
        contentValues.put("id", id);
        contentValues.put("number", number);
        contentValues.put("type", type);
        contentValues.put("channel", channel);
        contentValues.put("channel_name", channel_name);
        contentValues.put("channel_code", channel_code);

        long rowid = sqLiteDatabase.insert(DataTools.TABLE_TVLIST, null, contentValues);

        sqLiteDatabase.close();
        return rowid;
    }*/
}
