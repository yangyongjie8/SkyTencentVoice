package com.skyworthdigital.voice.tianmai;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skyworthdigital.voice.VoiceApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ives 2019/1/9
 */
public final class WeatherUtil {
    private WeatherUtil(){}

    /**
     * 返回今天天气情况
     * @param city
     * @param callback
     */
    public static void getWeatherToday(final String city, final CallbackWeather callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call call = VoiceApp.getOkHttpClient()
                        .newCall(new Request.Builder().url("http://gt.beevideo.tv/hometv/api/weather/info/"+city).build());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.callback(null);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            JSONObject rspObj = new JSONObject(response.body().string());
                            if("success".equalsIgnoreCase(rspObj.optString("msg"))){
                                JSONObject dataObj = rspObj.optJSONObject("data");
                                if(dataObj!=null){
                                    Gson gson = new GsonBuilder().serializeNulls().create();
                                    callback.callback(gson.fromJson(dataObj.toString(), WeatherBee.class));
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.callback(null);
                    }
                });
            }
        }).start();
    }
    public interface CallbackWeather {
        /**
         * 返回当天天气信息的实体，获取失败则回传参数为null
         * @param todayWeather
         */
        void callback(WeatherBee todayWeather);
    }
}
