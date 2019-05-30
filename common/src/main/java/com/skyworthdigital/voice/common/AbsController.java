package com.skyworthdigital.voice.common;

/**
 * Created by Ives 2019/5/29
 */
public abstract class AbsController implements AbsTTS.MyTTSListener {

    public volatile boolean isControllerVoice = true;//是否遥控器语音，可能是远场语音

    public abstract void onDestroy();

    public abstract boolean onKeyEvent(int code);

    public abstract boolean isRecognizing();

    public abstract void manualRecognizeStart();

    public abstract void manualRecognizeStop();

    public abstract void testYuyiParse(String str);

    public abstract void cancelYuyiParse();

    public abstract void manualRecognizeCancel();

    public abstract boolean isAsrDialogShowing();
}
