package com.skyworthdigital.voice.baidu_module.util;

import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.net.URLEncoder;

import cn.fengmang.assistant.asrlib.service.SSRService;


public class ReportUtils {

    public static void report2Smartmovie(String sn,String deviceid,String speech)
    {
        try {
            String url = "http://smartmovie.skyworthbox.com/SmartTianmai/api/reportspeech?usrid="+ sn+"&deviceid="+deviceid+"&txt=";
            url = url + URLEncoder.encode(speech, "utf-8");
            MLog.d("Report", ""+url);
            String response = "";
            String response_jni = SSRService.httpGet(url, response);
            MLog.d("Report","response_jni:" + response_jni);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 判断该字符串是否为中文
     */
    private static boolean isChinese(String str) {
        if (str.length() >= 1) {
            int n = (int) str.charAt(0);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

}
