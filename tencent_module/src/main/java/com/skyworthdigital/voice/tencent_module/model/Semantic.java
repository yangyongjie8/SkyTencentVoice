package com.skyworthdigital.voice.tencent_module.model;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.music.musictype.MusicSlots;

import java.io.Serializable;
import java.util.List;

/**
 * Created by SDT03046 on 2018/7/24.
 */

public class Semantic implements Serializable {

    public static final long serialVersionUID = 1L;

    @SerializedName("domain")
    public String mDomain;

    @SerializedName("intent")
    public String mIntent;

    @SerializedName("query")
    public String mQuery;

    @SerializedName("slots")
    public List<Slot> mSlots;

    public static final int INVALID_DIGIT = 0xffff;

    public FilmSlots getSearchKeyWords() {
        FilmSlots filmSlots = new FilmSlots();

        if (TextUtils.equals("search_tvseries", mIntent)) {
            filmSlots.setType("电视剧");
        } else if (TextUtils.equals("search_film", mIntent)) {
            filmSlots.setType("电影");
        } else if (TextUtils.equals("search_show", mIntent)) {
            filmSlots.setType("综艺");
        } else if (TextUtils.equals("search_cartoon", mIntent)) {
            filmSlots.setType("动画片");
        } else {
            filmSlots.setType("电影");
        }
        try {
            if (mSlots != null) {
                for (Slot temp : mSlots) {
                    switch (temp.mName) {
                        case "actor":
                            filmSlots.setActor(temp.mValueList.get(0).mText);
                            break;
                        case "type":
                            filmSlots.setFilmType(temp.mValueList.get(0).mText);
                            break;
                        case "director":
                            filmSlots.setDirector(temp.mValueList.get(0).mText);
                            break;
                        case "area":
                            filmSlots.setFilmArea(temp.mValueList.get(0).mText);
                            break;
                        case "language":
                            //filmSlots.setL(temp.mValueList.get(0).mText);
                            break;
                        case "film":
                            filmSlots.setFilm(temp.mValueList.get(0).mText);
                            break;
                        case "tvseries":
                            filmSlots.setFilm(temp.mValueList.get(0).mText);
                            break;
                        case "number":
                            //filmSlots.setL(temp.mValueList.get(0).mText);
                            break;
                        case "ranking":
                            filmSlots.setSortType(temp.mValueList.get(0).mText);
                            break;
                        case "time":
                            filmSlots.setYear(temp.mValueList.get(0).mText);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.i("semantic", e.toString());
        }
        return filmSlots;
    }

    public float getIndex() {
        if (mSlots != null && mSlots.size() > 0) {
            for (Slot temp : mSlots) {
                switch (temp.mName) {
                    case "digit":
                        if (temp.mValueList.get(0).mText.contains("%")) {
                            return Float.valueOf(StringUtils.getStringNumbers(temp.mValueList.get(0).mText)) / 100;
                        }
                        return Integer.parseInt(temp.mValueList.get(0).mText);
                    case "ordinal":
                    case "index_ordinal":
                        if (!TextUtils.isEmpty(temp.mValueList.get(0).mOrdinal)) {
                            if (!TextUtils.isEmpty(StringUtils.getStringNumbers(temp.mValueList.get(0).mOrdinal))) {
                                return Integer.parseInt(StringUtils.getStringNumbers(StringUtils.getStringNumbers(temp.mValueList.get(0).mOrdinal)));
                            }
                        }
                        if (!TextUtils.isEmpty(temp.mValueList.get(0).mOriginalText)) {
                            int num = StringUtils.chineseNumber2Int(temp.mValueList.get(0).mOriginalText);
                            if (num != 0) {
                                return num;
                            }
                        }
                        break;
                    case "number":
                        String strnum = StringUtils.getStringNumbers(temp.mValueList.get(0).mText);
                        if (!TextUtils.isEmpty(strnum)) {
                            return Integer.parseInt(strnum);
                        }
                        if (!TextUtils.isEmpty(temp.mValueList.get(0).mOriginalText)) {
                            int num = StringUtils.chineseNumber2Int(temp.mValueList.get(0).mOriginalText);
                            return num;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return INVALID_DIGIT;
    }

    public int getTimeLocation() {
        try {
            int digit = -1;
            int unit = -1;
            if (mSlots != null && mSlots.size() > 0) {
                for (Slot temp : mSlots) {
//                    switch (temp.mName) {
//                        case "moment_location":
                            if (!TextUtils.isEmpty(temp.mValueList.get(0).mUnit)) {
                                if (TextUtils.equals(temp.mValueList.get(0).mUnit, "秒")) {
                                    unit = 1;
                                } else if (TextUtils.equals(temp.mValueList.get(0).mUnit, "分")) {
                                    unit = 60;
                                } else if (TextUtils.equals(temp.mValueList.get(0).mUnit, "小时")) {
                                    unit = 3600;
                                }
                            }
                            if (!TextUtils.isEmpty(temp.mValueList.get(0).mAmount.mInteger)) {
                                return unit * Integer.parseInt(temp.mValueList.get(0).mAmount.mInteger);
                            } else if (!TextUtils.isEmpty(temp.mValueList.get(0).mAmount.mDecimal)) {
                                return (int) (unit * Float.parseFloat(temp.mValueList.get(0).mAmount.mDecimal));
                            }
                            return Semantic.INVALID_DIGIT;//Integer.parseInt(temp.mValueList.get(0).mAmount);
//                        default:
//                            break;
//                    }
                }
                if (digit > 0) {
                    if (unit > 0) {
                        return digit * unit;
                    } else {
                        return digit;
                    }
                }
            }
        } catch (Exception e) {
            Log.i("semantic", e.toString());
        }
        return INVALID_DIGIT;
    }

    public int getDuration() {
        try {
            int digit = -1;
            int unit = -1;
            if (mSlots != null && mSlots.size() > 0) {
                for (Slot temp : mSlots) {
                    switch (temp.mName) {
                        case "moment":
                        case "duration":
                            if (!TextUtils.isEmpty(temp.mValueList.get(0).mUnit))
                                if (TextUtils.equals(temp.mValueList.get(0).mUnit, "秒")) {
                                    unit = 1;
                                } else if (TextUtils.equals(temp.mValueList.get(0).mUnit, "分")) {
                                    unit = 60;
                                } else if (TextUtils.equals(temp.mValueList.get(0).mUnit, "小时")) {
                                    unit = 3600;
                                }
                            if (!TextUtils.isEmpty(temp.mValueList.get(0).mAmount.mInteger)) {
                                return unit * Integer.parseInt(temp.mValueList.get(0).mAmount.mInteger);
                            } else if (!TextUtils.isEmpty(temp.mValueList.get(0).mAmount.mDecimal)) {
                                return (int) (unit * Float.parseFloat(temp.mValueList.get(0).mAmount.mDecimal));
                            }
                            return 0xffff;//Integer.parseInt(temp.mValueList.get(0).mAmount);
                        case "digit":
                            digit = Integer.parseInt(temp.mValueList.get(0).mText);
                            break;
                        case "unit":
                            if (TextUtils.equals(temp.mValueList.get(0).mText, "min")) {
                                unit = 60;
                            } else if (TextUtils.equals(temp.mValueList.get(0).mText, "h")) {
                                unit = 3600;
                            } else if (TextUtils.equals(temp.mValueList.get(0).mText, "s")) {
                                unit = 1;
                            }
                        default:
                            break;
                    }
                }
                if (digit > 0) {
                    if (unit > 0) {
                        return digit * unit;
                    } else {
                        return digit;
                    }
                }
            }
        } catch (Exception e) {
            Log.i("semantic", e.toString());
        }
        return INVALID_DIGIT;
    }

    public MusicSlots getMusicSlots() {
        MusicSlots musicSlots = new MusicSlots();

        //if (TextUtils.equals("music", mDomain)) {
            try {
                if (mSlots != null && mSlots.size() > 0) {
                    for (Slot temp : mSlots) {
                        switch (temp.mName) {
                            case "singer":
                                musicSlots.mSinger = temp.mValueList.get(0).mText;
                                break;
                            case "song":
                                musicSlots.mSong = temp.mValueList.get(0).mText;
                                break;
                            case "toplist":
                            case "emotion":
                            case "instrument":
                            case "scene":
                            case "theme":
                            case "age":
                            case "style":
                            case "language":
                                if (!TextUtils.isEmpty(temp.mValueList.get(0).mText)) {
                                    musicSlots.mType.add(temp.mValueList.get(0).mText);
                                }
                                if (!TextUtils.isEmpty(temp.mValueList.get(0).mOriginalText) && !TextUtils.equals(temp.mValueList.get(0).mOriginalText, temp.mValueList.get(0).mText)) {
                                    musicSlots.mType.add(temp.mValueList.get(0).mOriginalText);
                                }
                                break;
                            case "album":
                                musicSlots.mAlbum = temp.mValueList.get(0).mOriginalText;
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("semantic", e.toString());
            }
        //}
        return musicSlots;
    }


    public ValueItem getTvliveSlots() {
        if (TextUtils.equals("tv", mDomain)) {
            try {
                if (mSlots != null) {
                    for (Slot temp : mSlots) {
                        switch (temp.mName) {
                            case "channel":
                                if (temp.mValueList != null && temp.mValueList.size() > 0) {
                                    return temp.mValueList.get(0);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.i("semantic", e.toString());
            }
        }
        return null;
    }
}
