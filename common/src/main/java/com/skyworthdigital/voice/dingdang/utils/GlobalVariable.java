package com.skyworthdigital.voice.dingdang.utils;

import com.skyworthdigital.voice.common.BuildConfig;

public class GlobalVariable {
    public static final int IQIYI_SOURCE = 3;
    public static final int TENCENT_SOURCE = 6;
    public static final int SOUHU_SOURCE = 4;
    public static final int MGTV_SOURCE = 14;
    public static final int YOUKU_SOURCE = 2;
    public static final int BESTV_SOURCE = 19;

    public static final String VOICE_PACKAGE_NAME = "com.skyworthdigital.voice.dingdang";
    //public static final String MEDIAPALY_ACTIVITY = "com.skyworthdigital.skyallmedia.player.SdkPlayerActivity";//"com.skyworthdigital.skyallmedia/.player.SdkPlayerActivity";
    public static final String MEDIAPALY_CLASS = "com.skyworthdigital.skyallmedia.player.SdkPlayerActivity";
    //继续语音识别的action。默认需要重新唤醒才能语音识别，收到这个action,则可以连续多次语音识别
    public static final String ACTION_VOICE_RECO_GOON = "com.skyworthdigital.action.RECO_GOON";
    public static final String INTENT_FILM_SEARCH = "SEARCH_FILM";
    public static final String INTENT_AUDIO_PLAY = "audio.unicast.play";
    public static final String INTENT_AUDIO_NEXT = "audio.unicast.next";
    public static final String INTENT_MUSIC_PLAY = "audio.music.play";
    public static final String INTENT_MUSIC_GOTO = "audio.music.goto";
    public static final String TYPE_TEXT = "Text";
    public static final String VOLUME_MAX = "max";
    public static final String FM_NAME = "fm_name";
    public static final String FM_URL = "fm_url";
    public static final String INTENT_AUDIO_JOKE = "audio.joke.play";
    public static final String INTENT_AUDIO_PAUSE = "audio.unicast.pause";
    public static final String INTENT_AUDIO_CONTINUE = "audio.unicast.continue";

    public static final String CMD_MUSIC_GOTO = "audio.music.goto";//音乐播放第几首
    public static final String INTENT_TV_LIVE = "tv.live.channel.search";

    //其它应用需要申请audio recorder时发送的广播。语音助手收到这个广播，会释放audio recorder，同时停止唤醒。
    public static final String APPLY_AUDIO_RECORDER_ACTION = "com.skyworthdigital.action.APPLY_AUDIO_RECORDER";

    //其它应用已释放audio recorder时发送的广播。这样语音助手可以重新获取audio recorder，启用语音唤醒
    public static final String RELEASE_AUDIO_RECORDER_ACTION = "com.skyworthdigital.action.RELEASE_AUDIO_RECORDER";

    public static final int AI_NONE= 0;
    public static final int AI_REMOTE = 1;
    public static final int AI_VOICE = 2;
}
