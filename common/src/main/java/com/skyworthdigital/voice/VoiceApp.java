package com.skyworthdigital.voice;

import android.content.Context;

import com.skyworthdigital.voice.common.utils.Utils;

/**
 * Created by Ives 2019/5/30
 */
public class VoiceApp {
    private static final String TAG = VoiceApp.class.getSimpleName();
    private static Context sInstance;
    private static VoiceApp sVoiceApp;
    public final static String mModel = Utils.get("ro.product.model");

    public int mAiType;

    public void onCreate(Context context) {
        sInstance = context;
        sVoiceApp = this;
        mAiType = Utils.getAiType();
    }


    public static Context getInstance() {
        return sInstance;
    }

    public static VoiceApp getVoiceApp(){
        return sVoiceApp;
    }

    public static String getModel() {
        return mModel;
    }
}
