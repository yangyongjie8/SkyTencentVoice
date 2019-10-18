package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.Utils;


/**
 * 操作盒子声音工具类
 */
public class VolumeUtils {
    private static final String TAG = VolumeUtils.class.getSimpleName();

    private static VolumeUtils mVolumeInstance = null;
    private AudioManager mAudioManager;
    private boolean mTempMute = false;
    private String mModel = Utils.get("ro.product.model");
    private int mTempVolume = -1;

    /**
     * 获取实例
     */
    public static VolumeUtils getInstance(Context context) {
        if (mVolumeInstance == null) {
            mVolumeInstance = new VolumeUtils(context);
        }
        return mVolumeInstance;
    }

    private VolumeUtils(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 增加音量
     * <p>
     * param volume
     */
    private int scaleVolume(int volume) {
        int max_vol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current_vol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if ((volume + current_vol) > max_vol) {
            return max_vol;
        }
        //LogUtil.log("scaleVolume max:"+max_vol+" current_vol:"+current_vol+" addVol:"+addVol);
        return (volume + current_vol);
    }

    public int getVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void setVolume(double volume) {
        int max_vol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int scaledVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (volume >= 0 && volume < 1.0) {
            volume = (int) (volume * max_vol);
            AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_ok));
        }
        if (volume >= 0 && volume <= max_vol) {
            scaledVolume = (int) volume;
            AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_volume_set) + scaledVolume);
        } else if (volume > max_vol) {
            scaledVolume = max_vol;
            AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_volume_max) + scaledVolume);
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, scaledVolume, AudioManager.FLAG_SHOW_UI);
    }

    public void setVolumeMax() {
        int max_vol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max_vol, AudioManager.FLAG_SHOW_UI);
    }

    public void setAlarmDefaultVolume(Context ctx){
        int max_vol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int scaledVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        if(VoiceApp.isDuer){
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, max_vol/2, AudioManager.FLAG_VIBRATE);
        }else {
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, max_vol/2, AudioManager.FLAG_VIBRATE);
        }
    }

    /**
     * 增加音量，并显示音量条
     * <p>
     * param volume
     */
    public void setVolumePlus(double volume) {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }else {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
        }
        int val = (int) volume;
        if (val == 1) {
            val = 2;
        }
        int scaledVolume = scaleVolume(val);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, scaledVolume, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 减小音量，并显示音量条，
     * <p>
     * param volume
     */
    public void setVolumeMinus(int volume) {
        int scaledVolume;
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }else {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
        }
        int current_vol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int addVol = (int) volume;
        if (addVol == 1) {
            addVol = 2;
        }
        scaledVolume = current_vol - addVol;
        if (scaledVolume < 0) {
            scaledVolume = 0;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, scaledVolume, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 设置静音
     */
    public void setMute() {
        mTempMute = false;
        MLog.d("wyf", "cmd mute");
        //if (mModel.equals("M2001")) {
        mTempVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
        //} else {
        //    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI);
        //}
    }

    /**
     * 取消静音
     */
    public void cancelMute() {
        mTempMute = false;
        MLog.d("wyf", "cmd unmute");
        //if (mModel.equals("M2001")) {
        if (mTempVolume > 0) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mTempVolume, AudioManager.FLAG_SHOW_UI);
        }
        //} else {
        //    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
        //}
    }

    public void setMuteWithNoUi(boolean mute) {
        MLog.i(TAG, "setMuteWithNoUi:" + mute);
        boolean cur = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cur = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        }else {
            cur = mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
        }
        MLog.i(TAG, "setMuteWithNoUi stream mute:"+cur);

        if (mute) {
            if (cur && !mTempMute) {
                //本身就是静音，就不用再设置了
                MLog.d("wyf", "origin is mute");
                mTempMute = false;
            } else {
                MLog.d("wyf", "set mute");
                mTempMute = true;
                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                }else {
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
            }
        } else if (mTempMute) {
            MLog.d("wyf", "cancel mute");
//            mTempMute = false;
            if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }else {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
        }
        boolean printMute = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            printMute = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        }else {
            printMute = mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
        }
        MLog.i(TAG, "setMuteWithNoUi stream mute final:"+printMute);
    }

    private int getRobotVolume() {
        int current_vol = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        if (current_vol > 14) {
            current_vol = 7;
        } else if (current_vol >= 2) {
            current_vol = current_vol / 2;
        }
        //LogUtil.log("getRobotVolume:" + current_vol);
        return current_vol;
    }

    public int setRobotVolume() {
        int current_vol = getRobotVolume();
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, current_vol, 0);
        //LogUtil.log("setRobotVolume:" + current_vol);
        return current_vol;
    }

    public void setAlarmVolumePlus(int volume) {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
        }else {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
        }

        int max_vol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int current_vol = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int scaledVolume = volume + current_vol;
        if (scaledVolume > max_vol) {
            scaledVolume = max_vol;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, scaledVolume, AudioManager.FLAG_SHOW_UI);
        AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_volume_plus) + scaledVolume);
    }

    /**
     * 减小音量，并显示音量条，
     * param volume
     */
    public void setAlarmVolumeMinus(int volume) {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
        }else {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI);
        }
        int current_vol = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int scaledVolume = current_vol - volume;
        if (scaledVolume < 0) {
            scaledVolume = 0;
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, scaledVolume, AudioManager.FLAG_SHOW_UI);
    }

    public boolean isM2001() {
        if (mModel.equals("M2001")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否有音频正在播放（即使静音）
     * @return
     */
    public boolean isAudioActive(){
        return mAudioManager.isMusicActive();
    }
}

