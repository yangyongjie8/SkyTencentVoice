package com.skyworthdigital.voice.common;

/**
 * Created by Ives 2019/5/29
 */
public abstract class AbsTTS {
    protected static AbsTTS mInstance = null;

    public static final int STATUS_INTERRUPT = 2;
    public static final int STATUS_TALKING = 1;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_TALKOVER = 4;

    public static AbsTTS getInstance(AbsTTS.MyTTSListener listener) {
        return mInstance;
    }

    public abstract void stopSpeak() ;

    public abstract boolean isSpeak();

    // 播放不显示
    public abstract void talkWithoutDisplay(String text);

    public abstract void talk(String tts, String output) ;

    public abstract void talkDelay(String tts, String output, int delay);

    public abstract void talk(String text) ;

    /**
     * 顺序追加文本，等待显示&播放
     * @param text
     */
    public abstract void talkSerial(String text);

    /**
     *
     * @param text
     * @param tag 本次语音标签，由{@link VoiceTagger}类创建
     */
    public abstract void talkThirdApp(String text, String tag);

    /**
     * 顺序追加文本，等待播放，不显示
     * @param tag 本次语音标签，由{@link VoiceTagger}类创建
     */
    public abstract void talkThirdAppWithoutDisplay(String text, String tag);

    /**
     * 解析语义数据，并将回复语进行语音合成
     */
    public abstract void parseSemanticToTTS(String semantic);

    public interface MyTTSListener {
        void onChange(int status);

        void onOutputChange(String output, int delay);
    }
}
