package com.skyworthdigital.voice.tencent_module.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2018/7/23.
 */

public class TemplateItem implements Serializable {

    public static final long serialVersionUID = 1L;
    @SerializedName("eDataType")
    public int  mDataType;

    @SerializedName("strDescription")
    public String mDescription;

    @SerializedName("strTitle")
    public String mTitle;

    @SerializedName("strDestURL")
    public String mDestURL;


    @SerializedName("strContentURL")
    public String mContentURL;

    @SerializedName("strContentID")
    public String mContentID;
}