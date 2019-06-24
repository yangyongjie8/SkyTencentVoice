package com.skyworthdigital.voiceassistant.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * 创维语音助手SDK
 * Created by Ives 2019/2/25
 */
public final class SkyVoiceSDK {
    private static ServiceConnection mConnection;
    private static IVoiceService mService;
    private static Context appContext;
    private static Handler mHandler;
    private static HandlerThread mHandlerThread;


    private SkyVoiceSDK(){}

    public static abstract class OnSDKStatusCallback {
        /**
         * sdk初始化完成，aidl服务连接建立完毕。
         */
        public abstract void onInitCompleted();
    }

    /**
     * 使用sdk提供的其它接口前需要调用本方法进行初始化.调用后不能保证立刻可以使用其它接口，一般需要等一会以让连接建立。
     * @param context
     */
    public static void init(Context context){
        init(context, null);
    }
    public static void init(Context context, final OnSDKStatusCallback callback){
        if(context==null){
            Log.e("SkyVoiceSDK", "argument Context is null!!Sdk has not complete initial!");
            return;
        }
        if(mConnection!=null || mService!=null){
            Log.i("SkyVoiceSDK", "Reinitialization, will release before.");
            release(context);
        }
        initHandler();
        appContext = context.getApplicationContext();

        Intent intent = new Intent();
        intent.setClassName("com.skyworthdigital.voiceassistant", "com.skyworthdigital.voiceassistant.service.VoiceService");
        intent.setAction("com.skyworthdigital.voiceassistant.service.VoiceService");
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("SkyVoiceSDK", "onServiceConnected");
                mService = IVoiceService.Stub.asInterface(service);
                if(callback!=null){
                    callback.onInitCompleted();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("SkyVoiceSDK", "onServiceDisconnected");
                mService = null;
                mConnection = null;
                tryConnect();
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Log.d("SkyVoiceSDK", "onBindingDied");
            }
        };

        appContext.bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
        tryConnect();
        Log.d("SkyVoiceSDK", "bind end.");
    }

    /**
     * 不再需要使用语音助手的功能时可使用本方法释放一些资源，下次使用时需要重新调用{@link SkyVoiceSDK#init(Context)}进行初始化。
     * 退出app时应当调用一次。
     * @param context 非空
     */
    public static void release(@NonNull Context context) {
        if(context==null){
            Log.w("SkyVoiceSDK", "context must not be null while invoking release method !!!");
            if(appContext==null) {
                return;
            }
        }else {
            appContext = context.getApplicationContext();
        }
        if(mHandlerThread!=null &&mHandlerThread.isAlive()){
            mHandlerThread.quit();
            mHandlerThread = null;
            mHandler = null;
        }
        if(mService!=null){
            mService = null;
        }
        if(mConnection!=null){
            appContext.unbindService(mConnection);
            mConnection = null;
        }
    }

    public static boolean hasInitialized(){
        return mService!=null;
    }
    private static void tryConnect(){
        if(mHandler!=null){
            mHandler.sendEmptyMessageDelayed(0, 5000L);
        }
    }

    private static void initHandler() {
        if(mHandlerThread==null || !mHandlerThread.isAlive()){
            mHandlerThread = new HandlerThread("SkyVoiceSDK_thread");
            mHandlerThread.start();
            mHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if(mService!=null)return true;//已建立连接

                    init(appContext);
                    return true;
                }
            });
        }
    }

    /**
     * 解除已监听的回调，支持包括InputMode在内的监听器。
     * @param listener
     */
    public static void unregisterListener(VoiceRecognizeListener listener) {
        if(mService==null){
            Log.w("SkyVoiceSDK", "SkyVoiceSDK has not been init");
            return;
        }
        try {
            mService.unregisterListener(listener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放并取消正在显示的对话框
     * @Param tag 播放时生成的随机字串
     **/
    public static void cancelPlay(final String tag) {
        if(checkServiceDisconnect(new OnSDKStatusCallback() {//service端断开时自动重新连接
            @Override
            public void onInitCompleted() {
                try {
                    mService.cancelPlay(tag);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }))return;
        try {
            mService.cancelPlay(tag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放一段声音，并显示对话框
     * @Param content 要播放的内容
     * @Param listener 监听该段文字播放的状态
     * @Return 随机字串tag，用于调用停止时传入。调用失败时返回null
     */
    public static String playVoice(String content, VoiceStatusListener listener) {
        if(!hasInitialized()){
            Log.w("SkyVoiceSDK", "SkyVoiceSDK has not been initialized." +
                    "You should complete initialization first.");
            return null;
        }

        try {
            return mService.playVoice(content, listener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 播放一段声音，不显示对话框
     * @Param content 要播放的内容
     * @Return 随机字串tag，用于调用停止时传入。调用失败时返回null
     */
    public static String playVoiceWithoutDialog(String content, VoiceStatusListener listener) {
        if(!hasInitialized()){
            Log.w("SkyVoiceSDK", "SkyVoiceSDK has not been initialized." +
                    "You should complete initialization first.");
            return null;
        }
        try {
            return mService.playVoiceWithoutDialog(content, listener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 监听语音回调，在一句话完整说完时回调。仅支持助手本身未能理解的内容。
     * @Param listener 语音监听器
     */
    public static void registerCompletedListener(final VoiceRecognizeListener listener) {
        if(checkServiceDisconnect(new OnSDKStatusCallback() {//service端断开时自动重新连接
            @Override
            public void onInitCompleted() {
                try {
                    mService.registerListener(listener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }))return;

        try {
            mService.registerListener(listener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
//    /**
//     * 监听正在对讲的语音识别回调
//     * @Param listener 语音监听器
//     */
//    public static void registerInputModeListener(final VoiceRecognizeListener listener) {
//        if(checkServiceDisconnect(new OnSDKStatusCallback() {//service端断开时自动重新连接
//            @Override
//            public void onInitCompleted() {
//                try {
//                    mService.registerInputModeListener(listener);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        })) return;
//
//        try {
//            mService.registerInputModeListener(listener);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
    // 检查连接是否断开，并自动重新初始化
    private static boolean checkServiceDisconnect(OnSDKStatusCallback callback){
        if(mService==null){
            Log.i("SkyVoiceSDK", "SkyVoiceSDK has not been init, will init automatically.");
            init(appContext, callback);//service端断开时自动重新连接
            return true;
        }
        return false;
    }
}
