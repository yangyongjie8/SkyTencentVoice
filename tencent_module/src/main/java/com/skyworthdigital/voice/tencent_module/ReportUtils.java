package com.skyworthdigital.voice.tencent_module;

import android.text.TextUtils;

import com.forest.bigdatasdk.ForestDataReport;
import com.forest.bigdatasdk.model.EventInfo;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.model.Semantic;
import com.skyworthdigital.voice.tencent_module.model.Slot;
import com.skyworthdigital.voice.tencent_module.model.ValueItem;

import org.json.JSONObject;

import java.util.List;


public class ReportUtils {
    private static final String EVENT_ID = "voice";
    private static final String EVENT_NAME = "voice";
    private static final String EVENT_TYPE_DEFAULT = "remote";
    private static final String EVENT_TYPE_SOUNDBOX = "wakeup";

    private static final String[] WORDS_EVENT = {"word1", "word2", "word3", "word4", "word5", "word6",};
    private static final String DATA_SPEECH = "origin_speech";
    private static final String DATA_DOMAIN = "domain";
    private static final String DATA_INTENT = "intent";
    private static final String DATA_ANALYZED = "analyzed";

    public static void report2BigData(Semantic nlu) {
        if (nlu == null) {
            return;
        }
        EventInfo info;
        int keycount = 0;
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
            info = new EventInfo(EVENT_ID, EVENT_NAME, EVENT_TYPE_SOUNDBOX);
        } else {
            info = new EventInfo(EVENT_ID, EVENT_NAME, EVENT_TYPE_DEFAULT);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(nlu.mDomain)) {
                jsonObject.put(DATA_DOMAIN, getKey(nlu.mDomain));
            }
            if (!TextUtils.isEmpty(nlu.mIntent)) {
                jsonObject.put(DATA_INTENT, nlu.mIntent);
            }

            jsonObject.put(DATA_SPEECH, nlu.mQuery);

            List<Slot> slots = nlu.mSlots;
            if (slots.size() > 0) {
                for (Slot tmp : slots) {
                    if (tmp.mValueList.size() > 0) {
                        for (ValueItem value : tmp.mValueList) {
                            if (keycount < WORDS_EVENT.length) {
                                jsonObject.put(WORDS_EVENT[keycount], value.mOriginalText);
                                keycount++;
                            }
                        }
                    }
                }
            }

            if (keycount < WORDS_EVENT.length) {
                for (int i = keycount; i < WORDS_EVENT.length; i++) {
                    jsonObject.put(WORDS_EVENT[i], "");
                }
            }

            if (keycount > 0) {
                jsonObject.put(DATA_ANALYZED, 1);
            } else {
                jsonObject.put(DATA_ANALYZED, 0);
            }

            info.setEventData(jsonObject.toString());
            MLog.d("Report", jsonObject.toString());
            ForestDataReport.getInstance().sendEvent(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断该字符串是否为中文
     */
    private static boolean isChinese(String str) {
        if (str.length() >= 1) {
            int n = (int) str.charAt(0);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

    private static String getKey(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] mainkeys = str.split("\\.");

        return mainkeys[mainkeys.length - 1];
    }

}
