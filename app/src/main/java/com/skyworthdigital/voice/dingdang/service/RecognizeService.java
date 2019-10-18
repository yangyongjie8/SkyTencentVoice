package com.skyworthdigital.voice.dingdang.service;


import android.accessibilityservice.AccessibilityService;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.skyworthdigital.voice.common.AbsController;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.BuildConfig;
import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.LedUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.guide.GuideTip;


/**
 * 语音服务
 */

public class RecognizeService extends AccessibilityService {
    private static final String TAG = "RecognizeService";
    private static final int VOICE_KEYCODE = 135;
    private static long mRecordStart, getmRecordEnd;

    @Override
    public void onInterrupt() {
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int code = event.getKeyCode();
        int action = event.getAction();
        MLog.i(TAG, "RecognizeService keyCode:" + code + "  action:" + action);
        switch (code) {
            case KeyEvent.KEYCODE_BACK:
            case com.skyworthdigital.voice.VoiceApp.KEYCODE_TA412_BACK:
                if (action == KeyEvent.ACTION_DOWN) {
                    Utils.openScreen(true);
                    LedUtil.closeHorseLight();
                    return AbsController.getInstance().onKeyEvent(code);
                }
                break;
            case VOICE_KEYCODE:
            case 138://dongo键值
            case 2054://TA412修改的语音键值
                if (action == KeyEvent.ACTION_DOWN) {
                    //mRecordStart = System.currentTimeMillis();
                    //if (TxController.getInstance().isStartValid()) {
                    Utils.openScreen(true);
                    VoiceApp.initBaiduInstances();// 避免实例被回收
                    VoiceApp.initTencentInstances();
                    AbsController.getInstance().isControllerVoice = true;
                    AbsController.getInstance().manualRecognizeStart();
                } else if (action == KeyEvent.ACTION_UP) {
                    //if (System.currentTimeMillis() - mRecordStart > 1000) {
                    AbsController.getInstance().manualRecognizeStop();
                    //} else {
                    //    TxController.getInstance().manualRecognizeCancel();
                    //}
                }
                break;
//            case KeyEvent.KEYCODE_MENU:
//                if(action == KeyEvent.ACTION_DOWN) {
//                    TxController.getInstance().manualRecognizeStart();
//                } else if(action == KeyEvent.ACTION_UP){
//                    TxController.getInstance().testYuyiParse("我要看刘德华的电影");
//                }

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (AbsController.getInstance().isAsrDialogShowing() && !VolumeUtils.getInstance(VoiceApp.getInstance()).isM2001()) {
                        VolumeUtils.getInstance(VoiceApp.getInstance()).setAlarmVolumeMinus(1);
                        return true;
                    }
                    VolumeUtils.getInstance(VoiceApp.getInstance()).setVolumeMinus(1);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (AbsController.getInstance().isAsrDialogShowing() && !VolumeUtils.getInstance(VoiceApp.getInstance()).isM2001()) {
                        VolumeUtils.getInstance(VoiceApp.getInstance()).setAlarmVolumePlus(1);
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
                return AbsController.getInstance().isAsrDialogShowing();

            default:
                break;
        }
        return super.onKeyEvent(event);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MLog.i(TAG, "RecognizeService onCreate");
//        mControler = new TxController();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mControler.onDestroy();
        MLog.i(TAG, "RecognizeService onDestroy");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            MLog.i(TAG, "RecognizeService onAccessibilityEvent event:"+event.toString());
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

