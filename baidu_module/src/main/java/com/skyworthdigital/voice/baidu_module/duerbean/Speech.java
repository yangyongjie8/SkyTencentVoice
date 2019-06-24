package com.skyworthdigital.voice.baidu_module.duerbean;

import com.google.gson.annotations.SerializedName;

/**
 * Speech字段
 * Created by SDT13227 on 2017/5/26.
 */

public class Speech {
    @SerializedName("type")
    private String mType;

    @SerializedName("content")
    private String mContent;

    public String getType() {
        return mType;
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public String toString() {
        return "type:" + mType + "|content:" + mContent;
    }
}
