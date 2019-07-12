package com.skyworthdigital.voice.tencent_module;

import android.content.Intent;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.beesearch.BeeSearchUtils;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.model.Semantic;

import static com.skyworthdigital.voice.scene.SkySceneService.INTENT_TOPACTIVITY_CALL;

/**
 * Created by Ives 2019/6/5
 */
public class TxCmds extends DefaultCmds {
    private static String TAG = TxCmds.class.getSimpleName();

    public static Intent composePlayControlIntent(AsrResult bean) {
        Intent intent;
        int num = 0xffff;
        try {
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
            intent = new Intent(INTENT_TOPACTIVITY_CALL);
            intent.setPackage(strPackage);
            intent.putExtra(PREDEFINE, PLAY_CMD);
            intent.putExtra(SEQUERY, bean.mQuery);
            intent.putExtra(CATEGORY_SERV, PLAY_CMD);//增加服务器识别类别匹配

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
                    MLog.i("TxCmds####", "num:"+num);
                    mIntent = PLAYER_CMD_FASTFORWARD;
                    if (num == Semantic.INVALID_DIGIT) {
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
}
