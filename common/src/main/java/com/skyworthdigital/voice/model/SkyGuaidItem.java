package com.skyworthdigital.voice.model;

/**
 * Created by SDT03046 on 2018/6/6.
 */

public class SkyGuaidItem {
    private Double weight;
    private String txt;
    private String domainid;
    int mColor;

    public SkyGuaidItem(String tag, double weight) {
        this.weight = weight;
        this.txt = tag;
    }

    public SkyGuaidItem(String tag, double weight, int color) {
        this.weight = weight;
        this.txt = tag;
        this.mColor = color;
    }

    public String getTxt() {
        return txt;
    }

    public double getWeight() {
        return weight;
    }

    public String getDomainid() {
        return domainid;
    }
}
