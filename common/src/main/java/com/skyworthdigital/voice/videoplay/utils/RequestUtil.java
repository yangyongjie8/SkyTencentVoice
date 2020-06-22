package com.skyworthdigital.voice.videoplay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.forest.bigdatasdk.httpdns.HttpDNS;
import com.skyworthdigital.voice.VoiceApp;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestUtil {

    //public static final String USER_HOST_URL = "account.skyworthbox.com"; // qnode.skyworthbox.com
    private static final String VIDEO_HOST_URL = "media.skyworthbox.com"; // qnode.skyworthbox.com
    //public static final String TMP_HOST_URL = "192.168.52.19:9000"; // qnode.skyworthbox.com  192.168.52.19:9000
    //public static final String SEARCH_HOST_URL = "search.skyworthbox.com"; // search.skyworthbox.com
    //public static final String LAUNCHER_HOST_URL = "qlauncher.skyworthbox.com"; // qnode.skyworthbox.com
    public static final String CDN_UPDATE_URL = "meta.cdn.skyworthbox.com";// "192.168.52.15";
    public static final String HTTP = "http://";
    public static final String VIDEO_DETAIL_ACTION2 = "/videoApi/api2.0/videoDetail.action";
    private static final String CHANNEL_ID = "qmz";
    private static final String SP_NAME = "launcher";
    private static final String IS_CDN = "isCDN";
//    public static final String SEARCH_CHANNEL_ID = "search";
    public static final String SEARCH_CHANNEL_ID = "mifeng";

    private static String handleByHttpDns(String originalUrl) {
        Log.i("RequestUtil","originalUrl = " + originalUrl);
        originalUrl = HttpDNS.getInstance()
                .replaceHost(originalUrl);
        return originalUrl;
    }

    public static String getHttpHost(String originalUrl) {
        try {
            URL url = new URL(originalUrl);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void postFromNet(Callback callback, String originalUrl, RequestBody body, String channel) {
        String newUrl = handleByHttpDns(originalUrl);
        Request.Builder builder = new Request.Builder().url(newUrl);
        String host = getHttpHost(originalUrl);
        if (host != null) {
            builder.addHeader("Host", host);
        }
        builder.addHeader("channel", channel);
        //builder.addHeader("pkgName", "");
        //builder.addHeader("version", "");
        builder.addHeader("token", "");
        final Request request = builder.post(body).build();
        Call call = VoiceApp.getVoiceApp().getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    public static String getVideoDetail2Url() {
        return getVideoHost() + VIDEO_DETAIL_ACTION2;
    }

    private static String getVideoHost() {
        String url = VIDEO_HOST_URL;
        try {
            Context launcher =
                    VoiceApp.getInstance().createPackageContext(
                            "com.skyworthdigital.sky2dlauncherv4",
                            Context.CONTEXT_IGNORE_SECURITY);
            if (launcher != null) {
                SharedPreferences sharedPreferences =
                        launcher.getSharedPreferences(SP_NAME, Context.MODE_WORLD_READABLE);
                if (sharedPreferences != null && sharedPreferences.getBoolean(IS_CDN, false)) {
                    url = CDN_UPDATE_URL;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HTTP + url;
    }

    public static void sendRequest(String originalUrl, Callback callback) {
        String newUrl = handleByHttpDns(originalUrl);
        Request.Builder builder = new Request.Builder().url(newUrl);
        String host = getHttpHost(originalUrl);
        if (host != null) {
            builder.addHeader("Host", host);
        }
        Request request = builder.addHeader("channel", CHANNEL_ID)
                .addHeader("pkgName", "com.skyworthdigital.skyallmedia")
                .addHeader("version", "1.0")
                .addHeader("token", "" + new TokenBuilder().getToken())
                .post(RequestBody.create(null, "")).build();
        Call call = VoiceApp.getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    public static void sendRequest2(Context context, String originalUrl, Callback callback, String channel) {
        String newUrl = handleByHttpDns(originalUrl);
        Request.Builder builder = new Request.Builder().url(newUrl);
        String host = getHttpHost(originalUrl);
        if (host != null) {
            builder.addHeader("Host", host);
        }
        Request request = builder.build();
        Call call = VoiceApp.getOkHttpClient().newCall(request);
        call.enqueue(callback);
    }

    /*public static boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping1次
            // PING的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "successful~";
                return true;
            } else {
                result = "failed~ cannot reach the IP address";
            }
        } catch (IOException e) {
            result = "failed~ IOException";
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
        } finally {
            LogUtil.log("result = " + result);
        }
        return false;
    }*/


    public static synchronized int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
