package com.skyworthdigital.voice.baidu_module;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.alarm.Alarm;
import com.skyworthdigital.voice.baidu_module.duerbean.DuerBean;
import com.skyworthdigital.voice.baidu_module.duerbean.Nlu;
import com.skyworthdigital.voice.baidu_module.duerbean.Result;
import com.skyworthdigital.voice.baidu_module.duerbean.Slots;
import com.skyworthdigital.voice.baidu_module.robot.BdTTS;
import com.skyworthdigital.voice.baidu_module.util.ActionUtils;
import com.skyworthdigital.voice.baidu_module.util.GsonUtils;
import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;
import com.skyworthdigital.voice.common.AbsAsrTranslator;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;

/**
 * 相当于百度版里的MainControler
 * Created by Ives 2019/6/18
 */
public class BdAsrTranslator extends AbsAsrTranslator<String> {
    private String TAG = BdAsrTranslator.class.getSimpleName();

    private static final String DOMAIN_FILM = "FILM";
    private static final String DOMAIN_COMMAND = "COMMAND";
    private static final String DOMAIN_AUDIOPLAY = "audio.unicast";
    private static final String DOMAIN_MUSIC = "audio.music";
    private static final String DOMAIN_AUDIOJOKE = "audio.joke";
    private static final String DOMAIN_TVLIVE = "tv.live";
    private static final String DOMAIN_ALARM = "alarm";

    public static AbsAsrTranslator getInstance(){
        if(instance[0]==null){
            synchronized (BdAsrTranslator.class) {
                if(instance[0]==null) {
                    instance[0] = new BdAsrTranslator();
                }
            }
        }
        return instance[0];
    }

    @Override
    public void translate(final String asrResult) {
        final Context context = VoiceApp.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DuerBean duerBean = GsonUtils.getDuerBean(asrResult);
                if (duerBean == null) {
                    MLog.i(TAG, "duerBean is null:" + asrResult);
                    return;
                }
                Result result = duerBean.getResult();

                if (result != null) {
                    Nlu nlu = result.getNlu();
                    MLog.i(TAG, "Nlu:" + nlu.toString());
                    String originSpeech = duerBean.getOriginSpeech();
                    Slots slots = nlu.getSlots();

                    //音箱的远场语音，百度返回的用户输入的原话内容结尾会多"，"，因此特殊处理。
                    if (VoiceModeAdapter.isAudioBox()) {
                        String endStr = "，";
                        int len = endStr.length();
                        if (originSpeech.endsWith(endStr)) {
                            originSpeech = originSpeech.substring(0, originSpeech.length() - len);
                            MLog.i(TAG, "originSpeech:" + originSpeech);
                        }
                    }

                    //如果已经进入直播，则先进入直播流程
                    if (BdTvLiveController.getInstance().isTvLive() || (slots != null && !TextUtils.isEmpty(slots.getTvStation()))) {
                        String channelname = null;
                        if (slots != null) {
                            channelname = slots.getChannelName();
                        }
                        if (BdTvLiveController.getInstance().control(asrResult, channelname, originSpeech)) {
                            return;
                        }
                    }

                    if (ActionUtils.globalCommandExecute(context, originSpeech)) {
                        MLog.i(TAG, "globalCommandExecute");//全局语音处理
                    } else {
                        switch (nlu.getDomain()) {
                            case DOMAIN_FILM:
                                if (TextUtils.equals("H4S06", VoiceApp.getModel())) {
                                    BdTTS.getInstance().talk(context.getString(R.string.str_unknown_note));
                                    break;
                                }
                                if (TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_FILM_SEARCH)) {
                                    MLog.i(TAG, "startSearch");
                                    ActionUtils.startSearch(context, duerBean, originSpeech/*, mvideoPlayListener*/);
                                }
                                break;
                            case DOMAIN_COMMAND:
                                ActionUtils.tvCommandExecute(context, nlu, /*componentName,*/ originSpeech);
                                break;
                            case DOMAIN_AUDIOJOKE:
                            case DOMAIN_AUDIOPLAY:
                                if (TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_AUDIO_PLAY)
                                        || TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_AUDIO_NEXT)
                                        || TextUtils.equals(nlu.getIntent(), DefaultCmds.PLAYER_CMD_AUDIO_GOTO)
                                        || TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_AUDIO_JOKE)) {
                                    ActionUtils.audioPlayExecute(context, asrResult);
                                } else if (TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_AUDIO_PAUSE)) {
                                    DefaultCmds.startPauseIntent(context, true, originSpeech);
                                } else if (TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_AUDIO_CONTINUE)) {
                                    DefaultCmds.startPauseIntent(context, false, originSpeech);
                                }
                                if (!result.getSpeech().getContent().isEmpty() && TextUtils.equals(GlobalVariable.TYPE_TEXT, result.getSpeech().getType())) {
                                    BdTTS.getInstance().talk(result.getSpeech().getContent());
                                }
                                break;
                            case DOMAIN_MUSIC:
                                if (TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_MUSIC_PLAY)
                                        || TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_MUSIC_GOTO)) {
                                    if (!ActionUtils.musicExecute(context, asrResult, originSpeech)) {
                                        BdTTS.getInstance().talk(context.getString(R.string.str_music_note));
                                    }else {
                                        BdTTS.getInstance().talk(context.getString(R.string.str_ok));
                                    }
                                }
                                break;
                            case DOMAIN_TVLIVE:
                                if (TextUtils.equals(nlu.getIntent(), GlobalVariable.INTENT_TV_LIVE)
                                        && !BdTvLiveController.getInstance().isTvLive()) {
                                    String channelname = null;
                                    if (slots != null) {
                                        channelname = slots.getChannelName();
                                    }
                                    BdTvLiveController.getInstance().control(asrResult, channelname, originSpeech);
                                }
                                break;
                            case DOMAIN_ALARM:
                                if (TextUtils.equals(nlu.getIntent(), "alarm")) {
                                    Alarm alarm = new Alarm();
                                    int ret = alarm.addClock(asrResult);
                                    if (ret == Alarm.ERROR_NOT_SUPPORT_REPEAT) {
                                        BdTTS.getInstance().talk(context.getString(R.string.str_alarm_notsupport_repeat));
                                        break;
                                    } else if (ret == Alarm.ERROR_OTHER) {
                                        BdTTS.getInstance().talk(context.getString(R.string.str_alarm_example));
                                        break;
                                    }
                                    if (!result.getSpeech().getContent().isEmpty() && TextUtils.equals(GlobalVariable.TYPE_TEXT, result.getSpeech().getType())) {
                                        BdTTS.getInstance().talk(result.getSpeech().getContent());
                                    }
                                } else {
                                    BdTTS.getInstance().talk(context.getString(R.string.str_audiolive_note));
                                }
                                break;
                            case "audio.live":
                                BdTTS.getInstance().talk(context.getString(R.string.str_audiolive_note));
                                break;
                            case "audio.news":
                                try {
                                    Intent intent = new Intent();
                                    intent.setAction("cn.mipt.videohj.intent.action.LOAD_NEWS");//intent.setAction("com.skyworthdigital.skyallmedia.ShortVideoList");
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    BdTTS.getInstance().talk(context.getString(R.string.str_news));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "universal_search":
                                try {
                                    if (TextUtils.equals(nlu.getIntent(), "baike")) {
                                        if (ActionUtils.baikeVideoExecute(context, asrResult)) {
                                            BdTTS.getInstance().talk(context.getString(R.string.str_ok));
                                            break;
                                        }
                                    }
                                    if (!result.getSpeech().getContent().isEmpty() && TextUtils.equals(GlobalVariable.TYPE_TEXT, result.getSpeech().getType())) {
                                        BdTTS.getInstance().talk(result.getSpeech().getContent());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default://未能识别或以上没有提供的领域，比如天气
                                if (ActionUtils.appLaunchExecute(context, nlu, originSpeech)) {
                                    MLog.i(TAG, "app_launcher");
                                } else if (!result.getSpeech().getContent().isEmpty() &&
                                        (TextUtils.equals(GlobalVariable.TYPE_TEXT, result.getSpeech().getType())
                                                || TextUtils.equals("txt", result.getSpeech().getType()))) {
                                    BdTTS.getInstance().talk(result.getSpeech().getContent());
                                }
                                break;
                        }
                    }
                }
            }
        }).start();
    }
}
