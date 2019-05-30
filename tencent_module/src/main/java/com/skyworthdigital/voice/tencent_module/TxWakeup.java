package com.skyworthdigital.voice.tencent_module;

import android.util.Log;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsWakeup;
import com.skyworthdigital.voice.common.IWakeupResultListener;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.tencent_module.record.MyRecorder;
import com.tencent.ai.sdk.atw.AtwSession;
import com.tencent.ai.sdk.atw.IAtwListener;
import com.tencent.ai.sdk.atw.WakeupRsp;
import com.tencent.ai.sdk.utils.ISSErrors;

import java.io.File;

/**
 * Created by Ives 2019/5/29
 */
public class TxWakeup extends AbsWakeup {
    private static final String TAG = "TxWakeup";
    private static TxWakeup mInstance;
    private MyRecorder mMyRecoder = MyRecorder.getInstance();
    /**
     * SDK唤醒模块Session
     */
    private AtwSession mAtwSession = null;
    /**
     * 是否延迟开启唤醒流程，一般在初始化时候设置
     */
    private boolean mPendingStartWakeup = false;
    private IWakeupResultListener mWkresultlistener = null;

    /**
     * 获取实例
     */
    public static TxWakeup getInstance(IWakeupResultListener wkresultlistener) {
        if (mInstance == null) {
            mInstance = new TxWakeup(wkresultlistener);
        }
        return mInstance;
    }

    private TxWakeup(IWakeupResultListener wkresultlistener) {
        mWkresultlistener = wkresultlistener;
        mMyRecoder.setWakeupListener(this);
    }

    public void init() {
        copyKeywordsModelAssets();
        String path = FileUtil.getKeywordsModelDir();
        File resFolder = new File(path);
        if (FileUtil.isDirectoryEmpty(resFolder)) {
            Log.e(TAG, path + " 目录为空，请将唤醒模型文件置于该目录下");
            return;
        }

        if (mPendingStartWakeup) {
            Log.e(TAG, "正在初始化中，请稍后再试");
            return;
        }

        if (null == mAtwSession) {
            mPendingStartWakeup = true;

            mAtwSession = new AtwSession(VoiceApp.getInstance(), path, mvwListener);
            Log.e(TAG, "初始化唤醒Session中");

            return;
        }
        // 开启唤醒
        //startWakeup();
    }

    public void release() {
        if (mMyRecoder != null) {
            mMyRecoder.stopRecord();
        }

        if (null != mAtwSession) {
            mAtwSession.release();
            mAtwSession = null;
        }
    }

    public void stopWakeup() {
        if (mAtwSession != null) {
            mAtwSession.stop();
        }
        mMyRecoder.stopRecord();
    }

    /**
     * 开启唤醒
     */
    public void startWakeup() {
        Log.e(TAG, "startWakeup");
        if (0 == Utils.getWakeupProperty()) {
            Log.e(TAG, "voice assistant is closed");
            return;
        }
        if (null == mAtwSession) {
            String path = FileUtil.getModelFilePath();//getKeywordsModelDir();
            mPendingStartWakeup = true;

            mAtwSession = new AtwSession(VoiceApp.getInstance(), path, mvwListener);
            Log.e(TAG, "初始化唤醒Session中");

            return;
        }
        // 停止上次录音
        mAtwSession.stop();
        //mMyRecoder.stopRecord();

        String message;
        int id = mAtwSession.start();
        if (id != ISSErrors.ISS_SUCCESS) {
            message = "Wakeup SessionStart error,id = " + id;
        } else {
            message = "Wakeup SessionStart succ";
            // 开始录音
            mMyRecoder.startRecord();

            Log.i(TAG, "开始唤醒流程");
        }
        Log.i(TAG, message);
    }

    private void copyKeywordsModelAssets() {
        try {
            FileUtil.copyModelFiles(VoiceApp.getInstance());
            /*String newPath = getKeywordsModelDir() + "/keywords_model/";
            String assetPath = "keywords_model";
            FileUtil.copyFilesFassets(VoiceApp.getInstance(), assetPath, newPath);
            newPath = getKeywordsModelDir() + "/vad/";
            assetPath = "vad";
            FileUtil.copyFilesFassets(VoiceApp.getInstance(), assetPath, newPath);*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onASrAudiobyte(byte[] buffer, int bufferSize) {
        //Log.i(TAG, "录音中..." + bufferSize);
        if (null != mAtwSession) {
            mAtwSession.appendAudioData(buffer, bufferSize);
        }
    }

    @Override
    public void onASrError(int code, String desc) {

    }

    private IAtwListener mvwListener = new IAtwListener() {

        @Override
        public void onAtwWakeup(WakeupRsp rsp) {
            Log.d(TAG, "wake up : " + rsp.iEndTimeMs);

            Log.i(TAG, "唤醒成功，你好语音助理, wakeup_time:" + rsp.iEndTimeMs);

            //mMyRecoder.stopRecord();
            mWkresultlistener.onSuccess(rsp.sText, "");
        }

        @Override
        public void onAtwInited(boolean state, int errId) {
            if (state) {
                Log.i(TAG, "init susscess");

                // 初始化以后，延迟开启唤醒Session
                if (mPendingStartWakeup) {
                    startWakeup();
                }
            } else {
                Log.i(TAG, "初始化失败，errId ：" + errId);
                String msg = "初始化失败，errId ：" + errId;
                mWkresultlistener.onError(errId, msg, "");
                mAtwSession = null;
            }

            mPendingStartWakeup = false;
        }
    };
}
