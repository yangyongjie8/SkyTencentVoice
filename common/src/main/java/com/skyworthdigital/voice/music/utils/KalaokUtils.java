package com.skyworthdigital.voice.music.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.forest.bigdatasdk.util.LogUtil;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;


/**
 * music control
 * Created by SDT03046 on 2017/12/15.
 */

public class KalaokUtils {
    private static final String KALAOK_PACKAGENAME = "com.tencent.karaoketv";//"com.audiocn.kalaok.tv.skyworth";

    public static void kalaokOpen() {
        Context context = VoiceApp.getInstance();
        try {
            Intent intent;
            if (checkApkExist(context, KALAOK_PACKAGENAME)) {
                intent = context.getPackageManager().getLaunchIntentForPackage(KALAOK_PACKAGENAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                kalaokInstallPage();
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void kalaokInstallPage() {
        AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_kalaok_uninstall));
        try {
            Intent intent = new Intent();
            intent.setPackage("com.mipt.store");
            intent.setAction("com.mipt.store.intent.APPID");
            intent.setData(Uri.parse("skystore://?packageName=" + KALAOK_PACKAGENAME));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            VoiceApp.getInstance().startActivity(intent);
        } catch (Exception e) {
            LogUtil.log("app store no found " + KALAOK_PACKAGENAME);
        }
    }

    private static boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean acitonExecute() {
        kalaokOpen();
        return true;
    }
}