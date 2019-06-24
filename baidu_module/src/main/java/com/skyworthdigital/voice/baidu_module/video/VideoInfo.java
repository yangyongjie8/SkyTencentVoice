package com.skyworthdigital.voice.baidu_module.video;


import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONObject;


public class VideoInfo {
    private String mUrl;
    private String mTitle;
    private String mIntroduction;
    //private String mArtist;

    public VideoInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject resource = jsonObject.getJSONObject("result").getJSONObject("resource").getJSONObject("data");
            mUrl = resource.getJSONObject("media_resources").getString("video_url");
            mIntroduction = resource.getString("introduction");
            mTitle = resource.getString("title");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        MLog.i("VideoInfo", "getUrl:" + mUrl);
        return mUrl;
    }

    public String getIntroduction() {
        return mIntroduction;
    }

    public String getTitle() {
        return mTitle;
    }

}