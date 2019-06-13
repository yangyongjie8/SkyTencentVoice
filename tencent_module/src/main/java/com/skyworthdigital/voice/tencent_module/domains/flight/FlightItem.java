package com.skyworthdigital.voice.tencent_module.domains.flight;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SDT03046 on 2018/8/14.
 */

public class FlightItem implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("lArriveTimestamp")
    public long mArriveTimestamp;

    @SerializedName("lDepartTimestamp")
    public long mDepartTimestamp;

    @SerializedName("sDestination")
    public Destination mDestination;

    @SerializedName("sCraftInfo")
    public CraftInfo mCraftInfo;

    @SerializedName("sOrigin")
    public OriginInfo mOrigin;

    @SerializedName("strCompanyName")
    public String mCompanyName;

    @SerializedName("strFlightNo")
    public String mFlightNo;

    @SerializedName("strIconUrl")
    public String mIconUrl;

    @SerializedName("vPolicyInfoList")
    public List<PolicyInfoItem> mPolicyInfoList;

    public class CraftInfo implements Serializable {
        public static final long serialVersionUID = 1L;

        @SerializedName("strCraftCode")
        public String mCraftCode;

        @SerializedName("strCraftName")
        public String mCraftName;

        @SerializedName("strKind")
        public String mKind;
    }

    public class Destination implements Serializable {
        public static final long serialVersionUID = 1L;

        @SerializedName("sAirport")
        public Airport mAirport;
        @SerializedName("strTerminal")
        public String mTerminal;
    }

    public class OriginInfo implements Serializable {
        public static final long serialVersionUID = 1L;
        @SerializedName("sAirport")
        public Airport mAirport;

        @SerializedName("strTerminal")
        public String mTerminal;
    }

    public class Airport implements Serializable {
        public static final long serialVersionUID = 1L;

        @SerializedName("strAirportName")
        public String mAirportName;

        @SerializedName("strCityName")
        public String mCityName;

        @SerializedName("strCraftName")
        public String mCraftName;

        @SerializedName("strKind")
        public String mKind;
    }

    public class PolicyInfoItem implements Serializable {
        public static final long serialVersionUID = 1L;

        @SerializedName("iPrice")
        public int mPrice;

        @SerializedName("iRemainCount")
        public int mRemainCount;

        @SerializedName("strKind")
        public String mKind;

        @SerializedName("fDiscountRate")
        public float mDiscountRate;

        @SerializedName("vClassInfoList")
        public List<ClassInfoItem> mClassInfoList;

    }

    public class ClassInfoItem implements Serializable {
        public static final long serialVersionUID = 1L;
        @SerializedName("strClassName")
        public String mClassName;

        @SerializedName("strSubClass")
        public String mSubClass;
    }
}