package com.skyworthdigital.voice.dingdang.domains.tvlive.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TVLIST = "create table if not exists "
            + DataTools.TABLE_TVLIST
            + "(key integer primary key AUTOINCREMENT, "
            + "id text not null, "
            + "number int not null, "
            + "type text not null, "
            + "channel text not null, "
            + "channel_name text not null, "
            + "channel_code text not null)";


    MyDatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("database", "onCreate");
        db.execSQL(CREATE_TVLIST);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v("database", "on upgrade oldVersion:" + oldVersion + " newVersion:" + newVersion);
        db.execSQL(CREATE_TVLIST);
    }
}