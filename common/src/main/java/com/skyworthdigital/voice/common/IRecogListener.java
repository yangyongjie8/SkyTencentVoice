package com.skyworthdigital.voice.common;



public interface IRecogListener {
    /**
     * ASR_START 输入事件调用后，引擎准备完毕
     */
    //void onAsrReady();

    /**
     * onAsrReady后检查到用户开始说话
     */
    void onAsrBegin();

    /**
     * onAsrBegin 后 随着用户的说话，返回的临时结果
     */
    void onAsrPartialResult(String results);

    /**
     * 检查到用户开始说话停止，或者ASR_STOP 输入事件调用后，
     */
    void onAsrEnd();

    /**
     * 最终的识别结果
     */
    void onAsrFinalResult(String results);

    void onAsrNluFinish(String recogResult);

    void onAsrError(long errorCode,  String errorMessage, String descMessage);

    //void onAsrVolume(int volume);

    //void onAsrAudio(byte[] data, int offset, int length);

    //void onAsrExit();
}
