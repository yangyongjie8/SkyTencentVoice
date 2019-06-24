package com.skyworthdigital.voice.baidu_module.music;

import android.content.Context;

import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.music.utils.KalaokUtils;


/**
 * music control
 * Created by SDT03046 on 2017/12/15.
 */

public class MusicControl {
    private final static String[] SPEECH_FILTER = {"我想唱", "我要唱", "唱"};

    public static boolean actionExecute(Context ctx, MusicInfo info, String speech) {
        boolean isListen = true;

        for (String temp : SPEECH_FILTER) {
            if (speech.startsWith(temp)) {
                isListen = false;
                break;
            }
        }
        MLog.i("MusicControl", "musicActionExecute");
        if (isListen) {
            return QQMusicUtils.acitonExecute(ctx, info);
        } else {
            return KalaokUtils.acitonExecute();
        }
    }
}