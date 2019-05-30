package com.skyworthdigital.voice.common;

/**
 * Created by fujiayi on 2017/6/21.
 */

public interface IWakeupResultListener {


    void onSuccess(String word, String result);

    //void onStop();

    void onError(int errorCode, String errorMessge, String result);
}
