package com.skyworthdigital.voice.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONArray;
import org.json.JSONException;


public class DbUtils {
    private static final String TAG = DbUtils.class.getSimpleName();
    private MyDatabaseHelper mMyDBHelper;
    private static final int DATABASE_VERSION = 7;


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
        Cursor cursor_id = null;
        try {
            for (int i = 0; i < xunmaTvList.length(); i++) {
                String category_id = xunmaTvList.getJSONObject(i).getString("category_id");
                String channel_id = xunmaTvList.getJSONObject(i).getString("channel_id");
                String chnname = xunmaTvList.getJSONObject(i).getString("chnname");

                String id_sql_sel = "SELECT * FROM " + DataTools.TABLE_TVLIST + " WHERE " +
                        "category_id='" + category_id + "' and channel_id='" + channel_id +"' and chnname='"+chnname+"'";

                cursor_id = sqLiteDatabase.rawQuery(id_sql_sel, null);
                if (cursor_id.moveToNext()) {
                    // 已有，无需更新
//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put("category_id", category_id);
//                    contentValues.put("channel_id", channel_id);
//                    contentValues.put("chnname", chnname);
//                    LogUtil.log("update item:" + " " + id_sql_sel);
//                    sqLiteDatabase.update(DataTools.TABLE_TVLIST, contentValues, "category_id=? and channel_id=?", new String[]{category_id,channel_id});
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("category_id", category_id);
                    contentValues.put("channel_id", channel_id);
                    contentValues.put("chnname", chnname);
                    MLog.i(TAG, "add item:" + " " + id_sql_sel);
                    sqLiteDatabase.insert(DataTools.TABLE_TVLIST, null, contentValues);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (cursor_id != null) {
                cursor_id.close(); // 记得关闭 corsor
            }
            sqLiteDatabase.close();
        }
        return true;
    }

    public String[] searchItem(String categoryId, String channelId, String slotname, String speechname) {
        if (TextUtils.isEmpty(categoryId) && TextUtils.isEmpty(channelId) && TextUtils.isEmpty(speechname) && TextUtils.isEmpty(slotname)) {
            return null;
        }
        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();

        try {
            // 查询比较特别,涉及到 cursor
            String current_sql_sel = "SELECT  * FROM " + DataTools.TABLE_TVLIST + " WHERE (";
            if (!TextUtils.isEmpty(categoryId) && TextUtils.isEmpty(channelId)) {
                current_sql_sel += "category_id=" + "'" + categoryId + "'" + " and channel_id=" + "'" + channelId + "'";
            }

            if (!TextUtils.isEmpty(slotname)) {
                if (!TextUtils.isEmpty(categoryId) && !TextUtils.isEmpty(channelId)) {
                    current_sql_sel += " or chnname" + " like '%" + slotname + "%'";
                } else {
                    current_sql_sel += "chnname" + " like '%" + slotname + "%'";
                }
            }

            if (!TextUtils.isEmpty(speechname)) {
                if ((!TextUtils.isEmpty(categoryId) && !TextUtils.isEmpty(channelId)) || !TextUtils.isEmpty(slotname)) {
                    current_sql_sel += " or chnname" + " like '%" + speechname + "%'";
                } else {
                    current_sql_sel += "chnname" + " like '%" + speechname + "%'";
                }
            }
            current_sql_sel += ")";
            MLog.i(TAG, "searchItem:" + " " + current_sql_sel);
            Cursor cursor = readableDatabase.rawQuery(current_sql_sel, null);
            if (cursor.moveToNext()) {
                MLog.i(TAG, "matched and get first item.");
                String category_id = cursor.getString(cursor.getColumnIndex("category_id"));
                String channel_id = cursor.getString(cursor.getColumnIndex("channel_id"));
                cursor.close(); // 记得关闭 corsor
                return new String[]{category_id,channel_id};
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
     *
     * @param channelName
     * @return [categoryId,channelId]
     */
    public String[] searchByName(String channelName) {
        if (TextUtils.isEmpty(channelName)) {
            return null;
        }

        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();
        try {
            String current_sql_sel = "SELECT  * FROM " + DataTools.TABLE_TVLIST + " WHERE chnname=" + "'" + channelName + "' or chnname like '%"+ channelName+"%'";
            Cursor cursor = readableDatabase.rawQuery(current_sql_sel, null);
            if (cursor.moveToNext()) {
                MLog.i(TAG, "searchByName matched");
                String category_id = cursor.getString(cursor.getColumnIndex("category_id"));
                String channel_id = cursor.getString(cursor.getColumnIndex("channel_id"));
                cursor.close(); // 记得关闭 corsor
                return new String[]{category_id,channel_id};
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
