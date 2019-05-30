package com.skyworthdigital.voice.common;

/**
 * Created by Ives 2019/5/29
 */
public abstract class AbsRecognizer {

    public abstract void register();

    public abstract void start();

    public abstract void cancel();

    public abstract void release();

    public abstract void stop();

    public abstract boolean isRecogning();

    public abstract void endRecognize();

    public abstract void yuyiParse(String str);
}
