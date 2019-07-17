package com.skyworthdigital.voice.tencent_module;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.alarm.AlarmHelper;
import com.skyworthdigital.voice.alarm.database.AlarmDbOperator;
import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.beesearch.BeeSearchUtils;
import com.skyworthdigital.voice.common.AbsController;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.DialogCellType;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.PrefsUtils;
import com.skyworthdigital.voice.dingdang.utils.SPUtil;
import com.skyworthdigital.voice.dingdang.utils.SkyRing;
import com.skyworthdigital.voice.globalcmd.GlobalUtil;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.music.MusicControl;
import com.skyworthdigital.voice.music.musictype.MusicSlots;
import com.skyworthdigital.voice.tencent_module.domains.poem.SkyPoemActivity;
import com.skyworthdigital.voice.tencent_module.domains.tv.TvControl;
import com.skyworthdigital.voice.tencent_module.model.AIDataType;
import com.skyworthdigital.voice.tencent_module.model.BaikeVideoItem;
import com.skyworthdigital.voice.tencent_module.model.Semantic;
import com.skyworthdigital.voice.tencent_module.model.TemplateItem;
import com.skyworthdigital.voice.tianmai.TianmaiIntent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDT03046 on 2018/7/24.
 */

public class ActionUtils {
    private static final String TAG = "ActionUtils";


    public static void jumpToVideoSearch(Context ctx, AsrResult bean) {
        String mIntent = bean.mSemanticJson.mSemantic.mIntent;
        switch (mIntent) {
            case "search_relation_person_kg":
                TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, bean, null, 0);
                TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
                TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
                break;
            default:
                TxController.getInstance().getAsrDialogControler().dialogDismiss(15000);
                BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
                break;
        }
    }

    public static void jumpToMusic(Context ctx, AsrResult bean) {
        String mIntent = bean.mSemanticJson.mSemantic.mIntent;
        if (mIntent.contains("play")) {
            TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
            if (bean.mSemanticJson != null && bean.mSemanticJson.mSemantic != null) {
                MusicControl.actionExecute(ctx, bean.mSemanticJson.mSemantic.getMusicSlots(), bean.mQuery);
            }
        } else {
            TxTTS.getInstance(null).talk("", ctx.getResources().getString(R.string.music_try_note));
        }
    }

    public static void jumpToBaikeVideo(Context ctx, AsrResult bean) {
        try {
            if ((BeeSearchParams.getInstance().isInSearchPage()
                    && (TextUtils.equals(bean.mDomain, "chat") || bean.mQuery.contains("剧情")))
                    || (bean.mSemanticJson.mStatus != null && bean.mSemanticJson.mStatus.mCode < 0)) {
                TxController.getInstance().getAsrDialogControler().dialogDismiss(15000);
                BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
                return;
            }
            if (bean.mData != null && TextUtils.equals(bean.mDomain, "baike") && TextUtils.equals(bean.mSemanticJson.mSemantic.mIntent, "search_baike")) {
                if (bean.mData.mBaikeVideo != null && bean.mData.mBaikeVideo.mVBaikeVideo != null && bean.mData.mBaikeVideo.mVBaikeVideo.size() > 0) {
                    Intent intent = new Intent("com.skyworthdigital.voice.dingdang.baikevideo");
                    intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                    if (!TextUtils.isEmpty(bean.mData.mKeyWord)) {
                        intent.putExtra("title", bean.mData.mKeyWord);
                    }
                    List<String> urls = new ArrayList<>();
                    for (BaikeVideoItem tmp : bean.mData.mBaikeVideo.mVBaikeVideo) {
                        if (!TextUtils.isEmpty(tmp.mUrl)) {
                            urls.add(tmp.mUrl);
                        }
                    }
                    if (urls.size() > 0) {
                        intent.putExtra("list", (Serializable) urls);
                        intent.putExtra("note", bean.mData.getIntroduction());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);
                        TxController.getInstance().getAsrDialogControler().dialogDismiss(0);
                        return;
                    }
                } else {
                    if (bean.mData != null && !TextUtils.isEmpty(bean.mData.mBaikeInfo)) {
                        TxController.getInstance().getAsrDialogControler().dialogRefreshDetail(ctx, bean.mData, DialogCellType.CELL_BAIKE_INFO);
                        if (!TextUtils.isEmpty(bean.mTips)) {
                            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
                        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
                            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
                        }
                        TxController.getInstance().getAsrDialogControler().dialogDismiss(35000);
                        TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isDingdangInvalidBack(bean.mAnswer)) {
            MLog.d(TAG, "dingdang invalid,go search");
            TxController.getInstance().getAsrDialogControler().dialogDismiss(15000);
            BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
            return;
        }
        TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, bean, null, 0);
        TxController.getInstance().getAsrDialogControler().dialogDismiss(5000);
        TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
    }

    public static void jumpToPoem(Context ctx, AsrResult bean) {
        try {
            if (bean.mData.mData.size() > 0) {
                Intent intent = new Intent(ctx, SkyPoemActivity.class);
                intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                if (!TextUtils.isEmpty(bean.mData.mData.get(0).mTitle)) {
                    intent.putExtra(GlobalVariable.FM_NAME, bean.mData.mData.get(0).mTitle);
                }

                intent.putExtra("list", (Serializable) bean.mData.mData);
                if (!TextUtils.isEmpty(bean.mData.mData.get(0).mDestURL)) {
                    intent.putExtra(GlobalVariable.FM_URL, bean.mData.mData.get(0).mDestURL);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MLog.d(TAG, "jumpToPoem");
                ctx.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isDingdangInvalidBack(bean.mAnswer)) {
            MLog.d(TAG, "dingdang invalid,go search");
            TxController.getInstance().getAsrDialogControler().dialogDismiss(15000);
            BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
            return;
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
        }
        TxController.getInstance().getAsrDialogControler().dialogDismiss(2000);
        TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
    }

    public static boolean jumpToFM(Context ctx, AsrResult bean) {
        try {
            if (bean.mTemplates != null && bean.mTemplates.size() > 0 && !TextUtils.isEmpty(bean.mTemplates.get(0).mDestURL)
                    && (TextUtils.equals(bean.mSemanticJson.mSemantic.mIntent, "play_radio")
                    || TextUtils.equals(bean.mSemanticJson.mSemantic.mIntent, "play"))) {
                Intent intent = new Intent("com.skyworthdigital.voice.dingdang.fmplay");
                intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                if (!TextUtils.isEmpty(bean.mTemplates.get(0).mTitle)) {
                    intent.putExtra(GlobalVariable.FM_NAME, bean.mTemplates.get(0).mTitle);
                }

                intent.putExtra("list", (Serializable) bean.mTemplates);
                intent.putExtra(GlobalVariable.FM_URL, bean.mTemplates.get(0).mDestURL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                VoiceApp.getInstance().startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
        }
        TxController.getInstance().getAsrDialogControler().dialogDismiss(2000);
        return false;
    }

    public static void jumpToSports(Context ctx, AsrResult bean) {
        try {
            TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            switch (mIntent) {
                case "search_record":
                    if (bean.mData != null && bean.mData.mSportsRecords != null && bean.mData.mSportsRecords.size() > 0
                            && bean.mData.mSportsRecords.get(0).mTeamStatVec.size() > 0) {
                        // MLog.d(TAG, "launch record");
                        //mAsrDialogControler.dialogRefreshDetail(mContext, bean.mData.mSportsRecords, DialogCellType.CELL_SPORT_RECORE);
                        //mAsrDialogControler.dialogDismiss(60000);
                        Intent intent = new Intent("com.skyworthdigital.voice.dingdang.sportsrecord");//ctx, SportsRecordActivity.class);
                        intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);

                        List<AsrResult.AsrData.SportsRecordObj> records = bean.mData.mSportsRecords;
                        intent.putExtra("list", (Serializable) bean.mData.mSportsRecords);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ctx.startActivity(intent);
                    }
                    break;
                case "search_schedule":
                case "search_time":
                case "search_channel":
                case "search_status":
                    if (bean.mData != null && bean.mData.mSportsdataObjs != null && bean.mData.mSportsdataObjs.size() > 0) {
                        Intent intent = new Intent("com.skyworthdigital.voice.dingdang.sports");
                        intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                        intent.putExtra("list", (Serializable) bean.mData.mSportsdataObjs);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ctx.startActivity(intent);
                    }
                    break;
                case "search_score":
                    if (bean.mData != null && bean.mData.mSportsScores != null && bean.mData.mSportsScores.size() > 0) {
                        TxController.getInstance().getAsrDialogControler().dialogDismiss(8000);
                        TxController.getInstance().getAsrDialogControler().dialogRefreshDetail(ctx, bean.mData.mSportsScores, DialogCellType.CELL_SPORT_SCORE);
                    }
                    break;
                case "search_statistics":
                case "search_information":
                    if (bean.mData != null && bean.mData.mVStatistics != null && bean.mData.mVStatistics.size() > 2) {
                        TxController.getInstance().getAsrDialogControler().dialogDismiss(20000);
                        TxController.getInstance().getAsrDialogControler().dialogRefreshDetail(ctx, bean.mData.mVStatistics, DialogCellType.CELL_SPORT_INFO);
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(bean.mTips)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
        }
        TxTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
    }

    public static void hideTrailer() {
        TxController.getInstance().getAsrDialogControler().hideTvDialog();
    }

    public static boolean jumpToTrailer(Context ctx, AsrResult bean) {
        try {
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            switch (mIntent) {
                case "search_tvlist":
                case "search_channel":
                case "search_time":
                    if (bean.mData != null && bean.mData.mTvProgramsList != null && bean.mData.mTvProgramsList.size() > 0) {
                        TxController.getInstance().getAsrDialogControler().showTvDialog(bean.mData.mTvProgramsList);
                        TxController.getInstance().getAsrDialogControler().setTvDialog(true);
                        return true;
                        //return tvdialog;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
            TxTTS.getInstance(null).talkWithoutDisplay(bean.mTips);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
            TxTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
        }
        TxController.getInstance().getAsrDialogControler().setTvDialog(false);
        return false;
    }

    public static void jumpToFlight(Context ctx, AsrResult bean) {
        try {
            TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            //MLog.d(TAG, "intent:" + mIntent);
            switch (mIntent) {
                case "search_ticket":
                    if (bean.mData != null && bean.mData.mFlightList != null && bean.mData.mFlightList.size() > 0) {
                        TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
                        Intent intent = new Intent("com.skyworthdigital.voice.dingdang.flightticket");
                        intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                        intent.putExtra("data", (Serializable) bean.mData);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ctx.startActivity(intent);
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
            TxTTS.getInstance(null).talkWithoutDisplay(bean.mTips);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
            TxTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
        }
    }

    public static void jumpToTrain(Context ctx, AsrResult bean) {
        try {
            if (!bean.mSession) {
                TxController.getInstance().getAsrDialogControler().dialogDismiss(60000);
            } else {
                TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
            }
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            //MLog.d(TAG, "intent:" + mIntent);
            switch (mIntent) {
                case "search_ticket":
                    if (bean.mData != null && bean.mData.mTrainInfos != null && bean.mData.mTrainInfos.size() > 0) {
                        Intent intent = new Intent("com.skyworthdigital.voice.dingdang.trainticket");
                        intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                        intent.putExtra("data", (Serializable) bean.mData);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ctx.startActivity(intent);
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
            TxTTS.getInstance(null).talkWithoutDisplay(bean.mTips);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
            TxTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
        }
    }

    public static void jumpToAlarm(Context ctx, AsrResult bean) {
        Alarm alarm = new Alarm();
        String tts = "";
        if (bean != null && bean.mSemanticJson != null && bean.mSemanticJson.mSemantic != null) {
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            switch (mIntent) {
                case "new":
                    int ret = alarm.addClock(bean.mSemanticJson.mSemantic.mSlots);
                    if (ret == Alarm.ERROR_EXIST) {
                        tts = ctx.getString(R.string.str_alarm_error);
                    } else {
                        tts = bean.mAnswer;
                    }
                    TxTTS.getInstance(null).talk(tts);
                    break;
                case "set_ringing":
                    MusicSlots musicSlots = bean.mSemanticJson.mSemantic.getMusicSlots();
                    StringBuilder searchword = new StringBuilder();
                    if (!TextUtils.isEmpty(musicSlots.mSong) || !TextUtils.isEmpty(musicSlots.mSinger)) {
                        if (!TextUtils.isEmpty(musicSlots.mSinger) && !TextUtils.isEmpty(musicSlots.mSong)) {
                            searchword.append(musicSlots.mSinger);
                            searchword.append(" ");
                            searchword.append(musicSlots.mSong);
                        } else if (!TextUtils.isEmpty(musicSlots.mSinger)) {
                            searchword.append(musicSlots.mSinger);
                        } else if (!TextUtils.isEmpty(musicSlots.mSong)) {
                            searchword.append(musicSlots.mSong);
                        }
                        PrefsUtils.setAlarmRing(ctx, searchword.toString());
                        tts = bean.mAnswer;
                    } else {
                        tts = ctx.getString(R.string.str_alarmring);
                    }
                    TxTTS.getInstance(null).talk(tts);
                    break;
                case "modify":
                    TxTTS.getInstance(null).talk(ctx.getString(R.string.str_alarm_modify));
                    break;
                case "delete":
                    AlarmHelper helper = new AlarmHelper(VoiceApp.getInstance());
                    int idx = (int) bean.mSemanticJson.mSemantic.getIndex();
                    if (idx != Semantic.INVALID_DIGIT && idx > 0) {
                        ArrayList<AlarmDbOperator.AlarmItem> list = helper.getAlarmlists();
                        if (list.size() >= idx) {
                            helper.deleteAlram(list.get(idx - 1));
                            String tip = ctx.getString(R.string.str_del_success);
                            tip = String.format(tip, "第" + idx + "个");
                            TxTTS.getInstance(null).talk(tip);
                        } else {
                            TxTTS.getInstance(null).talk(ctx.getString(R.string.str_del_err));
                        }
                    } else if (bean.mQuery.contains("全部") || bean.mQuery.contains("所有")) {
                        ArrayList<AlarmDbOperator.AlarmItem> list = helper.getAlarmlists();
                        for (int i = 0; i < list.size(); i++) {
                            helper.deleteAlram(list.get(i));
                        }
                        TxTTS.getInstance(null).talk(ctx.getString(R.string.str_del_all));
                    } else {
                        tts = helper.getAlarmlistsString();
                        if (TextUtils.isEmpty(tts)) {
                            tts = VoiceApp.getInstance().getString(R.string.str_alarm_empty);
                            TxTTS.getInstance(null).talk(tts);
                        } else {
                            TxController.getInstance().getAsrDialogControler().dialogDismiss(40000);
                            tts = tts + "\n" + ctx.getString(R.string.str_alarm_delete);
                            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, tts, 0);
                            TxTTS.getInstance(null).talkWithoutDisplay(ctx.getString(R.string.str_alarm_delete));
                        }
                    }
                    break;
                case "check":
                    helper = new AlarmHelper(VoiceApp.getInstance());
                    tts = helper.getAlarmlistsString();
                    if (TextUtils.isEmpty(tts)) {
                        tts = VoiceApp.getInstance().getString(R.string.str_alarm_empty);
                        TxTTS.getInstance(null).talk(tts);
                    } else {
                        TxController.getInstance().getAsrDialogControler().dialogDismiss(40000);
                        TxTTS.getInstance(null).talk(tts);
                    }
                    break;
                default:
                    tts = bean.mAnswer;
                    TxTTS.getInstance(null).talk(tts);
                    break;
            }
        }
    }

    public static void jumpToNews(Context ctx,AsrResult bean) {
        try {
            Intent intent = new Intent();
            intent.setAction("cn.mipt.videohj.intent.action.LOAD_NEWS");//"com.skyworthdigital.skyallmedia.ShortVideoList"
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            TxTTS.getInstance(null).talk(ctx.getString(R.string.str_news));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jumpToRecipe(Context ctx,AsrResult bean) {
        //MyTTS.getInstance(null).talk(ctx.getString(R.string.str_recipe));

        try {
            if (bean.mTemplates != null && bean.mTemplates.size() > 0) {
                TemplateItem item = bean.mTemplates.get(0);
                if (item.mDataType == AIDataType.E_AIDATATYPE_NEWS) {
                    Intent intent = new Intent("com.skyworthdigital.voice.dingdang.recipe");
                    intent.setPackage("com.skyworthdigital.voice.dingdang");
                    intent.putExtra("list", (Serializable) bean.mTemplates);
                    intent.putExtra("url", bean.mTemplates.get(0).mDestURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
        }
        TxController.getInstance().getAsrDialogControler().dialogDismiss(2000);
        TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
    }

    public static void jumpToHelp(Context ctx, AsrResult bean) {
        try {
            TxController.getInstance().getAsrDialogControler().dialogDismiss(5000);
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            switch (mIntent) {
                case "cando":
                    IStatus.resetDismissTime();
                    if (GuideTip.getInstance().isAudioPlay()
                            || GuideTip.getInstance().isVideoPlay()
                            || GuideTip.getInstance().isMediaDetail()
                            || GuideTip.getInstance().isMusicPlay()
                            || GuideTip.getInstance().isTvlive()
                            || GuideTip.getInstance().isPoem()) {
                        if (TxController.getInstance().getAsrDialogControler() != null && TxController.getInstance().getAsrDialogControler().mAsrDialog != null) {
                            TxController.getInstance().getAsrDialogControler().mAsrDialog.showGuideDialog(null);
                            TxTTS.getInstance(null).talk(ctx.getString(R.string.str_cando_note));
                            return;
                        }
                    } else {
                        TxTTS.getInstance(null).talk(ctx.getString(R.string.str_cando));
                        return;
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TxController.getInstance().getAsrDialogControler().dialogDismiss(AbsController.DISMISS_DELAY_NORMAL);
    }

    public static void jumpToChengyu(Context ctx, AsrResult bean) {
        TxController.getInstance().getAsrDialogControler().dialogDismiss(5000);
        try {
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            switch (mIntent) {
                case "search_chengyu":
                case "search_chengyu_story":
                    if (bean.mData != null && bean.mData.mIdiomCell != null && bean.mData.mIdiomCell.size() > 0) {
                        AsrResult.AsrData.IdiomCell cell = bean.mData.mIdiomCell.get(0);
                        StringBuilder sb = new StringBuilder();
                        if (!TextUtils.isEmpty(cell.mLemma)) {
                            sb.append(cell.mLemma);
                            sb.append("\n");
                        }
                        if (!TextUtils.isEmpty(cell.mPinYin)) {
                            sb.append(cell.mPinYin);
                            sb.append("\n");
                        }
                        if (!TextUtils.isEmpty(cell.mResult)) {
                            sb.append(cell.mResult);
                            TxTTS.getInstance(null).talk(cell.mResult, sb.toString());
                            return;
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, bean, null, 0);
        TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
        TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
    }

    public static void jumpToTvControl(Context ctx, AsrResult bean) {
        if (BeeSearchUtils.mSpeakSameInfo != null && BeeSearchParams.getInstance().isInSearchPage()) {
            //处于纠正逻辑
            TxController.getInstance().getAsrDialogControler().dialogDismiss(15000);
            BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
            return;
        }
        TxController.getInstance().getAsrDialogControler().dialogDismiss(3000);
        try {
            if (!TvControl.main(ctx, bean)) {
                TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, bean, null, 0);
                TxTTS.getInstance(null).parseSemanticToTTS(TextUtils.isEmpty(bean.mAnswer)?bean.mTips:bean.mAnswer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jumpToSound(Context ctx, final AsrResult bean) {
        MLog.d(TAG, "jumpToSound");
        try {
            if (bean.mTemplates != null && bean.mTemplates.size() > 0) {
                String url = bean.mTemplates.get(0).mDestURL;
                if (!TextUtils.isEmpty(url)) {
                    //dialogControl.dialogDismiss(10000);
                    TxTTS.getInstance(null).talkWithoutDisplay(ctx.getString(R.string.str_ok));
                    SkyRing.getInstance().play(url, "");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mTips, 0);
            TxTTS.getInstance(null).talkWithoutDisplay(bean.mTips);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            TxController.getInstance().getAsrDialogControler().dialogRefresh(ctx, null, bean.mAnswer, 0);
            TxTTS.getInstance(null).talkWithoutDisplay(bean.mAnswer);
        }
    }

    public static boolean sceneControl(Context ctx, AsrResult bean) {
        if (TextUtils.equals(bean.mDomain, "globalctrl")
                || TextUtils.equals(bean.mDomain, "help")
                || (TextUtils.equals(bean.mDomain, "tv") && (!TextUtils.equals("select_channel", bean.mSemanticJson.mSemantic.mIntent)))) {
            if (TextUtils.equals(bean.mQuery, "关闭")) {
                TxController.getInstance().getAsrDialogControler().dialogTxtClear();
                return true;
            }
            IStatus.resetDismissTime();
            return false;//需要处理
        } else if (BeeSearchParams.getInstance().isInSearchPage()) {
            if (GlobalUtil.getInstance().control(ctx, bean.mQuery, null)) {
                return true;
            }
            if (TextUtils.equals(bean.mDomain, "music") || TextUtils.equals(bean.mDomain, "fm") || TextUtils.equals(bean.mDomain, "news")) {
                if (bean.mQuery.contains("听") || bean.mQuery.endsWith("的歌") || bean.mQuery.contains("歌曲") || bean.mQuery.contains("新闻") || bean.mQuery.contains("音乐") || bean.mQuery.contains("唱的") || bean.mQuery.contains("电台") || bean.mQuery.contains("广播") || bean.mQuery.contains("专辑")) {
                    //myTTS.talkWithoutDisplay("", "试试这么说“我想听***”");
                    return false;
                }
                BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
                return true;
            }
            if (TextUtils.equals(bean.mDomain, "chat") || TextUtils.equals(bean.mDomain, "sports") || (TextUtils.equals(bean.mDomain, "baike") && TextUtils.equals(bean.mSemanticJson.mSemantic.mIntent, "search_baike"))) {
                BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
                return true;
            }

            IStatus.resetDismissTime();
            return false;
        } else if (GuideTip.getInstance().mIsQQmusic) {
            if (bean.mDomain.equals("music") && bean.mSemanticJson != null && bean.mSemanticJson.mSemantic != null
                    && bean.mQuery.contains("听") || bean.mQuery.contains("歌") || bean.mQuery.contains("专辑") || bean.mQuery.contains("音乐") || bean.mQuery.contains("曲") || bean.mQuery.contains("唱")) {
                TxController.getInstance().getAsrDialogControler().showHeadLoading();
                MusicControl.actionExecute(ctx, bean.mSemanticJson.mSemantic.getMusicSlots(), bean.mQuery);
            }
        } else if (GuideTip.getInstance().isAudioPlay()) {
            if (bean.mDomain.equals("fm")) {
                //ActionUtils.jumpToFM(ctx, dialogControl, bean);
            }
        }
        return true;
    }


    /**
     * 根据用户语音原话，特殊处理转化为播放控制命令
     */
    public static boolean specialCmdProcess(Context ctx, String speech) {
        MLog.i(TAG,"specialCmdProcess");
        if(StringUtils.doTwoMinSwitch(speech))return true;

        TianmaiIntent tianmaiIntent;
        if((tianmaiIntent=StringUtils.isTianMaiDemoSpeech(speech))!=null){
            DefaultCmds.startTianmaiPlay(ctx, tianmaiIntent);
            MLog.i(TAG,"special tianmai action");
            return true;
        }

        if(StringUtils.isIoTCmdFromSpeech(speech)){
            MLog.i(TAG, "special IoT cmd");
            return true;
        }

//        if("切换到叮当".equalsIgnoreCase(speech)||"切换到订单".equalsIgnoreCase(speech)){
//            VoiceApp.isDuer = false;
//        SPUtil.putString(SPUtil.KEY_VOICE_PLATFORM, SPUtil.VALUE_VOICE_PLATFORM_DINGDANG);
//            AbsTTS.getInstance(null).talk("我就是叮当");
//            return true;
//        }
        if("切换到百度".equalsIgnoreCase(speech)){
            VoiceApp.isDuer = true;
            SPUtil.putString(SPUtil.KEY_VOICE_PLATFORM, SPUtil.VALUE_VOICE_PLATFORM_BAIDU);
            TxController.getInstance().getAsrDialogControler().dialogDismiss(0);
            TxController.getInstance().onDestroy();
            AbsTTS.getInstance(null).talk("我是百度");
            return true;
        }

        if("关闭屏幕".equals(speech)){
            TxController.getInstance().getAsrDialogControler().dialogDismiss(0);
            Utils.openScreen(false);
            Utils.openHdmi(false);
            AbsTTS.getInstance(null).talk("已关闭");
            return true;
        }
        if("打开屏幕".equals(speech)||"恢复屏幕".equals(speech)||"显示屏幕".equals(speech)){
//            Utils.openHdmi(true);
            TxController.getInstance().getAsrDialogControler().dialogDismiss(0);
            Utils.openScreen(true);
            AbsTTS.getInstance(null).talk("已打开");
            return true;
        }

        return false;
    }
}
