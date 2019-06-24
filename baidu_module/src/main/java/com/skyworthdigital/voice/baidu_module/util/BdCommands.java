package com.skyworthdigital.voice.baidu_module.util;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.baidu_module.duerbean.DuerBean;
import com.skyworthdigital.voice.baidu_module.duerbean.Nlu;
import com.skyworthdigital.voice.baidu_module.duerbean.Result;
import com.skyworthdigital.voice.baidu_module.duerbean.Slots;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.guide.GuideTip;

import java.util.ArrayList;

import static com.skyworthdigital.voice.scene.SkySceneService.INTENT_TOPACTIVITY_CALL;

/**
 * Created by Ives 2019/6/18
 */
public class BdCommands extends DefaultCmds {

    private static final String ROW = "row";//行
    private static final String COL = "col";//列
    private static final String RE_ROW = "re_row";//倒数行
    private static final String RE_COL = "re_col";//倒数列

    public static boolean isPlay(@NonNull Nlu nlu) {
        if(DefaultCmds.isPlay(nlu.getIntent())){
            return true;
        }
        ArrayList<String> playactions = new ArrayList<>();
        playactions.add(PLAYER_CMD_PREVIOUS);
        playactions.add(PLAYER_CMD_NEXT);
        playactions.add(PLAYER_CMD_EPISODE);
        playactions.add(PLAYER_CMD_PAUSE);
        playactions.add(PLAYER_CMD_CONTINUE);
        playactions.add(COMMAND_LOCATION);
        playactions.add(COMMAND_RESOLUTION);
        playactions.add(PLAYER_CMD_FASTFORWARD);
        playactions.add(PLAYER_CMD_BACKFORWARD);
        playactions.add(PLAYER_CMD_GOTO);
        playactions.add(PLAYER_CMD_SPEED);
        playactions.add(AUDIO_UNICAST_CMD_SPEED);
        if (!TextUtils.equals(GuideTip.getInstance().getCurrentClass(), "com.skyworthdigital.voice.baidu_module.fm.SkyAudioPlayActivity")) {
            playactions.add(PLAYER_CMD_AUDIO_NEXT);
            playactions.add(PLAYER_CMD_AUDIO_PREVIOUS);
            Slots slots = nlu.getSlots();
            if (slots != null && TextUtils.equals(nlu.getIntent(), PLAYER_CMD_AUDIO_GOTO) && !TextUtils.isEmpty(slots.getEpisode())) {
                return true;
            }
        }
        return (playactions.contains(nlu.getIntent()));
    }

    public static Intent composePlayControlIntent(@NonNull DuerBean duerBean) {
        Result result = duerBean.getResult();
        if(result==null)return null;
        Nlu nlu = result.getNlu();
        String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
        Intent intent = new Intent(INTENT_TOPACTIVITY_CALL);
        intent.putExtra(SEQUERY, duerBean.getOriginSpeech());
        intent.putExtra(INTENT, nlu.getIntent());
        intent.setPackage(strPackage);
        Slots slots = nlu.getSlots();

        if (isPlay(nlu)) {
            intent.putExtra(CATEGORY_SERV, PLAY_CMD);//增加服务器识别类别匹配
            intent.setPackage(strPackage);
            double value;
            switch (nlu.getIntent()) {
                case COMMAND_RESOLUTION:
                    if (slots == null) {
                        break;
                    }
                    intent.putExtra("_name", slots.getName());
                    break;
                case PLAYER_CMD_FASTFORWARD:
                case PLAYER_CMD_BACKFORWARD:
                    if (slots == null) {
                        break;
                    }
                    intent.putExtra(VALUE, slots.getOffset());
                    break;
                case AUDIO_UNICAST_CMD_SPEED:
                    if (slots == null) {
                        break;
                    }
                    try {
                        if (!TextUtils.isEmpty(slots.getTime())) {
                            int offset = Integer.parseInt(slots.getTime());
                            MLog.i("BdCommands", "offset:" + offset);
                            if (!TextUtils.isEmpty(slots.getRewind()) && TextUtils.equals(slots.getRewind(), "1")) {
                                intent.putExtra(INTENT, PLAYER_CMD_BACKFORWARD);
                                intent.putExtra(VALUE, offset);
                            } else {
                                intent.putExtra(INTENT, PLAYER_CMD_FASTFORWARD);
                                intent.putExtra(VALUE, offset);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case PLAYER_CMD_GOTO:
                    if (slots == null) {
                        break;
                    }
                    if (TextUtils.isEmpty(slots.getSkipTitle())) {
                        intent.putExtra(VALUE, slots.getOffset());
                    } else {
                        MLog.i("BdCommands", "skip_title:" + slots.getSkipTitle());
                        intent.putExtra(INTENT, "player.skiptitle");
                        //intent.putExtra("skip_title", nlu.getSlots().getSkip_title());
                    }
                    break;
                case PLAYER_CMD_CONTINUE:
                    intent.putExtra(INTENT, PLAYER_CMD_PAUSE);//百度V3版本播放和暂停去掉了槽位值，播放改为了continue。我们送给全媒资的数据格式保持不变
                    intent.putExtra(VALUE, 0);
                    break;
                case PLAYER_CMD_PAUSE:
                    intent.putExtra(VALUE, 1);
                    break;
                case COMMAND_LOCATION:
                    if (slots == null) {
                        break;
                    }
                    int col = slots.getCol();
                    int row = slots.getRow();
                    int re_col = slots.getRe_col();
                    int re_row = slots.getRe_row();
                    int num = slots.getNumber();
                    if (col != 0) {
                        intent.putExtra(COL, col);
                        MLog.i("BdCommands", COL + col);
                    }
                    if (row != 0) {
                        intent.putExtra(ROW, row);
                        MLog.i("BdCommands",ROW + row);
                    }
                    if (re_row != 0) {
                        intent.putExtra(RE_ROW, re_row);
                        MLog.i("BdCommands",RE_ROW + re_row);
                    }
                    if (re_col != 0) {
                        intent.putExtra(RE_COL, re_col);
                        MLog.i("BdCommands",RE_COL + re_col);
                    }
                    if (num != 0) {
                        intent.putExtra(VALUE, num);
                        MLog.i("BdCommands","_value:" + num);
                    }
                    break;
                case PLAYER_CMD_EPISODE:
                    if (slots == null) {
                        break;
                    }
                    MLog.i("BdCommands","re_episode:" + slots.getReEpisode());
                    if (slots.getReEpisode() > 0) {
                        //intent.putExtra(GlobalVariable.RE_EPISODE, slots.getRe_episode());
                        intent.putExtra(VALUE, (-1) * slots.getReEpisode());
                    } else {
                        value = (double) slots.getValue();
                        intent.putExtra(VALUE, (int) value);
                    }
                    break;
                case PLAYER_CMD_AUDIO_GOTO:
                    if (slots == null) {
                        break;
                    }
                    try {
                        if (!TextUtils.isEmpty(slots.getEpisode())) {
                            intent.putExtra(INTENT, PLAYER_CMD_EPISODE);
                            value = Integer.valueOf(slots.getEpisode());
                            intent.putExtra(VALUE, (int) value);
                            return intent;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case PLAYER_CMD_AUDIO_PREVIOUS:
                    if (slots == null) {
                        break;
                    }
                    intent.putExtra(INTENT, PLAYER_CMD_PREVIOUS);
                    intent.putExtra(VALUE, 1);
                    break;
                case PLAYER_CMD_AUDIO_NEXT:
                    if (slots == null) {
                        break;
                    }
                    intent.putExtra(INTENT, PLAYER_CMD_NEXT);
                    intent.putExtra(VALUE, 1);
                    break;

                case PLAYER_CMD_SPEED:
                    if (slots == null) {
                        break;
                    }
                    value = (double) slots.getValue();
                    intent.putExtra(VALUE, (int) value);
                    break;
                case PLAYER_CMD_NEXT:
                case PLAYER_CMD_PREVIOUS:
                    intent.putExtra(VALUE, 1);
                    break;
                default:
                    break;
            }
            return intent;
        } else if (isMusicCmd(nlu.getIntent())) {
            if (slots == null) {
                return null;
            }
            intent.putExtra(SEQUERY, MUSIC_CMD);
            intent.setPackage(strPackage);
            try {
                int idx = Integer.parseInt((String) slots.getMusicIdx());
                MLog.i("BdCommands","music cmd:" + idx);
                intent.putExtra(VALUE, idx);
                return intent;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
