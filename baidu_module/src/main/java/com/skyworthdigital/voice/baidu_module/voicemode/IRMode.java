package com.skyworthdigital.voice.baidu_module.voicemode;

import android.content.Context;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.voice.VoiceInterface;

/**
 * 语音键唤醒模式
 * Created by SDT03046 on 2017/8/8.
 */

class IRMode implements VoiceMode {
    /**
     * 近场语音的pid,key，需要专门向百度申请
     * 默认的pid：726,key：com.baidu.dumi.tv
     */
    private static final int ASR_PID = 757;
    private static final String ASR_KEY = "com.baidu.dumi.tv.skyworthott";

    @Override
    public void startRecognize(Context var, DuerSDK sdk, VoiceInterface.IVoiceEventListener listener) {
        VoiceInterface.VoiceParam voiceParam = new VoiceInterface.VoiceParam();
        voiceParam.setAsrPid(ASR_PID);
        voiceParam.setAsrKey(ASR_KEY);
        //voiceParam.setVoiceSdkServerUrl("https://audiotest.baidu.com/v2");
        voiceParam.setVoiceMode(VoiceInterface.VOICEMODE.TOUCH);
        voiceParam.setKeyworld("");
        sdk.getVoiceRecognize().startRecognition(var, voiceParam, listener);
    }
}
