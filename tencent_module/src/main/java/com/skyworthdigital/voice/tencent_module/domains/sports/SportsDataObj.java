package com.skyworthdigital.voice.tencent_module.domains.sports;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class SportsDataObj implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("awayTeam")
    public AwayTeam mAwayTeam;

    @SerializedName("homeTeam")
    public HomeTeam mHomeTeam;

    @SerializedName("period")
    public String mPeriod;

    @SerializedName("roundType")
    public String mRoundType;

    @SerializedName("sportsStartTime")
    public String mSportsStartTime;

    @SerializedName("competition")
    public String mCompetition;

    public class AwayTeam implements Serializable {
        public static final long serialVersionUID = 1L;
        public String teamLogo;
        public String teamName;
        public String teamGoal;
    }

    public class HomeTeam implements Serializable {
        public static final long serialVersionUID = 1L;
        public String teamLogo;
        public String teamName;
        public String teamGoal;
    }
}
