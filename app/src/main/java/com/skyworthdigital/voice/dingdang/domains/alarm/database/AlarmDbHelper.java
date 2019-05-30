package com.skyworthdigital.voice.dingdang.domains.alarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.skyworthdigital.voice.VoiceApp;

import java.io.File;


public class AlarmDbHelper extends SQLiteOpenHelper {
    private static AlarmDbHelper mHelper;
    final static String ALARM_TABLE = "alarmTabble";
    private final static String ALARM_DB_NAME = "alarm.db";
    public final static String COL_ALARM_TIME = "alarm_time";
    public final static String COL_EVENT_NAME = "eventname";
    public final static String COL_SWITCH_STATUS = "switch_staus";
    public final static String COL_REPAET_MODE = "repeat";

    private static String getBasebasePath() {
        return getDatabaseDir() + "/" + ALARM_DB_NAME;
    }

    private static String getDatabaseDir() {
        File databaseDir = new File(VoiceApp.getInstance().getApplicationInfo().dataDir + "/databases/");
        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }

        return databaseDir.getPath();
    }

    private final static String CREATE_ALARM_TABLE = "CREATE TABLE " + ALARM_TABLE
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_EVENT_NAME + " TEXT NOT NULL, "
            + COL_ALARM_TIME + " TEXT NOT NULL,"
            + COL_SWITCH_STATUS + " INTEGER,"
            + COL_REPAET_MODE + " TEXT);";


    private AlarmDbHelper(Context aContext, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(aContext, dbName, factory, version);
    }


    public static synchronized AlarmDbHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new AlarmDbHelper(context, getBasebasePath(), null, 2);
        }
        return mHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createLoginTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_ALARM_TABLE);
    }

    private void createLoginTable(SQLiteDatabase db) {
        db.execSQL(CREATE_ALARM_TABLE);
    }

}
