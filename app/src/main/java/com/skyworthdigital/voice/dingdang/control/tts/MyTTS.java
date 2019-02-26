package com.skyworthdigital.voice.dingdang.control.tts;

import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.control.model.AsrResult;
import com.tencent.ai.sdk.tts.ITtsInitListener;
import com.tencent.ai.sdk.tts.ITtsListener;
import com.tencent.ai.sdk.tts.TtsSession;
import com.tencent.ai.sdk.utils.ISSErrors;

/**
 * Created by SDT03046 on 2018/7/19.
 */

public class MyTTS {
    private static final String TAG = "MyTTS";
    private static MyTTS mInstance = null;
    private TtsSession mTTSSession = null;
    private boolean mIsSpeak = false;
    private MyTTSListener myTTSListener;

    public static final int STATUS_INTERRUPT = 2;
    public static final int STATUS_TALKING = 1;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_TALKOVER = 4;

    public static MyTTS getInstance(MyTTSListener listener) {
        if (mInstance == null) {
            mInstance = new MyTTS(listener);
        }
        return mInstance;
    }

    private MyTTS(MyTTSListener listener) {
        myTTSListener = listener;
        ITtsInitListener ttSInitListener = new ITtsInitListener() {
            @Override
            public void onTtsInited(boolean state, int errId) {
                String msg = "";
                if (state) {
                    msg = "初始化成功";
                } else {
                    msg = "初始化失败，errId ：" + errId;
                }

                Log.d(TAG, msg);
                //printLog(msg);
            }
        };
        mTTSSession = new TtsSession(VoiceApp.getInstance(), ttSInitListener, "");
    }

    public void close() {
        // 销毁Session
        if (null != mTTSSession) {
            mTTSSession.stopSpeak();
            mTTSSession.release();
            mTTSSession = null;
        }
    }

    public void stopSpeak() {
        if (null != mTTSSession && mIsSpeak) {
            mIsSpeak = false;
            Log.d(TAG, "stop tts");
            mTTSSession.stopSpeak();
        }
    }

    public boolean isSpeak() {
        return mIsSpeak;
    }

    public void speak(String text) {
        // 请求语义
        try {
            if (null != mTTSSession) {
                mTTSSession.stopSpeak();
                // 设置是否需要播放
                int ret = mTTSSession.setParam(TtsSession.TYPE_TTS_PLAYING, TtsSession.TTS_PLAYING);
                Log.d(TAG, "tts：\n" + text);
                if (ret == ISSErrors.TTS_PLAYER_SUCCESS) {
                    if (!TextUtils.isEmpty(text)) {
                        mIsSpeak = true;
                        myTTSListener.onChange(STATUS_TALKING);
                        mTTSSession.startSpeak(text, mTTSListener);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void speak(String tts, String output) {
        // 请求语义
        try {
            if (null != mTTSSession) {
                mTTSSession.stopSpeak();
                // 设置是否需要播放
                int ret = mTTSSession.setParam(TtsSession.TYPE_TTS_PLAYING, TtsSession.TTS_PLAYING);
                if (ret == ISSErrors.TTS_PLAYER_SUCCESS) {
                    if (!TextUtils.isEmpty(tts)) {
                        Log.d(TAG, "tts:" + tts);
                        mIsSpeak = true;
                        myTTSListener.onChange(STATUS_TALKING);
                        mTTSSession.startSpeak(tts, mTTSListener);
                    }
                }
            }
            if (!TextUtils.isEmpty(output)) {
                myTTSListener.onOutputChange(output, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void speakDelay(String tts, String output, int delay) {
        // 请求语义
        try {
            if (null != mTTSSession) {
                mTTSSession.stopSpeak();
                // 设置是否需要播放
                int ret = mTTSSession.setParam(TtsSession.TYPE_TTS_PLAYING, TtsSession.TTS_PLAYING);
                if (ret == ISSErrors.TTS_PLAYER_SUCCESS) {
                    if (!TextUtils.isEmpty(tts)) {
                        Log.d(TAG, "tts:" + tts);
                        mIsSpeak = true;
                        myTTSListener.onChange(STATUS_TALKING);
                        mTTSSession.startSpeak(tts, mTTSListener);
                    }
                }
            }
            if (!TextUtils.isEmpty(output)) {
                myTTSListener.onOutputChange(output, delay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void speakAndShow(String text) {
        // 请求语义
        try {
            if (null != mTTSSession) {
                mTTSSession.stopSpeak();
                // 设置是否需要播放
                int ret = mTTSSession.setParam(TtsSession.TYPE_TTS_PLAYING, TtsSession.TTS_PLAYING);
                Log.d(TAG, "tts：\n" + text);
                if (ret == ISSErrors.TTS_PLAYER_SUCCESS) {
                    if (!TextUtils.isEmpty(text)) {
                        mIsSpeak = true;
                        myTTSListener.onChange(STATUS_TALKING);
                        mTTSSession.startSpeak(text, mTTSListener);
                        myTTSListener.onOutputChange(text, 0);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析语义数据，并将回复语进行语音合成
     */
    public void parseSemanticToTTS(AsrResult bean /*String semantic*/) {
        if (bean == null) {
            return;
        }

        try {
            String noScreenAnswer = bean.mAnswer;
            if (TextUtils.isEmpty(noScreenAnswer)) {
                noScreenAnswer = bean.mTips;
            }
            if (!TextUtils.isEmpty(noScreenAnswer)) {
                speak(noScreenAnswer);
            }
        } catch (Exception e) {
            Log.e(TAG, "parseSemanticToTTS : " + e.getMessage());
        }
    }

    private ITtsListener mTTSListener = new ITtsListener() {
        @Override
        public void onPlayCompleted() {
            String msg = "播放结束：onPlayCompleted";
            mIsSpeak = false;
            myTTSListener.onChange(STATUS_TALKOVER);
            Log.i(TAG, msg);
        }

        @Override
        public void onPlayBegin() {
            String msg = "播放开始：onPlayBegin";
            Log.i(TAG, msg);
        }

        @Override
        public void onPlayInterrupted() {
            mIsSpeak = false;
            myTTSListener.onChange(STATUS_INTERRUPT);
            String msg = "播放被中断：onPlayInterrupted";
            Log.i(TAG, msg);
        }

        @Override
        public void onError(int errorCode, String errorMsg) {
            mIsSpeak = false;
            myTTSListener.onChange(STATUS_ERROR);
            String msg = "播报出现错误：onError code=" + errorCode + " errorMsg=" + errorMsg;
            Log.i(TAG, msg);
        }

        @Override
        public void onProgressReturn(int textindex, int textlen) {
            String msg = "播放进度 - textindex ：" + textindex + ", textlen : " + textlen;
            Log.i(TAG, msg);
        }

        @Override
        public void onProgressRuturnData(byte[] data, boolean end) {
            String msg = "音频流返回 - data size : " + data.length + ", isEnd : " + end;
            Log.i(TAG, msg);
        }
    };

    public interface MyTTSListener {
        void onChange(int status);

        void onOutputChange(String output, int delay);
    }
}
