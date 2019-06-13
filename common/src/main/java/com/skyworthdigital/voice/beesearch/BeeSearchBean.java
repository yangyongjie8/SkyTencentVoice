package com.skyworthdigital.voice.beesearch;

import com.skyworthdigital.voice.model.SkyGuaidItem;

import java.util.List;

/**
 * Created by SDT03046 on 2018/6/6.
 */

public class BeeSearchBean {
    private String output;
    private int total;
    private List<String> userguaid;
    private String resultstr;
    private String tts;
    private List<SkyGuaidItem> weigteduserguaid;

    private String abnf;
    private String callid;
    private String localcallid;
    private int rsltcode;

    public String getTts() {
        return tts;
    }

    public int getRsltcode() {
        return rsltcode;
    }

    public int getTotal() {
        return total;
    }

    public List<SkyGuaidItem> getWeighteduserguaid() {
        return weigteduserguaid;
    }

    public String getAbnf() {
        return abnf;
    }

    public String getCallid() {
        return callid;
    }

    public String getLocalcallid() {
        return localcallid;
    }

    public String getOutput() {
        return output;
    }

    public String getResultstr() {
        return resultstr;
    }

    public List<String> getUserguaid() {
        return userguaid;
    }
}
