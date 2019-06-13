package com.skyworthdigital.voice.videoplay;


import com.google.gson.Gson;

/**
 * Created by SDT03046 on 2017/8/17.
 */

public class CollectionVideoInfo {
    private int userId;
    private int videoId;
    private String videoName;
    private int sourceId;
    private String sourceName;
    private String picUrl;
    private float score;
    private long time;
    private String duration;
    private int resolutionType;
    private String updateText;
    private int totalCount;
    private int subscript = 0;
    private float doubanScore;
    private int del = 0; // 0:正常  1:服务器端已删除
    private int isUploaded = 0; // 是否已经上传服务器 default 0:已经上传  1:上传失败/未上传
    private int isFinish; //0:not end  1:is end
    private String seriesText;
    private int isSummary = 0; // 1:show 0:default
    private String summary;
    private boolean hasUpdate = false;
    private String otherId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getSubscript() {
        return subscript;
    }

    public void setSubscript(int subscript) {
        this.subscript = subscript;
    }

    public float getDoubanScore() {
        return doubanScore;
    }

    public void setDoubanScore(float doubanScore) {
        this.doubanScore = doubanScore;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getResolutionType() {
        return resolutionType;
    }

    public void setResolutionType(int resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    public String getSeriesText() {
        return seriesText;
    }

    public void setSeriesText(String seriesText) {
        this.seriesText = seriesText;
    }

    public int getIsSummary() {
        return isSummary;
    }

    public void setIsSummary(int isSummary) {
        this.isSummary = isSummary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public SkyVideoInfo transToSkyVideoInfo() {
        SkyVideoInfo skyVideoInfo = new SkyVideoInfo();
        skyVideoInfo.setVideoId(getVideoId());
        return skyVideoInfo;
    }
}


