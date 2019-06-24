// IVoiceListener.aidl
package com.skyworthdigital.voiceassistant.service;

// Declare any non-default types here with import statements

interface IVoiceListener {
    /**
    * 回传相关语音指令到乐龄客户端
    * @Param code 乐龄指令编号:0 inputMode,-1 未能识别的指令, 其它按照乐龄指令清单
    * @Param command 语音指令原文
    */
    void onServiceRecognize(int code, String command);
}
