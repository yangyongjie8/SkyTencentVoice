package com.skyworthdigital.voice.tencent_module;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.skyworthdigital.skysmartsdk.RequestCallback;
import com.skyworthdigital.skysmartsdk.SkySmartSDK;
import com.skyworthdigital.skysmartsdk.bean.SkyBean;
import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsAsrTranslator;
import com.skyworthdigital.voice.common.AbsController;
import com.skyworthdigital.voice.common.AbsRecognizer;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.AbsWakeup;
import com.skyworthdigital.voice.common.IRecogListener;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.IWakeupResultListener;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.GsonUtils;
import com.skyworthdigital.voice.dingdang.utils.LedUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.scene.ISceneCallback;
import com.skyworthdigital.voice.scene.SkySceneService;
import com.tencent.ai.sdk.utils.ISSErrors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TxController extends AbsController implements AbsTTS.MyTTSListener {
    private static final String TAG = TxController.class.getSimpleName();
    private AbsRecognizer myRecognizer;
    private AbsWakeup myWakeup;
    private AbsTTS myTTS = TxTTS.getInstance(this);
    private SkyAsrDialogControl mAsrDialogControler = null;

    private Context mContext;
    public static final long DEFAULT_DISMISS_TIME = 60000;
    private static final long DEFAULT_DISMISS_3S = 3000;
    private SkySceneService mSceneService;
    private boolean mBound = false;
    private String mRecoResult;
    private TxController.BoxReceiver mBoxReceiver;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private static final String WAKEUP_OPEN_ACTION = "com.skyworthdigital.voice.action.WAKEUP_OPEN";
    private static final String WAKEUP_CLOSE_ACTION = "com.skyworthdigital.voice.action.WAKEUP_CLOSE";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isKeyDown = false;//是否语音遥控器按下
    private long mKeyDownTime = 0;
    private long mKeyUpTime = 0;

    public static TxController getInstance() {
        if (mManagerInstance[1] == null) {
            synchronized (TxController.class) {
                if (mManagerInstance[1] == null) {
                    mManagerInstance[1] = new TxController();
                }
            }
        }
        return (TxController) mManagerInstance[1];
    }

    private TxController() {
        mContext = com.skyworthdigital.voice.VoiceApp.getInstance();
        if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
            myWakeup = TxWakeup.getInstance(mWkresultlistener);
            myWakeup.init();
        }
        myRecognizer = TxRecognizer.getInstance(mRecogListener);
        mAsrDialogControler = new SkyAsrDialogControl();
        GuideTip.getInstance().setDialog(mAsrDialogControler);
        TxTvLiveController.getInstance().updateTvliveDbFromNet();
        registerReceiver();
    }

    private void registerReceiver(){
        mBoxReceiver = new TxController.BoxReceiver();
        final IntentFilter mScreenCheckFilter = new IntentFilter();
        mScreenCheckFilter.addAction(IStatus.ACTION_RESTART_ASR);
        mScreenCheckFilter.addAction(IStatus.ACTION_TTS);
        mScreenCheckFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenCheckFilter.addAction(Intent.ACTION_SCREEN_ON);
        mScreenCheckFilter.addAction(IStatus.ACTION_FORCE_QUIT_ASR);
        mScreenCheckFilter.addAction(WAKEUP_CLOSE_ACTION);
        mScreenCheckFilter.addAction(WAKEUP_OPEN_ACTION);
        mContext.registerReceiver(mBoxReceiver, mScreenCheckFilter);
    }

    @Override
    public void onDestroy() {
        mBound = false;
        mContext.unbindService(mConnection);
        com.skyworthdigital.voice.VoiceApp.getInstance().unregisterReceiver(mBoxReceiver);
    }

    @Override
    public boolean onKeyEvent(int code) {
        switch (code) {
            case KeyEvent.KEYCODE_BACK:
            case VoiceApp.KEYCODE_TA412_BACK:
                try {
                    if (mAsrDialogControler != null &&
                            mAsrDialogControler.mAsrDialog != null && mAsrDialogControler.mAsrDialog.isGuideDialogShow()) {
                        Log.d(TAG, "dialog showing");
                        mAsrDialogControler.mAsrDialog.closeGuideDialog();
                        return true;
                    } else if (mAsrDialogControler != null && !mAsrDialogControler.isTvDialog() &&
                            mAsrDialogControler.mAsrDialog != null && (!mAsrDialogControler.mAsrDialog.isGuideDialogShow()||mAsrDialogControler.mAsrDialog.isShowing())) {
                        Log.d(TAG, "not tv schedule & not dialog showing");
                        myTTS.stopSpeak();
                        IStatus.mSmallDialogDimissTime = System.currentTimeMillis() - 1;
                        mAsrDialogControler.dialogDismiss(0);
                        return true;
                    }
                    Log.d(TAG, "press back");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return false;
    }

    public SkyAsrDialogControl getAsrDialogControler() {
        return mAsrDialogControler;
    }

    public boolean isRecognizing(){
        return myRecognizer.isRecogning();
    }

    public void manualRecognizeStart() {
        MLog.i(TAG, "语音键按下");
        mKeyDownTime = System.currentTimeMillis();

        if (!mBound) {
            Intent intent = new Intent(mContext, SkySceneService.class);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            registerReceiver();
        }

//        if (isKeyDown) {
        myRecognizer.start();
//        }
        myTTS.stopSpeak();
        //myRecognizer.stop();
        String sayhi = WakeUpWord.getWord();
        IStatus.mRecognizeStatus = IStatus.STATUS_READY;
        mAsrDialogControler.show(mContext);
        IStatus.mAsrErrorCnt = 0;
        mAsrDialogControler.dialogRefresh(mContext, null, sayhi, 0);
        mAsrDialogControler.dialogDismiss(DEFAULT_DISMISS_TIME);
        VolumeUtils.getInstance(mContext).setMuteWithNoUi(true);
        mRecoResult = null;
        //SkyRing.getInstance().playDing();
        //myTTS.talkWithoutDisplay(sayhi);
//        if (isKeyDown) {
//            isKeyDown = false;
//        } else {
//            myRecognizer.cancel();
//            isKeyDown = true;
//            mAsrDialogControler.dialogRefresh(mContext, null, VoiceApp.getInstance().getResources().getString(R.string.str_reco_busy), 0);
//        }
    }

    public void manualRecognizeStop() {
        MLog.i(TAG, "语音键弹起");
        mKeyUpTime = System.currentTimeMillis();
        if (Math.abs(mKeyUpTime - mKeyDownTime) < 1000) {
            isKeyDown = true;
            myRecognizer.cancel();
            VolumeUtils.getInstance(mContext).setMuteWithNoUi(false);
            mAsrDialogControler.animStop();
            MLog.i(TAG, "cancel reco");
            return;
        }
        Runnable run = new Runnable() {
            @Override
            public void run() {
                myRecognizer.endRecognize();
                isKeyDown = true;
                VolumeUtils.getInstance(mContext).setMuteWithNoUi(false);
                LedUtil.openHorseLight();
                mAsrDialogControler.animStop();
            }
        };
        mHandler.postDelayed(run, 100);

    }

    public void testYuyiParse(String str) {
        mAsrDialogControler.setSpeechTextView(str);
        myRecognizer.yuyiParse(str);
    }

    public void cancelYuyiParse() {
        mAsrDialogControler.dialogDismiss(0l);
    }

    public void manualRecognizeCancel() {
        myRecognizer.cancel();
        VolumeUtils.getInstance(mContext).setMuteWithNoUi(false);
        mAsrDialogControler.animStop();
        mAsrDialogControler.dialogRefresh(mContext, null, com.skyworthdigital.voice.VoiceApp.getInstance().getResources().getString(R.string.str_input_note_hint), 0);
        mAsrDialogControler.dialogDismiss(3000);
    }

    private void wakeupSuccess() {
        MLog.d(TAG, "唤醒成功，启动识别");
        IStatus.setSceneType(IStatus.SCENE_GLOBAL);
        myTTS.stopSpeak();
        //myRecognizer.stop();
        if (0 == Utils.getWakeupProperty()) {
            myWakeup.stopWakeup();
            return;
        }
        String sayhi = WakeUpWord.getWord();
        IStatus.mRecognizeStatus = IStatus.STATUS_READY;
        mAsrDialogControler.show(mContext);
        IStatus.mAsrErrorCnt = 0;
        mAsrDialogControler.dialogRefresh(mContext, null, sayhi, 0);
        mAsrDialogControler.dialogDismiss(DEFAULT_DISMISS_TIME);
        //myTTS.talkWithoutDisplay(sayhi);
        myRecognizer.start();
    }

    public boolean isAsrDialogShowing(){
        return mAsrDialogControler!=null && mAsrDialogControler.mAsrDialog!=null && mAsrDialogControler.mAsrDialog.isShowing();
    }

    @Override
    public void dismissDialog(long delay) {
        mAsrDialogControler.dialogDismiss(delay);
    }

    private IWakeupResultListener mWkresultlistener = new IWakeupResultListener() {
        @Override
        public void onSuccess(String word, String result) {
            wakeupSuccess();
        }

        @Override
        public void onError(int errorCode, String errorMessge, String result) {
            MLog.d(TAG, "wakeup error" + errorCode + ":" + errorMessge);
        }
    };

    private IRecogListener mRecogListener = new IRecogListener() {
        @Override
        public void onAsrBegin() {
            MLog.d(TAG, "onAsrBegin:");
            IStatus.mRecognizeStatus = IStatus.STATUS_READY;
        }

        @Override
        public void onAsrEnd() {
            MLog.d(TAG, "onAsrEnd");
            IStatus.mRecognizeStatus = IStatus.STATUS_END;
            mAsrDialogControler.animStop();
        }

        @Override
        public void onAsrPartialResult(String results) {
            MLog.d(TAG, "Partial:" + results);
            mAsrDialogControler.dialogDismiss(DEFAULT_DISMISS_TIME);
            mRecoResult = null;
            IStatus.mRecognizeStatus = IStatus.STATUS_RECOGNITION;
            //if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
            mAsrDialogControler.dialogRefresh(mContext, null, results, 0);
            //}
            if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
                if (results.contains("叮当")) {
                    MLog.d(TAG, "asr wakeup checked");
                    wakeupSuccess();
                    return;
                }
            }
        }

        @Override
        public void onAsrFinalResult(String results) {
            MLog.d(TAG, "Final Result:" + results);
            IStatus.mRecognizeStatus = IStatus.STATUS_RECOGNITION;
            //if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
            mAsrDialogControler.dialogRefresh(mContext, null, results, 0);
            //}
        }

        @Override
        public void onAsrNluFinish(String recogResult) {
            MLog.d(TAG, "onAsrNluFinish:" + recogResult);
            IStatus.mRecognizeStatus = IStatus.STATUS_FINISHED;
            if (mAsrDialogControler != null && mAsrDialogControler.mAsrDialog != null) {
                skySceneProcess(mContext, recogResult);
            }
        }

        @Override
        public void onAsrError(long errorCode, String errorMessage, String descMessage) {
            //mAsrDialogControler.dialogRefresh(mContext, null, errorMessage, null);
            MLog.d(TAG, "onAsrError:" + descMessage + " code:" + errorCode);
            VolumeUtils.getInstance(mContext).setMuteWithNoUi(false);
            mAsrDialogControler.dialogDismiss(3000);
            LedUtil.closeHorseLight();
            IStatus.mAsrErrorCnt += 1;
            IStatus.mRecognizeStatus = IStatus.STATUS_ERROR;
            if (errorCode == ISSErrors.ISS_ERROR_NETWORK_RESPONSE_FAIL ||
                    errorCode == ISSErrors.ISS_ERROR_NETWORK_TIMEOUT
                    || errorCode == ISSErrors.ISS_ERROR_NETWORK_FAIL) {
                IStatus.mAsrErrorCnt = IStatus.getMaxAsrErrorCount();
                IStatus.setSceneType(IStatus.SCENE_NONE);
                //myRecognizer.stop();
                mAsrDialogControler.animStop();
                mAsrDialogControler.dialogDismiss(3000);
                mAsrDialogControler.dialogRefresh(mContext, null, com.skyworthdigital.voice.VoiceApp.getInstance().getResources().getString(R.string.str_error_network), 0);
            } else {
                if (mAsrDialogControler.mAsrDialog != null && (IStatus.isInScene() || (!IStatus.isInScene() && IStatus.mAsrErrorCnt < IStatus.getMaxAsrErrorCount()))) {
                    restartRecognize();
                } else if (!IStatus.isInScene() && IStatus.mAsrErrorCnt >= IStatus.getMaxAsrErrorCount()) {
                    MLog.d(TAG, "onAsrError code:" + errorCode);
                    //myRecognizer.stop();
                    mAsrDialogControler.animStop();
                    mAsrDialogControler.dialogDismiss(3000);
                    if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
                        if (errorCode == ISSErrors.ISS_ERROR_VOICE_TIMEOUT) {
                            mAsrDialogControler.dialogRefresh(mContext, null, com.skyworthdigital.voice.VoiceApp.getInstance().getResources().getString(R.string.str_error_audio), 0);
                        } else if (errorCode == ISSErrors.ISS_ERROR_NOT_INITIALIZED) {
                            mAsrDialogControler.dialogRefresh(mContext, null, com.skyworthdigital.voice.VoiceApp.getInstance().getResources().getString(R.string.str_error_init), 0);
                        } else if (errorCode == ISSErrors.ISS_ERROR_COMMOM_SERVICE_RESP) {
                            mAsrDialogControler.dialogRefresh(mContext, null, com.skyworthdigital.voice.VoiceApp.getInstance().getResources().getString(R.string.str_reco_busy), 0);
                        } else {
                            mAsrDialogControler.dialogRefresh(mContext, null, com.skyworthdigital.voice.VoiceApp.getInstance().getResources().getString(R.string.str_error_other) + errorCode, 0);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onChange(int status) {
        MLog.d(TAG, "onChange status:"+status);
        mAsrDialogControler.paipaiRefresh(status);
    }

    @Override
    public void onOutputChange(String output, int delay) {
        MLog.d(TAG, "onOutputChange");
        if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
            mAsrDialogControler.dialogRefresh(mContext, null, output, delay);
            mAsrDialogControler.dialogDismiss(3000);// 默认3秒消失
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mSceneService = ((SkySceneService.LocalBinder) service).getService();
            mBound = true;
            MLog.d(TAG, "onServiceConnected");
            mSceneService.setOnSceneListener(new ISceneCallback() {
                @Override
                public void onSceneCheckedOver(final boolean matched) {
                    MLog.d(TAG, "onScenCheckedOver：" + matched);
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                            mAsrDialogControler.dialogTxtClear();
                            if (matched) {
                                if (IStatus.mSceneDetectType == IStatus.SCENE_GIVEN) {
                                    if (!TxTTS.getInstance(null).isSpeak()) {
                                        IStatus.setSceneType(IStatus.SCENE_GIVEN);
                                        if (mAsrDialogControler.mAsrDialog != null && IStatus.isInScene()) {
                                            restartRecognize();
                                        }
                                        dialogResize(true);
                                    } else {
                                        IStatus.setSceneType(IStatus.SCENE_SHOULD_GIVEN);
                                        mAsrDialogControler.dialogDismiss(DEFAULT_DISMISS_3S);
                                    }
                                    mAsrDialogControler.showHeadLoading();
                                } else if (IStatus.mSceneDetectType == IStatus.SCENE_SEARCHPAGE) {
                                    IStatus.resetDismissTime();
                                } else {
                                    mAsrDialogControler.dialogDismiss(DEFAULT_DISMISS_3S);
                                }
                            }
                            if (!matched && !TextUtils.isEmpty(mRecoResult)) {
                                final AsrResult bean = GsonUtils.parseResult(mRecoResult, AsrResult.class);
                                if (bean == null) {
                                    MLog.d(TAG, "bean null" + mRecoResult);
                                    return;
                                }
                                MLog.d(TAG, "unmatch intent:" + bean.mSemanticJson.mSemantic.mIntent + " scenetype:" + IStatus.mSceneType);

                                // 自有平台结果
                                SkySmartSDK.executeCommand(VoiceApp.getInstance(), bean.mQuery, new RequestCallback() {
                                    @Override
                                    public void onFinish(SkyBean skyBean) {
                                        if(!doFinish(skyBean)){
                                            //百度平台结果
                                            Log.i(TAG, "voiceResultProc");
                                            AbsAsrTranslator.getInstance().translate(bean);
                                            mRecoResult = null;
                                        }
                                    }
                                });
                            }
                        }
                    };
                    mExecutor.execute(run);
                }

                @Override
                public void onSceneEmpty() {
                    IStatus.mSceneDetectType = IStatus.SCENE_NONE;
                    IStatus.mScene = null;
                    MLog.d(TAG, "onSceneEmpty");
                    IStatus.setSceneType(IStatus.SCENE_SHOULD_STOP);
                    mAsrDialogControler.dialogDismiss(3000);//防切到另一个场景时弹框已消失
                }

                @Override
                public void onSceneRegisted(String scene) {
                    MLog.d(TAG, "onSceneRegisted");
                    IStatus.mSceneDetectType = IStatus.SCENE_GIVEN;
                    IStatus.mScene = scene;
                    if (!TxTTS.getInstance(null).isSpeak()) {
                        IStatus.setSceneType(IStatus.SCENE_GIVEN);
                        if (mAsrDialogControler.mAsrDialog != null && IStatus.isInScene()) {
                            restartRecognize();
                        }
                        dialogResize(true);
                    } else {
                        IStatus.setSceneType(IStatus.SCENE_SHOULD_GIVEN);
                        mAsrDialogControler.dialogDismiss(DEFAULT_DISMISS_3S);
                    }
                }

                @Override
                public void onSearchPageRegisted() {
                    MLog.d(TAG, "onSearchPageRegisted");
                    IStatus.mSceneDetectType = IStatus.SCENE_SEARCHPAGE;
                    //if (!MyTTS.getInstance(null).isSpeak()) {
                    IStatus.setSceneType(IStatus.SCENE_SEARCHPAGE);
                    if (mAsrDialogControler.mAsrDialog != null && IStatus.isInScene()) {
                        restartRecognize();
                    }
                    dialogResize(false);
                    //}
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            MLog.d(TAG, "onServiceDisconnected");
            mBound = false;
            mSceneService = null;
        }
    };

    /**
     * 解析处理自有nlp结果
     * @param bean
     * @return true 已消费
     */
    private boolean doFinish(SkyBean bean){

        if(bean==null || (TextUtils.isEmpty(bean.getAnswer())&&TextUtils.isEmpty(bean.getAccident()))){
            Log.w(TAG, "无应答结果，可根据需要继续执行自有业务流程");
            return false;
        }
        if("unknown".equals(bean.getIntentType())){
            Log.w(TAG, "未被成功理解，可根据需要继续执行自有业务流程");
            return false;
        }
//                if(!"smarthome".equals(bean.getIntentType())){
//                    Log.w(TAG, "不是家居控制指令。");
//                    return;
//                }
        if(TextUtils.isEmpty(bean.getAccident())){// 完成应答
            Log.i(TAG, "应答："+bean.getAnswer());
            TxTTS.getInstance(null).talk(bean.getAnswer());
            return true;
        }else {
            return false;
        }
//        // 缓存slots，用于翻页查找
//        if("movie".equals(bean.getIntentType())){
//            if(!bean.getVideos().getMovieSlots().equals(slots)){
//                Log.w(TAG, "这次的slots和前一个不相同哦");
//                Log.i(TAG, "前一个："+slots);
//                slots = bean.getVideos().getMovieSlots();
//                Log.i(TAG, "最新："+slots);
//            }
//        }
    }

    private void skySceneProcess(final Context ctx, String result) {
        mRecoResult = result;
        if (mRecoResult != null) {
            try {
                Intent intent;
                final AsrResult bean = GsonUtils.parseResult(result, AsrResult.class);
                if (bean == null) {
                    MLog.d(TAG, "bean is null");
                    mAsrDialogControler.dialogDismiss(DISMISS_DELAY_NORMAL);
                    return;
                }
                String mIntent = (bean.mSemanticJson != null && bean.mSemanticJson.mSemantic != null
                        && bean.mSemanticJson.mSemantic.mIntent != null) ? (bean.mSemanticJson.mSemantic.mIntent) : "";
                Boolean isEnd = bean.mSession;
                if (bean.mServerRet < 0) {
                    myTTS.talk(ctx.getString(R.string.server_busy) + bean.mServerRet);
                    return;
                }
                if (bean.mSemanticJson != null && bean.mSemanticJson.mSemantic != null) {
                    ReportUtils.report2BigData(bean.mSemanticJson.mSemantic);
                }
                if (StringUtils.isExitCmdFromSpeech(bean.mQuery)) {
                    if (IStatus.mSceneType != IStatus.SCENE_GLOBAL) {
                        IStatus.mSmallDialogDimissTime = System.currentTimeMillis() - 1;
                        mAsrDialogControler.dialogDismiss(0);
                        return;
                    }
                }
                if (StringUtils.isHomeCmdFromSpeech(bean.mQuery)) {
                    if(GuideTip.getInstance().isLauncherHome()){
                        mAsrDialogControler.dialogDismiss(0);
                        return;
                    }
                    AppUtil.killTopApp();
                    TxTTS.getInstance(null).talk(ctx.getString(R.string.str_exit));
                    return;
                }
                if (StringUtils.isHelpCmdFromSpeech(bean.mQuery)) {
                    mAsrDialogControler.dialogTxtClear();
                    mAsrDialogControler.showHeadLoading();
                    bean.mSemanticJson.mSemantic.mIntent = "cando";
                    ActionUtils.jumpToHelp(mContext, bean);
                    return;
                }
                if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType != GlobalVariable.AI_REMOTE) {
                    if (!isEnd || IStatus.isInScene() || TextUtils.equals(bean.mAnswer, "我在，有什么需要我帮忙吗？")) {
                        restartRecognize();
                    }
                }

                if (DefaultCmds.isPlay(mIntent)) {
                    intent = TxCmds.composePlayControlIntent(bean);
                    if (intent != null) {
                        ctx.startService(intent);
                        return;
                    } else {
                        MLog.d(TAG, "not play intent");
                    }
                }

                intent = DefaultCmds.PlayCmdPatchProcess(bean.mQuery);
                if (intent != null) {
                    MLog.d(TAG, "CmdPatchProcess intent");
                    ctx.startService(intent);
                    return;
                }
                if (!ActionUtils.specialCmdProcess(ctx, bean.mQuery) && !TextUtils.isEmpty(bean.mQuery)) {
                    //if (!specialCmdProcess(ctx, originSpeech)) {
                    intent = new Intent(SkySceneService.INTENT_TOPACTIVITY_CALL);
                    String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
                    intent.putExtra(DefaultCmds.SEQUERY, bean.mQuery);
                    intent.setPackage(strPackage);
                    ctx.startService(intent);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class BoxReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            MLog.d(TAG, "BoxReceiver:" + action);
            switch (action) {
                case IStatus.ACTION_RESTART_ASR:
                    restartRecognize();
                    break;

                case IStatus.ACTION_FORCE_QUIT_ASR:
                    myRecognizer.stop();
                    mAsrDialogControler.animStop();
                    //mAsrDialogControler.dialogDismiss(1500);
                    //IStatus.mRecognizeStatus = IStatus.STATUS_FINISHED;
                    //myTTS.talkWithoutDisplay("先退下了，有事再叫我", "先退下了，有事再叫我");
                    break;

                case IStatus.ACTION_TTS:
                    String tts = intent.getStringExtra("tts");
                    myTTS.talk(tts);
                    break;

                case WAKEUP_CLOSE_ACTION:
                    Utils.setWakeupProperty(3);//closing
                    IStatus.mSceneType = IStatus.SCENE_NONE;
                    GuideTip tip = GuideTip.getInstance();
                    if (tip != null) {
                        tip.pauseQQMusic();//pause music when power off
                    }
                    if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE && myWakeup != null) {
                        myWakeup.stopWakeup();
                    }
                    myRecognizer.release();
                    mAsrDialogControler.animStop();
                    myTTS.stopSpeak();
                    IStatus.mSmallDialogDimissTime = System.currentTimeMillis() - 1;
                    mAsrDialogControler.dialogDismiss(0);
                    MLog.d(TAG, "WAKEUP_CLOSE");
                    Utils.setWakeupProperty(0);//closing
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    //case GlobalVariable.APPLY_AUDIO_RECORDER_ACTION:
                    //mSceenOn = false;
                    IStatus.mSceneType = IStatus.SCENE_NONE;
                    tip = GuideTip.getInstance();
                    if (tip != null) {
                        tip.pauseQQMusic();//pause music when power off
                    }
                    myRecognizer.release();
                    mAsrDialogControler.animStop();
                    myTTS.stopSpeak();
                    IStatus.mSmallDialogDimissTime = System.currentTimeMillis() - 1;
                    mAsrDialogControler.dialogDismiss(0);
                    MLog.d(TAG, "SCREEN_OFF");
                    break;

                case WAKEUP_OPEN_ACTION:
                    Utils.setWakeupProperty(2);//opening
                    if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
                        myWakeup.startWakeup();
                    }
                    myRecognizer.register();
                    myTTS.stopSpeak();
                    MLog.d(TAG, "WAKEUP_OPEN");
                    Utils.setWakeupProperty(1);//opening
                    break;
                case Intent.ACTION_SCREEN_ON:
                    //case GlobalVariable.RELEASE_AUDIO_RECORDER_ACTION:
                    if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE && myWakeup != null) {
                        myWakeup.startWakeup();
                    }
                    myRecognizer.register();
                    myTTS.stopSpeak();
                    MLog.d(TAG, "SCREEN_ON");
                    break;
                default:
                    break;
            }
        }
    }

    private void dialogResize(boolean small) {
        if (!mSceneService.isSceneEmpty() && mAsrDialogControler.mAsrDialog != null) {
            mAsrDialogControler.mAsrDialog.dialogResize(small);
        }
    }

    private void showGuideDialog(String scene) {
        if (!mSceneService.isSceneEmpty() && mAsrDialogControler.mAsrDialog != null) {
            mAsrDialogControler.mAsrDialog.showGuideDialog(scene);
        }
    }

    private void restartRecognize() {
        if (!myRecognizer.isRecogning()) {
            MLog.d(TAG, "启动识音");
            IStatus.mRecognizeStatus = IStatus.STATUS_READY;
            mAsrDialogControler.voiceRecordRefresh();
            mAsrDialogControler.dialogDismiss(DEFAULT_DISMISS_TIME);
            myRecognizer.start();
        } else {
            MLog.d(TAG, "识音中");
        }
    }

}
