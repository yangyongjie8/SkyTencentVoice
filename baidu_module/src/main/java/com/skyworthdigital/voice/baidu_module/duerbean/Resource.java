package com.skyworthdigital.voice.baidu_module.duerbean;


import com.google.gson.annotations.SerializedName;

/**
 * Resource字段
 * Created by SDT03046 on 2017/5/26.
 */

public class Resource {
    @SerializedName("data")
    private Object mData;

    @SerializedName("type")
    private String mType;


    public String getType() {
        return mType;
    }

    public Object getData() {
        return mData;
    }

    @Override
    public String toString() {
        return "type:" + mType + "|data:" + mData;
    }
}
