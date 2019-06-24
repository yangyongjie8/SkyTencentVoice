package com.skyworthdigital.voice.baidu_module.duerbean;

import com.google.gson.annotations.SerializedName;

/**
 * 度秘语音识别后的json返回字段
 * Created by SDT13227 on 2017/5/26.
 */
public class DuerBean {
    @SerializedName("result")
    private Result mResult;

    @SerializedName("se_query")
    private String mOriginSpeech;

    public Result getResult() {
        return mResult;
    }

    public String getOriginSpeech() {
        return mOriginSpeech;
    }

}