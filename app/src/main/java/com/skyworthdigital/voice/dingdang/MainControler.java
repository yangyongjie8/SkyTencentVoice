package com.skyworthdigital.voice.dingdang;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.skyworthdigital.voice.dingdang.control.ActionUtils;
import com.skyworthdigital.voice.dingdang.control.model.AsrResult;
import com.skyworthdigital.voice.dingdang.control.recognization.IRecogListener;
import com.skyworthdigital.voice.dingdang.control.recognization.IStatus;
import com.skyworthdigital.voice.dingdang.control.recognization.MyRecognizer;
import com.skyworthdigital.voice.dingdang.control.record.SkyVoiceProcessor;
import com.skyworthdigital.voice.dingdang.control.tts.MyTTS;
import com.skyworthdigital.voice.dingdang.control.tts.WakeUpWord;
import com.skyworthdigital.voice.dingdang.control.wakeup.IWakeupResultListener;
import com.skyworthdigital.voice.dingdang.control.wakeup.MyWakeup;
import com.skyworthdigital.voice.dingdang.domains.tvlive.TvLiveControl;
import com.skyworthdigital.voice.dingdang.domains.videosearch.model.BeeSearchParams;
import com.skyworthdigital.voice.dingdang.globalcmd.GlobalUtil;
import com.skyworthdigital.voice.dingdang.scene.ISceneCallback;
import com.skyworthdigital.voice.dingdang.scene.SkySceneService;
import com.skyworthdigital.voice.dingdang.service.RecognizeService;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.DefaultCmds;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.GsonUtils;
import com.skyworthdigital.voice.dingdang.utils.GuideTip;
import com.skyworthdigital.voice.dingdang.utils.IntentUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.ReportUtils;
import com.skyworthdigital.voice.dingdang.utils.StringUtils;
import com.skyworthdigital.voice.dingdang.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.tencent.ai.sdk.utils.ISSErrors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainControler implements MyTTS.MyTTSListener {
    private static final String TAG = "MainControler";
    private MyRecognizer myRecognizer;
    private MyWakeup myWakeup;
    private MyTTS myTTS = MyTTS.getInstance(this);
    private SkyAsrDialogControl mAsrDialogControler = null;

    private Context mContext;
    private static final long DEFAULT_DISMISS_TIME = 60000;
    private static final long DEFAULT_DISMISS_3S = 3000;
    private SkySceneService mSceneService;
    private boolean mBound = false;
    private String mRecoResult;
    private BoxReceiver mBoxReceiver;
    private boolean mTvdialog = false;
    private static MainControler mManagerInstance = null;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private static final String WAKEUP_OPEN_ACTION = "com.skyworthdigital.voice.action.WAKEUP_OPEN";
    private static final String WAKEUP_CLOSE_ACTION = "com.skyworthdigital.voice.action.WAKEUP_CLOSE";
    private Handler mHandler = new Handler();
    private boolean isKeyUp = true;
    private long mKeyDownTime = 0;
    private long mKeyUpTime = 0;
    public volatile boolean isControllerVoice = true;//是否遥控器语音，可能是远场语音

    public static MainControler getInstance() {
        if (mManagerInstance == null) {
            mManagerInstance = new MainControler();
        }
        return mManagerInstance;
    }

    private MainControler() {
        mContext = VoiceApp.getInstance();
        if (VoiceApp.getInstance().mAiType == GlobalVariable.AI_VOICE) {
            if (Utils.isQ3031Recoder()) {
                SkyVoiceProcessor.init();
            }
            myWakeup = MyWakeup.getInstance(mWkresultlistener);
            myWakeup.init();
        }
        myRecognizer = MyRecognizer.getInstance(mRecogListener);
        mAsrDialogControler = new SkyAsrDialogControl();
        GuideTip.getInstance().setDialog(mAsrDialogControler);
        TvLiveControl.getInstance().updateTvliveDbFromNet();
        if (!mBound) {
            Intent intent = new Intent(mContext, SkySceneService.class);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        mBoxReceiver = new BoxReceiver();
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

    public void onDestroy() {
        VoiceApp.getInstance().unregisterReceiver(mBoxReceiver);
    }

    public boolean onKeyEvent(int code) {
        switch (code) {
            case KeyEvent.KEYCODE_BACK:
            case RecognizeService.KEYCODE_TA412_BACK:
                try {
                    if (mAsrDialogControler != null &&
                            mAsrDialogControler.mAsrDialog != null && mAsrDialogControler.mAsrDialog.isGuideDialogShow()) {
                        Log.d(TAG, "dialog showing");
                        mAsrDialogControler.mAsrDialog.closeGuideDialog();
                        return true;
                    } else if (!mTvdialog && mAsrDialogControler != null &&
                            mAsrDialogControler.mAsrDialog != null && !mAsrDialogControler.mAsrDialog.isGuideDialogShow()) {
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

    private void main(final AsrResult bean) {
        Context ctx = VoiceApp.getInstance();
        try {
            mAsrDialogControler.dialogDismiss(3000);
            if (bean == null || TextUtils.isEmpty(bean.mDomain)) {
                return;
            } else if (bean.mReturnCode != 0) {
                MLog.d(TAG, "bean.mReturnCode=" + bean.mReturnCode);
                return;
            }
            MLog.d(TAG, "domain:" + bean.mDomain + "intent:" + bean.mSemanticJson.mSemantic.mIntent + " scenetype:" + IStatus.mSceneType);
            //如果已经进入直播，则先进入直播流程
            if (TvLiveControl.getInstance().isTvLive()) {
                mAsrDialogControler.dialogTxtClear();
                if (mTvdialog) {
                    ActionUtils.hideTrailer(mAsrDialogControler);
                }
                if (TextUtils.equals(bean.mDomain, "trailer")) {//节目单
                    mAsrDialogControler.showHeadLoading();
                    mTvdialog = ActionUtils.jumpToTrailer(ctx, mAsrDialogControler, bean);
                    return;
                }
                if (TvLiveControl.getInstance().control(bean.mSemanticJson.mSemantic.getTvliveSlots(), bean.mQuery, mAsrDialogControler)) {
                    return;
                }
            }

            if (bean.mSemanticJson != null && bean.mSemanticJson.mSemantic != null && (TextUtils.equals(bean.mSemanticJson.mSemantic.mIntent, "back")
                    || TextUtils.equals(bean.mSemanticJson.mSemantic.mIntent, "back_tvhomepage"))) {
                ActionUtils.jumpToTvControl(ctx, mAsrDialogControler, bean);
                return;
            }

            if (VoiceApp.getInstance().mAiType == GlobalVariable.AI_VOICE) {
                if (IStatus.mSceneType == IStatus.SCENE_GIVEN || IStatus.mSceneType == IStatus.SCENE_SEARCHPAGE) {
                    if (ActionUtils.sceneControl(ctx, mAsrDialogControler, bean)) {
                        MLog.d(TAG, "sceneControl");
                        return;
                    }
                }
            }
            if (GlobalUtil.getInstance().control(ctx, bean.mQuery, null)) {
                MLog.d(TAG, "globalCommandExecute");//全局语音处理
            } else {
                if (BeeSearchParams.getInstance().isInSearchPage() && (bean.mQuery.startsWith("修改") || bean.mQuery.startsWith("修正") || bean.mQuery.startsWith("纠正"))) {
                    bean.mDomain = "video";
                }
                if (VoiceApp.getInstance().mAiType == GlobalVariable.AI_VOICE) {
                    if (IStatus.mSceneType == IStatus.SCENE_SHOULD_GIVEN || IStatus.mSceneType == IStatus.SCENE_SHOULD_STOP) {
                        MLog.d(TAG, "scene not sure");
                        return;
                    }
                }
                switch (bean.mDomain) {
                    case "direct_search":
                    case "cinema":
                    case "video":
                        ActionUtils.jumpToVideoSearch(mContext, mAsrDialogControler, bean);
                        //ActionUtils.startSearch(mContext, bean/*, mvideoPlayListener*/);
                        break;
                    case "music":
                        ActionUtils.jumpToMusic(mContext, mAsrDialogControler, bean);
                        break;
                    case "joke":
                        mAsrDialogControler.dialogDismiss(5000);
                        try {
                            if (bean.mData != null && !TextUtils.isEmpty(bean.mData.mJokeText)) {
                                mAsrDialogControler.dialogRefresh(ctx, null, bean.mData.mJokeText, 0);
                                //mAsrDialogControler.dialogRefreshDetail(mContext, bean.mData, DialogCellType.CELL_BAIKE_INFO);
                                myTTS.talkWithoutDisplay(bean.mData.mJokeText);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "chat":
                    case "baike":
                        ActionUtils.jumpToBaikeVideo(ctx, mAsrDialogControler, bean);
                        break;
                    case "ancient_poem":
                        ActionUtils.jumpToPoem(ctx, mAsrDialogControler, bean);
                        break;
                    case "fm":
                        ActionUtils.jumpToFM(ctx, mAsrDialogControler, bean);
                        break;
                    case "globalctrl":
                    case "tv":
                        ActionUtils.jumpToTvControl(ctx, mAsrDialogControler, bean);
                        break;
                    case "help":
                        ActionUtils.jumpToHelp(ctx, mAsrDialogControler, bean);
                        break;
                    case "recipe":
                        ActionUtils.jumpToRecipe(ctx, mAsrDialogControler, bean);
                        break;
                    case "news":
                        ActionUtils.jumpToNews(ctx, mAsrDialogControler, bean);
                        break;
                    case "train":
                        ActionUtils.jumpToTrain(ctx, mAsrDialogControler, bean);
                        break;
                    case "flight":
                        ActionUtils.jumpToFlight(ctx, mAsrDialogControler, bean);
                        break;
                    case "sports":
                        ActionUtils.jumpToSports(ctx, mAsrDialogControler, bean);
                        //myTTS.parseSemanticToTTS(result);
                        break;
                    case "trailer":
                        if (mTvdialog) {
                            ActionUtils.hideTrailer(mAsrDialogControler);
                        }
                        mTvdialog = ActionUtils.jumpToTrailer(ctx, mAsrDialogControler, bean);
                        return;
                    case "weather":
                        if (bean.mData != null && bean.mData.mCityWeatherInfo != null && bean.mData.mCityWeatherInfo.size() > 0
                                && bean.mData.mCityWeatherInfo.get(0).mBgImg != null) {
                            if (bean.mData.mCityWeatherInfo.get(0).mBgImg.size() > 0) {
                                String url = bean.mData.mCityWeatherInfo.get(0).mBgImg.get(0).mImg;
                                mAsrDialogControler.dialogRefreshBg(url);
                            }
                        }
                        if (!TextUtils.isEmpty(bean.mTips)) {
                            mAsrDialogControler.dialogRefresh(ctx, null, bean.mTips, 0);
                            MyTTS.getInstance(null).talkWithoutDisplay(bean.mTips);
                        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
                            mAsrDialogControler.dialogRefresh(ctx, null, bean.mAnswer, 0);
                            MyTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
                        }
                        break;
                    case "reminder":
                    case "alarm":
                        ActionUtils.jumpToAlarm(ctx, mAsrDialogControler, bean);
                        break;
                    case "common_qa":
                    case "world_records":
                    case "science":
                    case "htwhys":
                        if (!TextUtils.isEmpty(bean.mTips)) {
                            mAsrDialogControler.dialogRefresh(ctx, null, bean.mTips, 0);
                            MyTTS.getInstance(null).talkWithoutDisplay(bean.mTips);
                        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
                            mAsrDialogControler.dialogRefresh(ctx, null, bean.mAnswer, 0);
                            MyTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
                        }
                        break;
                    case "chengyu":
                        ActionUtils.jumpToChengyu(ctx, mAsrDialogControler, bean);
                        break;
                    case "astro":
                        mAsrDialogControler.dialogRefresh(mContext, bean, null, 0);
                        mAsrDialogControler.dialogDismiss(30000);
                        myTTS.parseSemanticToTTS(bean);
                        break;
                    case "sound":
                        ActionUtils.jumpToSound(ctx, mAsrDialogControler, bean);
                        break;
                    default:
                        if (IntentUtils.appLaunchExecute(mContext, bean.mQuery)) {
                            MLog.d(TAG, "app_launcher");
                            break;
                        }
                        mAsrDialogControler.dialogRefresh(mContext, bean, null, 0);
                        mAsrDialogControler.dialogDismiss(3000);
                        myTTS.parseSemanticToTTS(bean);
                        break;
                }
            }
            if (VoiceApp.getInstance().mAiType == GlobalVariable.AI_VOICE) {
                if (IStatus.mSceneType != IStatus.SCENE_GIVEN && IStatus.mSceneType != IStatus.SCENE_SEARCHPAGE && !bean.mSession) {
                    MLog.d(TAG, "RESTART_ASR 222");
                    mAsrDialogControler.dialogDismiss(DEFAULT_DISMISS_TIME);
                }
            }
            if (mTvdialog) {
                ActionUtils.hideTrailer(mAsrDialogControler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRecognizing(){
        return myRecognizer.isRecogning();
    }

    public void manualRecognizeStart() {
        MLog.i(TAG, "语音键按下");
        mKeyDownTime = System.currentTimeMillis();
//        if (isKeyUp) {
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
//        if (isKeyUp) {
//            isKeyUp = false;
//        } else {
//            myRecognizer.cancel();
//            isKeyUp = true;
//            mAsrDialogControler.dialogRefresh(mContext, null, VoiceApp.getInstance().getResources().getString(R.string.str_reco_busy), 0);
//        }
    }

    public void manualRecognizeStop() {
        MLog.i(TAG, "语音键弹起");
        mKeyUpTime = System.currentTimeMillis();
        if (Math.abs(mKeyUpTime - mKeyDownTime) < 1000) {
            isKeyUp = true;
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
                isKeyUp = true;
                VolumeUtils.getInstance(mContext).setMuteWithNoUi(false);
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
        mAsrDialogControler.dialogRefresh(mContext, null, VoiceApp.getInstance().getResources().getString(R.string.str_input_note_hint), 0);
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
            IStatus.mRecognizeStatus = IStatus.STATUS_READY;
        }

        @Override
        public void onAsrEnd() {
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
            if (VoiceApp.getInstance().mAiType == GlobalVariable.AI_VOICE) {
                if (results.contains("叮当")) {
                    MLog.d(TAG, "asr wakeup checked");
                    wakeupSuccess();
                    return;
                }
            }
        }

        @Override
        public void onAsrFinalResult(String results) {
            MLog.d(TAG, "Final Partial:" + results);
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
                mAsrDialogControler.dialogRefresh(mContext, null, VoiceApp.getInstance().getResources().getString(R.string.str_error_network), 0);
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
                            mAsrDialogControler.dialogRefresh(mContext, null, VoiceApp.getInstance().getResources().getString(R.string.str_error_audio), 0);
                        } else if (errorCode == ISSErrors.ISS_ERROR_NOT_INITIALIZED) {
                            mAsrDialogControler.dialogRefresh(mContext, null, VoiceApp.getInstance().getResources().getString(R.string.str_error_init), 0);
                        } else if (errorCode == ISSErrors.ISS_ERROR_COMMOM_SERVICE_RESP) {
                            mAsrDialogControler.dialogRefresh(mContext, null, VoiceApp.getInstance().getResources().getString(R.string.str_reco_busy), 0);
                        } else {
                            mAsrDialogControler.dialogRefresh(mContext, null, VoiceApp.getInstance().getResources().getString(R.string.str_error_other) + errorCode, 0);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onChange(int status) {
        mAsrDialogControler.paipaiRefresh(status);
    }

    @Override
    public void onOutputChange(String output, int delay) {
        if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
            mAsrDialogControler.dialogRefresh(mContext, null, output, delay);
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
                                    if (!MyTTS.getInstance(null).isSpeak()) {
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

                                main(bean);
                                mRecoResult = null;
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
                    if (!MyTTS.getInstance(null).isSpeak()) {
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

    private void skySceneProcess(Context ctx, String result) {
        mRecoResult = result;
        if (mRecoResult != null) {
            try {
                Intent intent;
                AsrResult bean = GsonUtils.parseResult(result, AsrResult.class);
                if (bean == null) {
                    MLog.d(TAG, "bean is null");
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
                    AppUtil.killTopApp();
                    MyTTS.getInstance(null).talk(ctx.getString(R.string.str_exit));
                    return;
                }
                if (StringUtils.isHelpCmdFromSpeech(bean.mQuery)) {
                    mAsrDialogControler.dialogTxtClear();
                    mAsrDialogControler.showHeadLoading();
                    bean.mSemanticJson.mSemantic.mIntent = "cando";
                    ActionUtils.jumpToHelp(mContext, mAsrDialogControler, bean);
                    return;
                }
                if (VoiceApp.getInstance().mAiType != GlobalVariable.AI_REMOTE) {
                    if (!isEnd || IStatus.isInScene() || TextUtils.equals(bean.mAnswer, "我在，有什么需要我帮忙吗？")) {
                        restartRecognize();
                    }
                }

                if (DefaultCmds.isPlay(mIntent)) {
                    intent = DefaultCmds.composePlayControlIntent(bean);
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
                    if (VoiceApp.getInstance().mAiType == GlobalVariable.AI_VOICE && myWakeup != null) {
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
                    if (VoiceApp.getInstance().mAiType == GlobalVariable.AI_VOICE) {
                        myWakeup.startWakeup();
                    }
                    myRecognizer.register();
                    myTTS.stopSpeak();
                    MLog.d(TAG, "WAKEUP_OPEN");
                    Utils.setWakeupProperty(1);//opening
                    break;
                case Intent.ACTION_SCREEN_ON:
                    //case GlobalVariable.RELEASE_AUDIO_RECORDER_ACTION:
                    if (VoiceApp.getInstance().mAiType == GlobalVariable.AI_VOICE && myWakeup != null) {
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
