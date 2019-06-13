package com.skyworthdigital.voice.common;

/**
 * 第三方平台asr的翻译
 * Created by Ives 2019/6/11
 */
public abstract class AbsAsrTranslator<T> {
    protected static AbsAsrTranslator instance;

    public abstract void translate(T asrResult);

    public static AbsAsrTranslator getInstance() {
        return instance;
    }
    public static void clearInstance(){
        instance = null;
    }
}
