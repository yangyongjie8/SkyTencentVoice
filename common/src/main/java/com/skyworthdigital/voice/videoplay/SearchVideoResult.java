package com.skyworthdigital.voice.videoplay;


import java.util.List;

/**
 * Created by SDT03046 on 2017/8/17.
 */

public class SearchVideoResult {

    private int total;
    private List<SkyVideoInfo> rows;
    private List<ChannelSearchInfo> channelSearchInfos;
    private String footer;
    private String groups;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SkyVideoInfo> getRows() {
        return rows;
    }

    public void setRows(List<SkyVideoInfo> rows) {
        this.rows = rows;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public List<ChannelSearchInfo> getChannelSearchInfos() {
        return channelSearchInfos;
    }

    public void setChannelSearchInfos(List<ChannelSearchInfo> channelSearchInfos) {
        this.channelSearchInfos = channelSearchInfos;
    }
}

