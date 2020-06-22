package com.skyworthdigital.voice.dingdang.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.videoplay.utils.RequestUtil;

import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * User: yangyongjie
 * Date: 2020-04-08
 * Description:
 */
public class AppUpgradeService extends Service implements Callback {
    private static final String TAG = "AppUpgradeService";
    private static final String UPGRADE_CHANNEL = "mengdasheng1";
    private static final int HANDLE_MSG_CHECK_UPGRADE = 1001;
    private static final int HANDLE_MSG_DOWNLAOD_APP = 1002;
    private NewVersionInfo nvInfo;
    private Context mContext;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_MSG_CHECK_UPGRADE:
                    checkUpgrade();
                    break;
                case HANDLE_MSG_DOWNLAOD_APP:
                    downloadApk();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "AppUpgradeService oncreate");
        mContext = this;
        mHandler.sendEmptyMessageDelayed(HANDLE_MSG_CHECK_UPGRADE, 5 * 1000l);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AppUpgradeService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkUpgrade() {
        final String url = getUrl();
        Log.i(TAG, "=====checkUpgrade:" + url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestUtil.sendRequest2(mContext, url, AppUpgradeService.this, UPGRADE_CHANNEL);
            }
        }).start();
    }

    private void downloadApk() {
        AppDownloadTask task = new AppDownloadTask(mContext, nvInfo);
        new Thread(task).start();
    }

    private String getUrl() {
        String url = "http://meta.beevideo.bestv.com.cn/oms/download/clientVersion.action";
        url += "?sdkLevel=" + String.valueOf(android.os.Build.VERSION.SDK_INT);
        url += "&pkgName=" + getPackageName();
        url += "&verCode=" + InstallUtils.getVersionCode(this);
        url += "&channelCode=" + UPGRADE_CHANNEL;
//        url += "&pkgName=" + "cn.beevideo";
//        url += "&verCode=" + InstallUtils.getVersionCode(this);
//        url += "&channelCode=" + "default";
//        Log.i(TAG, "==== get url:" + url);
        return url;
    }


    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(response.body().byteStream(), HTTP.UTF_8);
            int eventType = xpp.getEventType();
            String tag = "";
            nvInfo = new NewVersionInfo();
            int currVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            nvInfo.setCurrVersion(currVersion);
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = xpp.getName();
                    if (tag.equals("status")) {
                        nvInfo.setStatus(parseInt(xpp.nextText()));
                    } else if (TextUtils.equals(tag, "version")) {
                        nvInfo.setNewVersion(parseInt(xpp.nextText()));
                    } else if (TextUtils
                            .equals(tag, "versionName")) {
                        nvInfo.setNewVersionName(xpp.nextText());
                    } else if (TextUtils.equals(tag, "url")) {
                        nvInfo.setUrl(xpp.nextText());
                    } else if (TextUtils.equals(tag, "level")) {
                        nvInfo.setLevel(parseInt(xpp.nextText()));
                    } else if (TextUtils.equals(tag, "size")) {
                        nvInfo.setSize(parseInt(xpp.nextText()));
                    } else if (TextUtils.equals(tag, "md5")) {
                        nvInfo.setMd5(xpp.nextText());
                    } else if (TextUtils.equals(tag, "time")) {
                        nvInfo.setTime(xpp.nextText());
                    } else if (TextUtils.equals(tag,
                            "desc")) {
                        nvInfo.setDesc(xpp.nextText());
                    } else if (TextUtils.equals(tag,
                            "thirdPartyUpgrade")) {
                        nvInfo.setThirdPartyUpgrade(parseInt(xpp.nextText()));
                    }
                }
                eventType = xpp.next();
            }
            Log.i(TAG, "=======apk download url:" + nvInfo.getUrl());
            mHandler.sendEmptyMessageDelayed(HANDLE_MSG_DOWNLAOD_APP, 60 * 1000l);
        } catch (Exception e) {
            Log.e(TAG, "onResponse ERROR!", e.fillInStackTrace());
        }
    }

    private int parseInt(String value) {
        if (value == null || value.trim().length() <= 0) {
            return -1;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
