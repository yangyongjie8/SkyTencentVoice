package com.skyworthdigital.voice;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;

import okhttp3.OkHttpClient;

/**
 * Created by Ives 2019/5/30
 */
public class VoiceApp {
    private static final String TAG = VoiceApp.class.getSimpleName();
    private static Context sInstance;
    private static VoiceApp sVoiceApp;
    private static OkHttpClient mOkHttpClient;
    public int mScreenWidth;
    private final static String mModel = Utils.get("ro.product.model");
    private static final String PROPERTY_BOARD_TYPE = "ro.board.type";
    private static final String AUDIO_BOX = "audiobox";
    public static final int KEYCODE_TA412_BACK = 111;

    public int mAiType;
    public static String deviceId="";
    public static String lanMac="";
    private boolean mIsAudioBox = false;

    public void onCreate(Context context) {
        sInstance = context;
        sVoiceApp = this;
        mAiType = Utils.getAiType();
        mOkHttpClient = new OkHttpClient();

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;

        String sn = Utils.get("ro.serialno");
        lanMac= AppUtil.getMachineHardwareAddress().replace(":","");
        if(sn==null||sn.isEmpty()) {
            //sn = String.format("testsn%d", System.currentTimeMillis());
            sn= lanMac;
        }
        deviceId=sn;

        setAudioBox();
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

    private void setAudioBox() {
        String boardType = Utils.get(PROPERTY_BOARD_TYPE);

        if (TextUtils.equals(boardType, AUDIO_BOX)) {
            Log.i(TAG, "********AUDIO_BOX");
            mIsAudioBox = true;
        }
    }
    public boolean isAudioBox() {
        return mIsAudioBox;
    }

    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
