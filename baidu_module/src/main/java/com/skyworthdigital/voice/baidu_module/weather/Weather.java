package com.skyworthdigital.voice.baidu_module.weather;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.baidu_module.R;
import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONObject;

import java.util.List;

/**
 * 天气展示类
 * Created by SDT03046 on 2017/6/23.
 */

public class Weather {
    private String TAG = Weather.class.getSimpleName();
    private WeatherData mWeatherData;
    private AsyncImageLoader mAsyncImageLoader;
    private final int WEATHER_NUM = 5;

    public Weather(String result) {
        mAsyncImageLoader = new AsyncImageLoader();
        mWeatherData = parseWeatherData(result);
    }

    private WeatherData parseWeatherData(String result) {
        WeatherData weatherData = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject jsonData = jsonObject.getJSONObject("result").getJSONObject("resource").getJSONObject("data");
            MLog.i(TAG,  "data:" + jsonData.toString());
            Gson gson = new Gson();

            weatherData = gson.fromJson(jsonData.toString(), WeatherData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherData;
    }

    private int[] mWeather_items = {
            R.id.weather_one,
            R.id.weather_two,
            R.id.weather_three,
            R.id.weather_four,
            R.id.weather_five,
    };

    public void show(View view) {
        List<WeatherInfo> info = mWeatherData.getWeather_info();// data.getWeather_info();
        try {
            for (int i = 0; i < WEATHER_NUM; i++) {
                LinearLayout item = (LinearLayout) view.findViewById(mWeather_items[i]);
                MLog.i(TAG,  "count:" + item.getChildCount());
                TextView date = (TextView) item.getChildAt(0);
                if (!TextUtils.isEmpty(info.get(i).getTime())) {
                    String time = info.get(i).getTime().split(" ")[0];
                    String riqi = info.get(i).getTime().split(" ")[1];
                    if (i == 0) {
                        date.setText("今天" + " / " + riqi);
                    } else {
                        date.setText(time + " / " + riqi);
                    }
                } else {
                    date.setText("");
                }


                LinearLayout layout1 = (LinearLayout) item.getChildAt(1);
                LinearLayout dayinfo = (LinearLayout) layout1.getChildAt(2);

                TextView weather = (TextView) dayinfo.getChildAt(0);
                if (!TextUtils.isEmpty(info.get(i).getWeather())) {
                    weather.setText(info.get(i).getWeather());
                } else {
                    weather.setText("");
                }
                TextView temp = (TextView) dayinfo.getChildAt(1);
                if (!TextUtils.isEmpty(info.get(i).getWeather())) {
                    temp.setText(info.get(i).getTemp());
                } else {
                    temp.setText("");
                }
            }
        } catch (Exception e) {
            MLog.i(TAG,  "error" + e.toString());
        }
    }

    public void resetWeatherIcons(View view) {
        try {
            for (int i = 0; i < WEATHER_NUM; i++) {
                LinearLayout item = (LinearLayout) view.findViewById(mWeather_items[i]);
                LinearLayout iconinfo = (LinearLayout) item.getChildAt(1);
                MLog.i(TAG,  "child cnt:" + iconinfo.getChildCount());
                ImageView pic;
                if (i == 0) {
                    pic = (ImageView) view.findViewById(R.id.today_icon);
                } else {
                    pic = (ImageView) iconinfo.getChildAt(1);
                }
                pic.setImageResource(R.drawable.index_weather_icon_default);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageView getWeatherImageId(View view, int idx) {
        ImageView pic;
        try {
            LinearLayout item = (LinearLayout) view.findViewById(mWeather_items[idx]);
            if (idx == 0) {
                return (ImageView) view.findViewById(R.id.today_icon);
            }
            LinearLayout iconinfo = (LinearLayout) item.getChildAt(1);
            MLog.i(TAG,  "child cnt:" + iconinfo.getChildCount());
            pic = (ImageView) iconinfo.getChildAt(1);
        } catch (Exception e) {
            pic = null;
            e.printStackTrace();
        }
        return pic;
    }

    public WeatherData getmWeatherData() {
        return mWeatherData;
    }

    private void loadIcon(final String url, final ImageView image) {
        if (image == null || url == null) {
            return;
        }
        if (mAsyncImageLoader == null) {
            mAsyncImageLoader = new AsyncImageLoader();
        }
        Drawable cacheImage = mAsyncImageLoader.getDrawable(url,
                new AsyncImageLoader.ImageCallback() {
                    // 请参见实现：如果第一次加载url时下面方法会执行
                    public void imageLoaded(Drawable imageDrawable) {
                        changeColor(imageDrawable,R.color.weather_icon);
                        image.setImageDrawable(imageDrawable);
                    }
                });
        if (cacheImage != null) {
            image.setImageDrawable(cacheImage);
        }
    }

    public void downloadIcons(View view) {
        List<WeatherInfo> info = mWeatherData.getWeather_info();
        try {
            for (int i = 0; i < WEATHER_NUM && i < info.size(); i++) {
                String url = info.get(i).getIcon();
                loadIcon(url, getWeatherImageId(view, i));
                //LogUtil.log("download:" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable changeColor(Drawable drawable, @ColorRes int colorId) {
        //Drawable drawable = ContextCompat
        //        .getDrawable(MyApplication.getInstance(), drawableId)
        //        .mutate();
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        ColorStateList colors = ColorStateList.valueOf(ContextCompat.getColor(VoiceApp.getInstance(), colorId));
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }
}
