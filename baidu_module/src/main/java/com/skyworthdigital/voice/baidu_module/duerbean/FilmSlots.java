package com.skyworthdigital.voice.baidu_module.duerbean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.io.Serializable;

/**
 * 视频搜索支持的类型，同时格式转换成我们需要的
 */

public class FilmSlots implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 电影类型：古装
     */
    @SerializedName("film_type")
    private String mFilmType;

    /**
     * 地区：韩国
     */
    @SerializedName("film_area")
    private String mFilmArea;
    /**
     * 上映时间：年
     */
    @SerializedName("time_slot")
    private String mTimeSlot;
    /**
     * 排序类型：高分
     */
    @SerializedName("sort_type")
    private String mSortType;

    /**
     * 电影名：琅琊榜
     */
    @SerializedName("film")
    private String mFilm;

    /**
     * 是否免费播放
     */
    @SerializedName("is_free")
    private String mIsFree;

    public void setType(String type) {
        if (!TextUtils.isEmpty(type) && type.contains(" ")) {
            this.mType = null;
        } else {
            this.mType = type;
        }
    }

    public void setPersonName(String person_name) {
        if (!TextUtils.isEmpty(person_name) && person_name.contains(" ")) {
            this.mPersonName = person_name.replace(" ", ",");
        } else {
            this.mPersonName = person_name;
        }
    }

    public void setActor(String actor) {
        if (!TextUtils.isEmpty(actor) && actor.contains(" ")) {
            this.mActor = actor.replace(" ", ",");
        } else {
            this.mActor = actor;
        }
    }

    public void setDirector(String director) {
        if (!TextUtils.isEmpty(director) && director.contains(" ")) {
            this.mDirector = director.replace(" ", ",");
        } else {
            this.mDirector = director;
        }
    }

    public void setFilmType(String film_type) {
        this.mFilmType = film_type;
    }

    public void setFilmArea(String film_area) {
        this.mFilmArea = film_area;
    }

    //"time_slot":"2017-1-1 0:0:0,2017-12-31 23:59:0"
    public void setTimeSlotYear(String time_slot) {
        if (TextUtils.isEmpty(time_slot)) {
            this.mTimeSlot = time_slot;
        } else {
            int tempPos = time_slot.indexOf("-");
            if (tempPos > 0) {
                this.mTimeSlot = time_slot.substring(0, tempPos);
            } else {
                this.mTimeSlot = time_slot;
            }
        }
    }

    public void setSortType(String sort_type) {
        if (!TextUtils.isEmpty(sort_type)) {
            if (TextUtils.equals(sort_type, "近期")) {
                this.mSortType = null;
            } else {
                String[] splitStr = sort_type.split(" ");
                this.mSortType = null;
                for (int i = 0; i < splitStr.length; i++) {
                    if (!splitStr[i].equals("近期")) {
                        if (this.mSortType != null) {
                            this.mSortType = this.mSortType + "," + splitStr[i];
                        } else {
                            this.mSortType = splitStr[i];
                        }
                    }
                }
            }
        } else {
            this.mSortType = sort_type;
        }
    }

    public void setFilm(String film) {
        this.mFilm = film;
    }

    public String getFilm() {
        return this.mFilm;
    }
    public void setIs_free(String is_free) {
        this.mIsFree = is_free;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(mType)) {
            sb.append("type:");
            sb.append(mType);
        }
        if (!TextUtils.isEmpty(mPersonName)) {
            sb.append(" person_name:");
            sb.append(mPersonName);
        }
        if (!TextUtils.isEmpty(mActor)) {
            sb.append(" actor:");
            sb.append(mActor);
        }
        if (!TextUtils.isEmpty(mDirector)) {
            sb.append(" director:");
            sb.append(mDirector);
        }
        if (!TextUtils.isEmpty(mFilmType)) {
            sb.append(" film_type:");
            sb.append(mFilmType);
        }
        if (!TextUtils.isEmpty(mFilmArea)) {
            sb.append(" film_area:");
            sb.append(mFilmArea);
        }
        if (!TextUtils.isEmpty(mSortType)) {
            sb.append(" sort_type:");
            sb.append(mSortType);
        }
        if (!TextUtils.isEmpty(mIsFree)) {
            sb.append(" is_free:");
            sb.append(mIsFree);
        }
        if (!TextUtils.isEmpty(mTimeSlot)) {
            sb.append(" time_slot:");
            sb.append(mTimeSlot);
        }
        if (!TextUtils.isEmpty(mFilm)) {
            sb.append(" film:");
            sb.append(mFilm);
        }

        String result = sb.toString();
        if (TextUtils.isEmpty(result)) {
            result = "";
        }
        MLog.d("FilmSlots", "FilmSlots:" + result);
        return result;
    }
}
