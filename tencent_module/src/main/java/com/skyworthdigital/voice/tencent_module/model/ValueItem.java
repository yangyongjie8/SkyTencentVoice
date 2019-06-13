package com.skyworthdigital.voice.tencent_module.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2018/7/24.
 */

public class ValueItem implements Serializable {

    public static final long serialVersionUID = 1L;

    @SerializedName("original_text")
    public String mOriginalText;

    @SerializedName("text")
    public String mText;

    @SerializedName("number_type")
    public int mNumber;

    @SerializedName("integer")
    public String mInteger;

    @SerializedName("fraction")
    public String mPercent;

    @SerializedName("ordinal")
    public String mOrdinal;

    @SerializedName("unit")
    public String mUnit;

    @SerializedName("amount")
    public Amount mAmount;

    @SerializedName("repeat")
    public Repeat mRepeat;

    @SerializedName("datetime")
    public DateTime mDateTime;

    /*"original_text": "一小时20分钟",
    "amount": {
                    "decimal": "",
                    "fraction": "",
                    "integer": "4800",
                    "number_type": 3,
                    "ordinal": "",
                    "original_text": ""
                  },
     */
    public class Amount implements Serializable {
        public static final long serialVersionUID = 1L;

        @SerializedName("decimal")
        public String mDecimal;

        @SerializedName("integer")
        public String mInteger;
    }

    public class Repeat implements Serializable {
        public static final long serialVersionUID = 1L;

        @SerializedName("repeat_datetime_type")
        public int mType;//1:day 3:week

        @SerializedName("interval")
        public Interval mInterval;

        public class Interval implements Serializable {
            public static final long serialVersionUID = 1L;

            @SerializedName("end")
            public IntervalTime mEnd;

            @SerializedName("start")
            public IntervalTime mStart;

            public class IntervalTime implements Serializable {
                public static final long serialVersionUID = 1L;
                @SerializedName("year")
                public int mYear;

                @SerializedName("mon")
                public int mMon;

                @SerializedName("day")
                public int mDay;

                @SerializedName("hour")
                public int mHour;

                @SerializedName("min")
                public int mMin;

                @SerializedName("sec")
                public int mSec;
            }
        }
    }

    public class DateTime implements Serializable {
        public static final long serialVersionUID = 1L;
        @SerializedName("year")
        public int mYear;

        @SerializedName("mon")
        public int mMon;

        @SerializedName("day")
        public int mDay;

        @SerializedName("hour")
        public int mHour;

        @SerializedName("min")
        public int mMin;

        @SerializedName("sec")
        public int mSec;
    }
}