package com.skyworthdigital.voice.tencent_module.domains.sports;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class TeamStatVecObj implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("lostMatchCount")
    public int mLostMatchCount;

    @SerializedName("winMatchCount")
    public int mWinMatchCount;

    @SerializedName("teamLogo")
    public String mTeamLogo;

    @SerializedName("teamName")
    public String mTeamName;

    @SerializedName("rank")
    public String mRank;

    @SerializedName("competition")
    public String mCompetition;

    @SerializedName("score")
    public String mScore;

    public String mTitle;

}
