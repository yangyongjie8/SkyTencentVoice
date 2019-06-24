package com.skyworthdigital.voice.baidu_module.fm;

import android.text.TextUtils;

import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONArray;
import org.json.JSONObject;


public class AudioInfo {
    private String mStream_format;
    private String mUrl;
    private int mProgress_report_interval_ms;
    private int mOffset;
    private String mTitle;
    private String mToken;
    //private String mArtist;

    public AudioInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            Object jsonData = jsonObject.getJSONObject("result").getJSONArray("directives").get(0);
            JSONObject audioObject = ((JSONObject) jsonData).getJSONObject("payload").getJSONObject("audio_item").getJSONObject("stream");
            mStream_format = audioObject.getString("stream_format");
            mUrl = audioObject.getString("url");
            mProgress_report_interval_ms = audioObject.getInt("progress_report_interval_ms");
            mToken = audioObject.getString("token");
            mOffset = audioObject.getInt("offset_ms");
            MLog.i("AudioInfo", "data:" + jsonData.toString());
            MLog.i("AudioInfo","audioObject:" + audioObject.toString());
            JSONObject mediaInfo = jsonObject.getJSONObject("result").getJSONObject("resource").getJSONObject("data").getJSONObject("media");
            //mTitle = mediaInfo.getString("title");
            JSONArray artist = mediaInfo.getJSONArray("artist");

            StringBuilder sb = new StringBuilder();
            sb.append(mediaInfo.getString("title"));

            if (artist.length() > 0) {
                sb.append(" 演播:");
                for (int i = 0; i < artist.length(); i++) {
                    sb.append((String) artist.get(i));
                    if (i < artist.length() - 1) {
                        sb.append(",");
                    }
                }
            }
            String ret = sb.toString();
            if (TextUtils.isEmpty(ret)) {
                mTitle = "";
            } else {
                mTitle = ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getOffset() {
        MLog.i("AudioInfo","mOffset:" + mOffset);
        return mOffset;
    }

    public String getStream_format() {
        return mStream_format;
    }

    public String getUrl() {
        MLog.i("AudioInfo","getUrl:" + mUrl);
        return mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getProgress_report_interval_ms() {
        return mProgress_report_interval_ms;
    }

    public String getToken() {
        return mToken;
    }
}