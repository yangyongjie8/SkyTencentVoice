package com.skyworthdigital.voice.baidu_module;


import android.util.Log;

import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;

public class WakeupStatus {
    private static String TAG = "WakeupStatus";
    private boolean mStatus;
    private static WakeupStatus mStatusInstance = null;

    public static WakeupStatus getInstance() {
        if (mStatusInstance == null) {
            mStatusInstance = new WakeupStatus();
        }
        return mStatusInstance;
    }

    private WakeupStatus() {
        mStatus = (1 == VoiceModeAdapter.getWakeupProperty());
    }

    public void setWakeupStatus(boolean status) {
        Log.i(TAG, "set:" + status);
        mStatus = status;
    }

    public boolean getWakeupStatus() {
        Log.i(TAG, "get:" + mStatus);
        return mStatus;
    }
}
