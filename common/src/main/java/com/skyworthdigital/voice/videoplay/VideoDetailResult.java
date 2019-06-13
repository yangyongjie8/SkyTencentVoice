package com.skyworthdigital.voice.videoplay;


/**
 * Created by SDT03046 on 2017/8/17.
 */

public class VideoDetailResult {

    private String msg;
    private int status;
    private SkyVideoDetailInfo data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public SkyVideoDetailInfo getData() {
        return data;
    }

    public void setData(SkyVideoDetailInfo data) {
        this.data = data;
    }

}

