package com.skyworthdigital.voice.tencent_module.record;

import android.util.Log;


import com.skyworthdigital.voice.common.IAsrDataListener;
import com.skyworthdigital.voice.common.IWakeupDataListener;

/**
 * Created by SDT03046 on 2018/7/18.
 */

public class MyRecorder implements PcmRecorder.RecordListener {
    private static final String TAG = "MyRecorder";
    /**
     * 录音线程
     */
    private PcmRecorder mPcmRecorder;

    private static MyRecorder mInstance;
    private IWakeupDataListener mWakeupListener = null;
    private IAsrDataListener mRecogListener = null;

    /**
     * 获取实例
     */
    public static MyRecorder getInstance() {
        if (mInstance == null) {
            mInstance = new MyRecorder();
        }
        return mInstance;
    }

    public void setWakeupListener(IWakeupDataListener wklistener) {
        mWakeupListener = wklistener;
    }

    public void setRecogListener(IAsrDataListener iRecogListener) {
        mRecogListener = iRecogListener;
    }

    public void startRecord() {
        // 开始录音
        mPcmRecorder = new PcmRecorder(this);
        mPcmRecorder.start();
        Log.i(TAG, "开始录音");
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (null != mPcmRecorder) {
            if (mPcmRecorder.stopThread()) {
                Log.i(TAG, "停止录音");
            }
        }
    }

    @Override
    public void onRecord(byte[] buffer, int bufferSize) {
        //Log.i(TAG, "录音中..." + bufferSize);
        if (mWakeupListener != null) {
            mWakeupListener.onASrAudiobyte(buffer, bufferSize);
        }

        if (null != mRecogListener) {
            mRecogListener.onASrAudiobyte(buffer, bufferSize);
        }
    }

    @Override
    public void onError(int code, String desc) {
        if (mWakeupListener != null) {
            mWakeupListener.onASrError(code, desc);
        }
        if (mRecogListener != null) {
            mRecogListener.onASrError(code, desc);
        }
    }
}
