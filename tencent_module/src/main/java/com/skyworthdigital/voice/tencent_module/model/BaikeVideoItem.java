package com.skyworthdigital.voice.tencent_module.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2018/8/10.
 */

public class BaikeVideoItem implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("sCover")
    public String mCover;

    @SerializedName("sUrl")
    public String mUrl;

    @SerializedName("sDuration")
    public String mDuration;
}