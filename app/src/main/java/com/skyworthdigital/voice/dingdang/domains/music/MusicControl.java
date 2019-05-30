package com.skyworthdigital.voice.dingdang.domains.music;

import android.content.Context;

import com.skyworthdigital.voice.dingdang.control.model.MusicSlots;
import com.skyworthdigital.voice.dingdang.domains.music.utils.KalaokUtils;
import com.skyworthdigital.voice.dingdang.domains.music.utils.QQMusicUtils;


/**
 * music control
 * Created by SDT03046 on 2017/12/15.
 */

public class MusicControl {
    private final static String[] SPEECH_FILTER = {"我想唱", "我要唱"};

    public static boolean actionExecute(Context ctx, MusicSlots info, String speech) {
        boolean isListen = true;

        for (String temp : SPEECH_FILTER) {
            if (speech.startsWith(temp)) {
                isListen = false;
                break;
            }
        }
        if (isListen) {
            return QQMusicUtils.acitonExecute(ctx, info, speech);
        } else {
            return KalaokUtils.acitonExecute();
        }
    }
}