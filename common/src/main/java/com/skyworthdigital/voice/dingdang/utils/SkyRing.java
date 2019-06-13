package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;

import java.io.IOException;


/**
 * 提示音
 * Created by SDT03046 on 2017/8/25.
 */

public class SkyRing {
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

    public void play(final String filename, final String tts) {
        try {
            if (mSkymediaPlayer == null) {
                mSkymediaPlayer = new MediaPlayer();
            } else {
                mSkymediaPlayer.reset();
            }
            mSkymediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mSkymediaPlayer.setDataSource(filename);
            mSkymediaPlayer.prepareAsync();
            MLog.d("wyf", "jumpToSound:" + filename);
            mSkymediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //LogUtil.log("ring");
                    mp.start();
                }
            });

            MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    MLog.d("wyf", "onCompletion:");
                    mSkymediaPlayer.release();
                    mSkymediaPlayer = null;
                    if (!TextUtils.isEmpty(tts)) {
                        AbsTTS.getInstance(null).talkWithoutDisplay(tts);
                    }
                }

            };
            mSkymediaPlayer.setOnCompletionListener(onCompletionListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        //LogUtil.log("media stop");
        if (mSkymediaPlayer != null) {
            mSkymediaPlayer.reset();
            mSkymediaPlayer.release();
        }
        mRingInstance = null;
    }

    public void playDing() {
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_REMOTE) {
            final Context ctx = VoiceApp.getInstance();
            if (mSkymediaPlayer == null) {
                mSkymediaPlayer = new MediaPlayer();
            } else {
                mSkymediaPlayer.reset();
            }
            try {
                AssetFileDescriptor afd = ctx.getAssets().openFd("ding.wav");
                mSkymediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }

            mSkymediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mSkymediaPlayer.prepareAsync();
            mSkymediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    MLog.d("wyf", "ring");
                    mp.start();
                }
            });
        }
    }

    public void play(final String filename) {
        final Context ctx = VoiceApp.getInstance();

        mSkymediaPlayer = new MediaPlayer();//MediaPlayer.create(context, resid);
        try {
            AssetFileDescriptor afd = ctx.getAssets().openFd(filename);
            mSkymediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSkymediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mSkymediaPlayer.prepareAsync();
        mSkymediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                MLog.d("wyf", "ring");
                mp.start();
            }
        });

        mSkymediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                    @Override
                                                    public void onCompletion(MediaPlayer arg0) {
                                                        MLog.d("wyf", "ring completion");
                                                        if (mSkymediaPlayer != null) {
                                                            mSkymediaPlayer.stop();
                                                            mSkymediaPlayer.release();
                                                            mSkymediaPlayer = null;
                                                            MLog.d("wyf", "ring release2");
                                                        }
                                                    }
                                                }

        );
    }
}
