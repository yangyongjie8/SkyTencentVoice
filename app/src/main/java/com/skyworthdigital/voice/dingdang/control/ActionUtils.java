package com.skyworthdigital.voice.dingdang.control;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.control.model.AIDataType;
import com.skyworthdigital.voice.dingdang.control.model.MusicSlots;
import com.skyworthdigital.voice.dingdang.control.model.Semantic;
import com.skyworthdigital.voice.dingdang.control.model.TemplateItem;
import com.skyworthdigital.voice.dingdang.domains.alarm.Alarm;
import com.skyworthdigital.voice.dingdang.domains.alarm.AlarmHelper;
import com.skyworthdigital.voice.dingdang.domains.alarm.database.AlarmDbOperator;
import com.skyworthdigital.voice.dingdang.domains.tianmai.TianmaiIntent;
import com.skyworthdigital.voice.dingdang.domains.videosearch.BeeSearchUtils;
import com.skyworthdigital.voice.dingdang.domains.videosearch.model.BeeSearchParams;
import com.skyworthdigital.voice.dingdang.domains.music.MusicControl;
import com.skyworthdigital.voice.dingdang.domains.poem.SkyPoemActivity;
import com.skyworthdigital.voice.dingdang.domains.tv.TvControl;
import com.skyworthdigital.voice.dingdang.control.model.AsrResult;
import com.skyworthdigital.voice.dingdang.control.model.BaikeVideoItem;
import com.skyworthdigital.voice.dingdang.control.recognization.IStatus;
import com.skyworthdigital.voice.dingdang.control.tts.MyTTS;
import com.skyworthdigital.voice.dingdang.globalcmd.GlobalUtil;
import com.skyworthdigital.voice.dingdang.utils.DefaultCmds;
import com.skyworthdigital.voice.dingdang.utils.DialogCellType;
import com.skyworthdigital.voice.dingdang.SkyAsrDialogControl;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.GuideTip;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.PrefsUtils;
import com.skyworthdigital.voice.dingdang.utils.SkyRing;
import com.skyworthdigital.voice.dingdang.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDT03046 on 2018/7/24.
 */

public class ActionUtils {
    private static final String TAG = "MainControler";


    public static void jumpToVideoSearch(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        String mIntent = bean.mSemanticJson.mSemantic.mIntent;
        switch (mIntent) {
            case "search_relation_person_kg":
                dialogControl.dialogRefresh(ctx, bean, null, 0);
                dialogControl.dialogDismiss(3000);
                MyTTS.getInstance(null).parseSemanticToTTS(bean);
                break;
            default:
                dialogControl.dialogDismiss(15000);
                BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
                break;
        }
    }

    public static void jumpToMusic(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        String mIntent = bean.mSemanticJson.mSemantic.mIntent;
        if (mIntent.contains("play")) {
            dialogControl.dialogDismiss(3000);
            if (bean.mSemanticJson != null && bean.mSemanticJson.mSemantic != null) {
                MusicControl.actionExecute(ctx, bean.mSemanticJson.mSemantic.getMusicSlots(), bean.mQuery);
            }
        } else {
            MyTTS.getInstance(null).speak("", ctx.getResources().getString(R.string.music_try_note));
        }
    }

    public static void jumpToBaikeVideo(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        try {
            if ((BeeSearchParams.getInstance().isInSearchPage()
                    && (TextUtils.equals(bean.mDomain, "chat") || bean.mQuery.contains("剧情")))
                    || (bean.mSemanticJson.mStatus != null && bean.mSemanticJson.mStatus.mCode < 0)) {
                dialogControl.dialogDismiss(15000);
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
                        dialogControl.dialogDismiss(0);
                        return;
                    }
                } else {
                    if (bean.mData != null && !TextUtils.isEmpty(bean.mData.mBaikeInfo)) {
                        dialogControl.dialogRefreshDetail(ctx, bean.mData, DialogCellType.CELL_BAIKE_INFO);
                        if (!TextUtils.isEmpty(bean.mTips)) {
                            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
                        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
                            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
                        }
                        dialogControl.dialogDismiss(35000);
                        MyTTS.getInstance(null).parseSemanticToTTS(bean);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isDingdangInvalidBack(bean.mAnswer)) {
            MLog.d(TAG, "dingdang invalid,go search");
            dialogControl.dialogDismiss(15000);
            BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
            return;
        }
        dialogControl.dialogRefresh(ctx, bean, null, 0);
        dialogControl.dialogDismiss(5000);
        MyTTS.getInstance(null).parseSemanticToTTS(bean);
    }

    public static void jumpToPoem(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
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
            dialogControl.dialogDismiss(15000);
            BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
            return;
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
        }
        dialogControl.dialogDismiss(2000);
        MyTTS.getInstance(null).parseSemanticToTTS(bean);
    }

    public static boolean jumpToFM(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
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
            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
        }
        dialogControl.dialogDismiss(2000);
        return false;
    }

    public static void jumpToSports(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        try {
            dialogControl.dialogDismiss(3000);
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
                        dialogControl.dialogDismiss(8000);
                        dialogControl.dialogRefreshDetail(ctx, bean.mData.mSportsScores, DialogCellType.CELL_SPORT_SCORE);
                    }
                    break;
                case "search_statistics":
                case "search_information":
                    if (bean.mData != null && bean.mData.mVStatistics != null && bean.mData.mVStatistics.size() > 2) {
                        dialogControl.dialogDismiss(20000);
                        dialogControl.dialogRefreshDetail(ctx, bean.mData.mVStatistics, DialogCellType.CELL_SPORT_INFO);
                    }
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(bean.mTips)) {
            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
        }
        MyTTS.getInstance(null).speak(bean.mAnswer);
    }

    public static void hideTrailer(SkyAsrDialogControl dialogControl) {
        dialogControl.hideTvDialog();
    }

    public static boolean jumpToTrailer(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        try {
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            switch (mIntent) {
                case "search_tvlist":
                case "search_channel":
                case "search_time":
                    if (bean.mData != null && bean.mData.mTvProgramsList != null && bean.mData.mTvProgramsList.size() > 0) {
                        dialogControl.showTvDialog(bean.mData.mTvProgramsList);
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
            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
            MyTTS.getInstance(null).speak(bean.mTips);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
            MyTTS.getInstance(null).speak(bean.mAnswer);
        }
        return false;
    }

    public static void jumpToFlight(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        try {
            dialogControl.dialogDismiss(3000);
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            //MLog.d(TAG, "intent:" + mIntent);
            switch (mIntent) {
                case "search_ticket":
                    if (bean.mData != null && bean.mData.mFlightList != null && bean.mData.mFlightList.size() > 0) {
                        dialogControl.dialogDismiss(3000);
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
            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
            MyTTS.getInstance(null).speak(bean.mTips);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
            MyTTS.getInstance(null).speak(bean.mAnswer);
        }
    }

    public static void jumpToTrain(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        try {
            if (!bean.mSession) {
                dialogControl.dialogDismiss(60000);
            } else {
                dialogControl.dialogDismiss(3000);
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
            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
            MyTTS.getInstance(null).speak(bean.mTips);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
            MyTTS.getInstance(null).speak(bean.mAnswer);
        }
    }

    public static void jumpToAlarm(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
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
                    MyTTS.getInstance(null).speakAndShow(tts);
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
                    MyTTS.getInstance(null).speakAndShow(tts);
                    break;
                case "modify":
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_alarm_modify));
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
                            MyTTS.getInstance(null).speakAndShow(tip);
                        } else {
                            MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_del_err));
                        }
                    } else if (bean.mQuery.contains("全部") || bean.mQuery.contains("所有")) {
                        ArrayList<AlarmDbOperator.AlarmItem> list = helper.getAlarmlists();
                        for (int i = 0; i < list.size(); i++) {
                            helper.deleteAlram(list.get(i));
                        }
                        MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_del_all));
                    } else {
                        tts = helper.getAlarmlistsString();
                        if (TextUtils.isEmpty(tts)) {
                            tts = VoiceApp.getInstance().getString(R.string.str_alarm_empty);
                            MyTTS.getInstance(null).speakAndShow(tts);
                        } else {
                            dialogControl.dialogDismiss(40000);
                            tts = tts + "\n" + ctx.getString(R.string.str_alarm_delete);
                            dialogControl.dialogRefresh(ctx, null, tts, 0);
                            MyTTS.getInstance(null).speak(ctx.getString(R.string.str_alarm_delete));
                        }
                    }
                    break;
                case "check":
                    helper = new AlarmHelper(VoiceApp.getInstance());
                    tts = helper.getAlarmlistsString();
                    if (TextUtils.isEmpty(tts)) {
                        tts = VoiceApp.getInstance().getString(R.string.str_alarm_empty);
                        MyTTS.getInstance(null).speakAndShow(tts);
                    } else {
                        dialogControl.dialogDismiss(40000);
                        MyTTS.getInstance(null).speakAndShow(tts);
                    }
                    break;
                default:
                    tts = bean.mAnswer;
                    MyTTS.getInstance(null).speakAndShow(tts);
                    break;
            }
        }
    }

    public static void jumpToNews(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        try {
            Intent intent = new Intent();
            intent.setAction("com.skyworthdigital.skyallmedia.ShortVideoList");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_news));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jumpToRecipe(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        //MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_recipe));

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
            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
        }
        dialogControl.dialogDismiss(2000);
        MyTTS.getInstance(null).parseSemanticToTTS(bean);
    }

    public static void jumpToHelp(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        try {
            dialogControl.dialogDismiss(5000);
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
                        if (dialogControl != null && dialogControl.mAsrDialog != null) {
                            dialogControl.mAsrDialog.showGuideDialog(null);
                            MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_cando_note));
                        }
                    } else {
                        MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_cando));
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jumpToChengyu(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        dialogControl.dialogDismiss(5000);
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
                            MyTTS.getInstance(null).speak(cell.mResult, sb.toString());
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
        dialogControl.dialogRefresh(ctx, bean, null, 0);
        dialogControl.dialogDismiss(3000);
        MyTTS.getInstance(null).parseSemanticToTTS(bean);
    }

    public static void jumpToTvControl(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        if (BeeSearchUtils.mSpeakSameInfo != null && BeeSearchParams.getInstance().isInSearchPage()) {
            //处于纠正逻辑
            dialogControl.dialogDismiss(15000);
            BeeSearchUtils.doBeeSearch(ctx, bean.mQuery, bean.mDomain);
            return;
        }
        dialogControl.dialogDismiss(3000);
        try {
            if (!TvControl.main(ctx, bean, dialogControl)) {
                dialogControl.dialogRefresh(ctx, bean, null, 0);
                MyTTS.getInstance(null).parseSemanticToTTS(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void jumpToSound(Context ctx, SkyAsrDialogControl dialogControl, final AsrResult bean) {
        MLog.d(TAG, "jumpToSound");
        try {
            if (bean.mTemplates != null && bean.mTemplates.size() > 0) {
                String url = bean.mTemplates.get(0).mDestURL;
                if (!TextUtils.isEmpty(url)) {
                    //dialogControl.dialogDismiss(10000);
                    MyTTS.getInstance(null).speak(ctx.getString(R.string.str_ok));
                    SkyRing.getInstance().play(url, "");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(bean.mTips)) {
            dialogControl.dialogRefresh(ctx, null, bean.mTips, 0);
            MyTTS.getInstance(null).speak(bean.mTips);
        } else if (!TextUtils.isEmpty(bean.mAnswer)) {
            dialogControl.dialogRefresh(ctx, null, bean.mAnswer, 0);
            MyTTS.getInstance(null).speak(bean.mAnswer);
        }
    }

    public static boolean sceneControl(Context ctx, SkyAsrDialogControl dialogControl, AsrResult bean) {
        if (TextUtils.equals(bean.mDomain, "globalctrl")
                || TextUtils.equals(bean.mDomain, "help")
                || (TextUtils.equals(bean.mDomain, "tv") && (!TextUtils.equals("select_channel", bean.mSemanticJson.mSemantic.mIntent)))) {
            if (TextUtils.equals(bean.mQuery, "关闭")) {
                dialogControl.dialogTxtClear();
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
                    //myTTS.speak("", "试试这么说“我想听***”");
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
                dialogControl.showHeadLoading();
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
        if(StringUtils.doTwoMinSwitch(speech))return true;

        TianmaiIntent tianmaiIntent;
        if((tianmaiIntent=StringUtils.isTianMaiDemoSpeech(speech))!=null){
            DefaultCmds.startTianmaiPlay(ctx, tianmaiIntent);
            MLog.i(TAG,"special tianmai action");
            return true;
        }
        return false;
    }
}
