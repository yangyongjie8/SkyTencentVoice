package com.skyworthdigital.voice.tencent_module.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2018/8/10.
 */

public class DataItem implements Serializable {
    public static final long serialVersionUID = 1L;

    @SerializedName("appreciation")
    public String mAppreciation;

    @SerializedName("author")
    public String mAuthor;

    @SerializedName("content")
    public String mContent;

    @SerializedName("dynasty")
    public String mDynasty;

    @SerializedName("interpretation")
    public String mInterpretation;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("url")
    public String mHtml;

    @SerializedName("voice")
    public String mDestURL;
}