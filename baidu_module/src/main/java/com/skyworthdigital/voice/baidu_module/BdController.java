package com.skyworthdigital.voice.baidu_module;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.DuerSDKImpl;
import com.baidu.duersdk.voice.VoiceInterface;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.alarm.database.AlarmDbOperator;
import com.skyworthdigital.voice.baidu_module.duerbean.DuerBean;
import com.skyworthdigital.voice.baidu_module.led.Led;
import com.skyworthdigital.voice.baidu_module.paipaiAnim.PaiPaiAnimUtil;
import com.skyworthdigital.voice.baidu_module.robot.BdTTS;
import com.skyworthdigital.voice.baidu_module.util.ActionUtils;
import com.skyworthdigital.voice.baidu_module.util.ActivityManager;
import com.skyworthdigital.voice.baidu_module.util.GsonUtils;
import com.skyworthdigital.voice.baidu_module.util.SkyRing;
import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;
import com.skyworthdigital.voice.baidu_module.voicemode.WakeupMode;
import com.skyworthdigital.voice.common.AbsController;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.LedUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.scene.ISceneCallback;
import com.skyworthdigital.voice.scene.SkySceneService;
import com.skyworthdigital.voice.sdk.VoiceService;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

//import com.skyworthdigital.voiceassistant.utils.DebugConfig;


public class BdController extends AbsController {
    private static final String TAG = "BdController";
    private VoiceModeAdapter mVoiceModeAdapter;
    public VoiceTriggerDialog mVoiceTriggerDialog;
    private AudioBoxReceiver mAudioboxReceiver;
    private String mDuerResult;
    private AbsTTS mRobot = null;//语音播报机器人实例
    private DuerSDK mDuserSdk = null;
    private boolean mBound = false;
    private boolean mDialogShow = false;
    private SkySceneService mService;
    private static Handler mVoiceHandler;
    private HandlerThread mProcThread;
    private Handler mProcHandler;
    private boolean mIsRecoCanStart = true;//默认必须为真。默认可以开始语音识别
    private long mRecoFinishTime = 0;//记录asr.exit状态的时间
    private long mStartRecTime = 0;//记录执行startRecognize时间，必须保证mStartRecTime>mRecoFinishTime+200ms
    private int mWakeupStatus = 0;//1:需要设置打开属性 2：需要设置关闭属性
    private VoiceStatus mVoiceState = VoiceStatus.NONE;
    public VoiceInterface.VoiceState mDuerState = VoiceInterface.VoiceState.READY;
    private int mRepeatRecoCount = 0, mRepeatRecoCountReal = 0;
    private static final String WAKEUP_NAME_DATA = "wp.data";
    private static final String WAKEUP_WORD = "word";
    private static final String WAKEUP_ENTER = "wp.enter";
    private static final String WAKEUP_EXIT = "wp.exit";
    private static final int RECOGNIZE_VALID_INTERVAL = 1000;
    private static final int RECOGNIZE_VALID_DELAY = 200;
    private static final int WAKE_THEN_REC_TIME_PRIEOD = 200;//唤醒和识别时间需间隔200毫秒
    private static final long DIALOG_DIMISS_DELAY = 10000;//语音识别完成最长显示时间
    private static final long RECOGNIZE_FINISH_DEALY = 5000;//语音识别结果显示的时间
    private static final long RECOGNIZE_MAX_DEALY = 60000;//语音输入的最大时长

    //音箱唤醒后，去屏保广播
    //private static final String SCREENSAVE_DISMISS = "com.android.skyworth.screensave.request.dismiss";


    private static final String WAKEUP_OPEN_ACTION = "com.skyworthdigital.voice.action.WAKEUP_OPEN";
    private static final String WAKEUP_CLOSE_ACTION = "com.skyworthdigital.voice.action.WAKEUP_CLOSE";
    private static final String ANGEL_KARAOKE_ACTION = "com.tlkg.action.VOICE_SEARCH_RESULT";
    private static final String SPEECH_CONTENT="com.skyworthdigital.voice.action.SPEECH_CONTENT";

    @Override
    public void onDestroy() {
        //TODO
        mBound = false;
        VoiceApp.getInstance().unbindService(mConnection);// 需要解绑，否则切换到叮当后还会收到回调。
        VoiceApp.getInstance().unregisterReceiver(mAudioboxReceiver);
    }

    @Override
    public boolean onKeyEvent(int code) {
        //TODO 暂未过滤事件
        if (isDialogShow()) {
            stopVoiceTriggerDialog();
            cancelRecognize();
            BdTTS.getInstance().stopSpeak();//需要stop掉sdk，否则会在下次调用sd时会继续播放当前实例留下的缓存
            return true;
        }
        return false;
    }

    @Override
    public boolean isRecognizing() {
        //TODO 暂时只是远场时需要调用，百度版不需要。暂时用dialog是否显示来判断
        return isDialogShow();
    }

    @Override
    public void manualRecognizeStart() {
        //TODO done
        if (isStartValid()) {
//          BdTTS.getInstance().stopTalk();
            //开始识别
//          Utils.set("audio.in.device.vaudio", "enable");
            showVoiceTriggerDialog(VoiceApp.getInstance());
            startRecognize(VoiceApp.getInstance());
        }
    }

    @Override
    public void manualRecognizeStop() {
        //TODO done
        if (isStopValid()) {
            //停止识别
            finishRecognize();
        } else {
            cancelRecognize();
        }
//          Utils.set("audio.in.device.vaudio", "disable");
    }

    @Override
    public void testYuyiParse(String str) {
        //TODO 百度版不支持文本合成
    }

    @Override
    public void cancelYuyiParse() {
        //TODO 百度版不支持文本合成
    }

    @Override
    public void manualRecognizeCancel() {
        //TODO 暂时没用

    }

    @Override
    public boolean isAsrDialogShowing() {
        //TODO done
        return isDialogShow();
    }

    @Override
    public void dismissDialog(long delay) {
        //mVoiceHandler.removeCallbacksAndMessages(null);
        mVoiceHandler.removeMessages(MSG_DIALOG_DISMISS);
        mVoiceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mVoiceTriggerDialog != null) {
                    MLog.i(TAG, "dismissDialog");
                    mVoiceTriggerDialog.dismiss();
                    mVoiceTriggerDialog.cancel();
                    mVoiceTriggerDialog = null;
                    mDialogShow = false;
                }
            }
        }, delay);
    }

    private enum VoiceStatus {
        NONE,
        WAKEUP_READY,
        WAKEUP_EXIT,
        ASR_READY,
        ASR_EXIT
    }

    /**
     * 获取实例
     */
    public static BdController getInstance() {
        if (mManagerInstance[0] == null) {
            synchronized (BdController.class) {
                if(mManagerInstance[0]==null) {
                    mManagerInstance[0] = new BdController();
                }
            }
        }
        return (BdController) mManagerInstance[0];
    }

    private BdController() {
        initManager();
    }

    private void initManager() {
        mDuserSdk = DuerSDKFactory.getDuerSDK();
        mVoiceModeAdapter = new VoiceModeAdapter();
        mRobot = BdTTS.getInstance();
        Context ctx = VoiceApp.getInstance();
        mVoiceHandler = new VoiceHandler(this);

        mProcThread = new HandlerThread(BdController.class.getSimpleName());
        mProcThread.start();
        mProcHandler = new Handler(mProcThread.getLooper());
        //DebugConfig.getInstance(ctx);

        registerReceiver();

        if (VoiceModeAdapter.isAudioBox()) {
            MLog.i(TAG, "唤醒词：" + WakeupMode.wakeUpWord1);
            initWakeUp(ctx);
            if (1 == VoiceModeAdapter.getWakeupProperty()) {
                mVoiceModeAdapter.startWakeUp(ctx, mDuserSdk);
            }
        }
        BdTvLiveController.getInstance().updateTvliveDbFromNet();
        Led.showLedOff();
        AlarmDbOperator dbOperator = new AlarmDbOperator(VoiceApp.getInstance());
        dbOperator.resetDb();
    }
    private void registerReceiver(){
        mAudioboxReceiver = new AudioBoxReceiver();
        final IntentFilter mScreenCheckFilter = new IntentFilter();
        mScreenCheckFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenCheckFilter.addAction(Intent.ACTION_SCREEN_ON);
        mScreenCheckFilter.addAction(GlobalVariable.APPLY_AUDIO_RECORDER_ACTION);
        mScreenCheckFilter.addAction(GlobalVariable.RELEASE_AUDIO_RECORDER_ACTION);
        mScreenCheckFilter.addAction(GlobalVariable.ACTION_VOICE_RECO_GOON);
        mScreenCheckFilter.addAction(WAKEUP_CLOSE_ACTION);
        mScreenCheckFilter.addAction(WAKEUP_OPEN_ACTION);
        mScreenCheckFilter.addAction(ANGEL_KARAOKE_ACTION);
        mScreenCheckFilter.addAction(SPEECH_CONTENT);
        VoiceApp.getInstance().registerReceiver(mAudioboxReceiver, mScreenCheckFilter);
    }

    public void stopManager() {
        //RecognizeService是长驻的，stopManager默认不需要执行
        if (mBound) {
            mService.stopSelf();
        }
        mRobot.stopSpeak();
        mVoiceModeAdapter.stopWakeUp(mDuserSdk);
        if (SkyRing.getInstance() != null) {
            SkyRing.getInstance().stop();
        }
    }

    public void startRecognize(final Context context) {
        MLog.i(TAG, "invokeRecognize");
        mProcHandler.post(new Runnable() {
            @Override
            public void run() {
                mVoiceModeAdapter.startRecognize(context, mDuserSdk, mVoicelistener);
                MLog.i(TAG, "voice start thread:"+Thread.currentThread().getName());
                if (!mBound) {
                    Intent intent = new Intent(context, SkySceneService.class);
                    context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

                    registerReceiver();
                }
                mRobot.stopSpeak();
            }
        });

        //Intent intentToMedia = new Intent("com.iflytek.keyevent.keydown");//如果当前弹幕输入框是显示状态，则需要通知media去关闭
        //sendBroadcast(intentToMedia);
        //Intent intentscreensave = new Intent("com.android.skyworth.screensave.request.dismiss");//如果当前弹幕输入框是显示状态，则需要通知media去关闭
        //sendBroadcast(intentscreensave);
    }

    public void finishRecognize() {
        MLog.i(TAG, "stopRecognize");
        LedUtil.openHorseLight();
        mDuserSdk.getVoiceRecognize().recognitionFinish(DuerSDKImpl.getInstance().getmContext());
    }

    public void cancelRecognize() {
        MLog.i(TAG, "cancelRecognition");
        Led.showLedOff();
        mRepeatRecoCount = 0;
        mDuserSdk.getVoiceRecognize().cancelRecognition(DuerSDKImpl.getInstance().getmContext());
        if (!VoiceModeAdapter.isAudioBox()) {
            Context ctx = VoiceApp.getInstance();
            VolumeUtils.getInstance(ctx).setMuteWithNoUi(false);
        }
    }

    public boolean isStartValid() {
        long curtime = System.currentTimeMillis();
        if (mIsRecoCanStart) {
            if (curtime > mRecoFinishTime + RECOGNIZE_VALID_DELAY) {
                mStartRecTime = curtime;
                mIsRecoCanStart = false;
                return true;
            } else {
                if (isDialogShow()) {
                    mVoiceTriggerDialog.setTextHint("按慢一点呢。");
                }
            }
        }
        MLog.i(TAG, "mIsRecoCanStart:" + mIsRecoCanStart + " curtime:" + curtime + " mRecoFinishTime:" + mRecoFinishTime);
        return false;
    }

    public boolean isStopValid() {
        if (!mIsRecoCanStart) {
            long stopcur = System.currentTimeMillis();

            if (stopcur > mStartRecTime + RECOGNIZE_VALID_INTERVAL) {
                return true;
            }
        }
        return false;
    }

    public void showVoiceTriggerDialog(final @NonNull Context ctx) {
        mVoiceHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mVoiceTriggerDialog == null) {
                    mVoiceTriggerDialog = new VoiceTriggerDialog(ctx);
                    mVoiceTriggerDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
                    mVoiceTriggerDialog.getWindow().setFlags(flags, flags);
                }
                if (mVoiceTriggerDialog != null && (!mVoiceTriggerDialog.isShowing() || !mDialogShow)) {
                    MLog.i(TAG, "dialog show");
                    mVoiceTriggerDialog.show();
                    //VolumeUtils.getInstance(ctx).setRobotVolume();
                    SkyRing skyRing = SkyRing.getInstance();
                    if (skyRing != null) {
                        if (VoiceModeAdapter.isAudioBox()) {
                            skyRing.play(0);
                        } else {
                            skyRing.play(1);
                        }
                    }
                } else {
                    SkyRing skyRing = SkyRing.getInstance();
                    if (skyRing != null) {
                        if (VoiceModeAdapter.isAudioBox() && !mVoiceModeAdapter.isRecognizeGoOn() && mVoiceTriggerDialog != null) {
                            mVoiceTriggerDialog.setTextHint(R.string.str_audiobox_hello);
                            mVoiceTriggerDialog.showPaiPaiByID(PaiPaiAnimUtil.ID_PAIPAI_HELLO);
                        }
                        skyRing.play(1);
                    }
                }
                mDialogShow = true;
            }
        });
    }

    public boolean isDialogShow() {
        return (mVoiceTriggerDialog != null && mVoiceTriggerDialog.isShowing());
    }

    public void stopVoiceTriggerDialog() {
        MLog.i(TAG, "stopVoiceTriggerDialog");
        VolumeUtils.getInstance(VoiceApp.getInstance()).setMuteWithNoUi(false);
        dismissDialog();
    }
    public void dismissDialog(){
        dismissDialog(0);
    }

    /**
     * 初始化语音唤醒
     */
    private void initWakeUp(final Context ctx) {
        //初始化唤醒监听器
        VoiceInterface.IWakeUpEventListener mWakeUpEventListener = new VoiceInterface.IWakeUpEventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                try {
                    Log.i(TAG, "唤醒返回数据为：name=" + name + " params=" + params + " data.size=" + (data != null ? data.length : "null") + " offset=" + offset + " length=" + length);
                    //每次唤醒成功，将会回调name=wp.data的时间，被激活的唤醒词在params的word字段
                    switch (name) {
                        case WAKEUP_NAME_DATA:
                            if (!TextUtils.isEmpty(params)) {
                                JSONObject jsonObject = new JSONObject(params);
                                //根据返回的错误码判断是否有正确结果
                                //拿到唤醒词
                                String word = jsonObject.getString(WAKEUP_WORD);
                                Log.i(TAG, "\n唤醒结果：" + word);
                                if (WakeupMode.wakeUpWord1.equals(word) || WakeupMode.wakeUpWord2.equals(word)) {
                                    mVoiceModeAdapter.setRecognizeGoOn(false);
                                    stopWakeupAndStartRecognize(ctx);
                                }
                            } else {
                                Log.i(TAG, "results: params = null");
                            }
                            break;
                        case WAKEUP_ENTER:
                            if (mWakeupStatus == 1) {
                                VoiceModeAdapter.setWakeupProperty(1);
                                mWakeupStatus = 0;
                            }
                            mVoiceState = VoiceStatus.WAKEUP_READY;
                            Log.i(TAG, "唤醒输入");
                            break;
                        case WAKEUP_EXIT:
                            Log.i(TAG, "唤醒停止");
                            mVoiceState = VoiceStatus.WAKEUP_EXIT;
                            if (mWakeupStatus == 2) {
                                VoiceModeAdapter.setWakeupProperty(0);
                                mWakeupStatus = 0;
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //唤醒已经停止
                    Log.i(TAG, "语音唤醒出错");
                }
            }
        };

        //注册监听器
        mDuserSdk.getVoiceRecognize().registerWpEventManagerListener(ctx,
                mWakeUpEventListener);
    }

    private void stopWakeupAndStartRecognize(final Context ctx) {
        //停止唤醒，开始识别
        mVoiceModeAdapter.stopWakeUp(mDuserSdk);
        Log.i(TAG, "stopWakeupAndStartRecognize");
        mDuerState = VoiceInterface.VoiceState.READY;
        mVoiceHandler.removeMessages(MSG_SHOW_ERROR);
        mVoiceHandler.removeMessages(MSG_ADDITION_MSG);
        mVoiceHandler.removeMessages(MSG_SHOW_RESULT);
        Runnable startRecRunnable = new Runnable() {
            @Override
            public void run() {
                //开始识别
                Log.i(TAG, "唤醒成功，请说内容...");
                startRecognize(ctx);
            }
        };

        if (!mVoiceHandler.postDelayed(startRecRunnable, WAKE_THEN_REC_TIME_PRIEOD)) {
            //百度要求kitt版本停止唤醒到识别时间间隔200ms
            Log.i(TAG, "message post fail");
        }

        //Intent intentscreensave = new Intent(SCREENSAVE_DISMISS);
        //ctx.sendBroadcast(intentscreensave);
        Led.showVoiceWakedup();
        showVoiceTriggerDialog(ctx);
    }

    /**
     * 语音识别回调
     */
    private final VoiceInterface.IVoiceEventListener mVoicelistener = new VoiceInterface.IVoiceEventListener() {
        @Override
        public void onVoiceEvent(final VoiceInterface.VoiceResult voiceResult) {
            if (null == voiceResult) return;

            //if (voiceResult.getStatus() != VOLUME) {
            //Log.v(TAG, "onVoiceEvent:" + voiceResult.getStatus());
            //Log.i(TAG, "voiceResult.getStatus()" + voiceResult.getStatus());
            mDuerState = voiceResult.getStatus();
            //}

            // 根据最新的结果，度秘不对外直接提供识别结果,所以如果是成功状态，则接着联网请求结果
            MLog.d(TAG, "status:"+voiceResult.getStatus().name());
            MLog.i(TAG, "voice onVoiceEvent thread:"+Thread.currentThread().getName());
            switch (voiceResult.getStatus()) {
                case READY: {
                    mVoiceState = VoiceStatus.ASR_READY;
                    // 引擎就绪
                    onRecognizeReady();
                }
                break;
                case BEGIN: {
                    // 引擎开始
                }
                break;
                case FINISH: {
                    mRepeatRecoCount = 0;
                    // 识别结束
                    //VolumeUtils.getInstance(MyApplication.getInstance()).setMuteWithNoUi(false);
                    mProcHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onRecognizeFinish(voiceResult);
                        }
                    });
                }
                break;
                case VOLUME: {
                    try {
                        if (mVoiceTriggerDialog != null && (!mVoiceTriggerDialog.isShowing() || !mDialogShow)) {
                            mVoiceTriggerDialog.show();
                            //VolumeUtils.getInstance(MyApplication.getInstance()).setRobotVolume();
                            mDialogShow = true;
                            MLog.i(TAG, "dialog show bujiu");
                        }
                    } catch (Exception e) {
                        MLog.i(TAG, "dialog show false");
                        //e.printStackTrace();
                    }
                    // 上屏语音音量 语音音量大小
                    onRecognizeVolume(voiceResult.getHighVolume());// voiceResult.getVolume());
                }
                break;
                case PARTIAL: {
                    // 上屏状态
                    onRecognizePartial(voiceResult.getSpeakText());
                }
                break;
                case REC_END: {
                    // 语音检测识别结束 已经检测到语音终点，等待网络返回
                    onRecognizeEnd();
                }
                break;
                case ERROR: {
                    if (VoiceModeAdapter.isAudioBox()) {
                        if (mRepeatRecoCount < 2) {
                            mRepeatRecoCount += 1;
                        } else {
                            mRepeatRecoCount = 0;
                        }
                    }
                    // 其他情况错误状态返回给调用方
                    //VolumeUtils.getInstance(MyApplication.getInstance()).setMuteWithNoUi(false);
                    onRecognizeError(voiceResult);
                }
                break;
                case EXIT: {
                    onRecognizeExit();
                    mVoiceState = VoiceStatus.ASR_EXIT;
                }
                break;
                default:
                    break;
            }
        }
    };

    private void onRecognizeReady() {
        mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_REC_READY));
        onSetDialogDismissTimer(RECOGNIZE_MAX_DEALY);
        //Intent intentscreensave = new Intent(SCREENSAVE_DISMISS);
        //MyApplication.getInstance().sendBroadcast(intentscreensave);
        if (mVoiceHandler != null) {
            mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_REC_READY));
        }
    }

    private void onRecognizeVolume(int volume) {
        mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_SHOW_RECOGNIZING, volume, 0));
    }


    private void onRecognizeResult(String msg) {
        mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_SHOW_RESULT, msg));
    }


    private void onRecognizeError(VoiceInterface.VoiceResult voiceResult) {
        //LogUtil.log("onError");
        int note;
        switch (voiceResult.getErrorCode()) {
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                note = R.string.str_error_network_timeout;
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                note = R.string.str_error_network;
                break;
            case SpeechRecognizer.ERROR_AUDIO:
                note = R.string.str_error_audio;//3101
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                note = R.string.str_error_audiopermission;
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                note = R.string.str_reco_busy;
                break;
            case SpeechRecognizer.ERROR_SERVER:
                note = R.string.str_error_server;
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                note = R.string.str_error_client;
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                note = R.string.str_error_speech_timeout;
                break;
            case SpeechRecognizer.ERROR_NO_MATCH://7101
                note = R.string.str_error_nomatch;
                break;
            default:
                note = R.string.str_error_unknown;
                break;
        }
        mVoiceModeAdapter.setRecognizeGoOn(false);
        MLog.i(TAG, "ErrorCode:" + voiceResult.getErrorCode() + " cnt:" + mRepeatRecoCount);
        if (mVoiceTriggerDialog != null) {
            mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_SHOW_ERROR, note));
        }
        Led.showLedOff();
        LedUtil.closeHorseLight();
    }


    private void onRecognizeEnd() {
        mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_REC_END));
    }

    private void onRecognizePartial(String msg) {
        mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_REC_PARTIAL, msg));
    }

    private void onRecognizeFinish(VoiceInterface.VoiceResult voiceResult) {
        Context ctx = VoiceApp.getInstance();
        if (mVoiceTriggerDialog != null && voiceResult != null) {
            try {
                String result = voiceResult.getDuerResult();
                String originSpeech = voiceResult.getSpeakText();
                MLog.i(TAG, "result:" + result);
                if (!TextUtils.isEmpty(result)) {
                    DuerBean duerBean = GsonUtils.getDuerBean(result);
                    mDuerResult = result;
                    if (duerBean != null || originSpeech != null) {
                        ActionUtils.skySceneProcess(ctx, duerBean, originSpeech);
                    } else {
                        MLog.i(TAG, "dealReceiveMsg result null " + result);
                        onSetDialogDismissTimer(DIALOG_DIMISS_DELAY);
                    }
                } else {
                    onSetDialogDismissTimer(DIALOG_DIMISS_DELAY);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onSetDialogDismissTimer(DIALOG_DIMISS_DELAY);
            }
        }
    }

    private void onRecognizeExit() {
        mVoiceModeAdapter.setRecognizeGoOn(false);
        mIsRecoCanStart = true;
        mRecoFinishTime = System.currentTimeMillis();

        mVoiceHandler.sendEmptyMessageDelayed(MSG_REC_EXIT, 0);

        if (!ActivityManager.isSeachActivityOn()) {
            onSetDialogDismissTimer(DIALOG_DIMISS_DELAY);
        }

        if (mRepeatRecoCount > 0) {
            MLog.i(TAG, "repeat reco check:" + mRepeatRecoCountReal + " cnt:" + mRepeatRecoCount);
            if (mRepeatRecoCountReal != mRepeatRecoCount) {
                MLog.i(TAG, "重新识别");
                mRepeatRecoCountReal = mRepeatRecoCount;
                mDuerState = VoiceInterface.VoiceState.READY;
                startRecognize(VoiceApp.getInstance());
                //Intent intent = new Intent(GlobalVariable.ACTION_VOICE_RECO_GOON);
                //mContext.sendBroadcast(intent);
                return;
            }
        }

        mRepeatRecoCountReal = 0;
        //识别完成，进入唤醒
        if (VoiceModeAdapter.isAudioBox() && mAudioboxReceiver.getScreenStatus()) {
            MLog.i(TAG, "请说唤醒词:小派小派");
            Led.showLedOff();

            Runnable wakeuprun = new Runnable() {
                @Override
                public void run() {
                    if (1 == VoiceModeAdapter.getWakeupProperty()) {
                        mVoiceModeAdapter.startWakeUp(VoiceApp.getInstance(), mDuserSdk);
                    }
                }
            };

            if (!mVoiceHandler.postDelayed(wakeuprun, WAKE_THEN_REC_TIME_PRIEOD)) {
                //百度要求kitt版本停止唤醒到识别时间间隔200ms
                Log.i(TAG, "message post fail");
            }

        }
    }

    private void onSetDialogDismissTimer(long delayMillis) {
        MLog.i(TAG, "voice SetDialogDismisss:" + delayMillis);
        mVoiceHandler.removeMessages(MSG_DIALOG_DISMISS);
        mVoiceHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, delayMillis);
    }

    private void removeMessages() {
        mVoiceHandler.removeMessages(MSG_DIALOG_DISMISS);
        mVoiceHandler.removeMessages(MSG_REC_EXIT);
        mVoiceHandler.removeMessages(MSG_SHOW_ERROR);
        mVoiceHandler.removeMessages(MSG_SHOW_RECOGNIZING);
        mVoiceHandler.removeMessages(MSG_SHOW_RESULT);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((SkySceneService.LocalBinder) service).getService();
            mBound = true;
            //LogUtil.log("onServiceConnected");
            //注册回调接口
            mService.setOnSceneListener(new ISceneCallback() {
                @Override
                public void onSceneCheckedOver(boolean matched) {
                    MLog.i(TAG, "onScenCheckedOver");
                    onRecognizeResult(mDuerResult);
                    if (matched && !ActivityManager.isSeachActivityOn()) {
                        onSetDialogDismissTimer(RECOGNIZE_FINISH_DEALY);
                    }
                    if (!matched) {
                        //开始识别
                        Log.i(TAG, "voiceResultProc");
                        BdAsrTranslator.getInstance().translate(mDuerResult);
                    }
                }

                @Override
                public void onSceneEmpty() {
                    // TODO
                }

                @Override
                public void onSceneRegisted(String scene) {
                    // TODO
                }

                @Override
                public void onSearchPageRegisted() {
                    // TODO
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mService = null;
        }
    };

    public class AudioBoxReceiver extends BroadcastReceiver {
        private boolean mSceenOn = true;

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            MLog.i(TAG, "AudioRecoderReceiver:" + intent.getAction());
            switch (action) {
                case Intent.ACTION_SCREEN_OFF:
                case GlobalVariable.APPLY_AUDIO_RECORDER_ACTION:
                    mSceenOn = false;
                    GuideTip tip = GuideTip.getInstance();
                    if (tip != null) {
                        tip.pauseQQMusic();//pause music when power off
                    }
                    if (0 == VoiceModeAdapter.getWakeupProperty()) {
                        break;
                    }
                    if (WakeupStatus.getInstance().getWakeupStatus()) {
                        WakeupStatus.getInstance().setWakeupStatus(false);
                        //停止唤醒，开始识别
                        Led.showLedOff();
                        mRobot.stopSpeak();
                        mVoiceModeAdapter.stopWakeUp(mDuserSdk);
                        mVoiceModeAdapter.setRecognizeGoOn(false);
                        onSetDialogDismissTimer(0);
                    }
                    cancelRecognize();
                    MLog.i(TAG, "SCREEN_OFF");
                    break;
                case Intent.ACTION_SCREEN_ON:
                case GlobalVariable.RELEASE_AUDIO_RECORDER_ACTION:
                    mSceenOn = true;
                    if (0 == VoiceModeAdapter.getWakeupProperty()) {
                        break;
                    }
                    if (!WakeupStatus.getInstance().getWakeupStatus()) {
                        WakeupStatus.getInstance().setWakeupStatus(true);
                        mVoiceHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (1 == VoiceModeAdapter.getWakeupProperty()) {
                                    mVoiceModeAdapter.startWakeUp(context, mDuserSdk);
                                }
                            }
                        }, WAKE_THEN_REC_TIME_PRIEOD);
                    }
                    MLog.i(TAG, "SCREEN_ON 说唤醒词'小派小派'，再说其他内容!");
                    break;
                case GlobalVariable.ACTION_VOICE_RECO_GOON:
                    MLog.i(TAG, "second time recognize");
                    stopWakeupAndStartRecognize(context);
                    break;

                case WAKEUP_OPEN_ACTION:
                    if (VoiceModeAdapter.isAudioBox()) {
                        VoiceModeAdapter.setWakeupProperty(2);//opening
                        mVoiceHandler.removeMessages(MSG_WAKEUP_OPEN);
                        Message msg = new Message();
                        msg.what = MSG_WAKEUP_OPEN;
                        mVoiceHandler.sendMessageDelayed(msg, 50);
                    }
                    break;
                case WAKEUP_CLOSE_ACTION:
                    if (VoiceModeAdapter.isAudioBox()) {
                        VoiceModeAdapter.setWakeupProperty(3);//closing
                        mVoiceHandler.removeMessages(MSG_WAKEUP_CLOSE);
                        Message msg = new Message();
                        msg.what = MSG_WAKEUP_CLOSE;
                        mVoiceHandler.sendMessageDelayed(msg, 50);
                    }
                    break;
                case ANGEL_KARAOKE_ACTION:
                    if (0 == VoiceModeAdapter.getWakeupProperty()) {
                        break;
                    }
                    if (intent.hasExtra("voice_result")) {
                        if (intent.hasExtra("voice_type")) {
                            String type = intent.getStringExtra("voice_type");
                            MLog.i(TAG, "KARAOKE type:" + type);
                        }
                        int result = intent.getIntExtra("voice_result", 1);
                        if (result == 0) {
                            if (mVoiceTriggerDialog != null && mDialogShow) {
                                BdTTS.getInstance().talk(context.getString(R.string.str_music_unfound));
                            } else {
                                BdTTS.getInstance().talkWithoutDisplay(context.getString(R.string.str_music_unfound));
                            }
                        }
                        MLog.i(TAG, "KARAOKE search result:" + result);
                    }
                    break;
                case SPEECH_CONTENT:
                    if(intent.hasExtra("content")) {

                        String content = intent.getStringExtra("content");
                        MLog.i(TAG, "receiveMsg:" + content);
                        if (mVoiceTriggerDialog != null && mDialogShow) {
                            BdTTS.getInstance().talk(content);
                        } else {
                            BdTTS.getInstance().talkWithoutDisplay(content);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        public boolean getScreenStatus() {
            //LogUtil.log("getScreenStatus:" + mSceenOn);
            return mSceenOn;
        }
    }

    private static final int ROBOT_INPUT = 1;
    private static final int CLIENT_INPUTING = 2;

    private static final int MSG_REC_READY = 0;
    private static final int MSG_SHOW_RECOGNIZING = 1;
    private static final int MSG_REC_END = 2;
    public static final int MSG_REC_PARTIAL = 3;
    private static final int MSG_SHOW_RESULT = 4;
    private static final int MSG_SHOW_ERROR = 5;
    private static final int MSG_REC_EXIT = 6;
    private static final int MSG_DIALOG_DISMISS = 9;
    private static final int MSG_WAKEUP_OPEN = 12;
    private static final int MSG_WAKEUP_CLOSE = 13;
    private static final int MSG_ADDITION_MSG = 30;
    private static final int MSG_ROBOT_SPEECH_OVER = 31;
    private static final int MSG_ROBOT_SPEECH_ERROR = 32;

    private static class VoiceHandler extends Handler {
        private final WeakReference<BdController> mWeakReference;

        VoiceHandler(BdController dialog) {
            super(Looper.getMainLooper());
            mWeakReference = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            final BdController manager = mWeakReference.get();
            VoiceTriggerDialog dialog = manager.mVoiceTriggerDialog;
            if (dialog != null) {
                switch (msg.what) {
                    case MSG_REC_READY:
                        dialog.mRecognizeStatus = msg.what;
                        dialog.hideCoversation();
                        break;
                    case MSG_SHOW_RESULT:
                        dialog.mRecognizeStatus = msg.what;
                        dialog.setRecordAnimStatus(false);
                        dialog.recordAnimStop();
                        dialog.mVoiceLine.stop();
                        String result = (String) msg.obj;
                        dialog.jokeShow();
                        dialog.coversationShow(result);
                        break;

                    case MSG_SHOW_RECOGNIZING:
                        dialog.mRecognizeStatus = msg.what;
                        if (dialog.getRecordAnimStatus()) {
                            if (VoiceModeAdapter.isAudioBox()) {
                                dialog.mVoiceLine.setWaveHeight_big(msg.arg1 / 10);
                            } else {
                                dialog.mVoiceLine.setWaveHeight_big(msg.arg1 / 20);
                            }
                        }
                        break;
                    case MSG_REC_PARTIAL:
                        dialog.mRecognizeStatus = msg.what;
                        dialog.showTextContent(CLIENT_INPUTING, (String) msg.obj);
                        Led.showVoiceRecognizing();
                        VoiceService.trySendRecognizingCommand((String) msg.obj);
                        break;
                    case MSG_SHOW_ERROR:
                        //LogUtil.log("MSG_SHOW_ERROR");
                        dialog.mRecognizeStatus = msg.what;

                        if (manager.mRepeatRecoCount == 0) {
                            dialog.setRecordAnimStatus(false);
                            dialog.recordAnimStop();
                            dialog.mVoiceLine.stop();
                            BdTTS.getInstance().talk(dialog.getContext().getString((int) msg.obj));
                        } else if (manager.mRepeatRecoCount == 1) {
                            dialog.setTextHint(dialog.getContext().getString((int) msg.obj) + dialog.getContext().getString(R.string.str_again));
                        } else if (manager.mRepeatRecoCount == 2) {
                            dialog.setTextHint(dialog.getContext().getString((int) msg.obj) + dialog.getContext().getString(R.string.str_again2));
                        }
                        break;
                    case MSG_REC_END:
                        //LogUtil.log("MSG_REC_END");
                        dialog.mRecognizeStatus = msg.what;
                        dialog.showPaiPaiByID(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
                        break;
                    case MSG_DIALOG_DISMISS:
                        MLog.i(TAG, "MSG_DIALOG_DISMISS");
                        mVoiceHandler.removeMessages(MSG_DIALOG_DISMISS);
//                        String content = BdTTS.getInstance().getWords();
                        if (manager.mAudioboxReceiver.getScreenStatus() && BdTTS.getInstance().isSpeak()) {
//                            LogUtil.log("not dismiss: " + content);
                            break;
                        }
                        if (VoiceModeAdapter.isAudioBox()) {
                            VoiceModeAdapter voiceMode = new VoiceModeAdapter();
                            if (voiceMode.isRecognizeGoOn()) {
                                break;
                            }
                        }
                        if (dialog.mRecognizeStatus > MSG_REC_PARTIAL) {
                            manager.removeMessages();
                            manager.mDialogShow = false;
                            VolumeUtils.getInstance(VoiceApp.getInstance()).setMuteWithNoUi(false);
                            dialog.dismiss();
//                            manager.stopVoiceTriggerDialog();
                        } else {
                            //LogUtil.log("mRecognizeStatus: " + dialog.mRecognizeStatus);
                            mVoiceHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, DIALOG_DIMISS_DELAY);
                        }
                        break;
                    case MSG_REC_EXIT:
                        dialog.mRecognizeStatus = msg.what;
                        if (manager.mRepeatRecoCount == 0) {
                            dialog.mVoiceLine.stop();
                        }
                        break;
                    case MSG_ADDITION_MSG:
                        if (manager.mDuerState.ordinal() > VoiceInterface.VoiceState.VOLUME.ordinal()) {
                            MLog.i(TAG, "state:" + manager.mDuerState);
                            dialog.showTextContent(ROBOT_INPUT, (String) msg.obj);
                        }
                        break;
                    case MSG_ROBOT_SPEECH_OVER:
                        dialog.robotSpeechFinish();
                        break;
                    case MSG_ROBOT_SPEECH_ERROR:
                        dialog.robotSpeechError();
                        break;
                    default:
                        break;
                }
            }
            switch (msg.what) {
                case MSG_WAKEUP_OPEN:
                    MLog.i(TAG, "wakeup: on");
                    manager.mWakeupStatus = 1;
                    manager.mVoiceModeAdapter.startWakeUp(VoiceApp.getInstance(), manager.mDuserSdk);
                    break;
                case MSG_WAKEUP_CLOSE:
                    MLog.i(TAG, "wakeup: off");
                    if (manager.mVoiceState == VoiceStatus.ASR_READY) {
                        manager.cancelRecognize();
                        VoiceModeAdapter.setWakeupProperty(0);
                        break;
                    } else {
                        manager.mRobot.stopSpeak();
                        manager.mWakeupStatus = 2;
                        manager.mVoiceModeAdapter.stopWakeUp(manager.mDuserSdk);
                        manager.onSetDialogDismissTimer(0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void postEvent(EventMsg event) {
        //Log.i("one", "onEventMainThread:" + event.getWhat());
        switch (event.mWhat) {
            case EventMsg.MSG_ADDITION_MSG:
                if (event.mObj != null) {
                    if(mVoiceTriggerDialog == null){
                        showVoiceTriggerDialog(VoiceApp.getInstance());
                    }else if(!mDialogShow){
                        mVoiceTriggerDialog.show();
                    }
                    mDialogShow = true;
                    //LogUtil.log("onEventMainThread:" + event.getWhat() + " " + event.mObj);
                    //mVoiceTriggerDialog.showTextContent(ROBOT_INPUT, (String) event.mObj);
                    if (mDuerState.ordinal() > VoiceInterface.VoiceState.VOLUME.ordinal()) {
                        MLog.i(TAG, "state:" + mDuerState);
                        mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_ADDITION_MSG, (String) event.mObj));
                    }
                }
                break;
            case EventMsg.MSG_ROBOT_SPEECH_ERROR:
                if (isDialogShow()) {
                    mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_ROBOT_SPEECH_ERROR));
                }
                break;
            case EventMsg.MSG_ROBOT_SPEECH_OVER:
                if (isDialogShow()) {
                    mVoiceHandler.sendMessage(mVoiceHandler.obtainMessage(MSG_ROBOT_SPEECH_OVER));
                }
                break;
            case EventMsg.MSG_TEXTMARQUEE_OVER:
                if (isDialogShow()) {
                    if (mVoiceTriggerDialog.isRobotTalkError()) {
                        onSetDialogDismissTimer(0);
                    }
                }
                break;
            case EventMsg.MSG_DISMISS_DIALOG:
                //LogUtil.log("onEventMainThread:" + event.getWhat() + " " + event.mArg);
                onSetDialogDismissTimer(event.mArg);
                break;
            case EventMsg.MSG_ROBOT_READ_PROGRESS:
                if (isDialogShow()) {
                    mVoiceTriggerDialog.setReadProgress(event.mArg);
                }
                break;
            default:
                break;
        }
    }
}
