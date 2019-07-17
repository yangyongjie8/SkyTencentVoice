package com.skyworthdigital.voice;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.common.AbsController;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.tianmai.TianmaiIntent;
import com.skyworthdigital.voice.tianmai.WeatherBee;
import com.skyworthdigital.voice.tianmai.WeatherUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.skyworthdigital.voice.scene.SkySceneService.INTENT_TOPACTIVITY_CALL;

/**
 * 预定义命令
 * 有：$PLAY,$MUSICPLAY
 */

public class DefaultCmds {
    public static final String PLAY_CMD = "#PLAY";
    public static final String MUSIC_CMD = "#MUSICPLAY";
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
    protected static final String COMMAND_RESOLUTION = "command.resolution";
    public static final String PLAYER_CMD_PREVIOUS = "player.previous";//上一集
    public static final String PLAYER_CMD_NEXT = "player.next";//下一集
    public static final String PLAYER_CMD_EPISODE = "player.episode";//第几集
    public static final String PLAYER_CMD_PAGE = "page_select";
    public static final String PLAYER_CMD_PAUSE = "player.pause";
    protected static final String PLAYER_CMD_CONTINUE = "player.continue";//暂停或播放
    public static final String PLAYER_CMD_FASTFORWARD = "player.fastforward";//快进
    public static final String PLAYER_CMD_BACKFORWARD = "player.backforward";//快退
    protected static final String AUDIO_UNICAST_CMD_SPEED = "audio.unicast.speed";
    public static final String PLAYER_CMD_GOTO = "player.goto";
    protected static final String PLAYER_CMD_SPEED = "player.speed";//多倍速播放
    protected static final String PLAYER_CMD_SKIPTITLE = "player.skiptitle";
    protected static final String PLAYER_CMD_AUDIO_PREVIOUS = "audio.unicast.previous";
    protected static final String PLAYER_CMD_AUDIO_NEXT = "audio.unicast.next";
    public static final String PLAYER_CMD_AUDIO_GOTO = "audio.unicast.goto";
    public static final String CMD_OPEN_DETAILS = "open.details";
    public static final String VALUE = "_value";
    //public static final String RE_EPISODE = "re_episode";
    public static final String INTENT = "_intent";
    public static final String SEQUERY = "_sequery";
    public static final String COMMAND = "_command";
    public static final String CATEGORY_SERV = "_category_serv";// server识别的类别，比如#PLAY
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

    public static boolean isMusicCmd(String command) {
        ArrayList<String> playactions = new ArrayList<>();
        playactions.add(GlobalVariable.CMD_MUSIC_GOTO);

        return playactions.contains(command);
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
            intent.putExtra(CATEGORY_SERV, PLAY_CMD);
            intent.putExtra(SEQUERY, speech);
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
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_volume_unmute));
                VolumeUtils.getInstance(ctx).cancelMute();
                return true;
            } else if (StringUtils.isExitCmdFromSpeech(speech)) {
                MLog.i(TAG, "SystemCmdPatch");
                if(GuideTip.getInstance().isLauncherHome()){
                    AbsController.getInstance().dismissDialog(0);
                    return true;
                }
                AppUtil.killTopApp();
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_exit));
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

    public static void startEpisodeIntent(Context ctx, int value, String originSpeech) {
        String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
        Intent intent = new Intent(INTENT_TOPACTIVITY_CALL);
        intent.putExtra(SEQUERY, originSpeech);
        intent.putExtra(CATEGORY_SERV, PLAY_CMD);
        intent.setPackage(strPackage);
        intent.putExtra(INTENT, PLAYER_CMD_EPISODE);

        intent.putExtra(VALUE, value);
        ctx.startService(intent);
    }

    public static void startPauseIntent(Context ctx, boolean pause, String originSpeech) {
        String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
        Intent intent = new Intent(INTENT_TOPACTIVITY_CALL);
        intent.putExtra(SEQUERY, originSpeech);
        intent.putExtra(CATEGORY_SERV, PLAY_CMD);
        intent.setPackage(strPackage);
        intent.putExtra(INTENT, PLAYER_CMD_PAUSE);//百度V3版本播放和暂停去掉了槽位值，播放改为了continue。我们送给全媒资的数据格式保持不变
        if (pause) {
            intent.putExtra(VALUE, 1);
        } else {
            intent.putExtra(VALUE, 0);
        }
        ctx.startService(intent);
    }

    public static void startTianmaiPlay(final Context ctx, final TianmaiIntent intent){
        if("六点行程".equals(intent.getName())){
            // 起床提醒
            // 天气
            // 逐条播放今日行程
            AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_tianmai_tip_wakeup));
            WeatherUtil.getWeatherToday("南京", new WeatherUtil.CallbackWeather() {
                @Override
                public void callback(WeatherBee todayWeather) {
                    if(todayWeather==null){
                        AbsTTS.getInstance(null).talkSerial(ctx.getString(R.string.str_error_network));
                        return;
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append("今天最高温度").append(todayWeather.getHigh_t()).append("摄氏度")
                            .append("，最低温度").append(todayWeather.getLow_t()).append("摄氏度")
                            .append("，相对湿度").append(todayWeather.getShidu());
                    if(todayWeather.getZhishu()!=null&&todayWeather.getZhishu().size()>0) {
                        sb.append(todayWeather.getZhishu().get(0));
                    }
                    AbsTTS.getInstance(null).talkSerial(sb.toString());

                    List<String> allSchedules = splitContent(intent.getVoiceContent());
                    for (String schedule : allSchedules) {
                        AbsTTS.getInstance(null).talkSerial(schedule);
                    }
                }
            });
        }else {
            AbsTTS.getInstance(null).talk(intent.getVoiceContent());
        }
    }
    private static List<String> splitContent(String content){
        if(content==null)return new ArrayList<>();
        return Arrays.asList(content.split("#"));
    }
}
