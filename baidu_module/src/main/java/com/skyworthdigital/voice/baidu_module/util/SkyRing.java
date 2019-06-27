package com.skyworthdigital.voice.baidu_module.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.baidu.duersdk.voice.VoiceInterface;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.baidu_module.BdController;
import com.skyworthdigital.voice.baidu_module.paipaiAnim.PaiPaiAnimUtil;
import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;

import java.io.IOException;


/**
 * 提示音
 * Created by SDT03046 on 2017/8/25.
 */

public class SkyRing {
    private String TAG = SkyRing.class.getSimpleName();
    private static SkyRing mRingInstance = null;
    private MediaPlayer mSkymediaPlayer;

    static public SkyRing getInstance() {
        if (mRingInstance == null) {
            mRingInstance = new SkyRing();
        }
        return mRingInstance;
    }

    private SkyRing() {
    }

    public void play(int type) {
        VoiceModeAdapter mode = new VoiceModeAdapter();
        if ((!VoiceModeAdapter.isAudioBox() && type == 1) || (mode.isRecognizeGoOn())) {
            play("ding.wav");
        } else {
            play("hello.wav");
        }
    }


    private void play(final String filename) {
        final Context ctx = VoiceApp.getInstance();
//        VolumeUtils.getInstance(ctx).setMuteWithNoUi(false);
        VolumeUtils.getInstance(ctx).setMuteWithNoUi(true);
        mSkymediaPlayer = new MediaPlayer();//MediaPlayer.create(context, resid);
        try {
            AssetFileDescriptor afd = ctx.getAssets().openFd(filename);
            mSkymediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSkymediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mSkymediaPlayer.prepareAsync();
        mSkymediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                MLog.i(TAG, "ring");
                mp.start();
            }
        });

        mSkymediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                MLog.i(TAG, "ring completion");
                BdController bdController = BdController.getInstance();
                if (bdController != null && bdController.isDialogShow()) {
                    if (bdController.mDuerState.ordinal() < VoiceInterface.VoiceState.DUER_RESULT.ordinal()) {
                        if (mSkymediaPlayer != null) {
                            mSkymediaPlayer.stop();
                            mSkymediaPlayer.release();
                            mSkymediaPlayer = null;
                            MLog.i(TAG, "ring release2");
                        }
                        bdController.mVoiceTriggerDialog.recordAnimStart();
                    } else if (BdController.getInstance().mDuerState == VoiceInterface.VoiceState.EXIT) {
                        bdController.mVoiceTriggerDialog.showPaiPaiByID(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
                        MLog.i(TAG, "PaiPai:default");
                    }
                }
            }
        });
    }

    public void stop() {
        MLog.i(TAG, "media stop");
        if (mSkymediaPlayer != null) {
            mSkymediaPlayer.reset();
            mSkymediaPlayer.release();
        }
        mRingInstance = null;
    }
}
