package com.skyworthdigital.voice.tencent_module.domains.train;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SDT03046 on 2018/8/14.
 */

public class TrainInfoItem implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("fromStation")
    public String mFromStation;

    @SerializedName("toStation")
    public String mToStation;

    @SerializedName("trainNum")
    public String mTrainNum;

    @SerializedName("toTime")
    public String mtoTime;

    @SerializedName("fromTime")
    public String mfromTime;

    @SerializedName("seats")
    public List<SeatItem> mSeats;

    @SerializedName("useTime")
    public int mUseTime;

    public class SeatItem implements Serializable {
        public static final long serialVersionUID = 1L;

        @SerializedName("price")
        public float mPrice;

        @SerializedName("remainNum")
        public int mRemainNum;

        @SerializedName("seatName")
        public String mSeatName;
    }
}