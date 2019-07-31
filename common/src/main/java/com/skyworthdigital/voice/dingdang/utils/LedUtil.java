package com.skyworthdigital.voice.dingdang.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 控制盒子自身led灯的类
 * Created by Ives 2019/7/31
 */
public class LedUtil {

    // 打开跑马灯
    public static void openHorseLight(){
        setLedMode(6);
    }
    // 关闭跑马灯
    public static void closeHorseLight(){
        setLedMode(7);
    }
    private static void setLedMode(int mode){
        try {
            Class clz = Class.forName("com.skyworth.led.Tlc591xx");
            Method staticMethod = clz.getMethod("getDefaultInstance");
            Object instance = staticMethod.invoke(null);
            Method method = clz.getMethod("setLedMode", int.class);
            method.invoke(instance, mode);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            MLog.e("LedUtil", "openHorseLight exception! Class Not Found.");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            MLog.e("LedUtil", "openHorseLight exception! NoSuchMethod.");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            MLog.e("LedUtil", "openHorseLight exception!");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            MLog.e("LedUtil", "openHorseLight exception!");
        }
    }
}
