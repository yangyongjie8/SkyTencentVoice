package com.skyworthdigital.voice.dingdang.control.tts;

import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.tencent_module.TxTTS;

/**
 * Created by SDT03046 on 2018/7/19.
 */

public class MyTTS {
    public static AbsTTS getInstance(AbsTTS.MyTTSListener listener) {
        return TxTTS.getInstance(listener);
    }

}
