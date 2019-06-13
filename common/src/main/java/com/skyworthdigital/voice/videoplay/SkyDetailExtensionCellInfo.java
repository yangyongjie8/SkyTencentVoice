package com.skyworthdigital.voice.videoplay;


import com.skyworthdigital.voice.globalcmd.Action;

public class SkyDetailExtensionCellInfo {
    private int id;
    private String title;
    private String imgUrl;
    private int tvod;
    private int orderIndex;
    private String type;
    private int vip;
    private int cornerMarkType;
    private Action action;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getTvod() {
        return tvod;
    }

    public void setTvod(int tvod) {
        this.tvod = tvod;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getCornerMarkType() {
        return cornerMarkType;
    }

    public void setCornerMarkType(int cornerMarkType) {
        this.cornerMarkType = cornerMarkType;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
