package com.skyworthdigital.voiceassistant.scene;


import android.content.Intent;

/**
 * 场景注册接口
 */
public interface ISkySceneListener {
    /**
     * 场景命令注册
     * return:json格式的字符串，举例：
     * {
     * "_scene": "com.skyworthdigital.voiceassistant.videosearch.SkyVoiceSearchAcitivity",
     * "_commands": {
     * "next": [ "换一批","换一页","下一页","换一瓶","换EP"],
     * "play": [ "$PLAY" ]
     * }
     * }
     */
    String onCmdRegister();

    /**
     * 场景命令执行
     */
    void onCmdExecute(Intent intent);
}
