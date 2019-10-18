package com.skyworthdigital.voice.baidu_module.robot;

import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.tts.TTSInterface;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.baidu_module.BdController;
import com.skyworthdigital.voice.baidu_module.EventMsg;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.VoiceTagger;
import com.skyworthdigital.voice.dingdang.utils.LedUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.sdk.VoiceService;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 语音播报控制类
 * Created by SDT03046 on 2017/6/20.
 */

public class BdTTS extends AbsTTS {
    private String TAG = BdTTS.class.getSimpleName();

    private BdTTS(){
        initTTS();
        HandlerThread handlerThread = new HandlerThread(BdTTS.class.getSimpleName());
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), mProcCallback);
    }

    public static AbsTTS getInstance(){
        if(mInstance[0]==null){
            synchronized (BdTTS.class){
                if (mInstance[0]==null){
                    mInstance[0] = new BdTTS();
                }
            }
        }
        return mInstance[0];
    }

    private LinkedList<Content> mContentList = new LinkedList<>();//第三方消息只会有一句话在队列里，但可能被切成多个。
    private ReentrantLock lock = new ReentrantLock(true);
    private boolean mIsTalking = false;//业务是否处于播放状态，不代表一定已经发出声音。
    private Handler mHandler;
    private static final int SPEECH_MAX_LENGTH = 512;//一次合成所允许的最长字符数


    private static final int MSG_TALK_NEXT = 0;
    private static final int MSG_STOP = 1;
    private static final int MSG_TALK_NOW = 2;
    private static final int MSG_TALK_NOW_WITHOUT_DISPLAY = 3;
    private static final int MSG_TALK_SERIAL = 4;
    private static final int MSG_TALK_THIRD_APP = 5;//第三方app调用的播报
    private static final int MSG_TALK_THIRD_APP_WITHOUT_DISPLAY = 6;

    private Handler.Callback mProcCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            MLog.d(TAG, "voice handleMessage, msg:"+msg.what);
            switch (msg.what){
                case MSG_TALK_NEXT://播放一段text
                    talkNext();
                    break;
                case MSG_STOP://停止播放
                    doStopTalk();
                    break;
                case MSG_TALK_NOW:
                    doStopTalk();
                    lock.lock();
                    pushSplots((Content) msg.obj);
                    lock.unlock();
                    talkNext();
                    break;
                case MSG_TALK_NOW_WITHOUT_DISPLAY:
                    doStopTalk();
                    lock.lock();
                    pushSplots((Content) msg.obj);
                    lock.unlock();
                    talkNext();
                    break;
                case MSG_TALK_SERIAL:
                    lock.lock();
                    pushSplots((Content) msg.obj);
                    lock.unlock();
                    if(!mIsTalking){
                        talkNext();
                    }
                    break;
                case MSG_TALK_THIRD_APP:
                    //有助手正在播报的消息，排队
                    //否则，只第三方app的消息（含其它app的）或助手的排队消息，立即播放
                    if(!mContentList.isEmpty() && !TextUtils.isEmpty(mContentList.getFirst().tag)){
                        doStopTalk();
                    }
                    lock.lock();
                    pushSplots((Content) msg.obj);
                    lock.unlock();
                    if(!mIsTalking){
                        talkNext();
                    }
                    break;
                case MSG_TALK_THIRD_APP_WITHOUT_DISPLAY:
                    if(!mContentList.isEmpty() && !TextUtils.isEmpty(mContentList.getFirst().tag)){
                        doStopTalk();
                    }
                    lock.lock();
                    pushSplots((Content) msg.obj);
                    lock.unlock();
                    if(!mIsTalking){
                        talkNext();
                    }
                    break;
                default:
                    Log.e("BdTTS", "invalid msg what:"+msg.what);
                    break;
            }
            return true;
        }
    };

    private static final int TTS_VOLUME = 1;
    private static final int TTS_SPEED = 5;
    private static final int TTS_SPEAKER = 0;
    private static final int TTS_PITCH = 9;
    private static final String TTS_APPID = "8653937";
    private static final String TTS_APKKEY = "VqVPqAZrxivfgI6IUzw4luRo";
    private static final String TTS_SECRETKEY = "688521fb04b6ed73b825d9803582c798";
    private void initTTS() {
        //设置参数
        TTSInterface.TTSParam ttsParam = new TTSInterface.TTSParam();
        ttsParam.setVolume(TTS_VOLUME);
        ttsParam.setSpeed(TTS_SPEED);
        ttsParam.setSpeeaker(TTS_SPEAKER);
        ttsParam.setPitch(TTS_PITCH);
        ttsParam.setAudioStreamType(AudioManager.STREAM_ALARM);
        //请在yuyin.baidu.com上申请自己的appid，apikey，scretkey，选中语音合成服务
        ttsParam.setAppId(TTS_APPID);
        ttsParam.setApikey(TTS_APKKEY);
        ttsParam.setSecretkey(TTS_SECRETKEY);
        ttsParam.setAudioRate(TTSInterface.TTSParam.AUDIO_BITRATE_AMR_15K85);

        DuerSDKFactory.getDuerSDK().getSpeech();
        DuerSDKFactory.getDuerSDK().getSpeech().initTTS(VoiceApp.getInstance(), ttsParam, new TTSInterface.InitTTSListener() {
            @Override
            public void onInitResult(int errorCode, String errorMessage) {
                MLog.i(TAG, "initTTS:" + "初始化结果:errorCode=" + errorCode + " errorMessage=" + errorMessage);
            }
        });

        /**
         * TTS回调接口
         */
        TTSInterface.ITTSListener mTTSListener = new TTSInterface.ITTSListener() {

            @Override
            public void onSynthesizeStart(String utteranceId) {
                //LogUtil.log("playStatus:语音合成开始 = " + utteranceId);
            }

            @Override
            public void onSynthesizeDataArrived(String utteranceId, byte[] audioData, int progress) {
                //LogUtil.log("接收到的数据流 = " + utteranceId);
            }

            @Override
            public void onSynthesizeFinish(String utteranceId) {
                MLog.i(TAG, "playStatus:语音合成结束 = " + utteranceId);
                LedUtil.closeHorseLight();
            }

            @Override
            public void onSpeechStart(String utteranceId) {
                MLog.i(TAG, "playStatus:语音合成开始播音 = " + utteranceId);
            }

            @Override
            public void onSpeechProgressChanged(String utteranceId, int progress) {
            }

            @Override
            public void onSpeechFinish(String utteranceId) {
                MLog.i(TAG, "= 语音播报结束 = " + utteranceId);
                mIsTalking = false;

                lock.lock();
                Content content = mContentList.removeFirst();
                lock.unlock();
                checkSendThirthAppListener(content, VoiceService.STATUS_CHANGED_VALUE_FINISH);

                if(mContentList.isEmpty() || !mContentList.getFirst().needDisplay){
                    BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_ROBOT_SPEECH_OVER));
                }else {
                    mHandler.sendEmptyMessage(MSG_TALK_NEXT);
                }
            }

            @Override
            public void onError(String utteranceId, TTSInterface.TtsError error) {
                MLog.i(TAG, "= 语音播报出错 = " + utteranceId + " error:" + error.toString()+" list size:"+mContentList.size());
                mIsTalking = false;
                lock.lock();
                Content content = mContentList.removeFirst();
                clearList();
                lock.unlock();
                checkSendThirthAppListener(content, VoiceService.STATUS_CHANGED_VALUE_OVER);
                LedUtil.closeHorseLight();
            }
        };
        DuerSDKFactory.getDuerSDK().getSpeech().addTTSStateListener(mTTSListener);
    }
    @Override
    public boolean isSpeak(){
        return mIsTalking;
    }
    // 根据tag判断是否对应内容正在播放
    @Override
    public boolean isContentTalking(String tag){
        if(TextUtils.isEmpty(tag))return isSpeak();//tag为空，只需判断当前是否在播放

        Content content = mContentList.peekFirst();
        return isSpeak() && content!=null && tag.equals(content.tag);
    }
    // 根据tag移除队列内容，暂只供第三方app用
    @Override
    public void removeContent(String tag){
        if(TextUtils.isEmpty(tag))return;
        lock.lock();
        Iterator<Content> it = mContentList.iterator();
        Content next = null;
        while (it.hasNext()){
            if(tag.equals(next.tag)){
                it.remove();
                checkSendThirthAppListener(next, VoiceService.STATUS_CHANGED_VALUE_CANCEL);
            }
        }
        lock.unlock();
    }

    /**
     * 停止播放，并清空队列
     */
    @Override
    public void stopSpeak(){
        mHandler.sendEmptyMessage(MSG_STOP);
    }

    private void doStopTalk(){
        lock.lock();
        mIsTalking = false;
        // todo 是否需要判断状态
//        if(DuerSDKFactory.getDuerSDK().getSpeech().isSpeaking()) {
        DuerSDKFactory.getDuerSDK().getSpeech().stop();
        DuerSDKFactory.getDuerSDK().getSpeech().closeTTS();
//        }
        clearList();
        lock.unlock();
    }

    // 主动清除队列，并发送回调
    private void clearList(){
        MLog.d(TAG, "clearList, list size:"+mContentList.size());
        for (Content content : mContentList) {
            checkSendThirthAppListener(content, VoiceService.STATUS_CHANGED_VALUE_CANCEL);
        }
        mContentList.clear();
    }
    // 检查并发送结果到第三方app
    private void checkSendThirthAppListener(Content content, int status){
        if(!TextUtils.isEmpty(content.tag)){// 来自第三方应用
            VoiceService.trySendVoiceStatusCommand(content.tag, status);
        }
    }

    // 播放下一段内容
    private void talkNext(){
        if(!mContentList.isEmpty() && !mIsTalking) {
            mIsTalking = true;
            BdTTS.Content content = mContentList.getFirst();
            // todo 是否需要判断状态
            DuerSDKFactory.getDuerSDK().getSpeech().closeTTS();
            DuerSDKFactory.getDuerSDK().getSpeech().openTTS();
            playVoice(content);
            if(content.needDisplay){
                BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_ADDITION_MSG, 0, content.text));
            }
        }
    }

    private void playVoice(BdTTS.Content content){
        // todo 是否需要判断状态
        String uuid = UUID.randomUUID().toString();
        DuerSDKFactory.getDuerSDK().getSpeech().play(content.text, uuid);
        checkSendThirthAppListener(content, VoiceService.STATUS_CHANGED_VALUE_START);
    }

    /**
     * 显示并立即播放
     * @param text
     */
    @Override
    public void talk(@NonNull String text){//setWords
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_NOW, new BdTTS.Content(text, true, null)));
    }

    /**
     * 立即播放，不显示
     * @param text
     */
    @Override
    public void talkWithoutDisplay(String text){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_NOW_WITHOUT_DISPLAY, new BdTTS.Content(text, false, null)));
    }

    @Override
    public void talk(String tts, String output) {
        // todo
    }

    @Override
    public void talkDelay(String tts, String output, int delay) {
        //todo
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TALK_NOW, new BdTTS.Content(tts, true, null)), delay);
    }

    /**
     * 顺序追加文本，等待显示&播放
     * @param text
     */
    @Override
    public void talkSerial(String text){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_SERIAL, new BdTTS.Content(text, true, null)));
    }

    /**
     *
     * @param text
     * @param tag 本次语音标签，由{@link VoiceTagger}类创建
     */
    public void talkThirdApp(String text, String tag){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_THIRD_APP, new BdTTS.Content(text, true, tag)));
    }

    /**
     * 顺序追加文本，等待播放，不显示
     * @param tag 本次语音标签，由{@link VoiceTagger}类创建
     */
    public void talkThirdAppWithoutDisplay(String text, String tag){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_THIRD_APP_WITHOUT_DISPLAY, new BdTTS.Content(text, false, tag)));
    }

    @Override
    public void parseSemanticToTTS(String semantic) {

    }

    private void pushSplots(@NonNull final BdTTS.Content newContent){
        if(newContent==null || TextUtils.isEmpty(newContent.text))return;

        if(newContent.text.length() <= SPEECH_MAX_LENGTH){//正常入队
            BdTTS.Content lastContent;
            if((lastContent = mContentList.peekLast())!=null && lastContent.equals(newContent))return;//忽略同一个播放来源的同一段文字

            addContent2List(newContent);

        }else {// 截成多片入队
            int sliceTotal = (int) Math.ceil(newContent.text.length() / (float)SPEECH_MAX_LENGTH);
            int sliceNo = 0;
            while (sliceNo<sliceTotal) {
                if(sliceNo+1==sliceTotal){// 最后一截
                    addContent2List(new BdTTS.Content(newContent.text.substring(SPEECH_MAX_LENGTH * sliceNo), newContent.needDisplay, newContent.tag));
                }else {
                    addContent2List(new BdTTS.Content(newContent.text.substring(SPEECH_MAX_LENGTH * sliceNo, SPEECH_MAX_LENGTH * (sliceNo + 1)), newContent.needDisplay, newContent.tag));
                }
                sliceNo++;
            }
        }
    }
    // 将内容按照助手消息优先的原则入队，助手消息可插非助手消息的队
    private void addContent2List(BdTTS.Content content){
        if(TextUtils.isEmpty(content.tag)){//来自助手的消息，放在前面排队。
            int size = mContentList.size();
            Content temp;
            int thirthPos = 0;
            for (int i = 0; i < size; i++) {
                temp = mContentList.get(i);
                if(!TextUtils.isEmpty(temp.tag)){//找到了最前面的非助手消息
                    thirthPos = i;
                    break;
                }
            }
            if(thirthPos==0){//全是助手消息
                mContentList.addLast(content);

            }else {
                mContentList.add(thirthPos, content);
            }
        }else {//来自第三方app消息，直接放在队尾
            mContentList.addLast(content);
        }
    }

    private static class Content {
        private String text;
        private boolean needDisplay;
        private String tag;// 助手的消息tag为空，第三方app的不为空

        public Content(String text, boolean needDisplay, String tag) {
            this.text = text;
            this.needDisplay = needDisplay;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Content content = (Content) o;

            if (needDisplay != content.needDisplay) return false;
            if (text != null ? !text.equals(content.text) : content.text != null) return false;
            return tag != null ? tag.equals(content.tag) : content.tag == null;
        }

        @Override
        public int hashCode() {
            int result = text != null ? text.hashCode() : 0;
            result = 31 * result + (needDisplay ? 1 : 0);
            result = 31 * result + (tag != null ? tag.hashCode() : 0);
            return result;
        }
    }
}
