package com.skyworthdigital.voice.baidu_module;


public class EventMsg {
    public int mWhat;
    public int mArg;
    public Object mObj;

    //public static final int MSG_RECOGNIZE_GOON = 1;
    //public static final int MSG_TEXTISMARQUEE = 3;
    public static final int MSG_TEXTMARQUEE_OVER = 2;
    public static final int MSG_DISMISS_DIALOG = 4;
    public static final int MSG_ROBOT_SPEECH_OVER = 5;
    public static final int MSG_ROBOT_SPEECH_ERROR = 6;
    public static final int MSG_ADDITION_MSG = 7;
    public static final int MSG_ROBOT_READ_PROGRESS = 9;

    public EventMsg(int what) {
        this.mWhat = what;
    }

    public EventMsg(int what, int arg) {
        this.mWhat = what;
        this.mArg = arg;
    }

    public EventMsg(int what, int arg, Object obj) {
        this.mWhat = what;
        this.mObj = obj;
        this.mArg = arg;
    }

    public int getWhat() {
        return this.mWhat;
    }
}
