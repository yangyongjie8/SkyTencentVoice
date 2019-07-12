package com.skyworthdigital.voice.beesearch;


import com.skyworthdigital.voice.common.utils.Utils;

public class BeeSearchParams {
    public static final String ASR_PATH = "http://smartmovie.skyworthbox.com";//"http://119.23.12.86";
    public static final String VOICE_MODEL="/SmartMovie";
    public static final String IR_MODEL="/SmartMovie";
    public static final String GETNLU_PATH = "/api/parsewords";
    public static final String GETCMD_PATH = "/api/parsecmd";
    public static final String DOSEARCH_PATH = "/api/dosearch";
    public static final String GETMOVIEMETA_PATH = "/api/getmoviemetajson";
    public static final String RESULT_UNKNOWN = "-1";
    public static final String RESULT_SEARCH = "1";
    public static final String RESULT_CMD = "2";
    public static final String RESULT_CORRECT = "3";
    public static final String RESULT_UNCERTAIN = "4";
    public static final String RESULT_PLAY_DIRECT = "5";
    public static final String PARAM_INTERFACE_VER = "version=10208";
    
    private String mLocalcallid = "";
    private String mLastReply = "";
    private final String mUserid = Utils.get("ro.serialno");
    private static BeeSearchParams mSearchParamInstance;
    private BeeSearchVideoResult mVideoInfo;
    private boolean mIsSearch = false;


    public static BeeSearchParams getInstance() {
        if (mSearchParamInstance == null) {
            mSearchParamInstance = new BeeSearchParams();
        }
        return mSearchParamInstance;
    }

    public void setLastReply(String last) {
        this.mLastReply = last;
    }


    public String getLastReply() {
        return this.mLastReply;
    }

    public String getLocalcallid() {
        return this.mLocalcallid;
    }

    public void setLocalcallid(String localcallid) {
        this.mLocalcallid = localcallid;
    }

    public String getUserid() {
        return mUserid;
    }

    public void setMetasInfo(BeeSearchVideoResult videoInfo) {
        this.mVideoInfo = videoInfo;
    }

    public BeeSearchVideoResult getMetasInfo() {
        return mVideoInfo;
    }

    public boolean isInSearchPage() {
        return mIsSearch;
    }

    public void setIsInSearch(boolean search) {
        this.mIsSearch = search;
    }
}
