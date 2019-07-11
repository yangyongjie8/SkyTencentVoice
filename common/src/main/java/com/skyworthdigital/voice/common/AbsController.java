package com.skyworthdigital.voice.common;

import com.skyworthdigital.voice.VoiceApp;

/**
 * Created by Ives 2019/5/29
 */
public abstract class AbsController {

    public volatile boolean isControllerVoice = true;//是否遥控器语音，可能是远场语音
    public static final int DISMISS_DELAY_NORMAL = 3000;// 正常延时
    protected volatile static AbsController[] mManagerInstance = new AbsController[2];

    public static AbsController getInstance() {
        if(VoiceApp.isDuer){
            return mManagerInstance[0];
        }else {
            return mManagerInstance[1];
        }
    }

    public abstract void onDestroy();
    // 按返回键
    public abstract boolean onKeyEvent(int code);
    // 正在识别
    public abstract boolean isRecognizing();
    // 开始识别
    public abstract void manualRecognizeStart();
    // 停止识别
    public abstract void manualRecognizeStop();
    // 文本合成语音(目前仅叮当用)
    public abstract void testYuyiParse(String str);
    // 取消合成文本语音
    public abstract void cancelYuyiParse();
    // 取消识别（和停止识别暂时看不出区别，暂时不使用）
    public abstract void manualRecognizeCancel();
    // 正在显示识别对话框
    public abstract boolean isAsrDialogShowing();
    // 取消对话框（目前仅叮当需要）
    public abstract void dismissDialog(long delay);
}
