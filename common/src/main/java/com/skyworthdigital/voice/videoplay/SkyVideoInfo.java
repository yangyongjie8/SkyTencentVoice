package com.skyworthdigital.voice.videoplay;

import java.io.Serializable;

/**
 * Created by SDT03046 on 2017/8/17.
 */

public class SkyVideoInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3218220353864267031L;
    private int videoId; // videoId，取详情时需要
    private int mergedId; // 合并id, 播放时上报统计
    private String name;
    private String picUrl;
    private int channelId;
    private float doubanScore; // 豆瓣评分
    private float qmzScore; // 视频源评分
    private int sourceId; // 视频源ID
    private int isTvod;
    private String otherId;
    private int subscript; // 角标类型，0无角标，1 vip，2点播
    private int isFinish; //0:not end  1:is end
    private int isSeries = 1;// 1:videoSet, click to detail  0:video, click to play
    private String seriesText;
    private int infoOrderIndex = 1;
    private String pid;
    private String vid;
    private int timeLength; //duration 1581
    private String issueTime;
    private long issueTimeStamp;
    private int isVerticalPic = 1; //1:vertical，0:horizontal
    private String drm;
    private int isSummary = 0; // 1:show 0:default
    private String summary;
    private int isHistoryPlay = 1;
    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getMergedId() {
        return mergedId;
    }

    public void setMergedId(int mergedId) {
        this.mergedId = mergedId;
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

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public float getDoubanScore() {
        return doubanScore;
    }

    public void setDoubanScore(float doubanScore) {
        this.doubanScore = doubanScore;
    }

    public float getQmzScore() {
        return qmzScore;
    }

    public void setQmzScore(float qmzScore) {
        this.qmzScore = qmzScore;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getIsTvod() {
        return isTvod;
    }

    public void setIsTvod(int isTvod) {
        this.isTvod = isTvod;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    public int getSubscript() {
        return subscript;
    }

    public void setSubscript(int subscript) {
        this.subscript = subscript;
    }

    public int getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(int isSeries) {
        this.isSeries = isSeries;
    }

    public String getSeriesText() {
        return seriesText;
    }

    public void setSeriesText(String seriesText) {
        this.seriesText = seriesText;
    }

    public int getInfoOrderIndex() {
        return infoOrderIndex;
    }

    public void setInfoOrderIndex(int infoOrderIndex) {
        this.infoOrderIndex = infoOrderIndex;
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

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(String issueTime) {
        this.issueTime = issueTime;
    }

    public long getIssueTimeStamp() {
        return issueTimeStamp;
    }

    public void setIssueTimeStamp(long issueTimeStamp) {
        this.issueTimeStamp = issueTimeStamp;
    }

    public int getIsVerticalPic() {
        return isVerticalPic;
    }

    public void setIsVerticalPic(int isVerticalPic) {
        this.isVerticalPic = isVerticalPic;
    }

    public String getDrm() {
        return drm;
    }

    public void setDrm(String drm) {
        this.drm = drm;
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

    public int getIsHistoryPlay() {
        return isHistoryPlay;
    }

    public void setIsHistoryPlay(int isHistoryPlay) {
        this.isHistoryPlay = isHistoryPlay;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    // 褰变汉鍗曚釜浣滃搧 涓?鍒楄〃鍗曚釜褰辫 淇℃伅鐨勫尯鍒?
    private int year;
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    /*public Cell transToCell() {
        Cell cell = new Cell();
        cell.setId("" + getVideoId());
        cell.setTitle(getName());
//        cell.setDescription(description);
        cell.setImgUrl(getPicUrl());
//        cell.setIconRes(iconRes);
        cell.setRow(getY());
        cell.setColumn(getX());
        cell.setRowSize(getH());
        cell.setColumnSize(getW());
        return cell;
    }*/

    /*public SkyVideoSubInfo transToSkyVideoSubInfo() {
        SkyVideoSubInfo subInfo = new SkyVideoSubInfo();
        // 鍒楄〃鐩存帴璺宠浆鎾斁锛岃祫璁被鍒楄〃鍙戦€佸脊骞曪紙鍥犱负杞崲鍚巌nfoId涓?锛夊鑷存墍鏈夎浆鎹㈠悗璺宠浆鎾斁閮借兘
        // 鏄剧ず璇ユ潯寮瑰箷锛屾殏鏃惰缃负-1涓嶄綔澶勭悊銆?
        subInfo.setInfoid(-1);
        subInfo.setName(getName());
        subInfo.setVideoId(getVideoId());
        subInfo.setOtherId(getOtherId());
        subInfo.setVid(getVid());
        subInfo.setPid(getPid());
        subInfo.setPicUrl(getPicUrl());
        subInfo.setOrderIndex(getInfoOrderIndex());
        subInfo.setDrm(getDrm());
        return subInfo;
    }*/
}

