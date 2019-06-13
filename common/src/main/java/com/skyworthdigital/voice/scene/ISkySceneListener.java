package com.skyworthdigital.voice.scene;


import android.content.Intent;

/**
 * 场景注册接口
 */
public interface ISkySceneListener {
    /**
     * 场景命令注册
     * return:json格式的字符串，举例：
     * {
     * "_sceneName": "com.skyworthdigital.voiceassistant.videosearch.SkyVoiceSearchAcitivity",
     * "_commands": {
     * "next": [ "换一批","换一页","下一页","换一瓶","换EP"],
     * "play": [ "#PLAY" ]
     * }
     * }
     */
    String onCmdRegister();

    /**
     * 返回场景名称，同json里的_sceneName
     * @return
     */
    String getSceneName();
    /**
     * 场景命令执行
     */
    void onCmdExecute(Intent intent);
}
