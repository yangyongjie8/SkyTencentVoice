package com.skyworthdigital.voice.videoplay;

/**
 * Created by SDT03046 on 2017/8/17.
 */

public class CollectionPersonInfo {
    private int performerId;
    private String name;
    private String picUrl;
    private boolean isDirector = false;
    private int isUploaded = 0; // 是否已经上传服务器 default 0:已经上传  1:上传失败/未上传

    public int getPerformerId() {
        return performerId;
    }

    public void setPerformerId(int performerId) {
        this.performerId = performerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public boolean isDirector() {
        return isDirector;
    }

    public void setDirector(boolean isDirector) {
        this.isDirector = isDirector;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }
}

