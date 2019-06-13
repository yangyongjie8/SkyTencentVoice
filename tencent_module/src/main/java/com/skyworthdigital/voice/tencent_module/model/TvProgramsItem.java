package com.skyworthdigital.voice.tencent_module.model;

/**
 * Created by SDT03046 on 2018/8/6.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TvProgramsItem implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("strChLogo")
    public String mChLogo;

    @SerializedName("strChannel")
    public String mChannelName;

    @SerializedName("strWeekPrint")
    public String mWeekPrint;

    @SerializedName("strEndTime")
    public String mEndTime;

    @SerializedName("strTVName")
    public String mTVName;

    @SerializedName("strTime")
    public String mStartTime;

    @SerializedName("strDate")
    public String mDate;

    @SerializedName("strThumb")
    public String mThumb;

    @SerializedName("iIsPlaying")
    public int mIsPlaying;

    @SerializedName("iHot")
    public int iHot;

    public boolean isTitle=false;
}
