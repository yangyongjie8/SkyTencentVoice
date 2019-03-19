package com.skyworthdigital.voice.dingdang.service;


import android.accessibilityservice.AccessibilityService;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.skyworthdigital.voice.dingdang.MainControler;
import com.skyworthdigital.voice.dingdang.utils.GuideTip;
import com.skyworthdigital.voice.dingdang.utils.MLog;


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
        Log.i(TAG, "keyCode:" + code + "  action:" + action);
        switch (code) {
            case KeyEvent.KEYCODE_BACK:
                if (action == KeyEvent.ACTION_DOWN) {
                    return MainControler.getInstance().onKeyEvent(code);
                }
                break;
            case VOICE_KEYCODE:
            case 2054://TA412修改的语音键值
                if (action == KeyEvent.ACTION_DOWN) {
                    //mRecordStart = System.currentTimeMillis();
                    //if (MainControler.getInstance().isStartValid()) {
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

