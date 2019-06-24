package com.skyworthdigital.voice.baidu_module.music;

import com.skyworthdigital.voice.baidu_module.duerbean.DuerBean;
import com.skyworthdigital.voice.baidu_module.duerbean.Nlu;
import com.skyworthdigital.voice.baidu_module.duerbean.Result;
import com.skyworthdigital.voice.baidu_module.duerbean.Slots;
import com.skyworthdigital.voice.baidu_module.util.GsonUtils;


public class MusicInfo {
    private String hmm_singer;
    private String hmm_song;
    private String singer;
    private String song;
    private String unit;
    private String sort_type;
    private String hmm_top_name;
    private String hmm_unit;
    private String top_name;
    private String song_type;

    public MusicInfo(String result) {
        try {
            DuerBean duerBean = GsonUtils.getDuerBean(result);
            if (duerBean != null) {
                Result tempResult = duerBean.getResult();
                if (tempResult != null) {
                    Nlu tempNlu = tempResult.getNlu();
                    if (tempNlu != null) {
                        Slots slots = tempNlu.getSlots();
                        if (slots != null) {
                            hmm_singer = slots.getHmm_singer();
                            hmm_song = slots.getHmm_song();
                            singer = slots.getSinger();
                            song = slots.getSong();
                            unit = slots.getUnit();
                            sort_type = slots.getSortType();
                            hmm_top_name = slots.getHmm_top_name();
                            hmm_unit = slots.getHmm_unit();
                            top_name = slots.getTop_name();
                            song_type = slots.getTag();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getHmm_singer() {
        return hmm_singer;
    }

    private String getHmm_song() {
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

    public String getTop_name() {
        return top_name;
    }
    public String getSort_type() {
        return sort_type;
    }

    public String getType() {
        return song_type;
    }

    @Override
    public String toString() {
        return "hmm_singer:" + getHmm_singer() + "|hmm_song:"
                + getHmm_song() + "|singer:" + singer + "|song:"
                + song + "|unit:" + getUnit() + "|type:" + getSort_type()
                + hmm_top_name + "|hmm_singer:" + hmm_unit + "|hmm_song:" + top_name + "|song_type:" + getType();
    }
}