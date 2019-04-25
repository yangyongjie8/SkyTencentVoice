package com.skyworthdigital.voice.dingdang.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.skyworthdigital.voice.dingdang.BuildConfig;
import com.skyworthdigital.voice.dingdang.VoiceApp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Ives 2019/1/13
 */
public class AppUtil {
    public static String topPackageName;//缓存最前台的app包名

    // app是否存在
    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ ){
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
    // 启动指定app
    public static void startApp(Context context, String targetPackageName){
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(targetPackageName);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 在binder进程中调用，获取调用当前代码的进程的包名
     * @return
     */
    public static String getAppPkg() {
        return getAppPkg(Binder.getCallingPid());
    }

    /**
     * 获取该进程的包名
     * @param pid
     * @return
     */
    public static String getAppPkg(int pid) {
        String processName = "";
        ActivityManager activityManager = (ActivityManager) VoiceApp.getInstance().getSystemService(ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info : list) {
                if (info.pid == pid) {
                    if(info.processName.contains(":")) {
                        processName = info.processName.substring(0, info.processName.indexOf(":"));
                    }else {
                        processName = info.processName;
                    }
                    break;
                }
            }
        }
        return processName;
    }

    /**
     * 指定的app是否在前台运行
     * @param packageName
     * @return
     */
    public static boolean isForegroundRunning(@NonNull String packageName){
        return packageName.equals(topPackageName);
    }
    /**
     * 杀死指定包名的进程
     * @param context
     * @param packageName
     * @return true 杀死成功， false 出现异常
     */
    private static boolean killPackage(Context context, String packageName){
        if(TextUtils.isEmpty(packageName))return false;

//        需要权限android.permission.FORCE_STOP_PACKAGES
        ActivityManager mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = null;
        try {
            method = mAm.getClass().getMethod("forceStopPackage", String.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        try {
            method.invoke(mAm, packageName);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void killTopApp(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){// 5.0及以上获取不到正在运行的进程
            String packageName = "";
            ActivityManager activityManager = (ActivityManager) VoiceApp.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rTasks = activityManager.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo runningTask: rTasks) {
                packageName = runningTask.topActivity.getPackageName();
                MLog.d("AppUtil", "running Task:" + packageName);
                if(BuildConfig.APPLICATION_ID.equals(packageName)){//本语音app，则返回关掉对话。偶现。
                    Utils.simulateKeystroke(KeyEvent.KEYCODE_BACK);
                    return;
                }else if(!killPackage(VoiceApp.getInstance(), packageName)){
                    // 没杀成功，模拟home键
                    MLog.d("AppUtil", "kill failure, simulate home key.");
                    break;
                }else if(packageName.equals(topPackageName)){
                    return;//top app之上的app都已经杀死
                }
            }
        }
        Utils.simulateKeystroke(KeyEvent.KEYCODE_HOME);
    }
}
