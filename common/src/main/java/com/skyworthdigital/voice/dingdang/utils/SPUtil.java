package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.skyworthdigital.voice.VoiceApp;

/**
 * Created by Ives on 2018/10/6 0006.
 */
public class SPUtil {
    private static final String SP_FILE_NAME = "setting";

    public static final String KEY_SP_DEMO_SWITCH_ON = "key_sp_demo_switch_2min";// 演示用的2分钟周期开关

    public static final String KEY_VOICE_PLATFORM = "voice_platform";// 语音语义平台：dingdang/baidu
    public static final String VALUE_VOICE_PLATFORM_DINGDANG = "dingdang";// 叮当
    public static final String VALUE_VOICE_PLATFORM_BAIDU = "baidu";// 百度

    private SPUtil(){}

    public static void putString(String key, String value){
        SharedPreferences sp = VoiceApp.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static void putBoolean(String key, boolean value){
        SharedPreferences sp = VoiceApp.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static boolean getBoolean(String key){
        return VoiceApp.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public static String getString(String key){
        return VoiceApp.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE).getString(key, null);
    }

    public static String getString(String key, String defValue){
        return VoiceApp.getInstance().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE).getString(key, defValue);
    }
}
