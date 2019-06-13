package com.skyworthdigital.voice.tianmai;

import java.util.List;

/**
 * 从蜜蜂视频接口获取的天气信息
 * Created by Ives 2019/1/9
 */
public class WeatherBee {
//             "wind": {
//        "wind_ji": "2级",
//                "wind_type": "东风"
//    },
//            "zhishu": [
//            "白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。"
//            ]
    private String shidu;//湿度，举例：50%
    private String weatherType;//天气类型，举例：阴
    private String city;
    private String high_t;//最高温度，举例：19
    private String low_t;//最低温度
    private String quality;//空气质量，举例：较差
    private String province;//城市
    private String pm25;
    private String temperature;//当前温度
    private String aqi;
    private String weatherTypeCode;
    private Object wind;//风向风力，是个json object，暂不处理
    private List<String> zhishu;//指数描述，举例：白天不太热也不太冷，相信您在这样的天气条件下，……

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(String weatherType) {
        this.weatherType = weatherType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHigh_t() {
        return high_t;
    }

    public void setHigh_t(String high_t) {
        this.high_t = high_t;
    }

    public String getLow_t() {
        return low_t;
    }

    public void setLow_t(String low_t) {
        this.low_t = low_t;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getWeatherTypeCode() {
        return weatherTypeCode;
    }

    public void setWeatherTypeCode(String weatherTypeCode) {
        this.weatherTypeCode = weatherTypeCode;
    }

    public Object getWind() {
        return wind;
    }

    public void setWind(Object wind) {
        this.wind = wind;
    }

    public List<String> getZhishu() {
        return zhishu;
    }

    public void setZhishu(List<String> zhishu) {
        this.zhishu = zhishu;
    }

    @Override
    public String toString() {
        return "WeatherBee{" +
                "shidu='" + shidu + '\'' +
                ", weatherType='" + weatherType + '\'' +
                ", city='" + city + '\'' +
                ", high_t='" + high_t + '\'' +
                ", low_t='" + low_t + '\'' +
                ", quality='" + quality + '\'' +
                ", province='" + province + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", temperature='" + temperature + '\'' +
                ", aqi='" + aqi + '\'' +
                ", weatherTypeCode='" + weatherTypeCode + '\'' +
                ", wind='" + wind + '\'' +
                ", zhishu=" + zhishu +
                '}';
    }
}
