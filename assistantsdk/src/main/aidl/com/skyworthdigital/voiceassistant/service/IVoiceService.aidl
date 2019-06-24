// ICommand.aidl
package com.skyworthdigital.voiceassistant.service;

// Declare any non-default types here with import statements
import com.skyworthdigital.voiceassistant.service.IVoiceListener;
import com.skyworthdigital.voiceassistant.service.IVoiceStatusListener;

interface IVoiceService {

    void unregisterListener(in IVoiceListener listener);

//    void sendSceneExecuteResult(String command, boolean result,String describe);

    /**
    * 停止播放并取消正在显示的对话框
    * @Param tag 播放时生成的随机字串
    **/
    void cancelPlay(String tag);

    /**
    * 播放一段声音，并显示对话框
    * @Param content 要播放的内容
    * @Return 随机字串tag，用于调用停止时传入
    */
    String playVoice(String content, IVoiceStatusListener listener);
    /**
    * 播放一段声音，不显示对话框
    * @Param content 要播放的内容
    * @Return 随机字串tag，用于调用停止时传入
    */
    String playVoiceWithoutDialog(String content, IVoiceStatusListener listener);

    /**
    * 监听语音回调
    * @Param listener 语音回调
    */
    void registerListener(in IVoiceListener listener);
    /**
    * 监听正在对讲的语音识别回调
    * @Param listener 语音回调
    */
//    void registerInputModeListener(in IVoiceListener listener);
}
