package com.skyworthdigital.voice;

import android.content.Context;

import com.skyworthdigital.voice.common.utils.Utils;

import okhttp3.OkHttpClient;

/**
 * Created by Ives 2019/5/30
 */
public class VoiceApp {
    private static final String TAG = VoiceApp.class.getSimpleName();
    private static Context sInstance;
    private static VoiceApp sVoiceApp;
    private static OkHttpClient mOkHttpClient;
    public final static String mModel = Utils.get("ro.product.model");
    public static final int KEYCODE_TA412_BACK = 111;

    public int mAiType;

    public void onCreate(Context context) {
        sInstance = context;
        sVoiceApp = this;
        mAiType = Utils.getAiType();
        mOkHttpClient = new OkHttpClient();
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

    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
