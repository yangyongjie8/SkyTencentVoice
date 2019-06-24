package com.skyworthdigital.voice.baidu_module.voicemode;

import android.content.Context;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.voice.VoiceInterface;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 唤醒词唤醒模式
 * Created by SDT03046 on 2017/8/8.
 */

public class WakeupMode implements VoiceMode {
    private String TAG = WakeupMode.class.getSimpleName();
    /**
     * 远场语音的pid,key，需要专门向百度申请
     */
    private static final int ASR_PID = 728;
    private static final String ASR_KEY = "com.baidu.dumi.soundbar";
    //TODO:唤醒词请使用自己的唤醒词
    public static final String wakeUpWord1 = "小派小派";
    public static final String wakeUpWord2 = "小度小度";
    private static boolean isRecognizeGoon = false;//是否不需重新唤醒，继续进行语音识别，

    @Override
    public void startRecognize(Context var, DuerSDK sdk, VoiceInterface.IVoiceEventListener listener) {
        VoiceInterface.VoiceParam voiceParam = new VoiceInterface.VoiceParam();
        voiceParam.setAsrPid(ASR_PID);
        voiceParam.setAsrKey(ASR_KEY);
        //voiceParam.setVoiceSdkServerUrl("https://audiotest.baidu.com/v2");
        voiceParam.setVoiceMode(VoiceInterface.VOICEMODE.AUTO_REC);
        voiceParam.setVoiceResultMode(VoiceInterface.VOICERESULTMODE.VOICE_DUER);
        {
            //增加一些参数,如果sdk中，已经打包进定位模块后，则这个位置不用设置经纬度信息,否则需要调用方自己传入真实的位置信息
            if (!sdk.getBDLocation().isAvailable()) {
                try {
                    JSONObject duerParam = new JSONObject();
                    //设置位置信息
                    // 坐标系名称 wgs84为标准经纬度
                    duerParam.put("location_system", "wgs84");
                    // 经度；double类型
                    duerParam.put("longitude", 116.403119);
                    // 纬度；double类型
                    duerParam.put("latitude", 39.924564);
                    voiceParam.setExtraParam(duerParam.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        sdk.getVoiceRecognize().startRecognition(var, voiceParam, listener);
    }

    /**
     * 启动唤醒检测。通过唤醒词唤醒,智能音箱才需要唤醒
     */
    void startWakeUp(Context var, DuerSDK sdk) {
        MLog.i(TAG, "audio box start wakeup");
        VoiceInterface.VoiceParam voiceParam = new VoiceInterface.VoiceParam();
        JSONArray wakeWords = new JSONArray();
        wakeWords.put(wakeUpWord1).put(wakeUpWord2);
        voiceParam.setWakeupWord(wakeWords);
        voiceParam.setFromKitt(true);
        sdk.getVoiceRecognize().startWakeUp(var, voiceParam);
        //voiceStatusTxt.setText("");
    }

    /**
     * 停止唤醒
     */
    void stopWakeUp(DuerSDK sdk) {
        sdk.getVoiceRecognize().stopWakeUp();
    }

    void setRecognizeGoOn(boolean status) {
        isRecognizeGoon = status;
    }

    boolean isRecognizeGoOn() {
        return isRecognizeGoon;
    }
}
