package com.skyworthdigital.voice.baidu_module.voicemode;

import android.content.Context;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.voice.VoiceInterface;

/**
 * 语音唤醒模式接口
 * Created by SDT03046 on 2017/8/8.
 */

interface VoiceMode {
    void startRecognize(Context var, DuerSDK sdk, VoiceInterface.IVoiceEventListener listener);
}
