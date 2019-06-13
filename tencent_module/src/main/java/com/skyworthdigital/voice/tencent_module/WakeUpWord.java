package com.skyworthdigital.voice.tencent_module;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.SkyRing;

/**
 * Created by SDT03046 on 2018/7/19.
 */

public class WakeUpWord {
    private static final String[] SAYHI2 = {"我在~","来了~", "说吧~"};
    private static final String[] SAYHI = {"我在", "啥事", "你说", "在呢", "在", "说吧", "来了", "在咧", "来啦"};
    private static final String REMOTEVOICE_NOTE = "按住语音键说话~";
    private static int mWordIndex = 0;

    /*mediaplay 播放音频的方式来打招呼*/
    public static String getWord() {
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_REMOTE) {
            return REMOTEVOICE_NOTE;
        }
        try {
            //int[] idx = Utils.getRandomNumbers(0, SAYHI.length, 1);
            if (mWordIndex == 0) {
                SkyRing.getInstance().play("wozai.wav");
                mWordIndex = 1;
                return SAYHI2[0];
            } else if (mWordIndex == 1) {
                SkyRing.getInstance().play("laile.wav");
                mWordIndex = 2;
                return SAYHI2[1];
            } else {
                SkyRing.getInstance().play("shuoba.wav");
                mWordIndex = 0;
                return SAYHI2[2];
            }
            /*mWordIndex++;
            if (mWordIndex >= SAYHI.length) {
                mWordIndex = 0;
            }
            return SAYHI[mWordIndex];*/

        } catch (Exception e) {
            SkyRing.getInstance().play("wozai.wav");
            mWordIndex = 0;
            return SAYHI2[0];
        }
    }

    /*tts的方式来打招呼,目前发现tts的时候会降低唤醒率*/
    public static String getWordy() {
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_REMOTE) {
            return REMOTEVOICE_NOTE;
        }
        try {
            //int[] idx = Utils.getRandomNumbers(0, SAYHI.length, 1);
            mWordIndex++;
            if (mWordIndex >= SAYHI.length) {
                mWordIndex = 0;
            }
            return SAYHI[mWordIndex];

        } catch (Exception e) {
            mWordIndex = 0;
            return SAYHI[0];
        }
    }
}
