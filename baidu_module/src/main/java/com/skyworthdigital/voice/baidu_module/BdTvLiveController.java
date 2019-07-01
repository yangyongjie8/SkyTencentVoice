package com.skyworthdigital.voice.baidu_module;

import android.text.TextUtils;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.baidu_module.robot.BdTTS;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.db.DbUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tv.AbsTvLiveControl;

import org.json.JSONObject;

/**
 * Created by Ives 2019/6/11
 */
public class BdTvLiveController extends AbsTvLiveControl {

    private BdTvLiveController() {
    }

    public static AbsTvLiveControl getInstance() {
        if(mTvUtilInstance[0]==null){
            synchronized (BdTvLiveController.class) {
                if(mTvUtilInstance[0]==null) {
                    mTvUtilInstance[0] = new BdTvLiveController();
                }
            }
        }
        return mTvUtilInstance[0];
    }

    @Override
    public boolean control(String duerResult, String channelName, String speech) {
        if ((duerResult == null && channelName == null && speech == null)) {
            return false;
        }

        String[] result;
        mSearchChannelId = null;
        mSearchChannelName = null;
        if (checkApkExist(TVLIVE_PACKAGENAME)) {
            if (speech != null) {
                for (String temp : UPCHANNEL) {
                    if (TextUtils.equals(temp, speech)) {
                        preChannel();
                        return true;
                    }
                }
                for (String temp : DOWNCHANNEL) {
                    if (TextUtils.equals(temp, speech)) {
                        nextChannel();
                        return true;
                    }
                }
                mSpeechChannelName = Utils.filterBy(speech, SPEECH_FILTER);
                MLog.i(TAG, "speech name：" + mSpeechChannelName + " channelname:" + channelName);
            }

            DbUtils dao = new DbUtils(VoiceApp.getInstance());
            result = dao.searchByName(mSpeechChannelName);
            if (result != null) {
                jumpToChannel(result[0], result[1]);
                return true;
            }

            if (duerResult != null) {
                parseResult(duerResult);
            }

            result = dao.searchItem(mSearchCategoryId, mSearchChannelId, channelName, mSpeechChannelName);
            if (result != null) {
                jumpToChannel(result[0], result[1]);
                return true;
            }
            if (!TextUtils.isEmpty(mSearchCategoryId) && !TextUtils.isEmpty(mSearchChannelId)) {
                BdTTS.getInstance().talk(VoiceApp.getInstance().getString(R.string.str_tvlive_unfind));
            }
        } else {
            tvLiveInstallPage();
        }
        return false;
    }

    private boolean parseResult(String result) {
        if (TextUtils.isEmpty(result)) {
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("result")) {
                JSONObject resultObject = jsonObject.getJSONObject("result");
                if (resultObject.has("directives")) {
                    Object jsonData = resultObject.getJSONArray("directives").get(0);
                    JSONObject payloadObject = ((JSONObject) jsonData).getJSONObject("payload");//.getJSONObject("audio_item").getJSONObject("stream");
                    if (payloadObject.has("category_id")) {
                        mSearchCategoryId = payloadObject.getString("category_id");
                    }
                    if (payloadObject.has("channel_id")) {
                        mSearchChannelId = payloadObject.getString("channel_id");
                    }
                    if (payloadObject.has("channel_name")) {
                        mSearchChannelName = payloadObject.getString("channel_name");
                    }
                    if (!TextUtils.isEmpty(mSearchCategoryId) && !TextUtils.isEmpty(mSearchChannelId)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
