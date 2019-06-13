package com.skyworthdigital.voice.beesearch;


import com.skyworthdigital.voice.videoplay.SkyVideoInfo;

import java.util.List;

/**
 * Created by SDT03046 on 2018/6/6.
 */

public class BeeSearchVideoResult {

    private int totalpage;
    private int total;
    private int curpage;
    private List<SkyVideoInfo> videolist;
    private int labels;
    private int rsltcode;

    public int getCurpage() {
        return curpage;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public int getRsltcode() {
        return rsltcode;
    }

    public int getTotal() {
        return total;
    }

    public List<SkyVideoInfo> getVideolist() {
        return videolist;
    }

    public int getLabels() {
        return labels;
    }
}