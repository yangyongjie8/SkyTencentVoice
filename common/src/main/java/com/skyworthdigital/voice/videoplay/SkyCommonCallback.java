package com.skyworthdigital.voice.videoplay;


import android.util.Log;

import com.skyworthdigital.voice.common.utils.StringUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class SkyCommonCallback implements Callback {

    private String mMethodName = "SkyCommonCallback";

    public SkyCommonCallback(String messageInfo) {
        this.mMethodName = messageInfo;
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            onSuccessed(StringUtils.convertStreamToString(response.body().byteStream()));
        } catch (Exception e) {
            onFailure(call, new IOException(e));
            Log.e("SkyCommonCallback",mMethodName + " onResponse, Throwable=" + e.getLocalizedMessage());
            //e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.e("SkyCommonCallback",mMethodName + " onError, Throwable=" + e.getLocalizedMessage());
        onFail();
    }

    public abstract void onFail();

    public abstract void onSuccessed(String ret);

}

