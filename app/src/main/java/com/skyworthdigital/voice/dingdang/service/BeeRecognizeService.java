package com.skyworthdigital.voice.dingdang.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.skyworthdigital.voice.dingdang.MainControler;

/**
 * User: yangyongjie
 * Date: 2019-01-17
 * Description:
 */
public class BeeRecognizeService extends Service {
    public static final String TAG = "BeeRecognizeService";
    public static final String ACTION_VOICE_ACTIVATE = "com.skyworthdigital.voice.ACTIVATE";
    public static final String ACTION_VOICE_RECOGNIZE = "com.skyworthdigital.voice.RECOGNIZE";
    public static final String ACTION_VOICE_CANCEL = "com.skyworthdigital.voice.CANCEL";
    public static final String KEY_ORIGINAL_TXT = "original_txt";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_VOICE_ACTIVATE.equals(action)) {
            MainControler.getInstance().isControllerVoice = false;
            MainControler.getInstance().manualRecognizeStart();
        } else if (ACTION_VOICE_RECOGNIZE.equals(action)) {
            String txt = intent.getStringExtra(KEY_ORIGINAL_TXT);
            Log.d(TAG, "RECOGNIZE:" + txt);
            MainControler.getInstance().testYuyiParse(txt);
        } else if (ACTION_VOICE_CANCEL.equals(action)) {
            Log.d(TAG, "VOICE_CANCEL");
            MainControler.getInstance().cancelYuyiParse();

        }
        return super.onStartCommand(intent, flags, startId);
    }
}
