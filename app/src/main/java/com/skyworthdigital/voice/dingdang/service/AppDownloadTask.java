package com.skyworthdigital.voice.dingdang.service;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AppDownloadTask implements Runnable {
    private static final String TAG = "FileCacheTask";
    private static final String DIR_MYAPP = "myapp";

    private Context mContext;
    private NewVersionInfo nvInfo;


    public AppDownloadTask(Context context, NewVersionInfo nvInfo) {
        mContext = context;
        this.nvInfo = nvInfo;
    }

    @Override
    public void run() {
        if (nvInfo != null && nvInfo.hasNewVersion()) {
            // 这里不做重复下载判断，因为只有开机启动才调用一次，不会产生这个逻辑问题
            String fileCachePath = FileCacheUtils.getCacheFilePath(mContext, DIR_MYAPP, nvInfo.getUrl());
            File apkFile = new File(fileCachePath);
            if (!apkFile.exists()) {
                downloadFile(nvInfo.getUrl());
            }

            if (nvInfo.getMd5() != null) {
                String fileMd5 = MD5Utils.getFileMD5(apkFile);
                if (fileMd5 == null) {
                    if (apkFile.exists()) {
                        apkFile.delete();
                    }
                } else {
                    if (!nvInfo.getMd5().equalsIgnoreCase(fileMd5)) {
                        if (apkFile.exists()) {
                            apkFile.delete();
                        }
                    } else {
                        if (apkFile.exists()) {
                            installApp(apkFile);
                        }
                    }
                }

            }
        }
    }

    private void downloadFile(final String url) {
        final long startTime = System.currentTimeMillis();
        LogUtil.i(TAG, "startTime=" + startTime);
        Log.i(TAG, "======= donwnloadFile:" + url);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "close")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogUtil.i(TAG, "download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = FileCacheUtils.getCacheFilePath(mContext, DIR_MYAPP, url);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.e(TAG, "download progress : " + progress);
                    }
                    fos.flush();
                    Log.e(TAG, "download success");
                    Log.e(TAG, "totalTime=" + (System.currentTimeMillis() - startTime));
                    downloadComplete(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "download failed : " + e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private void downloadComplete(File file) {
        if (file != null && file.exists()) {
            installApp(file);
        }
    }

    private void installApp(File file) {
        Log.i(TAG, "==== install app ====" + file.getPath());
//        InstallUtils.installNormal(mContext, file);
        InstallUtils.installSilent(mContext, file);
    }
}
