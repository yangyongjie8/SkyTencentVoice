package com.skyworthdigital.voice.tencent_module;

import com.google.gson.annotations.SerializedName;
import com.skyworthdigital.voice.tencent_module.domains.flight.FlightItem;
import com.skyworthdigital.voice.tencent_module.domains.flight.TicketParams;
import com.skyworthdigital.voice.tencent_module.domains.sports.SportsDataObj;
import com.skyworthdigital.voice.tencent_module.domains.sports.TeamStatVecObj;
import com.skyworthdigital.voice.tencent_module.domains.train.TrainInfoItem;
import com.skyworthdigital.voice.tencent_module.domains.train.TrainParams;
import com.skyworthdigital.voice.tencent_module.model.BaikeVideoItem;
import com.skyworthdigital.voice.tencent_module.model.DataItem;
import com.skyworthdigital.voice.tencent_module.model.SemanticJson;
import com.skyworthdigital.voice.tencent_module.model.TemplateItem;
import com.skyworthdigital.voice.tencent_module.model.TvProgramsItem;

import java.io.Serializable;
import java.util.List;


public class AsrResult implements Serializable {

    public static final long serialVersionUID = 1L;
    @SerializedName("answer")
    public String mAnswer;//有屏幕设备播报文本

    @SerializedName("service")
    public String mDomain;

    @SerializedName("query")
    public String mQuery;

    @SerializedName("template")
    public List<TemplateItem> mTemplates;

    @SerializedName("semantic_json")
    public SemanticJson mSemanticJson;

    @SerializedName("tips")
    public String mTips;//播报提示

    @SerializedName("server_ret")
    public int mServerRet;//服务器状态

    @SerializedName("session")
    public boolean mSession;//会话是否结束

    @SerializedName("rc")
    public int mReturnCode;//返回码，0 为正常，其他为错误

    @SerializedName("data")
    public AsrData mData;

    public class AsrData implements Serializable {
        public static final long serialVersionUID = 1L;
        @SerializedName("sKeyWord")
        public String mKeyWord;

        @SerializedName("sPicUrl")
        public String mPicUrl;

        @SerializedName("sBaikeInfo")
        public String mBaikeInfo;

        @SerializedName("jokeText")
        public String mJokeText;

        @SerializedName("strTipsText")
        public String mTipsText;

        @SerializedName("sportsdataObjs")
        public List<SportsDataObj> mSportsdataObjs;

        @SerializedName("sportsRecords")
        public List<SportsRecordObj> mSportsRecords;

        @SerializedName("sportsScores")
        public List<SportsScoreObj> mSportsScores;

        @SerializedName("vStatistics")
        public List<VStatisticsObj> mVStatistics;

        @SerializedName("vecTvProgramsList")
        public List<TvProgramsObj> mTvProgramsList;

        @SerializedName("stBaikeVideo")
        public BaikeVideo mBaikeVideo;

        @SerializedName("vCardItems")
        public List<CardItem> mCardItems;

        @SerializedName("data")
        public List<DataItem> mData;

        @SerializedName("vFlightList")
        public List<FlightItem> mFlightList;

        @SerializedName("trainInfos")
        public List<TrainInfoItem> mTrainInfos;

        @SerializedName("sQueryParams")
        public TicketParams mTicketParams;

        @SerializedName("queryParams")
        public TrainParams mTrainParams;

        @SerializedName("vIdiomCell")
        public List<IdiomCell> mIdiomCell;

        @SerializedName("vecCityWeatherInfo")
        public List<CityWeatherInfoItem> mCityWeatherInfo;

        public class SportsRecordObj implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("group")
            public String mGroup;

            @SerializedName("competition")
            public String mCompetition;

            @SerializedName("season")
            public String mSeason;

            @SerializedName("teamStatVec")
            public List<TeamStatVecObj> mTeamStatVec;

        }

        public class SportsScoreObj implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("awayTeam")
            public SportsDataObj.AwayTeam mAwayTeam;

            @SerializedName("homeTeam")
            public SportsDataObj.HomeTeam mHomeTeam;

            @SerializedName("period")
            public String mPeriod;

            @SerializedName("roundType")
            public String mRoundType;

            @SerializedName("sportsStartTime")
            public String mSportsStartTime;

            @SerializedName("gifLinks")
            public List<GifLinkObj> mGifLinks;

            @SerializedName("competition")
            public String mCompetition;

            public class GifLinkObj implements Serializable {
                public static final long serialVersionUID = 1L;
                public String dateTime;
                public String href;
                public String title;
            }
        }

        public class VStatisticsObj implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("sCnName")
            public String mCnName;

            @SerializedName("sValue")
            public String mValue;
        }

        public class CardItem implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("sLabel")
            public String mLabel;

            @SerializedName("sValue")
            public String mValue;
        }

        public class TvProgramsObj implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("vecTvProgramsItem")
            public List<TvProgramsItem> mTvProgramsItems;
        }

        public class IdiomCell implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("sLemma")
            public String mLemma;

            @SerializedName("sPinYin")
            public String mPinYin;

            @SerializedName("sResult")
            public String mResult;
        }

        public class CityWeatherInfoItem implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("vBgImg")
            public List<BgImgCell> mBgImg;

            public class BgImgCell implements Serializable {
                public static final long serialVersionUID = 1L;

                @SerializedName("sImg")
                public String mImg;
            }
        }

        public class BaikeVideo implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("vBaikeVideo")
            public List<BaikeVideoItem> mVBaikeVideo;
        }

        public String getIntroduction() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mCardItems.size(); i++) {
                sb.append(mCardItems.get(i).mLabel);
                sb.append(":");
                sb.append(mCardItems.get(i).mValue);
                if (i < mCardItems.size() - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }
}