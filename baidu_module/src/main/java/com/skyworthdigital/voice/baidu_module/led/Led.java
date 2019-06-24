package com.skyworthdigital.voice.baidu_module.led;

import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 音箱面板的LED灯显示的公共类
 * 语音的状态通过面板LED灯的不同状态显示出来。
 * 通过写结点的方式改变led灯状态
 * Created by SDT03046 on 2017/8/9.
 */

public class Led {
    private final static String LED_PATH = "sys/class/skytp/actionled";
    private final static String LED_OFF = "0";
    private final static String LED_WAKEUP = "1";
    private final static String LED_VOICERECOGNIZING = "2";
    private final static String TAG = "led";
    private static String mLedStatus = LED_OFF;
    /**
     * 获取节点
     */
    /*private static String getString(String path) {
        String prop = "waiting";// 默认值
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            prop = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }*/

    /**
     * 写节点
     */
    /*private static void ledWrite(final String sys_path, final String status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process p = null;
                DataOutputStream os = null;
                try {
                    p = Runtime.getRuntime().exec("sh");
                    os = new DataOutputStream(p.getOutputStream());
                    String cmd = "echo " + status + " > " + sys_path + "\n";
                    Log.i(TAG, "led status: " + status + " cmd:" + cmd);
                    p = Runtime.getRuntime().exec(cmd);
                    os.writeBytes(cmd);
                    os.writeBytes("exit\n");
                    os.flush();
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.i(TAG, " can't write " + sys_path + e.getMessage());
                } finally {
                    if (p != null) {
                        p.destroy();
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }*/
    private static void ledWrite(final String path, final String status) {
        if (VoiceModeAdapter.isAudioBox()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedWriter bufWriter = null;
                    try {
                        bufWriter = new BufferedWriter(new FileWriter(path));
                        bufWriter.write(status);  // 这儿进行的写操作
                        Log.i(TAG, "led status: " + status);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (bufWriter != null) {
                            try {
                                bufWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    /**
     * 音箱面板上的led灯灭
     */
    public static void showLedOff() {
        if (VoiceModeAdapter.isAudioBox()) {
            mLedStatus = LED_OFF;
            ledWrite(LED_PATH, LED_OFF);
        }
    }

    /**
     * 语音唤醒时，音箱面板上的led显示
     */
    public static void showVoiceWakedup() {
        if (VoiceModeAdapter.isAudioBox()) {
            mLedStatus = LED_WAKEUP;
            ledWrite(LED_PATH, LED_WAKEUP);
        }
    }

    /**
     * 语音识别过程中，音箱面板上的led显示
     */
    public static void showVoiceRecognizing() {
        if (VoiceModeAdapter.isAudioBox()) {
            if (!TextUtils.equals(mLedStatus, LED_VOICERECOGNIZING)) {
                mLedStatus = LED_VOICERECOGNIZING;
                ledWrite(LED_PATH, LED_VOICERECOGNIZING);
            }
        }
    }
}
