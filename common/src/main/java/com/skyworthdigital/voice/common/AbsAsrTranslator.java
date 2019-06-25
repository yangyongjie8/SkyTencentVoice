package com.skyworthdigital.voice.common;

import com.skyworthdigital.voice.VoiceApp;

/**
 * 第三方平台asr的解释
 * Created by Ives 2019/6/11
 */
public abstract class AbsAsrTranslator<T> {
    protected volatile static AbsAsrTranslator[] instance = new AbsAsrTranslator[2];

    public abstract void translate(T asrResult);

    public static AbsAsrTranslator getInstance() {
        if(VoiceApp.isDuer){
            return instance[0];
        }else {
            return instance[1];
        }
    }
}
