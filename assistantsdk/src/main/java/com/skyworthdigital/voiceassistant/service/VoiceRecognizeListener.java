package com.skyworthdigital.voiceassistant.service;

/**
 * 监听用户语音输入内容的监听器。
 * Created by Ives 2019/3/1
 */
public abstract class VoiceRecognizeListener extends IVoiceListener.Stub {
    /**
     * @Param code 0 正在对讲；
     *            -1 尚未能理解的整句内容（不含“我没有听懂，我没有听清”等尚未识别到文字的情况）；
     *            其它数字，第三方app约定指令。
     * @Param command 识别到的文字
     */
    @Override
    public final void onServiceRecognize(int code, String command){
        // 对于registerInputModeListener注册的监听，code只会是0
        // registerCompletedListener，code只会是-1或其它约定的指令值，不会是0.
        onRecognize(code, command);
    }
    public abstract void onRecognize(int code, String command);
}
