package com.skyworthdigital.voice.common.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class DeviceUtil {

    public static String getMacAddress(Context context){
        //wifi mac地址，6.0手机不适用
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);//需要权限：android.permission.READ_PHONE_STATE
        WifiInfo info = wifi.getConnectionInfo();
        String wifiMac = info.getMacAddress();
        return wifiMac==null?"":wifiMac;
    }
}
