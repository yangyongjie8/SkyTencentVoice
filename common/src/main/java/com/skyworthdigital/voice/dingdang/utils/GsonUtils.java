package com.skyworthdigital.voice.dingdang.utils;


import com.google.gson.Gson;

public class GsonUtils {

    public static <T> T parseResult(String ret, Class<T> classOfT) {
        try {
            return new Gson().fromJson(ret, classOfT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
