package com.skyworthdigital.voice.tencent_module;

import android.text.TextUtils;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.db.DbUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tv.AbsTvLiveControl;

/**
 * Created by Ives 2019/6/11
 */
public class TxTvLiveController extends AbsTvLiveControl {

    public TxTvLiveController() {
    }

    public static AbsTvLiveControl getInstance() {
        if(mTvUtilInstance[1]==null){
            synchronized (TxTvLiveController.class) {
                if(mTvUtilInstance[1]==null) {
                    mTvUtilInstance[1] = new TxTvLiveController();
                }
            }
        }
        return mTvUtilInstance[1];
    }

    @Override
    public boolean control(final String asrResult, String channelName, String query) {//腾讯叮当的asrResult就是频道名，实际是在调用前做了解析。参数channelName为空
        if (asrResult == null && TextUtils.isEmpty(query)) {
            return false;
        }
        MLog.d(TAG, "tvLive control " + query);
        String channelname = null, searchChannelCode = null, searchChannelName = null;
        if (!TextUtils.isEmpty(asrResult)) {
            channelname = asrResult;
            searchChannelName = asrResult;
        }

        String[] result;

        //mSearchChannelName = null;
        if (checkApkExist(TVLIVE_PACKAGENAME)) {
            if (!TextUtils.isEmpty(query)) {
                for (String temp : UPCHANNEL) {
                    if (TextUtils.equals(temp, query)) {
                        preChannel();
                        return true;
                    }
                }
                for (String temp : DOWNCHANNEL) {
                    if (TextUtils.equals(temp, query)) {
                        nextChannel();
                        return true;
                    }
                }
                mSpeechChannelName = Utils.filterBy(query, SPEECH_FILTER);
                MLog.d(TAG, "speech name：" + mSpeechChannelName);
            }

            DbUtils dao = new DbUtils(VoiceApp.getInstance());
            result = dao.searchByName(mSpeechChannelName);
            if (result != null) {
                jumpToChannel(result[0], result[1]);
                return true;
            }

            result = dao.searchItem(mSearchCategoryId, mSearchChannelId, channelname, mSpeechChannelName);
            if (result != null) {
                jumpToChannel(result[0], result[1]);
                return true;
            }
        } else {
            tvLiveInstallPage();
        }
        return false;
    }
}
