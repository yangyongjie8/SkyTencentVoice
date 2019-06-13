package com.skyworthdigital.voice.tencent_module.domains.train;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2018/8/15.
 */

public class TrainParams implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("startDate")
    public String mDate;

    @SerializedName("fromName")
    public String mFrom;

    @SerializedName("toName")
    public String mTo;
}
