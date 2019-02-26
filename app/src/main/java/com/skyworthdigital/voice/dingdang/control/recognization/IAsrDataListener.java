package com.skyworthdigital.voice.dingdang.control.recognization;



public interface IAsrDataListener {


    //void onSuccess(String word, String result);

    //void onStop();

    //void onError(int errorCode, String errorMessge, WakeUpResult result);

    void onASrAudiobyte(byte[] buffer, int bufferSize);
    void onASrError(int code, String desc);
}
