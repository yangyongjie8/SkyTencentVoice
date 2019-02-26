package com.skyworthdigital.voice.dingdang.control.record;

import android.util.Log;

import com.tencent.androidvprocessor.VoiceProcessor;

/**
 * Created by SDT03046 on 2018/11/24.
 */

public class SkyVoiceProcessor {
    private static VoiceProcessor mVoiceProcessor = null;
    private static String RECORD_PATH = "/sdcard/speech";
    private static final String TAG = "SkyVoiceProcessor";

    public static boolean init() {
        int num_channels = 4;
        int num_refs = 2;
        mVoiceProcessor = new VoiceProcessor();
        if (!mVoiceProcessor.initialize(num_channels, num_refs, RECORD_PATH + "/")) {
            Log.i(TAG, "VoiceProcessor Initialize Failed");
            mVoiceProcessor = null;
            return false;
        }
        Log.i(TAG, "VoiceProcessor Initialize success");
        return true;
    }

    public static void onDestroy() {
        if (mVoiceProcessor != null) {
            mVoiceProcessor.shutdown();
        }
    }


    public static VoiceProcessor getVoiceProcessor() {
        return mVoiceProcessor;
    }
}
