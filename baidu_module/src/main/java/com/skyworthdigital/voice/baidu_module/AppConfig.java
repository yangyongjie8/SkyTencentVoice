package com.skyworthdigital.voice.baidu_module;

public class AppConfig {
    public static final String APP_CONFIGFILE = "sdcard/baidu/duersdk/sdkconfig.txt";

    /**
     * 百度释放的appid 和 appkey
     * 原始公共的是：
     * "appid": "dm0CA5A2AF18B344BF"
     * "appkey" : "3719E9B3D97F4785ABA531FBFCA63225"
     **/
    static final String PAI_APPID = "dm0C51376D05DC41FF";
    static final String PAI_APPKEY = "0B22785EF15C4416A661F93576DF505D";

    static final String AUDIOBOX_APPID = "dm3AD31974C0264781";
    static final String AUDIOBOX_APPKEY = "3810B6847C70390A29838A1398161CC";

    public static String getAppid(boolean isAudiobox) {
        if (isAudiobox) {
            return AUDIOBOX_APPID;
        } else {
            return PAI_APPID;
        }
    }

    public static String getAppkey(boolean isAudiobox) {
        if (isAudiobox) {
            return AUDIOBOX_APPKEY;
        } else {
            return PAI_APPKEY;
        }
    }
}
