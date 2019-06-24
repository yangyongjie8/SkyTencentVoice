// ICommandCallback.aidl
package com.skyworthdigital.voiceassistant.service;

// Declare any non-default types here with import statements

interface IVoiceStatusListener {
    /**
    * @Param status 0 正常播放结束 1 已取消 2 停止或出错
    **/
    void onServiceStatusChanged(int status);
}
