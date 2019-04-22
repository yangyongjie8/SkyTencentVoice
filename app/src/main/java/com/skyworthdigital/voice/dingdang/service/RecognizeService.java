package com.skyworthdigital.voice.dingdang.service;


import android.accessibilityservice.AccessibilityService;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.skyworthdigital.voice.dingdang.BuildConfig;
import com.skyworthdigital.voice.dingdang.MainControler;
import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.GuideTip;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;


/**
 * 语音服务
 */

public class RecognizeService extends AccessibilityService {
    private static final String TAG = "RecognizeService";
    private static final int VOICE_KEYCODE = 135;
    public static final int KEYCODE_TA412_BACK = 111;
    private static long mRecordStart, getmRecordEnd;

    @Override
    public void onInterrupt() {
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int code = event.getKeyCode();
        int action = event.getAction();
        Log.i(TAG, "keyCode:" + code + "  action:" + action);
        switch (code) {
            case KeyEvent.KEYCODE_BACK:
            case KEYCODE_TA412_BACK:
                if (action == KeyEvent.ACTION_DOWN) {
                    return MainControler.getInstance().onKeyEvent(code);
                }
                break;
            case VOICE_KEYCODE:
            case 2054://TA412修改的语音键值
                if (action == KeyEvent.ACTION_DOWN) {
                    //mRecordStart = System.currentTimeMillis();
                    //if (MainControler.getInstance().isStartValid()) {
                    MainControler.getInstance().isControllerVoice = true;
                    MainControler.getInstance().manualRecognizeStart();
                } else if (action == KeyEvent.ACTION_UP) {
                    //if (System.currentTimeMillis() - mRecordStart > 1000) {
                        MainControler.getInstance().manualRecognizeStop();
                    //} else {
                    //    MainControler.getInstance().manualRecognizeCancel();
                    //}
                }
                break;
//            case KeyEvent.KEYCODE_MENU:
//                if(action == KeyEvent.ACTION_DOWN) {
//                    MainControler.getInstance().manualRecognizeStart();
//                } else if(action == KeyEvent.ACTION_UP){
//                    MainControler.getInstance().testYuyiParse("我要看刘德华的电影");
//                }

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (MainControler.getInstance().isAsrDialogShowing() && !VolumeUtils.getInstance(VoiceApp.getInstance()).isM2001()) {
                        VolumeUtils.getInstance(VoiceApp.getInstance()).setVoiceVolumeMinus(1);
                        return true;
                    }
                    VolumeUtils.getInstance(VoiceApp.getInstance()).setVolumeMinus(1);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (MainControler.getInstance().isAsrDialogShowing() && !VolumeUtils.getInstance(VoiceApp.getInstance()).isM2001()) {
                        VolumeUtils.getInstance(VoiceApp.getInstance()).setVoiceVolumePlus(1);
                        return true;
                    }
                    VolumeUtils.getInstance(VoiceApp.getInstance()).setVolumePlus(1);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return MainControler.getInstance().isAsrDialogShowing();

            default:
                break;
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        //mControler = new MainControler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mControler.onDestroy();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            MLog.i(TAG, "onAccessibilityEvent event:"+event.toString());
            if(!BuildConfig.APPLICATION_ID.equals(event.getPackageName().toString())) {
                AppUtil.topPackageName = (String) event.getPackageName();
            }
            if (!TextUtils.isEmpty(event.getPackageName())) {
                //String curPackage = event.getPackageName().toString();
                String curClass = event.getClassName().toString();
                GuideTip.getInstance().setmCurrentCompenent(curClass);
                //Log.i(TAG, curPackage);
            } else {
                //LogUtil.log("onAccessibilityEvent null");
            }
        }
    }
}

