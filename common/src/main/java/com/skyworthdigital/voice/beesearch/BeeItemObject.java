package com.skyworthdigital.voice.beesearch;

import java.util.ArrayList;
import java.util.List;


public class BeeItemObject {
    private String word;
    private String operationCode;
    private String output;
    private String tts;
    private List<String> speaksame;
    public BeeCommand cmd;
    public ArrayList<AmObject> am;
    public int videoId;
    public int infoindex;

    public int[] score = new int[4];

    public BeeCommand getCmd() {
        return cmd;
    }

    public List<String> getSpeaksame() {
        return speaksame;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public String getOutput() {
        return output;
    }

    public String getWord() {
        return word;
    }

    public String getTts() {
        return tts;
    }

    public String speaksameToString() {
        StringBuilder sb = new StringBuilder();
        int idx = 1;

        for (String temp : speaksame) {
            sb.append(idx);
            sb.append(".");
            sb.append(temp);
            if (idx < speaksame.size()) {
                sb.append("、");
            }
            idx++;
        }
        return sb.toString();
    }

    public static class AmObject {
        public String content;
        public String op;
    }
}
