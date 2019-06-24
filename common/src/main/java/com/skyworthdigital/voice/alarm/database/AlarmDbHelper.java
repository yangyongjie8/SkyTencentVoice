package com.skyworthdigital.voice.alarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.skyworthdigital.voice.VoiceApp;

import java.io.File;


public class AlarmDbHelper extends SQLiteOpenHelper {
    public static AlarmDbHelper mHelper;

    public final static String ALARM_TABLE = "alarmTabble";
    public final static String ALARM_DB_NAME = "alarm.db";
    public final static String COL_ALARM_TIME = "alarm_time";
    public final static String COL_EVENT_NAME = "eventname";
    public final static String COL_CONTENT_TITLE = "content_title";
    public final static String COL_SWITCH_STATUS = "switch_staus";
    public final static String COL_REPAET_MODE = "repeat";
    public final static String COL_SOUND_MODE = "sound";// sound/displayOnly

    public final static String VALUE_SOUND_MODE_SOUND = "sound";
    public final static String VALUE_SOUND_MODE_DISPLAYONLY = "displayOnly";

    public static String getBasebasePath() {
        return getDatabaseDir() + "/" + ALARM_DB_NAME;
    }

    private static String getDatabaseDir() {
        File databaseDir = new File(VoiceApp.getInstance().getApplicationInfo().dataDir + "/databases/");
        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }

        return databaseDir.getPath();
    }

    public final static String CREATE_ALARM_TABLE = "CREATE TABLE " + ALARM_TABLE
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_EVENT_NAME + " TEXT NOT NULL, "
            + COL_CONTENT_TITLE + " TEXT, "
            + COL_ALARM_TIME + " INTEGER NOT NULL,"
            + COL_SWITCH_STATUS + " INTEGER,"
            + COL_REPAET_MODE + " TEXT,"
            + COL_SOUND_MODE + " TEXT);";


    private AlarmDbHelper(Context aContext, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(aContext, dbName, factory, version);
    }


    public static synchronized AlarmDbHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new AlarmDbHelper(context, getBasebasePath(), null, 3);
        }
        return mHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createLoginTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<2) {
            db.execSQL(CREATE_ALARM_TABLE);
        }
        if(oldVersion<3){
            db.execSQL("alter table "+ALARM_TABLE+" add column "+ COL_CONTENT_TITLE +"  VARCHAR");
            db.execSQL("alter table "+ALARM_TABLE+" add column "+ COL_SOUND_MODE +"  VARCHAR");
        }
    }

    private void createLoginTable(SQLiteDatabase db) {
        db.execSQL(CREATE_ALARM_TABLE);
    }

}
