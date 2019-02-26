package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;

/**
 * Created by SDT03046 on 2018/7/27.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler crashHandler = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultCaughtExceptionHandler;

    //使用饿汉单例模式
    public static CrashHandler getInstance() {
        return crashHandler;
    }

    public void init(Context context) {
        //获取默认的系统异常捕获器
        mDefaultCaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        //把当前的crash捕获器设置成默认的crash捕获器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        MLog.d("CrashHandler", "uncaughtException: " + thread.getName() + " " + ex.getMessage());
        MLog.d("CrashHandler",ex.getCause().toString());
        //这里可以将异常信息保存或上传
        //...

        //可根据情况选择是否干掉当前的进程


        //注意需要清空所有已经启动的activity，否则你的错误提示框可能会弹出很多次
        //MyApplication.clearActivity();
        //启动错误处理页面，你也可以在这里写上传服务器什么的
        //Intent intent = new Intent("com.crash.start");
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra("message", ex.getMessage());
        //mContext.startActivity(intent);
        // Process.killProcess(Process.myPid());//杀掉进程

        /*我看网上不少栗子是下面这么写的，但是实际测试的结果是，Android6.0以上的系统这么写没有任何问题，
        但如果是6.0以下的系统主线程异常这样写也没有问题，
        * 但如果6.0以下系统并且异常出现在子线程中，这么写就会执行系统那个丑陋的白色程序异常停止运行的提示框，
        * 然后点击确定后直接退出程序，不会运行你希望的友好退出界面*/
//        if (mDefaultCaughtExceptionHandler != null) {
//            mDefaultCaughtExceptionHandler.uncaughtException(thread,ex);
//        } else {
//            Process.killProcess(Process.myPid());//杀掉进程
//        }
    }
}
