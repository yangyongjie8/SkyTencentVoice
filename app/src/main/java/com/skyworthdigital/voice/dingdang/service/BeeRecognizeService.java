package com.skyworthdigital.voice.dingdang.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.skyworthdigital.voice.common.AbsController;
import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.music.utils.QQMusicUtils;

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
            QQMusicUtils.isPauseInRemote = true;
            GuideTip.getInstance().pauseQQMusic();
            AbsController.getInstance().isControllerVoice = false;
            AbsController.getInstance().manualRecognizeStart();
        } else if (ACTION_VOICE_RECOGNIZE.equals(action)) {
            String txt = intent.getStringExtra(KEY_ORIGINAL_TXT);
            Log.d(TAG, "RECOGNIZE:" + txt);
            recoverySound();
            AbsController.getInstance().testYuyiParse(txt);
        } else if (ACTION_VOICE_CANCEL.equals(action)) {
            Log.d(TAG, "VOICE_CANCEL");
            recoverySound();
            AbsController.getInstance().cancelYuyiParse();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void recoverySound(){
        VolumeUtils.getInstance(VoiceApp.getInstance()).setMuteWithNoUi(false);
        if(GuideTip.getInstance().mIsQQmusic && QQMusicUtils.isPauseInRemote){
            QQMusicUtils.isPauseInRemote = false;
            GuideTip.getInstance().playQQMusic();
        }
    }
}
