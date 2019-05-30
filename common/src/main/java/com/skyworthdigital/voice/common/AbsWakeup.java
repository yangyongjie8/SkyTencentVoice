package com.skyworthdigital.voice.common;

/**
 * Created by Ives 2019/5/29
 */
public abstract class AbsWakeup implements IWakeupDataListener {


    public abstract void init();

    public abstract void release();

    public abstract void stopWakeup();

    /**
     * 开启唤醒
     */
    public abstract void startWakeup();
}