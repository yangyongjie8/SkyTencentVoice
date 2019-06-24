package com.skyworthdigital.voice.baidu_module.duerbean;

import com.google.gson.annotations.SerializedName;

/**
 * Result字段
 */
public class Result {
    @SerializedName("nlu")
    private Nlu mNlu;

    @SerializedName("speech")
    private Speech mSpeech;

    @SerializedName("resource")
    private Resource mResource;

    public Nlu getNlu() {
        return mNlu;
    }

    public Speech getSpeech() {
        return mSpeech;
    }

    public Resource getresource() {
        return mResource;
    }
}