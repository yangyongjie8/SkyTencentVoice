package com.skyworthdigital.voice.tencent_module;

import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.dingdang.utils.LedUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.sdk.VoiceService;
import com.tencent.ai.sdk.tts.ITtsInitListener;
import com.tencent.ai.sdk.tts.ITtsListener;
import com.tencent.ai.sdk.tts.TtsSession;
import com.tencent.ai.sdk.utils.ISSErrors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ives 2019/5/29
 */

public class TxTTS extends AbsTTS {
    private static final String TAG = "TxTTS";
    private TtsSession mTTSSession = null;
    private boolean mIsSpeak = false;
    private MyTTSListener myTTSListener;

    private LinkedList<TxTTS.Content> mContentList = new LinkedList<>();//第三方消息只会有一句话在队列里，但可能被切成多个。
    private ReentrantLock lock = new ReentrantLock(true);
    private Handler mHandler;
    private static final int SPEECH_MAX_LENGTH = 512;//一次合成所允许的最长字符数
    private Content lastText;//最近一次播放的内容。用于叮当主动中断后仍然会调用onComplete导致的与百度版的不一致现象，从而引起的onComplete中getFirst()时已是空报出异常。

    public static AbsTTS getInstance(MyTTSListener listener) {
        if (mInstance[1] == null) {
            synchronized (TxTTS.class) {
                if(mInstance[1]==null) {
                    mInstance[1] = new TxTTS(listener);
                }
            }
        }
        return mInstance[1];
    }

    private TxTTS(MyTTSListener listener) {
        myTTSListener = listener;
        initTTS();

        HandlerThread handlerThread = new HandlerThread(TxTTS.class.getSimpleName());
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper(), mProcCallback);
    }

    private void initTTS() {
        ITtsInitListener ttSInitListener = new ITtsInitListener() {
            @Override
            public void onTtsInited(boolean state, int errId) {
                String msg = "";
                if (state) {
                    msg = "初始化成功";
                } else {
                    msg = "初始化失败，errId ：" + errId;
                }

                Log.d(TAG, msg);
                //printLog(msg);
            }
        };
        mTTSSession = new TtsSession(VoiceApp.getInstance(), ttSInitListener, "");
        mTTSSession.setStreamType(AudioManager.STREAM_ALARM);
//        mTTSSession.setTTSPlayVolum(10);
    }

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
                    if(!mIsSpeak){
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
                    if(!mIsSpeak){
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
                    if(!mIsSpeak){
                        talkNext();
                    }
                    break;
                default:
                    Log.e("Robot", "invalid msg what:"+msg.what);
                    break;
            }
            return true;
        }
    };

    public void close() {
        // 销毁Session
        if (null != mTTSSession) {
            mTTSSession.stopSpeak();
            mTTSSession.release();
            mTTSSession = null;
        }
    }

    public void stopSpeak() {
        if (null != mTTSSession && mIsSpeak) {
            mIsSpeak = false;
            Log.d(TAG, "stop tts");
            mHandler.sendEmptyMessage(MSG_STOP);
            myTTSListener.onChange(STATUS_TALKOVER);
        }
    }

    public boolean isSpeak() {
        return mIsSpeak;
    }
    private void doStopTalk(){
        lock.lock();
        mIsSpeak = false;
        mTTSSession.stopSpeak();
        lastText = mContentList.peekFirst();
        clearList();
        lock.unlock();
    }
    // 主动清除队列，并发送回调
    private void clearList(){
        for (Content content : mContentList) {
//            checkSendThirthAppListener(content, VoiceService.STATUS_CHANGED_VALUE_CANCEL);
        }
        mContentList.clear();
    }

    // 检查并发送结果到第三方app
    private void checkSendThirthAppListener(Content content, int status){
        if(!TextUtils.isEmpty(content.tag)){// 来自第三方应用
//            VoiceService.trySendVoiceStatusCommand(content.tag, status);
        }
    }

    // 播放下一段内容
    private void talkNext(){
        if(!mContentList.isEmpty() && !mIsSpeak && mTTSSession!=null) {
            mIsSpeak = true;
            TxTTS.Content content = mContentList.getFirst();
            mTTSSession.stopSpeak();
            // 设置是否需要播放
            int ret = mTTSSession.setParam(TtsSession.TYPE_TTS_PLAYING, TtsSession.TTS_PLAYING);
            if (ret == ISSErrors.TTS_PLAYER_SUCCESS) {
                myTTSListener.onChange(STATUS_TALKING);
                mTTSSession.startSpeak(content.text, mTTSListener);
            }
            if(content.needDisplay){
                myTTSListener.onOutputChange(content.displayText, 0);
            }
        }else if (mTTSSession==null){
            MLog.e(TAG, "mTTSSession is null, check it please.");
        }
    }

    // 播放不显示
    public void talkWithoutDisplay(String text) {
        Log.d(TAG, "talk without display:"+text);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_NOW_WITHOUT_DISPLAY, new TxTTS.Content(text, null, null)));
    }

    public void talk(String tts, String output) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_NOW, new TxTTS.Content(tts, output, null)));
    }

    public void talkDelay(String tts, String output, int delay) {//todo 原来的delay只在显示时延迟，播报不会同步
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_NOW, new TxTTS.Content(tts, output, null)));
    }

    public void talk(String text) {//同 talk(tts,output)
        Log.d(TAG, "talk now:"+text);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_NOW, new TxTTS.Content(text, text, null)));
    }

    /**
     * 顺序追加文本，等待显示&播放
     * @param text
     */
    public void talkSerial(String text){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_SERIAL, new TxTTS.Content(text, text, null)));
    }

    /**
     *
     * @param text
     * @param tag 本次语音标签，由{@link com.skyworthdigital.voice.common.VoiceTagger}类创建
     */
    public void talkThirdApp(String text, String tag){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_THIRD_APP, new TxTTS.Content(text, text, tag)));
    }

    /**
     * 顺序追加文本，等待播放，不显示
     * @param tag 本次语音标签，由{@link com.skyworthdigital.voice.common.VoiceTagger}类创建
     */
    public void talkThirdAppWithoutDisplay(String text, String tag){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_TALK_THIRD_APP_WITHOUT_DISPLAY, new TxTTS.Content(text, null, tag)));
    }

    /**
     * 解析语义数据，并将回复语进行语音合成
     */
    public void parseSemanticToTTS(String semantic) {

        try {
            if (!TextUtils.isEmpty(semantic)) {
                talkWithoutDisplay(semantic);
            }
        } catch (Exception e) {
            Log.e(TAG, "parseSemanticToTTS : " + e.getMessage());
        }
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

    private ITtsListener mTTSListener = new ITtsListener() {
        @Override
        public void onPlayCompleted() {
            String msg = "播放结束：onPlayCompleted";
            mIsSpeak = false;
            Log.i(TAG, msg);

            Content content;
            lock.lock();
            if(mContentList.peek()!=null) {
                content = mContentList.removeFirst();
            }
            lock.unlock();
//            checkSendThirthAppListener(content==null?lastText:content, VoiceService.STATUS_CHANGED_VALUE_FINISH);

            if(mContentList.isEmpty() || !mContentList.getFirst().needDisplay){
//                VoiceManager.getInstance().postEvent(new EventMsg(EventMsg.MSG_ROBOT_SPEECH_OVER));
                myTTSListener.onChange(STATUS_TALKOVER);
            }else {
                mHandler.sendEmptyMessage(MSG_TALK_NEXT);
            }
        }

        @Override
        public void onPlayBegin() {
            String msg = "播放开始：onPlayBegin";
            Log.i(TAG, msg);
            LedUtil.closeHorseLight();
        }

        @Override
        public void onPlayInterrupted() {
            mIsSpeak = false;
            myTTSListener.onChange(STATUS_INTERRUPT);
            String msg = "播放被中断：onPlayInterrupted";
            Log.i(TAG, msg);
            LedUtil.closeHorseLight();
        }

        @Override
        public void onError(int errorCode, String errorMsg) {
            mIsSpeak = false;
            myTTSListener.onChange(STATUS_ERROR);
            String msg = "播报出现错误：onError code=" + errorCode + " errorMsg=" + errorMsg;
            Log.i(TAG, msg);

            Content content = null;
            lock.lock();
            if(mContentList.peek()!=null) {
                content = mContentList.removeFirst();
            }
            clearList();
            lock.unlock();
//            checkSendThirthAppListener(content==null?lastText:content, VoiceService.STATUS_CHANGED_VALUE_OVER);
            LedUtil.closeHorseLight();
        }

        @Override
        public void onProgressReturn(int textindex, int textlen) {
            String msg = "播放进度 - textindex ：" + textindex + ", textlen : " + textlen;
            Log.i(TAG, msg);
        }

        @Override
        public void onProgressRuturnData(byte[] data, boolean end) {
            String msg = "音频流返回 - data size : " + data.length + ", isEnd : " + end;
            Log.i(TAG, msg);
        }
    };

    private void pushSplots(@NonNull final TxTTS.Content newContent){
        if(newContent==null || TextUtils.isEmpty(newContent.text))return;

        if(newContent.text.length() <= SPEECH_MAX_LENGTH){//正常入队
            TxTTS.Content lastContent;
            if((lastContent = mContentList.peekLast())!=null && lastContent.equals(newContent))return;//忽略同一个播放来源的同一段文字

            addContent2List(newContent);

        }else {// 截成多片入队
            int sliceTotal = (int) Math.ceil(newContent.text.length() / (float)SPEECH_MAX_LENGTH);
            int sliceNo = 0;
            while (sliceNo<sliceTotal) {
                if(sliceNo+1==sliceTotal){// 最后一截
                    addContent2List(new TxTTS.Content(newContent.text.substring(SPEECH_MAX_LENGTH * sliceNo), newContent.displayText, newContent.tag));
                }else {
                    addContent2List(new TxTTS.Content(newContent.text.substring(SPEECH_MAX_LENGTH * sliceNo, SPEECH_MAX_LENGTH * (sliceNo + 1)), newContent.displayText, newContent.tag));
                }
                sliceNo++;
            }
        }
    }

    // 将内容按照助手消息优先的原则入队，助手消息可插非助手消息的队
    private void addContent2List(TxTTS.Content content){
        if(TextUtils.isEmpty(content.tag)){//来自助手的消息，尽量放在前面排队。
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
        private String displayText;
        private boolean needDisplay;
        private String tag;// 助手的消息tag为空，第三方app的不为空

        public Content(String text, String displayText, String tag) {
            this.text = text;
            this.needDisplay = !TextUtils.isEmpty(displayText);
            this.displayText = displayText;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Content content = (Content) o;

            if (needDisplay != content.needDisplay) return false;
            if (text != null ? !text.equals(content.text) : content.text != null) return false;
            if (displayText != null ? !displayText.equals(content.displayText) : content.displayText != null)
                return false;
            return tag != null ? tag.equals(content.tag) : content.tag == null;
        }

        @Override
        public int hashCode() {
            int result = text != null ? text.hashCode() : 0;
            result = 31 * result + (displayText != null ? displayText.hashCode() : 0);
            result = 31 * result + (needDisplay ? 1 : 0);
            result = 31 * result + (tag != null ? tag.hashCode() : 0);
            return result;
        }
    }
}
