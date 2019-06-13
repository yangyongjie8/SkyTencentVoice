package com.skyworthdigital.voice.videoplay;


import java.util.List;

/**
 * Created by SDT03046 on 2017/8/17.
 */

public class SkyVideoDetailInfo {
    private int videoId;
    private String picUrl;
    private String bigPic;
    private String name; // 半路兄弟",
    private String annotation; // 简介
    private int chnId; // 1
    private String chnName; // 电视剧
    private String areaId; // ":1,
    private String areaName; // ":"内地",
    private long screenTime; // 上映时间 1271577016000
    private int year; // 年份
    private String performer; // 演员 如：张丰毅,李强,柯蓝
    private String director; // 导演
    private int sourceId; // 源ID
    private String otherId; // 第三方视频详情ID（列如爱奇艺详情ID）
    private String duration;
    private float doubanScore;
    private float qmzScore;
    private int isFinish; // 是否完结，0否，1是
    private int isVip; // 0否，1是
    private int isTvod; // 是否点播视频，0否，1是
    private int price;
    private List<String> picList; // 剧照
    private String validTime;// 有效时间
    private String sourceName; // 源名称，如爱奇艺
    private int episodeTotal; // 总集
    private int episodeLast; // 当前更新最新集数
    private int infosTotal; // 实际子集总数
    private int infosTotalPage; // 3
    private List<SkyVideoSubInfo> infos; // 子集列表
    private String cateName;
    private List<CollectionPersonInfo> directors;
    private List<CollectionPersonInfo> performers;
    private int detailType = 1; // 1:vertical 2:vertical_poster horizontical_sub 3:horizontical
    private String infoPeriod;
    private int isPeriod = 0; // 1:qi 0:ji
    private String horizontalPicUrl;
    private String verticalPicUrl;

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getBigPic() {
        return bigPic;
    }

    public void setBigPic(String bigPic) {
        this.bigPic = bigPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public int getChnId() {
        return chnId;
    }

    public void setChnId(int chnId) {
        this.chnId = chnId;
    }

    public String getChnName() {
        return chnName;
    }

    public void setChnName(String chnName) {
        this.chnName = chnName;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public long getScreenTime() {
        return screenTime;
    }

    public void setScreenTime(long screenTime) {
        this.screenTime = screenTime;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<String> getPicList() {
        return picList;
    }

    public void setPicList(List<String> picList) {
        this.picList = picList;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public int getEpisodeTotal() {
        return episodeTotal;
    }

    public void setEpisodeTotal(int episodeTotal) {
        this.episodeTotal = episodeTotal;
    }

    public int getEpisodeLast() {
        return episodeLast;
    }

    public void setEpisodeLast(int episodeLast) {
        this.episodeLast = episodeLast;
    }

    public int getInfosTotal() {
        return infosTotal;
    }

    public void setInfosTotal(int infosTotal) {
        this.infosTotal = infosTotal;
    }

    public int getInfosTotalPage() {
        return infosTotalPage;
    }

    public void setInfosTotalPage(int infosTotalPage) {
        this.infosTotalPage = infosTotalPage;
    }

    public List<SkyVideoSubInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<SkyVideoSubInfo> infos) {
        this.infos = infos;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public List<CollectionPersonInfo> getDirectors() {
        return directors;
    }

    public void setDirectors(List<CollectionPersonInfo> directors) {
        this.directors = directors;
    }

    public List<CollectionPersonInfo> getPerformers() {
        return performers;
    }

    public void setPerformers(List<CollectionPersonInfo> performers) {
        this.performers = performers;
    }

    public int getDetailType() {
        return detailType;
    }

    public void setDetailType(int detailType) {
        this.detailType = detailType;
    }

    public String getInfoPeriod() {
        return infoPeriod;
    }

    public void setInfoPeriod(String infoPeriod) {
        this.infoPeriod = infoPeriod;
    }

    public int getIsPeriod() {
        return isPeriod;
    }

    public void setIsPeriod(int isPeriod) {
        this.isPeriod = isPeriod;
    }

    public String getHorizontalPicUrl() {
        return horizontalPicUrl;
    }

    public void setHorizontalPicUrl(String horizontalPicUrl) {
        this.horizontalPicUrl = horizontalPicUrl;
    }

    public String getVerticalPicUrl() {
        return verticalPicUrl;
    }

    public void setVerticalPicUrl(String verticalPicUrl) {
        this.verticalPicUrl = verticalPicUrl;
    }

    public void addOtherSubInfos(List<SkyVideoSubInfo> otherSubInfos) {
        if (infos != null) {
            infos.addAll(otherSubInfos);
        }
        else {
            infos = otherSubInfos;
        }
    }

    public SkyVideoSubInfo getFirstSkyVideoSubInfo() {
        SkyVideoSubInfo currentVideoSubInfo = null;
        if (infos != null && infos.size() > 0) {
            currentVideoSubInfo = infos.get(0);
        }
        return currentVideoSubInfo;
    }

    public SkyVideoSubInfo getSkyVideoSubInfoByIndex(int playIndex) {
        SkyVideoSubInfo currentVideoSubInfo = getCurrentVideoSubInfo(playIndex);
        return currentVideoSubInfo;
    }

    private SkyVideoSubInfo getCurrentVideoSubInfo(int playIndex) {
        for (SkyVideoSubInfo subInfo : getInfos()) {
            if (subInfo != null && subInfo.getOrderIndex() == playIndex) {
                return subInfo;
            }
        }
        return null;
    }

    public CollectionVideoInfo buildCollectionVideoInfo() {
        CollectionVideoInfo collectionVideoInfo = new CollectionVideoInfo();
        try {
            collectionVideoInfo.setVideoId(getVideoId());
            collectionVideoInfo.setVideoName(getName());
            collectionVideoInfo.setSourceId(getSourceId());
            collectionVideoInfo.setSourceName(getSourceName());
            collectionVideoInfo.setPicUrl(getPicUrl());
            collectionVideoInfo.setDoubanScore(getDoubanScore());
            collectionVideoInfo.setScore(getQmzScore());
            int subscript = 0;
            if (getIsVip() == 1) {
                subscript = 1;
            }
            else if (getIsTvod() == 1) {
                subscript = 2;
            }
            collectionVideoInfo.setSubscript(subscript);
            collectionVideoInfo.setTime(System.currentTimeMillis());
            collectionVideoInfo.setDuration(duration);
            collectionVideoInfo.setUpdateText("");
            collectionVideoInfo.setTotalCount(getEpisodeLast());
            return collectionVideoInfo;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


