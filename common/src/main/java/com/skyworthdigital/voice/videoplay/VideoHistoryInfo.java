package com.skyworthdigital.voice.videoplay;

import com.google.gson.Gson;

public class VideoHistoryInfo {
    private int userId;
    private int infoId;
    private int videoId;
    private String otherId;
    private String pid;
    private String vid;
    private String videoName;
    private String episodeDesc;
    private int sourceId;
    private String sourceName;
    private int playedDrama;
    private int playedDuration;
    private String picUrl;
    private float score;
    private long time;// 观看的系统时间
    private int duration;
    private int infosTotal;
    private int subscript = 0;
    private float doubanScore;
    private int del = 0; // 0:正常 1:服务器端已删除
    private int isUploaded = 0; // 是否已经上传服务器 default 0:已经上传  1:上传失败/未上传

    private boolean hasUpdate = false;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getInfoId() {
        return infoId;
    }

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getEpisodeDesc() {
        return episodeDesc;
    }

    public void setEpisodeDesc(String episodeDesc) {
        this.episodeDesc = episodeDesc;
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

    public int getPlayedDrama() {
        return playedDrama;
    }

    public void setPlayedDrama(int playedDrama) {
        this.playedDrama = playedDrama;
    }

    public int getPlayedDuration() {
        return playedDuration;
    }

    public void setPlayedDuration(int playedDuration) {
        this.playedDuration = playedDuration;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getInfosTotal() {
        return infosTotal;
    }

    public void setInfosTotal(int infosTotal) {
        this.infosTotal = infosTotal;
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

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public int getIsUploaded() {
        return isUploaded;
    }

    /**
     * @param isUploaded  0:upload successed  1:upload false
     * */
    public void setIsUploaded(int isUploaded) {
        this.isUploaded = isUploaded;
    }

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    public SkyVideoSubInfo buildSkyVideoSubInfo() {
        SkyVideoSubInfo videoInfo = new SkyVideoSubInfo();
        try {
            videoInfo.setInfoid(getInfoId());
            videoInfo.setVideoId(getVideoId());
            videoInfo.setOtherId(getOtherId());
            videoInfo.setPid(getPid());
            videoInfo.setVid(getVid());
            if (getSubscript() == 1) {
                videoInfo.setIsVip(1);
            }
            else if (getSubscript() == 2) {
                videoInfo.setIsTvod(1);
            }
            videoInfo.setName(getVideoName());
            videoInfo.setPlayDuration(getPlayedDuration());
            videoInfo.setOrderIndex(getPlayedDrama());
            videoInfo.setDuration(getDuration());
            videoInfo.setPicUrl(getPicUrl());
            videoInfo.setName(getVideoName());
            return videoInfo;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SkyVideoInfo buildSkyVideoInfo() {
        SkyVideoInfo videoInfo = new SkyVideoInfo();
        try {
            videoInfo.setVideoId(getVideoId());
            videoInfo.setPicUrl(getPicUrl());
            videoInfo.setName(getVideoName());
            return videoInfo;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public boolean isSameAs(VideoHistoryInfo info) {
        if (getVideoId() == info.getVideoId()
            && getSubscript() == info.getSubscript()
            && getScore() == info.getScore()
            && getDoubanScore() == info.getDoubanScore()
            && getPicUrl().equals(info.getPicUrl())) {
            return true;
        }
        return false;
    }

    public void copy(VideoHistoryInfo info) {
        setVideoId(info.getVideoId());
        setInfoId(info.getInfoId());
        setPlayedDrama(info.getPlayedDrama());
        setPlayedDuration(info.getPlayedDuration());
        setScore(info.getScore());
        setTime(info.getTime());
        setDuration(info.getDuration());
        setInfosTotal(info.getInfosTotal());
        setVid(info.getVid());
        setOtherId(info.getOtherId());
        setPid(info.getPid());
        setEpisodeDesc(info.getEpisodeDesc());
        setPicUrl(info.getPicUrl());
    }*/

}
