package com.skyworthdigital.voice.beesearch;

import java.util.List;

/**
 * Created by smartrobot on 2018/2/5.
 */

public class SpeakSameInfo {
    private List<String> speaksame;
    private String lastText;
    private String keyword;

    public SpeakSameInfo() {

    }

    public SpeakSameInfo(List<String> speaksame, String lastText, String keyword) {
        this.speaksame = speaksame;
        this.lastText = lastText;
        this.keyword = keyword;
    }

    public void setSpeaksame(List<String> speaksame) {
        this.speaksame = speaksame;
    }

    public List<String> getSpeaksame() {
        return speaksame;
    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }

    public String getLastText() {
        return lastText;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
