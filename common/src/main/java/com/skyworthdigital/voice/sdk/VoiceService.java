package com.skyworthdigital.voice.sdk;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.VoiceTagger;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.tianmai.TianmaiCommandUtil;
import com.skyworthdigital.voiceassistant.service.IVoiceListener;
import com.skyworthdigital.voiceassistant.service.IVoiceService;
import com.skyworthdigital.voiceassistant.service.IVoiceStatusListener;

/***
 * AIDL方式实现与其它应用的通信。
 */

public class VoiceService extends Service {
    private static final String TAG = "VoiceService";

    //private static final String[] COMMANDS = {"play", "pause", "stop"};

    private static final int SEND_COMMAND = 1;
    private static final int SEND_COMMAND_ING = 2;
    private static final int STATUS_CHANGED = 3;
    private static final int CANCEL_PLAY = 4;

    public static final int STATUS_CHANGED_VALUE_START = 0;// 指定的语音开始播放
    public static final int STATUS_CHANGED_VALUE_FINISH = 1;// 播放正常完成
    public static final int STATUS_CHANGED_VALUE_CANCEL = 2;// 播放取消
    public static final int STATUS_CHANGED_VALUE_OVER = 3;// 播放异常结束

    private CommandBinder mCommandBinder = new CommandBinder();

    private static RemoteCallbackList<IVoiceListener> mCallbacks =
            new RemoteCallbackList<>();
    private static RemoteCallbackList<IVoiceListener> mInputModeCallbacks =
            new RemoteCallbackList<>();
    private static RemoteCallbackList<IVoiceStatusListener> mStatusCallbacks =
            new RemoteCallbackList<>();

    private static Looper mServiceLooper;
    private static ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate...");

        HandlerThread thread = new HandlerThread("Command");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + intent);

        // 暂时将发送命令的位置放在此处，即有客户端绑定时，延时发送。
        //mServiceHandler.sendMessageDelayed(mServiceHandler.obtainMessage(SEND_COMMAND), 1000);
        return mCommandBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy...");
        // 重置静态变量
        synchronized (VoiceService.class) {
            mCallbacks.kill();
            mCallbacks = new RemoteCallbackList<>();
            mInputModeCallbacks.kill();
            mInputModeCallbacks = new RemoteCallbackList<>();
            mStatusCallbacks.kill();
            mStatusCallbacks = new RemoteCallbackList<>();
        }
        mServiceLooper.quit();
        mServiceHandler = null;
    }

    public class CommandBinder extends IVoiceService.Stub {
        @Override
        public void registerListener(IVoiceListener listener) {
            if(listener!=null){
                String pkgName = AppUtil.getAppPkg();
                MLog.i(TAG, "registerListener packageName:"+ pkgName);
                synchronized (mCallbacks) {
                    mCallbacks.register(listener, pkgName);// 允许同一个app注册多个callback
                }
            }
        }
//        @Override
//        public void registerInputModeListener(IVoiceListener listener) {
//            LogUtil.log("registerInputModeListener listener:"+ (listener==null?"null":"not null"));
//            if(listener!=null){
//                String pkgName = AppUtil.getAppPkg();
//                LogUtil.log("registerInputModeListener packageName:"+ pkgName);
//                mInputModeCallbacks.register(listener, pkgName);// 允许同一个app注册多个callback
//            }
//        }

        @Override
        public void unregisterListener(IVoiceListener listener) {
            if (listener != null) {
                synchronized (mCallbacks) {
                    mCallbacks.unregister(listener);
                }
                synchronized (mInputModeCallbacks) {
                    mInputModeCallbacks.unregister(listener);
                }
            }
        }
        /***
         * 其它应用通过这个接口可实现告知百度语音命令执行的结果，并语音播放describe的内容。
         * 例如全媒资正在执行播放下一集命令，但是已经是最后一集，则可调用这个接口，语音播报“已经是最后一集了”
         */
//        @Deprecated
//        public void sendSceneExecuteResult(String command, boolean result, String describe) throws RemoteException {
//            Log.d(TAG, "setCommandExecuteResult: " + command + ", result = " + result + " " + describe);
//            if (!result) {
//                Robot.getInstance().talkNow(describe);
//            }
//        }

        /***
         * 其它应用通过这个接口可实现立刻将语音的弹框消失掉。
         * 例如全媒资在执行快进快退命令前，希望语音框先消失掉，这时能让用户看到视频快进快退进度条的变化过程。
         */
        @Override
        public void cancelPlay(String tag) {
            Log.d(TAG, "cancelPlay by third part app, tag:"+tag);
            if(TextUtils.isEmpty(tag))return;

            mServiceHandler.sendMessage(mServiceHandler.obtainMessage(CANCEL_PLAY, tag));
        }

        @Override
        public String playVoice(String content, IVoiceStatusListener listener) throws RemoteException {
            if(TextUtils.isEmpty(content))return null;

            String tag = VoiceTagger.makeTag();
            if(listener!=null){
                synchronized (mStatusCallbacks) {
                    mStatusCallbacks.register(listener, tag);
                }
            }
            AbsTTS.getInstance(null).talkThirdApp(content, tag);
            return tag;
        }

        @Override
        public String playVoiceWithoutDialog(String content, IVoiceStatusListener listener) throws RemoteException {
            if(TextUtils.isEmpty(content))return null;

            String tag = VoiceTagger.makeTag();
            if(listener!=null){
                synchronized (mStatusCallbacks) {
                    mStatusCallbacks.register(listener, tag);
                }
            }
            AbsTTS.getInstance(null).talkThirdAppWithoutDisplay(content, tag);
            return tag;
        }
    }

    public final class ServiceHandler extends Handler {
        private ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CANCEL_PLAY://取消播放
                    if(AbsTTS.getInstance(null).isContentTalking((String) msg.obj)) {
                        AbsTTS.getInstance(null).stopSpeak();
                        GuideTip.getInstance().dismissDialog();
                    }else {
                        AbsTTS.getInstance(null).removeContent((String) msg.obj);
                    }
                    break;
                case SEND_COMMAND:// 发送识别完的内容
                    sendVoiceCommand(TianmaiCommandUtil.PACKAGENAME_LELINGQINGZHI, (String) msg.obj, msg.arg1);
                    break;
                case SEND_COMMAND_ING:// 发送正在识别的内容
                    sendVoiceInputModeCommand(TianmaiCommandUtil.PACKAGENAME_LELINGQINGZHI, (String) msg.obj);
                    break;
                case STATUS_CHANGED://状态改变
                    int status = msg.arg1;
                    String tag = (String) msg.obj;
                    sendStatusCommand(tag, status);
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    // 发送对话订阅状态
    public static void trySendVoiceStatusCommand(@NonNull String tag, int status) {
        if(mServiceHandler==null){
            MLog.i(TAG, "handler thread released, may have no binding component.");
            return;
        }
        mServiceHandler.sendMessage(Message.obtain(mServiceHandler, STATUS_CHANGED, status, status, tag));
    }
    // 发送正在识别的内容
    public static void trySendRecognizingCommand(String command) {
        if(mServiceHandler==null){
            MLog.i(TAG, "handler thread released, may have no binding component.");
            return;
        }
        mServiceHandler.sendMessage(Message.obtain(mServiceHandler, SEND_COMMAND_ING, command));
    }
    // 发送完成识别的整句内容

    /**
     *
     * @param command
     * @param code 0 正在对讲 1 未能理解的整句内容（不含“我没有听懂，我没有听清”等尚未识别到文字的情况）；其它数字，第三方app约定指令
     */
    public static void trySendRecognizeCompletedCommand(String command, int code) {
        if(mServiceHandler==null){
            MLog.i(TAG, "handler thread released, may have no binding component.");
            return;
        }
        mServiceHandler.sendMessage(Message.obtain(mServiceHandler, SEND_COMMAND, code, -1, command));
    }

    private void sendStatusCommand(@NonNull String cookie, int status) {
        MLog.i(TAG, "sendCommand... cookie:"+cookie);
        if(cookie==null)return;

        synchronized (mStatusCallbacks) {
            int count = mStatusCallbacks.beginBroadcast();
            for (int i = 0; i < count; i++) {
                if (cookie.equalsIgnoreCase((String) mStatusCallbacks.getBroadcastCookie(i))) {
                    MLog.i(TAG, "found the listener with cookie.");
                    try {
                        mStatusCallbacks.getBroadcastItem(i).onServiceStatusChanged(status);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mStatusCallbacks.finishBroadcast();
        }
    }
    private void sendVoiceCommand(String pkgName, String command, int code){
        MLog.i(TAG, "sendVoiceCommand command:"+command+" pkgName:"+pkgName+" top:"+AppUtil.topPackageName);
        if(!AppUtil.isForegroundRunning(pkgName))return;//pkg不在前台

        synchronized (mCallbacks) {
            int count = mCallbacks.beginBroadcast();
            for (int i = 0; i < count; i++) {
                if (pkgName.equalsIgnoreCase((String) mCallbacks.getBroadcastCookie(i))) {
                    MLog.i(TAG, "found the listener with cookie.");
                    try {
                        mCallbacks.getBroadcastItem(i).onServiceRecognize(code, command);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mCallbacks.finishBroadcast();
        }
    }
    private void sendVoiceInputModeCommand(String pkgName, String command){
        MLog.i(TAG, "sendVoiceInputModeCommand command:"+command+" pkgName:"+pkgName+" top:"+AppUtil.topPackageName);
        if(!AppUtil.isForegroundRunning(pkgName))return;//pkg不在前台

        synchronized (mInputModeCallbacks) {
            int count = mInputModeCallbacks.beginBroadcast();
            for (int i = 0; i < count; i++) {
                if (pkgName.equalsIgnoreCase((String) mInputModeCallbacks.getBroadcastCookie(i))) {
                    MLog.i(TAG, "found the listener with cookie.");
                    try {
                        mInputModeCallbacks.getBroadcastItem(i).onServiceRecognize(0, command);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mInputModeCallbacks.finishBroadcast();
        }
    }
}
