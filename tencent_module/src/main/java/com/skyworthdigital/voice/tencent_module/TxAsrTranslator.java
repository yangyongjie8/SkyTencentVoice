package com.skyworthdigital.voice.tencent_module;

import android.content.Context;
import android.text.TextUtils;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.common.AbsAsrTranslator;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.IntentUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.globalcmd.GlobalUtil;
import com.skyworthdigital.voice.tv.AbsTvLiveControl;

/**
 * Created by Ives 2019/6/12
 */
public class TxAsrTranslator extends AbsAsrTranslator<AsrResult> {

    private String TAG = TxAsrTranslator.class.getSimpleName();

    public static AbsAsrTranslator getInstance(){
        if(instance[1]==null){
            synchronized (TxAsrTranslator.class) {
                if(instance[1]==null) {
                    instance[1] = new TxAsrTranslator();
                }
            }
        }
        return instance[1];
    }

    @Override
    public void translate(final AsrResult bean) {
        Context ctx = com.skyworthdigital.voice.VoiceApp.getInstance();
        try {
            TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
            if (bean == null || TextUtils.isEmpty(bean.mDomain)) {
                return;
            } else if (bean.mReturnCode != 0) {
                MLog.d(TAG, "bean.mReturnCode=" + bean.mReturnCode);
                return;
            }
            MLog.d(TAG, "domain:" + bean.mDomain + " intent:" + bean.mSemanticJson.mSemantic.mIntent + " scenetype:" + IStatus.mSceneType);
            //如果已经进入直播，则先进入直播流程
            if (AbsTvLiveControl.getInstance().isTvLive()) {
                TxController.getInstance().getAsrDialogControler().dialogTxtClear();
                if (TxController.getInstance().getAsrDialogControler().isTvDialog()) {
                    MLog.d(TAG, "tv dialog showing");
                    ActionUtils.hideTrailer();
                }
                if (TextUtils.equals(bean.mDomain, "trailer")) {//节目单
                    MLog.d(TAG, "to show tv trailer");
                    TxController.getInstance().getAsrDialogControler().showHeadLoading();
                    ActionUtils.jumpToTrailer(ctx, bean);
                    return;
                }
                if (AbsTvLiveControl.getInstance().control(
                        bean.mSemanticJson.mSemantic.getTvliveSlots()==null?null:bean.mSemanticJson.mSemantic.getTvliveSlots().mText,
                        null,
                        bean.mQuery)) {
                    MLog.d(TAG, "can not got the tv channel");
                    return;
                }
            }

            if (bean.mSemanticJson != null && bean.mSemanticJson.mSemantic != null && (TextUtils.equals(bean.mSemanticJson.mSemantic.mIntent, "back")
                    || TextUtils.equals(bean.mSemanticJson.mSemantic.mIntent, "back_tvhomepage"))) {
                MLog.d(TAG, "jumpToTvControl");
                ActionUtils.jumpToTvControl(ctx, bean);
                return;
            }

            if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
                if (IStatus.mSceneType == IStatus.SCENE_GIVEN || IStatus.mSceneType == IStatus.SCENE_SEARCHPAGE) {
                    if (ActionUtils.sceneControl(ctx, bean)) {
                        MLog.d(TAG, "sceneControl");
                        return;
                    }
                }
            }
            SkyAsrDialogControl _asrDialogController = TxController.getInstance().getAsrDialogControler();
            if (GlobalUtil.getInstance().control(ctx, bean.mQuery, null)) {
                MLog.d(TAG, "globalCommandExecute");//全局语音处理
            } else {
                if (BeeSearchParams.getInstance().isInSearchPage() && (bean.mQuery.startsWith("修改") || bean.mQuery.startsWith("修正") || bean.mQuery.startsWith("纠正"))) {
                    bean.mDomain = "video";
                }
                if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
                    if (IStatus.mSceneType == IStatus.SCENE_SHOULD_GIVEN || IStatus.mSceneType == IStatus.SCENE_SHOULD_STOP) {
                        MLog.d(TAG, "scene not sure");
                        return;
                    }
                }
                switch (bean.mDomain) {
                    case "direct_search":
                    case "cinema":
                    case "video":
                        ActionUtils.jumpToVideoSearch(VoiceApp.getInstance(), bean);
                        //ActionUtils.startSearch(mContext, bean/*, mvideoPlayListener*/);
                        break;
                    case "music":
                        ActionUtils.jumpToMusic(VoiceApp.getInstance(), bean);
                        break;
                    case "joke":
                        TxController.getInstance().getAsrDialogControler().dialogDismiss(5000);
                        try {
                            if (bean.mData != null && !TextUtils.isEmpty(bean.mData.mJokeText)) {
                                TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mData.mJokeText, 0);
                                //mAsrDialogControler.dialogRefreshDetail(mContext, bean.mData, DialogCellType.CELL_BAIKE_INFO);
                                TxTTS.getInstance(null).talkWithoutDisplay(bean.mData.mJokeText);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "chat":
                    case "baike":
                        ActionUtils.jumpToBaikeVideo(ctx, bean);
                        break;
                    case "ancient_poem":
                        ActionUtils.jumpToPoem(ctx, bean);
                        break;
                    case "fm":
                        ActionUtils.jumpToFM(ctx, bean);
                        break;
                    case "globalctrl":
                    case "tv":
                        ActionUtils.jumpToTvControl(ctx, bean);
                        break;
                    case "help":
                        ActionUtils.jumpToHelp(ctx, bean);
                        break;
                    case "recipe":
                        ActionUtils.jumpToRecipe(ctx, bean);
                        break;
                    case "news":
                        ActionUtils.jumpToNews(ctx, bean);
                        break;
                    case "train":
                        ActionUtils.jumpToTrain(ctx, bean);
                        break;
                    case "flight":
                        ActionUtils.jumpToFlight(ctx, bean);
                        break;
                    case "sports":
                        ActionUtils.jumpToSports(ctx, bean);
                        //myTTS.parseSemanticToTTS(result);
                        break;
                    case "trailer":
                        if (TxController.getInstance().getAsrDialogControler().isTvDialog()) {
                            ActionUtils.hideTrailer();
                        }
                        ActionUtils.jumpToTrailer(ctx, bean);
                        return;
                    case "weather":
                        if (bean.mData != null && bean.mData.mCityWeatherInfo != null && bean.mData.mCityWeatherInfo.size() > 0
                                && bean.mData.mCityWeatherInfo.get(0).mBgImg != null) {
                            if (bean.mData.mCityWeatherInfo.get(0).mBgImg.size() > 0) {
                                String url = bean.mData.mCityWeatherInfo.get(0).mBgImg.get(0).mImg;
                                _asrDialogController.dialogRefreshBg(url);
                            }
                        }
                        if (!TextUtils.isEmpty(bean.mTips)) {
                            _asrDialogController.dialogRefresh(ctx, null, bean.mTips, 0);
                            TxTTS.getInstance(null).talkWithoutDisplay(bean.mTips);
                        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
                            _asrDialogController.dialogRefresh(ctx, null, bean.mAnswer, 0);
                            TxTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
                        }
                        break;
                    case "reminder":
                    case "alarm":
                        ActionUtils.jumpToAlarm(ctx, bean);
                        break;
                    case "common_qa":
                    case "world_records":
                    case "science":
                    case "htwhys":
                        if (!TextUtils.isEmpty(bean.mTips)) {
                            _asrDialogController.dialogRefresh(ctx, null, bean.mTips, 0);
                            TxTTS.getInstance(null).talkWithoutDisplay(bean.mTips);
                        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
                            _asrDialogController.dialogRefresh(ctx, null, bean.mAnswer, 0);
                            TxTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
                        }
                        break;
                    case "chengyu":
                        ActionUtils.jumpToChengyu(ctx, bean);
                        break;
                    case "astro":
                        _asrDialogController.dialogRefresh(ctx, bean, null, 0);
                        _asrDialogController.dialogDismiss(30000);
                        TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
                        break;
                    case "sound":
                        ActionUtils.jumpToSound(ctx, bean);
                        break;
                    default:
                        if (IntentUtils.appLaunchExecute(ctx, bean.mQuery)) {
                            MLog.d(TAG, "app_launcher");
                            break;
                        }
                        _asrDialogController.dialogRefresh(ctx, bean, null, 0);
                        _asrDialogController.dialogDismiss(3000);
                        TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
                        break;
                }
            }
            if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
                if (IStatus.mSceneType != IStatus.SCENE_GIVEN && IStatus.mSceneType != IStatus.SCENE_SEARCHPAGE && !bean.mSession) {
                    MLog.d(TAG, "RESTART_ASR 222");
                    _asrDialogController.dialogDismiss(TxController.DEFAULT_DISMISS_TIME);
                }
            }
            if (_asrDialogController.isTvDialog()) {
                ActionUtils.hideTrailer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
