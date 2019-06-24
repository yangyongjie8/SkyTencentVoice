package com.skyworthdigital.voice.baidu_module.duerbean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Slots字段
 * Created by SDT13227 on 2017/5/26.
 */

public class Slots {

    /**
     * 类型：电影、电视剧
     */
    @SerializedName("type")
    private String mType;

    /**
     * 相关人物：成龙 导演：张艺谋
     */
    @SerializedName("person_name")
    private String mPersonName;

    /**
     * 演员：成龙
     */
    @SerializedName("actor")
    private String mActor;

    /**
     * 导演：张艺谋
     */
    @SerializedName("director")
    private String mDirector;

    /**
     * 动作：搜索、播放
     */
    @SerializedName("action_type")
    private String mActionType;

    /**
     * 电影类型：古装
     */
    @SerializedName("film_type")
    private String mFilmType;

    /**
     * 文件标签：内地
     */
    @SerializedName("file_tag")
    private String mFileTag;

    /**
     * 地区：韩国
     */
    @SerializedName("film_area")
    private String mFilmArea;

    /**
     * 上映时间：2013-01-01,2013-12-31
     */
    @SerializedName("time_slot")
    private String mTimeSlot;

    /**
     * 排序类型：高分
     */
    @SerializedName("sort_type")
    private String mSortType;

    /**
     * 剧集：第一集
     */
    @SerializedName("whepisode")
    private String mWhepisode;

    /**
     * 电影名：琅琊榜
     */
    @SerializedName("film")
    private String mFilm;

    /**
     * 角色：好莱坞
     */
    @SerializedName("tv_role")
    private String mTvRole;

    /**
     * 电视台
     */
    @SerializedName("tv_station")
    private String mTvStation;

    /**
     * 系列电影名
     */
    @SerializedName("series_film")
    private String mSeriesFilm;

    /**
     * 主持人
     */
    @SerializedName("presentor")
    private String mPresentor;

    /**
     * 标签
     */
    @SerializedName("film_tag")
    private String mFilmTag;

    /**
     * 描述性内容
     */
    @SerializedName("descriptor")
    private String mDescriptor;

    /**
     * 语言
     */
    @SerializedName("film_language")
    private String mFilmLanguage;

    /**
     * 是否即将上映
     */
    @SerializedName("pre_release")
    private String mPreRelease;

    /**
     * 是否免费播放
     */
    @SerializedName("is_free")
    private String mIsFree;

    /**
     * 是否正在热播
     */
    @SerializedName("release")
    private String mRelease;

    /**
     * 第几部
     */
    @SerializedName("whdepart")
    private String mWhdepart;

    /**
     * 奖项
     */
    @SerializedName("tv_award")
    private String mTvAward;

    /**
     * 后缀（部或集未知）
     */
    @SerializedName("whsuffix")
    private String mWhsuffix;

    /**
     * query关键词
     */
    @SerializedName("keyword")
    private String mKeyword;

    /**
     * 以下几个command命令会用到
     */
    @SerializedName("name")
    private String mName;

    @SerializedName("value")
    private Object mValue;

    @SerializedName("offset")
    private int mOffset;

    @SerializedName("time_point")
    private int mTimePoint;

    @SerializedName("col")
    private int mCol;//command.location 列

    @SerializedName("row")
    private int mRow;//command.location 行

    @SerializedName("re_col")
    private int mReCol;//command.location 倒数列

    @SerializedName("re_row")
    private int mReRow;//command.location 倒数行

    @SerializedName("num")
    private int mNumber;//第几个

    @SerializedName("index")
    private Object mMusicIdx;//第几个

    @SerializedName("re_episode")
    private int mReEpisode;//最后一集

    @SerializedName("channel_name")
    private String mChannelName;

    @SerializedName("skip_title")
    private String mSkipTitle;//跳过片头

    @SerializedName("episode")
    private String mEpisode;

    /**
     * 以下几个music会用到
     */
    private String hmm_singer;
    private String hmm_song;
    private String singer;
    private String song;
    private String unit;
    private String hmm_top_name;
    private String hmm_unit;
    private String top_name;
    private String tag;

    /**
     * 以下几个fm会用到
     */
    private String time;
    private String rewind;

    public String getRewind() {
        return rewind;
    }

    public String getTime() {
        return time;
    }

    public String getHmm_singer() {
        return hmm_singer;
    }

    public String getHmm_song() {
        return hmm_song;
    }

    public String getSinger() {
        return singer;
    }

    public String getSong() {
        return song;
    }

    public String getUnit() {
        return unit;
    }

    public String getHmm_top_name() {
        return hmm_top_name;
    }

    public String getTop_name() {
        return top_name;
    }

    public String getHmm_unit() {
        return hmm_unit;
    }

    public String getTag() {
        return tag;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getPersonname() {
        return mPersonName;
    }

    public void setPersonname(String person_name) {
        this.mPersonName = person_name;
    }

    public String getActor() {
        return mActor;
    }

    public void setActor(String actor) {
        this.mActor = actor;
    }

    public String getDirector() {
        return mDirector;
    }

    public void setDirector(String director) {
        this.mDirector = director;
    }

    public String getActionType() {
        return mActionType;
    }

    public void setAction_type(String action_type) {
        this.mActionType = action_type;
    }

    public String getFilmType() {
        return mFilmType;
    }

    public void setFilmType(String film_type) {
        this.mFilmType = film_type;
    }

    public String getFileTag() {
        return mFileTag;
    }

    public void setFileTag(String file_tag) {
        this.mFileTag = file_tag;
    }

    public String getFilmArea() {
        return mFilmArea;
    }

    public void setFilmArea(String film_area) {
        this.mFilmArea = film_area;
    }

    public String getTimeSlot() {
        return mTimeSlot;
    }

    public void setTimeSlot(String time_slot) {
        this.mTimeSlot = time_slot;
    }

    public String getSortType() {
        return mSortType;
    }

    public void setSortType(String sort_type) {
        this.mSortType = sort_type;
    }

    public String getWhepisode() {
        return mWhepisode;
    }

    public void setWhepisode(String whepisode) {
        this.mWhepisode = whepisode;
    }

    public String getFilm() {
        return mFilm;
    }

    public void setFilm(String film) {
        this.mFilm = film;
    }

    public void setTvRole(String tv_role) {
        this.mTvRole = tv_role;
    }

    public String getTvRole() {
        return mTvRole;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getChannelName() {
        return mChannelName;
    }

    public String getTvStation() {
        return mTvStation;
    }

    public void setTvStation(String tv_station) {
        this.mTvStation = tv_station;
    }

    public String getSeriesFilm() {
        return mSeriesFilm;
    }

    public void setSeriesFilm(String series_film) {
        this.mSeriesFilm = series_film;
    }

    public String getPresentor() {
        return mPresentor;
    }

    public void setPresentor(String presentor) {
        this.mPresentor = presentor;
    }

    public String getDescriptor() {
        return mDescriptor;
    }

    public void setDescriptor(String descriptor) {
        this.mDescriptor = descriptor;
    }

    public String getFilmLanguage() {
        return mFilmLanguage;
    }

    public void setFilm_language(String film_language) {
        this.mFilmLanguage = film_language;
    }

    public String getIsFree() {
        return mIsFree;
    }

    public void setIsFree(String is_free) {
        this.mIsFree = is_free;
    }

    public String getKeyword() {
        return mKeyword;
    }

    public void setKeyword(String keyword) {
        this.mKeyword = keyword;
    }

    public String getPreRelease() {
        return mPreRelease;
    }

    public void setPreRelease(String pre_release) {
        this.mPreRelease = pre_release;
    }

    public String getRelease() {
        return mRelease;
    }

    public void setRelease(String release) {
        this.mRelease = release;
    }

    public String getWhdepart() {
        return mWhdepart;
    }

    public void setWhdepart(String whdepart) {
        this.mWhdepart = whdepart;
    }

    public String getWhsuffix() {
        return mWhsuffix;
    }

    public void setWhsuffix(String whsuffix) {
        this.mWhsuffix = whsuffix;
    }

    public String getFilmTag() {
        return mFilmTag;
    }

    public void setFilmTag(String film_tag) {
        this.mFilmTag = film_tag;
    }

    public String getTvAward() {
        return mTvAward;
    }

    public void setTv_award(String tv_award) {
        this.mTvAward = tv_award;
    }

    public String getPersonName() {
        return mPersonName;
    }

    public void setPersonName(String person_name) {
        this.mPersonName = person_name;
    }

    public int getCol() {
        return mCol;
    }

    public int getRow() {
        return mRow;
    }

    public int getRe_col() {
        return mReCol;
    }

    public int getRe_row() {
        return mReRow;
    }

    public int getNumber() {
        return mNumber;
    }

    public int getReEpisode() {
        return mReEpisode;
    }

    public String getSkipTitle() {
        return mSkipTitle;
    }

    public Object getValue() {
        return mValue;
    }

    public Object getMusicIdx() {
        return mMusicIdx;
    }

    public int getOffset() {
        return mOffset;
    }

    public int getTimePoint() {
        return mTimePoint;
    }

    public String getEpisode() {
        return mEpisode;
    }

    public String getSearchWord() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mType)) {
            sb.append(",type:");
            sb.append(mType);
        }
        if (!TextUtils.isEmpty(mPersonName)) {
            sb.append(",person_name:");
            sb.append(mPersonName);
        }
        if (!TextUtils.isEmpty(mActor)) {
            sb.append(",actor:");
            sb.append(mActor);
        }
        if (!TextUtils.isEmpty(mDirector)) {
            sb.append(",director:");
            sb.append(mDirector);
        }
        if (!TextUtils.isEmpty(mFilmType)) {
            sb.append(",film_type");
            sb.append(mFilmType);
        }
        if (!TextUtils.isEmpty(mFileTag)) {
            sb.append(",file_tag:");
            sb.append(mFileTag);
        }
        if (!TextUtils.isEmpty(mFilmArea)) {
            sb.append(",film_area:");
            sb.append(mFilmArea);
        }
        if (!TextUtils.isEmpty(mTimeSlot)) {
            sb.append(",time_slot:");
            sb.append(mTimeSlot);
        }
        if (!TextUtils.isEmpty(mSortType)) {
            sb.append(",sort_type:");
            sb.append(mSortType);
        }
        if (!TextUtils.isEmpty(mWhepisode)) {
            sb.append(",whepisode:");
            sb.append(mWhepisode);
        }
        if (!TextUtils.isEmpty(mTvStation)) {
            sb.append(",tv_station:");
            sb.append(mTvStation);
        }
        if (!TextUtils.isEmpty(mSeriesFilm)) {
            sb.append(",series_film:");
            sb.append(mSeriesFilm);
        }
        if (!TextUtils.isEmpty(mPresentor)) {
            sb.append(",presentor:");
            sb.append(mPresentor);
        }
        if (!TextUtils.isEmpty(mFilmLanguage)) {
            sb.append(",film_language:");
            sb.append(mFilmLanguage);
        }
        if (!TextUtils.isEmpty(mIsFree)) {
            sb.append(",is_free:");
            sb.append(mIsFree);
        }
        if (!TextUtils.isEmpty(mWhdepart)) {
            sb.append(",whdepart:");
            sb.append(mWhdepart);
        }
        if (!TextUtils.isEmpty(mRelease)) {
            sb.append(",release:");
            sb.append(mRelease);
        }
        if (!TextUtils.isEmpty(mTvAward)) {
            sb.append(",tv_award:");
            sb.append(mTvAward);
        }
        if (!TextUtils.isEmpty(mFilm)) {
            sb.append(",film:");
            sb.append(mFilm);
        }
        if (!TextUtils.isEmpty(mTvRole)) {
            sb.append(",tv_role:");
            sb.append(mTvRole);
        }
        if (!TextUtils.isEmpty(mWhsuffix)) {
            sb.append(",whsuffix:");
            sb.append(mWhsuffix);
        }
        String result = sb.toString();
        if (!TextUtils.isEmpty(result)) {
            //result = result.trim();
            if (result.startsWith(",")) {
                result = result.substring(1);
            }
        } else {
            result = "";
        }
        return result;
    }

    public FilmSlots getFilmSlots() {
        FilmSlots filmSlots = new FilmSlots();
        filmSlots.setType(mType);
        if (TextUtils.isEmpty(mFilm)) {//不支持刘德华演的无间道这种组合
            filmSlots.setActor(mActor);
            filmSlots.setDirector(mDirector);
            filmSlots.setPersonName(mPersonName);
        }
        if (TextUtils.equals(tag, "评分很高")) {
            mSortType = "高分";
        } else if (!TextUtils.isEmpty(tag) && TextUtils.isEmpty(mFilm)) {
            mFilm = tag;
        }
        filmSlots.setFilm(mFilm);
        filmSlots.setFilmArea(mFilmArea);
        filmSlots.setFilmType(mFilmType);
        filmSlots.setIs_free(mIsFree);
        filmSlots.setTimeSlotYear(mTimeSlot);
        filmSlots.setSortType(mSortType);
        return filmSlots;
    }

    @Override
    public String toString() {
        return getSearchWord();
    }
}
