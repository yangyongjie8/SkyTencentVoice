package com.skyworthdigital.voice.tianmai;

/**
 * 天脉应用演示意图
 * Created by Ives 2019/1/8
 */
public final class TianmaiIntent {
    private boolean turnOn;//是否启用
    private String name;
    private String matchRegex;
    private String voiceContent;

    public TianmaiIntent(String name, boolean turnOn, String matchRegex) {
        this.turnOn = turnOn;
        this.name = name;
        this.matchRegex = matchRegex;
    }

    public TianmaiIntent(String name, String matchRegex) {
        this.turnOn = true;
        this.name = name;
        this.matchRegex = matchRegex;
    }

    public boolean isTurnOn() {
        return turnOn;
    }

    public void setTurnOn(boolean turnOn) {
        this.turnOn = turnOn;
    }

    public String getVoiceContent() {
        return voiceContent;
    }

    public void setVoiceContent(String voiceContent) {
        this.voiceContent = voiceContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatchRegex() {
        return matchRegex;
    }

    public void setMatchRegex(String matchRegex) {
        this.matchRegex = matchRegex;
    }

    @Override
    public String toString() {
        return "TianmaiIntent{" +
                "turnOn=" + turnOn +
                ", name='" + name + '\'' +
                ", matchRegex='" + matchRegex + '\'' +
                ", voiceContent='" + voiceContent + '\'' +
                '}';
    }
}
