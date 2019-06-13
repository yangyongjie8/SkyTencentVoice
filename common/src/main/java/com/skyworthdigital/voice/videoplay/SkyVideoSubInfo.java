package com.skyworthdigital.voice.videoplay;

import java.io.Serializable;

public class SkyVideoSubInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5328280738623710792L;
    private int infoid; // 子集id:2322
    private int videoId;
    private String otherId;
    private String pid; // 90430900
    private String vid; // 1502c8a6fe8511dfaa6aa4badb2c35a1
    private String name;
    private int playDuration;
    private int orderIndex;
    private int duration;
    private int isVip;
    private int isTvod;
    private int isCoupon;
    private int price;
    private String picUrl;
    private int isClip;
    private String year;
    private String summary;
    private String drm;

    public int getInfoid() {
        return infoid;
    }

    public void setInfoid(int infoid) {
        this.infoid = infoid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }

    public int getIsTvod() {
        return isTvod;
    }

    public void setIsTvod(int isTvod) {
        this.isTvod = isTvod;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public int getIsCoupon() {
        return isCoupon;
    }

    public void setIsCoupon(int isCoupon) {
        this.isCoupon = isCoupon;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getIsClip() {
        return isClip;
    }

    public void setIsClip(int isClip) {
        this.isClip = isClip;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDrm() {
        return drm;
    }

    public void setDrm(String drm) {
        this.drm = drm;
    }

}
