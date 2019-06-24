package com.skyworthdigital.voice.baidu_module.voicemode;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.voice.VoiceInterface;
import com.skyworthdigital.voice.VoiceApp;


/**
 * 远场唤醒词唤醒和语音键唤醒适配
 * Created by SDT03046 on 2017/8/8.
 */

public class VoiceModeAdapter implements VoiceMode {
    private VoiceMode mVoiceMode;

    public VoiceModeAdapter() {
        if (isAudioBox()) {
            mVoiceMode = new WakeupMode();
        } else {
            mVoiceMode = new IRMode();
        }
    }

    /**
     * 是否是智能音箱项目，用来判断是否唤醒词唤醒模式，还是语音键触发
     */
    public static boolean isAudioBox() {
        return VoiceApp.getVoiceApp().isAudioBox();
    }

    public static void setWakeupProperty(int on) {
        //0:closed 1:opened 2:opening 3:closing
        Log.i("wakeup", "setWakeupProperty:" + on);
        Settings.System.putInt(VoiceApp.getInstance().getContentResolver(), "voiceopen", on);
    }

    public static int getWakeupProperty() {
        return Settings.System.getInt(VoiceApp.getInstance().getContentResolver(), "voiceopen", 1);
    }

    @Override
    public void startRecognize(Context var, DuerSDK sdk, VoiceInterface.IVoiceEventListener listener) {
        mVoiceMode.startRecognize(var, sdk, listener);
    }

    /**
     * 启动唤醒检测。通过唤醒词唤醒,智能音箱才需要唤醒
     */
    public void startWakeUp(Context var, DuerSDK sdk) {
        if (isAudioBox()) {
            ((WakeupMode) mVoiceMode).startWakeUp(var, sdk);
        }
    }

    /**
     * 停止唤醒
     */
    public void stopWakeUp(DuerSDK sdk) {
        if (isAudioBox()) {
            ((WakeupMode) mVoiceMode).stopWakeUp(sdk);
        }
    }

    public void setRecognizeGoOn(boolean status) {
        if (isAudioBox()) {
            ((WakeupMode) mVoiceMode).setRecognizeGoOn(status);
        }
    }

    public boolean isRecognizeGoOn() {
        return (isAudioBox() && ((WakeupMode) mVoiceMode).isRecognizeGoOn());
    }
}
