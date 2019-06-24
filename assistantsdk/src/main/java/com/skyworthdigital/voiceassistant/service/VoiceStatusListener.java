package com.skyworthdigital.voiceassistant.service;


/**
 * 监听指定的播放内容的播放情况。
 * Created by Ives 2019/3/1
 */
public abstract class VoiceStatusListener extends IVoiceStatusListener.Stub {

    @Override
    public final void onServiceStatusChanged(int status){
        onStatusChanged(status);
    }

    /**
     *
     * @param status 0 播放开始
     *               1 播放正常结束
     *               2 播放取消
     *               3 播放异常结束
     */
    public abstract void onStatusChanged(int status);
}
