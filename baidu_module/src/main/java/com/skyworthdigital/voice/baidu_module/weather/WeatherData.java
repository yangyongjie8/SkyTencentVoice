package com.skyworthdigital.voice.baidu_module.weather;

import java.util.ArrayList;
import java.util.List;

/**
 * 天气Data字段
 * Created by SDT13227 on 2017/5/26.
 */

public class WeatherData {
    private String city;
    private String current_temp;
    private String pm25;
    private String temp;
    private String time;
    private String weather;
    private String weather_all;
    private String wind;

    private List<WeatherInfo> weather_info = new ArrayList<WeatherInfo>();

    String getWeather() {
        return weather;
    }

    String getCity() {
        return city;
    }

    String getCurrent_temp() {
        return current_temp;
    }

    public String getPm25() {
        return pm25;
    }

    public String getTemp() {
        return temp;
    }

    public String getTime() {
        return time;
    }

    public String getWeather_all() {
        return weather_all;
    }

    public String getWind() {
        return wind;
    }

    public List<WeatherInfo> getWeather_info() {
        return weather_info;
    }


    //@Override
    //public String toString() {
    //    return getSearchWord();
    //}
}
