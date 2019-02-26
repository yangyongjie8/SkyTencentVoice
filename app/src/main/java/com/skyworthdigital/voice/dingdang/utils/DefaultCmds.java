package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.control.model.Semantic;
import com.skyworthdigital.voice.dingdang.control.recognization.IStatus;
import com.skyworthdigital.voice.dingdang.control.tts.MyTTS;
import com.skyworthdigital.voice.dingdang.domains.videosearch.BeeSearchUtils;
import com.skyworthdigital.voice.dingdang.domains.videosearch.model.BeeSearchParams;
import com.skyworthdigital.voice.dingdang.control.model.AsrResult;

import java.util.ArrayList;

import static com.skyworthdigital.voice.dingdang.scene.SkySceneService.INTENT_TOPACTIVITY_CALL;

/**
 * 预定义命令
 * 有：$PLAY,$MUSICPLAY
 */

public class DefaultCmds {
    public static final String PLAY_CMD = "$PLAY";
    public static final String MUSIC_CMD = "$MUSICPLAY";
    public static final String COMMAND_VOL_UP = "volume.up";
    public static final String COMMAND_VOL_DOWN = "volume.down";
    public static final String COMMAND_VOL_SET = "volume.set";
    public static final String COMMAND_MUTE = "volume.mute";
    public static final String COMMAND_SLEEP = "command.sleep";
    public static final String COMMAND_TV_OFF = "command.tvoff";
    public static final String COMMAND_BACK = "command.back";//返回指令
    public static final String COMMAND_GO = "command.go";
    public static final String COMMAND_EXIT = "command.exit";
    public static final String COMMAND_APP_OPEN = "application.open";
    public static final String COMMAND_APP_CLOSE = "application.close";
    public static final String COMMAND_SIGNAL = "command.signal";
    public static final String COMMAND_PAGE_NEXT = "page.next";
    public static final String COMMAND_PAGE_PRE = "page.previous";
    public static final String COMMAND_LOCATION = "command.location";//定位指令
    private static final String COMMAND_RESOLUTION = "command.resolution";
    public static final String PLAYER_CMD_PREVIOUS = "player.previous";//上一集
    public static final String PLAYER_CMD_NEXT = "player.next";//下一集
    public static final String PLAYER_CMD_EPISODE = "player.episode";//第几集
    public static final String PLAYER_CMD_PAGE = "page_select";
    public static final String PLAYER_CMD_PAUSE = "player.pause";
    private static final String PLAYER_CMD_CONTINUE = "player.continue";//暂停或播放
    public static final String PLAYER_CMD_FASTFORWARD = "player.fastforward";//快进
    public static final String PLAYER_CMD_BACKFORWARD = "player.backforward";//快退
    private static final String AUDIO_UNICAST_CMD_SPEED = "audio.unicast.speed";
    public static final String PLAYER_CMD_GOTO = "player.goto";
    private static final String PLAYER_CMD_SPEED = "player.speed";//多倍速播放
    private static final String PLAYER_CMD_SKIPTITLE = "player.skiptitle";
    private static final String PLAYER_CMD_AUDIO_PREVIOUS = "audio.unicast.previous";
    private static final String PLAYER_CMD_AUDIO_NEXT = "audio.unicast.next";
    public static final String PLAYER_CMD_AUDIO_GOTO = "audio.unicast.goto";
    public static final String CMD_OPEN_DETAILS = "open.details";
    private static final String ROW = "row";//行
    private static final String COL = "col";//列
    private static final String RE_ROW = "re_row";//倒数行
    private static final String RE_COL = "re_col";//倒数列
    public static final String VALUE = "_value";
    //public static final String RE_EPISODE = "re_episode";
    public static final String INTENT = "_intent";
    public static final String SEQUERY = "_sequery";
    public static final String COMMAND = "_command";
    public static final String PREDEFINE = "predefine";
    public static final String FUZZYMATCH = "$fuzzy";
    private static final String TAG = "DefaultCmds";
    //public static final String SCENE_QUERY_ACTION = "com.skyworthdigital.voiceassistant.topActivity.QUERY";

    public static boolean isPlay(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        ArrayList<String> playactions = new ArrayList<>();

        playactions.add("speed_play");
        playactions.add("episode_select");
        playactions.add("page_select");
        playactions.add("play_forward");
        playactions.add("fast_forward");
        playactions.add("fast_backward");
        playactions.add("fast_reverse");
        playactions.add("play_skipback");
        playactions.add("play_rewind");
        playactions.add("list");
        playactions.add("skip_open_theme");
        playactions.add("skip_title");
        playactions.add("channellist");
        playactions.add("page_down");
        playactions.add("page_up");
        playactions.add("pause");
        playactions.add("resume");
        playactions.add("play_start");
        playactions.add("play_pause");
        playactions.add("replay");
        playactions.add("next");
        playactions.add("change");
        playactions.add("prev");
        playactions.add("index_v2");
        playactions.add("progress_to");
        playactions.add("play_by_episode");
        playactions.add("play_located");
        //playactions.add("search_tvseries");
        playactions.add("Open_details");
        playactions.add("play_skipforward");
        MLog.d(TAG, "isPlay:" + name + " " + playactions.contains(name));
        return (playactions.contains(name));
    }

    public static Intent composePlayControlIntent(AsrResult bean) {
        Intent intent;
        int num = 0xffff;
        try {
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
            intent = new Intent(INTENT_TOPACTIVITY_CALL);
            intent.setPackage(strPackage);
            intent.putExtra(PREDEFINE, PLAY_CMD);

            switch (mIntent) {
                case "change"://换一个频道
                case "page_up"://向后翻页
                case "next":
                case "play_skipforward":
                    if ("joke".equals(bean.mDomain)) {
                        return null;
                    }
                    mIntent = PLAYER_CMD_NEXT;
                    intent.putExtra(VALUE, 1);
                    break;
                case "play_skipback":
                case "prev":
                    mIntent = PLAYER_CMD_PREVIOUS;
                    intent.putExtra(VALUE, 1);
                    break;
                case "speed_play":
                    mIntent = PLAYER_CMD_SPEED;
                    break;
                case "play_by_episode":
                case "episode_select":
                    mIntent = PLAYER_CMD_EPISODE;
                    num = (int) bean.mSemanticJson.mSemantic.getIndex();
                    if ((bean.mQuery.contains("倒数") || bean.mQuery.contains("最后")) && num != Semantic.INVALID_DIGIT) {
                        num *= (-1);
                        intent.putExtra(VALUE, num);
                    }
                    break;
                case "page_select":
                    mIntent = PLAYER_CMD_PAGE;
                    num = (int) bean.mSemanticJson.mSemantic.getIndex();
                    break;
                case "replay":
                    mIntent = PLAYER_CMD_GOTO;
                    num = 0;
                    break;
                case "play_rewind"://tv
                case "fast_reverse"://fm
                case "fast_backward":
                    num = bean.mSemanticJson.mSemantic.getTimeLocation();
                    if (num != Semantic.INVALID_DIGIT) {
                        mIntent = PLAYER_CMD_GOTO;
                        break;
                    } else {
                        mIntent = PLAYER_CMD_BACKFORWARD;
                        num = bean.mSemanticJson.mSemantic.getDuration();
                        if (num == Semantic.INVALID_DIGIT) {
                            num = 30;
                        }
                    }
                    break;
                case "fast_forward":
                case "play_forward":
                    num = bean.mSemanticJson.mSemantic.getTimeLocation();
                    if (num != Semantic.INVALID_DIGIT) {
                        mIntent = PLAYER_CMD_GOTO;
                        break;
                    } else {
                        mIntent = PLAYER_CMD_FASTFORWARD;
                        num = bean.mSemanticJson.mSemantic.getDuration();
                        if (num == Semantic.INVALID_DIGIT) {
                            num = 30;
                        }
                    }
                    break;
                case "progress_to":
                    num = bean.mSemanticJson.mSemantic.getTimeLocation();
                    mIntent = PLAYER_CMD_GOTO;
                    if (num == Semantic.INVALID_DIGIT) {
                        num = bean.mSemanticJson.mSemantic.getDuration();
                    }
                    break;
                case "play_located":
                    num = bean.mSemanticJson.mSemantic.getDuration();
                    if (num != Semantic.INVALID_DIGIT) {
                        mIntent = PLAYER_CMD_GOTO;
                        break;
                    }
                    break;
                /*case "search_tvseries":
                    if (GuideTip.getInstance().isMediaDetail() || GuideTip.getInstance().isVideoPlay()) {
                        mIntent = PLAYER_CMD_EPISODE;
                        num = (int) bean.mSemanticJson.mSemantic.getIndex();
                        if (num == 0xffff) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    break;*/
                case "Open_details":
                    if (BeeSearchParams.getInstance().isInSearchPage()) {
                        mIntent = CMD_OPEN_DETAILS;
                        num = (int) bean.mSemanticJson.mSemantic.getIndex();
                        if (num > 12) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                    break;
                case "index_v2":
                case "list":
                    if (BeeSearchUtils.mSpeakSameInfo != null) {
                        return null;
                    }
                    GuideTip tip = GuideTip.getInstance();
                    if (tip != null && (tip.isVideoPlay() || tip.isMediaDetail())) {
                        mIntent = PLAYER_CMD_EPISODE;
                    } else {
                        mIntent = COMMAND_LOCATION;
                    }
                    num = (int) bean.mSemanticJson.mSemantic.getIndex();
                    break;
                case "skip_open_theme":
                case "skip_title":
                    mIntent = PLAYER_CMD_SKIPTITLE;
                    break;
                case "play_start":
                case "resume":
                    mIntent = PLAYER_CMD_PAUSE;
                    num = 0;
                    break;
                case "pause":
                case "play_pause":
                    mIntent = PLAYER_CMD_PAUSE;
                    num = 1;
                    break;
                case "channellist"://频道列表
                case "page_down"://向前翻页
                    mIntent = PLAYER_CMD_PREVIOUS;
                    num = 1;
                    break;
                default:
                    return null;
            }

            intent.putExtra(SEQUERY, PLAY_CMD);
            MLog.d(TAG, "idx:" + num);
            if (num != Semantic.INVALID_DIGIT) {
                intent.putExtra(VALUE, num);
            }
            intent.putExtra(INTENT, mIntent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return intent;
    }

    /***
     * 处理语义识别不正确的一些情况，如解决“播放第*期”等不能映射成播放命令
     ***/
    public static Intent PlayCmdPatchProcess(String speech) {
        MLog.d(TAG, "CmdPatchProcess");
        try {
            String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
            Intent intent = new Intent(INTENT_TOPACTIVITY_CALL);
            intent.setPackage(strPackage);
            intent.putExtra(PREDEFINE, PLAY_CMD);
            intent.putExtra(SEQUERY, PLAY_CMD);
            if (GuideTip.getInstance().isVideoPlay()
                    || GuideTip.getInstance().isAudioPlay()
                    || GuideTip.getInstance().isMediaDetail()) {
                if (StringUtils.isPrevCmdFromSpeech(speech)) {
                    MLog.d(TAG, "prev cmd");
                    intent.putExtra(INTENT, PLAYER_CMD_PREVIOUS);
                    return intent;
                } else if (StringUtils.isReplayCmdFromSpeech(speech)) {
                    MLog.d(TAG, "replay");
                    intent.putExtra(INTENT, PLAYER_CMD_GOTO);
                    intent.putExtra(VALUE, 0);
                    return intent;
                } else {
                    String mIntent = PLAYER_CMD_EPISODE;
                    int num = StringUtils.getIndexFromSpeech(speech);
                    MLog.d(TAG, "idx:" + num);
                    intent.putExtra(INTENT, mIntent);
                    if (num > 0) {
                        intent.putExtra(VALUE, num);
                        return intent;
                    }
                }
            } else if (BeeSearchParams.getInstance().isInSearchPage()) {
                String mIntent = COMMAND_LOCATION;
                int num = StringUtils.getIndexFromSpeech(speech);
                MLog.d(TAG, "idx:" + num);
                intent.putExtra(INTENT, mIntent);
                if (num > 0) {
                    intent.putExtra(VALUE, num);
                    return intent;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 处理系统命令语义识别不正确的一些情况，如解决“取消静音”等
     ***/
    public static boolean SystemCmdPatchProcess(Context ctx, String speech) {
        MLog.d(TAG, "sysCmdPatchProcess");
        try {
            if (StringUtils.isUnmuteCmdFromSpeech(speech)) {
                MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_volume_unmute));
                VolumeUtils.getInstance(ctx).cancelMute();
                return true;
            } else if (StringUtils.isExitCmdFromSpeech(speech)) {
                if (IStatus.mSceneType != IStatus.SCENE_GLOBAL) {
                    IStatus.mSmallDialogDimissTime = System.currentTimeMillis() - 1;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
