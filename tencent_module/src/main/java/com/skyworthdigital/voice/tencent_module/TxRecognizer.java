package com.skyworthdigital.voice.tencent_module;

import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.IAsrDataListener;
import com.skyworthdigital.voice.common.IRecogListener;
import com.skyworthdigital.voice.common.AbsRecognizer;
import com.skyworthdigital.voice.tencent_module.record.MyRecorder;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.tencent.ai.sdk.control.SpeechManager;
import com.tencent.ai.sdk.tr.ITrListener;
import com.tencent.ai.sdk.tr.TrSession;
import com.tencent.ai.sdk.utils.ISSErrors;

/**
 * Created by Ives 2019/5/29
 */
public class TxRecognizer extends AbsRecognizer implements IAsrDataListener {

    private static final String TAG = "TxRecognizer";
    private MyRecorder mMyRecoder = MyRecorder.getInstance();
    /**
     * 语音识别的类型
     */
    private int mSpeechRecognizeType;
    /**
     * 语音->文本->语义->TTS
     */
    private static final int SPEECH_RECOGNIZE_TYPE_ALL = 0;
    /**
     * 语音->文本->语义
     */
    private static final int SPEECH_RECOGNIZE_TYPE_SEMANTIC = 1;
    /**
     * 语音->文本
     */
    private static final int SPEECH_RECOGNIZE_TYPE_TXT = 2;

    /**
     * SDK语音&语义识别的Session
     */
    private TrSession mTrSession;
    private IRecogListener mRecogListener = null;
    private boolean mRecognizing = false;

    /**
     * 获取实例
     */
    public static AbsRecognizer getInstance(IRecogListener recogListener) {
        if (mInstance == null) {
            mInstance = new TxRecognizer(recogListener);
        }
        return mInstance;
    }

    private TxRecognizer(IRecogListener recogListener) {
        mRecogListener = recogListener;
        mMyRecoder.setRecogListener(this);
        register();
    }

    public void register() {
        //onManualModeSet();
        // 初始化TrSession
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
            mTrSession = TrSession.getInstance(VoiceApp.getInstance(), mTrListener, 0, "", "");
        } else {
            mTrSession = TrSession.getInstance(VoiceApp.getInstance(), mRemoteTrListener, 0, "", "");
        }
        // 选择识别类型, 默认语音转文字
        onRecognizeTypeSet(SPEECH_RECOGNIZE_TYPE_ALL);
    }

    public void start() {
        startRecognize();
    }

    public void cancel() {
        if (null != mTrSession) {
            mTrSession.stop();
        }
        mMyRecoder.stopRecord();
        mRecognizing = false;
    }

    public void release() {
        mMyRecoder.stopRecord();
        mRecognizing = false;
        if (null != mTrSession) {
            mTrSession.release();
            mTrSession = null;
        }

        /*if (null != mTTSSession) {
            mTTSSession.stopSpeak();
            mTTSSession.release();
            mTTSSession = null;
        }*/
    }

    /**
     * 选择录音模式
     * <p>
     * setManualMode true为手动压住发言，false为自动检测说话结束
     */
    private void onManualModeSet() {
        // 按键发言
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_REMOTE) {
            //if (manual) {
            SpeechManager.getInstance().setManualMode(true);
            //MLog.i(TAG, "\n当前录音模式：按键发言，下次开启语音识别生效");
        } else {
            // 自动结束录音
            SpeechManager.getInstance().setManualMode(false);
            //MLog.i(TAG, "\n当前录音模式：自动检测说话结束，下次开启语音识别生效");
        }
    }


    /**
     * 选择识别类型
     *
     * @param type
     */
    private void onRecognizeTypeSet(int type) {
        if (null == mTrSession) {
            return;
        }

        mSpeechRecognizeType = type;
        if (type == SPEECH_RECOGNIZE_TYPE_ALL) {
            //MLog.e(TAG, "\n当前识别类型：语音 -> 语义，下次开启语音识别生效");

            mTrSession.setParam(TrSession.ISS_TR_PARAM_VOICE_TYPE, TrSession.ISS_TR_PARAM_VOICE_TYPE_RSP_ALL);

            // 初始化TTSSession
            /*if (null == mTTSSession) {
                mTTSSession = new TtsSession(this, mTTSInitListener, "");
            }*/
        } else if (type == SPEECH_RECOGNIZE_TYPE_TXT) {
            //MLog.e(TAG, "\n当前识别类型：语音 -> 文本，下次开启语音识别生效");

            mTrSession.setParam(TrSession.ISS_TR_PARAM_VOICE_TYPE, TrSession.ISS_TR_PARAM_VOICE_TYPE_RSP_VOICE);

            /*if (null != mTTSSession) {
                mTTSSession.release();
                mTTSSession = null;
            }*/
        } else if (type == SPEECH_RECOGNIZE_TYPE_SEMANTIC) {
            //MLog.e(TAG, "\n当前识别类型：语音 -> 文本，下次开启语音识别生效");

            mTrSession.setParam(TrSession.ISS_TR_PARAM_VOICE_TYPE, TrSession.ISS_TR_PARAM_VOICE_TYPE_RSP_ALL);
            /*if (null != mTTSSession) {
                mTTSSession.release();
                mTTSSession = null;
            }*/
        } else {
            //MLog.e(TAG, "\n未知类型");
        }
    }

    public void stop() {
        mRecognizing = false;
        if (mTrSession != null) {
            MLog.e(TAG, "\n停止");
            mTrSession.stop();
        }
        mMyRecoder.stopRecord();
    }

    public boolean isRecogning() {
        return mRecognizing;
    }

    private void tempFit412RepeatRun(){
        if(mMyRecoder!=null) {
            mMyRecoder.startRecord();
        }
    }

    /**
     * 开启语音识别
     */
    private void startRecognize() {
//        if(Utils.isP201IPtv()) {
//            tempFit412RepeatRun();
//        }
        // 停止上次录音
        if(mMyRecoder!=null) {
            mMyRecoder.stopRecord();
        }
        if (mTrSession == null) {
            register();
            //MLog.e(TAG, "\n停止");
        }else {
            mTrSession.stop();
        }

        String message = null;
        int id = mTrSession.start(TrSession.ISS_TR_MODE_CLOUD_REC, false);
        if (id != ISSErrors.ISS_SUCCESS) {
            message = "Tr SessionStart error,id = " + id;
            MLog.e(TAG, message);
            if (mRecogListener != null) {
                mRecogListener.onAsrError(SpeechRecognizer.ERROR_CLIENT, message, message);
            }
        } else {
            mRecognizing = true;
            if(TxController.getInstance().isControllerVoice) {
                mMyRecoder.startRecord();
            }
            // 开始录音
            // mPcmRecorder = new PcmRecorder(this);
            //mPcmRecorder.start();

            //MLog.i(TAG, "\n开始语音识别流程：");
            MLog.i(TAG, "开始录音");
        }
    }

    public void endRecognize() {
        if (null != mTrSession) {
            mTrSession.endAudioData();
        }
        mMyRecoder.stopRecord();
    }

    public void yuyiParse(String str) {
        if(null != mTrSession) {

            mTrSession.appendTextString(str,true, "test_text_2_semantic");
        }
    }

    private ITrListener mTrListener = new ITrListener() {
        private String mAsrParticalLast = "";

        @Override
        public void onTrInited(boolean state, int errId) {
            String msg = "onTrInited - state : " + state + ", errId : " + errId;
            //MLog.i(TAG, "onTrInited - state : " + state + ", errId : " + errId);
            if (state) {
                MLog.e(TAG, "TrSession init成功");
            } else {
                if (mRecogListener != null) {
                    mRecognizing = false;
                    mRecogListener.onAsrError(ISSErrors.ISS_ERROR_NOT_INITIALIZED, msg, msg);
                }
                MLog.e(TAG, "TrSession init失败, errId : " + errId);
            }
        }

        @Override
        public void onTrVoiceMsgProc(long uMsg, long wParam, String lParam, Object extraData) {
            //String msg = null;
            if (uMsg == TrSession.ISS_TR_MSG_SpeechStart) {
                //msg = "检测到说话开始";
                mAsrParticalLast = "";
                if (mRecogListener != null) {
                    mRecogListener.onAsrBegin();
                }
            } else if (uMsg == TrSession.ISS_TR_MSG_SpeechEnd) {
                //msg = "检测到说话结束";
                if (mRecogListener != null) {
                    mRecognizing = false;
                    mRecogListener.onAsrEnd();
                }
            } else if (uMsg == TrSession.ISS_TR_MSG_ProcessResult) {
                if (mRecogListener != null) {
                    MLog.i(TAG, "onTrVoiceMsgProc - uMsg : " + uMsg + ", wParam : " + wParam + ", lParam : " + lParam + " system time:" + System.currentTimeMillis());
                    if (!TextUtils.equals(mAsrParticalLast, lParam)) {
                        mRecogListener.onAsrPartialResult(lParam);
                        mAsrParticalLast = lParam;
                    }
                }
            } else if (uMsg == TrSession.ISS_TR_MSG_VoiceResult) {
                //msg = "语音 -> 文本 结束，结果为：" + lParam;
                //mMyRecoder.stopRecord();
                MLog.i(TAG, "onTrVoiceMsgProc - uMsg : " + uMsg + ", wParam : " + wParam + ", lParam : " + lParam + " system time:" + System.currentTimeMillis());
                if (mRecogListener != null) {
                    mRecogListener.onAsrFinalResult(lParam);
                }
            }

            //if (!TextUtils.isEmpty(msg)) {
            //MLog.i(TAG, msg);
            //}
        }

        @Override
        public void onTrSemanticMsgProc(long uMsg, long wParam, int cmd, String lParam, Object extraMsg) {
            //MLog.i(TAG, "onTrSemanticMsgProc - uMsg : " + uMsg + ", wParam : " + wParam + ", lParam : " + lParam + ", extraMsg : " + extraMsg);
            MLog.i(TAG, "语音 -> 语义 结束");
            //MLog.d(TAG, lParam);

            //mMyRecoder.stopRecord();
            if (mRecogListener != null) {
                mRecognizing = false;
//                mRecogListener.onAsrNluFinish(lParam);
                mRecogListener.onAsrFinalResult(lParam);
            }

            if (mSpeechRecognizeType == SPEECH_RECOGNIZE_TYPE_ALL) {
                //parseSemanticToTTS(lParam);
            }
        }

        @Override
        public void onTrVoiceErrMsgProc(long uMsg, long errCode, String lParam, Object extraData) {
            MLog.i(TAG, "onTrVoiceErrMsgProc - uMsg : " + uMsg + ", errCode : " + errCode + ", lParam : " + lParam);
            MLog.i(TAG, "语音 -> 文本 出现错误，errCode ：" + errCode + ", msg : " + lParam);
            //mMyRecoder.stopRecord();
            if (mRecogListener != null) {
                mRecognizing = false;
                mRecogListener.onAsrError(errCode, lParam, lParam);
            }
        }

        @Override
        public void onTrSemanticErrMsgProc(long uMsg, long errCode, int cmd, String lParam, Object extraMsg) {
            MLog.i(TAG, "onTrSemanticErrMsgProc - uMsg : " + uMsg + ", errCode : " + errCode + ", cmd : " + cmd
                    + ", lParam : " + lParam + ", extraMsg : " + extraMsg);
            MLog.i(TAG, "语音 -> 语义 出现错误，errCode ：" + errCode + ", cmd : " + cmd + ", msg : " + lParam);
            if (mRecogListener != null) {
                mRecognizing = false;
                mRecogListener.onAsrError(errCode, lParam, lParam);
            }
            //mMyRecoder.stopRecord();
        }
    };

    private ITrListener mRemoteTrListener = new ITrListener() {
        private String mAsrParticalLast = "";
        @Override
        public void onTrInited(boolean state, int errId) {
            //String msg = "onTrInited - state : " + state + ", errId : " + errId;
            //Log.i(TAG, "onTrInited - state : " + state + ", errId : " + errId);
            if (state) {
                Log.i(TAG, "TrSession init成功");
            } else {
                Log.i(TAG, "TrSession init失败, errId : " + errId);
            }
        }

        @Override
        public void onTrVoiceMsgProc(long uMsg, long wParam, String lParam, Object extraData) {
            //String msg = null;
            Log.i(TAG, "onTrVoiceMsgProc - uMsg : " + uMsg + ", wParam : " + wParam + ", lParam : " + lParam);
            if (uMsg == TrSession.ISS_TR_MSG_SpeechStart) {
                //msg = "检测到说话开始";
                mAsrParticalLast = "";
            } else if (uMsg == TrSession.ISS_TR_MSG_VoiceResult) {
                //msg = "语音 -> 文本 结束，结果为：" + lParam;
                mMyRecoder.stopRecord();
                if (mRecogListener != null) {
                    mRecogListener.onAsrFinalResult(lParam);
                }
            } else if (uMsg == TrSession.ISS_TR_MSG_ProcessResult) {
                if (mRecogListener != null) {
                    MLog.i(TAG, "onTrVoiceMsgProc - uMsg : " + uMsg + ", wParam : " + wParam + ", lParam : " + lParam + " system time:" + System.currentTimeMillis());
                    if (!TextUtils.equals(mAsrParticalLast, lParam)) {
                        mRecogListener.onAsrPartialResult(lParam);
                        mAsrParticalLast = lParam;
                    }
                }
            }
            //if (!TextUtils.isEmpty(msg)) {
            //    MLog.i(TAG, msg);
            //}
        }

        @Override
        public void onTrSemanticMsgProc(long uMsg, long wParam, int cmd, String lParam, Object extraMsg) {
            Log.i(TAG, "onTrSemanticMsgProc - uMsg : " + uMsg + ", wParam : " + wParam + ", lParam : " + lParam + ", extraMsg : " + extraMsg);
            Log.i(TAG, "语音 -> 语义 结束，结果为 ：");
            //Log.i(TAG, lParam);

            mMyRecoder.stopRecord();
            if (mRecogListener != null) {
                mRecognizing = false;
                mRecogListener.onAsrNluFinish(lParam);
            }
        }

        @Override
        public void onTrVoiceErrMsgProc(long uMsg, long errCode, String lParam, Object extraData) {
            Log.i(TAG, "onTrVoiceErrMsgProc - uMsg : " + uMsg + ", errCode : " + errCode + ", lParam : " + lParam);
            Log.i(TAG, "语音 -> 文本 出现错误，errCode ：" + errCode + ", msg : " + lParam);
            mMyRecoder.stopRecord();
            if (mRecogListener != null) {
                mRecognizing = false;
                mRecogListener.onAsrError(errCode, lParam, lParam);
            }
        }

        @Override
        public void onTrSemanticErrMsgProc(long uMsg, long errCode, int cmd, String lParam, Object extraMsg) {
            Log.i(TAG, "onTrSemanticErrMsgProc - uMsg : " + uMsg + ", errCode : " + errCode + ", cmd : " + cmd
                    + ", lParam : " + lParam + ", extraMsg : " + extraMsg);
            Log.i(TAG, "语音 -> 语义 出现错误，errCode ：" + errCode + ", cmd : " + cmd + ", msg : " + lParam);

            mMyRecoder.stopRecord();
            if (mRecogListener != null) {
                mRecognizing = false;
                mRecogListener.onAsrError(errCode, lParam, lParam);
            }
        }
    };


    @Override
    public void onASrAudiobyte(byte[] buffer, int bufferSize) {
        if (null != mTrSession) {
            MLog.d(TAG, "onASrAudiobyte:"+ new String(buffer));
            mTrSession.appendAudioData(buffer, bufferSize);
        }
    }

    @Override
    public void onASrError(int code, String desc) {
        mRecognizing = false;
        MLog.e(TAG, "my recognizer onerror:" + code + " :" + desc);
    }
}
