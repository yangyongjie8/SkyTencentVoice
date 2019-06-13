package com.skyworthdigital.voice.tencent_module.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SDT03046 on 2018/7/24.
 */

public class Slot implements Serializable {

    public static final long serialVersionUID = 1L;

    @SerializedName("name")
    public String mName;

    @SerializedName("type")
    public String mType;

    @SerializedName("values")
    public List<ValueItem> mValueList;


}