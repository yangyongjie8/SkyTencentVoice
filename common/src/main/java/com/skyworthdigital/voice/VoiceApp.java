package com.skyworthdigital.voice;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.skyworthdigital.voice.common.BuildConfig;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.SPUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

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
    public static boolean isDuer = BuildConfig.isDuer;

    public void onCreate(Context context) {
        sInstance = context;
        sVoiceApp = this;
        mAiType = Utils.getAiType();
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        mOkHttpClient = okBuilder.build();

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

        if(!TextUtils.isEmpty(SPUtil.getString(SPUtil.KEY_VOICE_PLATFORM))){// 有记忆值则使用记忆值
            isDuer = SPUtil.VALUE_VOICE_PLATFORM_BAIDU.equalsIgnoreCase(SPUtil.getString(SPUtil.KEY_VOICE_PLATFORM));
        }
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
