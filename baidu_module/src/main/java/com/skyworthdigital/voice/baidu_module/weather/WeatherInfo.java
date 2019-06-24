package com.skyworthdigital.voice.baidu_module.weather;


public class WeatherInfo {
    private String current_temp;
    private String pm25;
    private String icon;
    private String pm_level;
    private String temp;
    private String time;
    private String weather;
    private String wind;

    String getCurrent_temp() {
        return current_temp;
    }

    public String getPm25() {
        return pm25;
    }

    public String getIcon() {
        return icon;
    }

    public String getPm_level() {
        return pm_level;
    }

    String getTemp() {
        return temp;
    }

    public String getTime() {
        return time;
    }

    String getWeather() {
        return weather;
    }

    public String getWind() {
        return wind;
    }

    @Override
    public String toString() {
        return "icon:" + icon + "|temp:" + temp + "|time:" + time + "|weather:" + weather + "|wind:" + wind;
    }
}
