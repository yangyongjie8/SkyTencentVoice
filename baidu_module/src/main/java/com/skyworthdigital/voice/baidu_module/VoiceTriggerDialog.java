package com.skyworthdigital.voice.baidu_module;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.duersdk.voice.VoiceInterface;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.baidu_module.duerbean.DuerBean;
import com.skyworthdigital.voice.baidu_module.duerbean.Resource;
import com.skyworthdigital.voice.baidu_module.duerbean.Speech;
import com.skyworthdigital.voice.baidu_module.paipaiAnim.PaiPaiAnimUtil;
import com.skyworthdigital.voice.baidu_module.robot.BdTTS;
import com.skyworthdigital.voice.baidu_module.stock.Stock;
import com.skyworthdigital.voice.baidu_module.util.ActivityManager;
import com.skyworthdigital.voice.baidu_module.util.GsonUtils;
import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;
import com.skyworthdigital.voice.baidu_module.weather.Weather;
import com.skyworthdigital.voice.baidu_module.weather.WeatherData;
import com.skyworthdigital.voice.baidu_module.weather.WeatherInfo;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.view.Rotate3DAnimation;
import com.skyworthdigital.voice.view.SkyVerticalMarqueeTextview;
import com.skyworthdigital.voice.view.VoiceLine;

import java.util.List;

//import android.graphics.drawable.Animatable;
//import android.support.annotation.Nullable;


public class VoiceTriggerDialog extends Dialog {
    private String TAG = VoiceTriggerDialog.class.getSimpleName();
    private Context mContext;

    private SkyVerticalMarqueeTextview mTextViewOne;
    private TextView mTextViewTip;
    //private FrameLayout mLayoutHead;
    private LinearLayout mlayoutMain;
    private LinearLayout mLayoutDialog;
    private LinearLayout mlayout_stock;
    private LinearLayout mlayout_weather;
    private ImageView mHeadAnimator;
    private ImageView mRecordImg;
    public VoiceLine mVoiceLine;

    private boolean mRecordStart = false;
    private boolean isRobotTalkError = false;
    private Weather mWeather;
    private String mJokeUrl = null;
    public int mRecognizeStatus = 0xff;
    private VoiceModeAdapter mVoiceMode = new VoiceModeAdapter();

    private static final int ROBOT_INPUT = 1;

    private static final int SPEECH_FINISH_DEALY = 6000;//语音播报结束后，显示2秒后消失
    private static final int SPEECH_FINISH_DEALY_LONG = 10000;//带笑话图片和天气的播报结束后，显示16秒后消失
    private static final int SEARCH_DISMISS_DEALY = 30000;//搜索页自动消失的时间
    private int TEXTROBOT_MAX_HEIGHT = 689;
    private int TEXT_LAYOUT_MIN_HEIGHT = 50;
    private static final String TYPE_STOCK = "stock";
    private static final String TYPE_WEATHER = "weather";
    private static final int VOLUME_PLUS = 24;
    private static final int VOLUME_MINUS = 25;

    public VoiceTriggerDialog(Context context) {
        super(context, R.style.voice_trigger_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_voice_trigger);
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MLog.i(TAG, "onStart");
        if (VoiceModeAdapter.isAudioBox()) {
            setTextHint(R.string.str_audiobox_hello);
        } else {
            setTextHint(R.string.str_input_note_hint);
            //BdTTS.getInstance().talk(getContext().getString(R.string.str_input_note));
        }
    }

    @Override
    public void show() {
        if(Thread.currentThread().getId()!= Looper.getMainLooper().getThread().getId()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    VoiceTriggerDialog.super.show();
                    GuideTip tip = GuideTip.getInstance();
                    if (tip != null) {
                        String tipStr = tip.getGuidetips();
                        if (!TextUtils.isEmpty(tipStr)) {
                            mTextViewTip.setText(tipStr);
                        }
                    }
                    showPaiPaiByID(PaiPaiAnimUtil.ID_PAIPAI_HELLO);
                }
            });
        }else {

            super.show();
            GuideTip tip = GuideTip.getInstance();
            if (tip != null) {
                String tipStr = tip.getGuidetips();
                if (!TextUtils.isEmpty(tipStr)) {
                    mTextViewTip.setText(tipStr);
                }
            }
            showPaiPaiByID(PaiPaiAnimUtil.ID_PAIPAI_HELLO);
        }
        MLog.i(TAG, "================>show");
    }

    @Override
    public void dismiss() {
        BdTTS.getInstance().stopSpeak();
//        BdTTS.getInstance().talk(null);
        mHeadAnimator.setVisibility(View.VISIBLE);
        mRecordImg.setVisibility(View.GONE);
        PaiPaiAnimUtil.getInstance().release();
        hideCoversation();
        try {
            if (mWeather != null) {
                mWeather.resetWeatherIcons(getWindow().getDecorView());
            }
            mWeather = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        MLog.i(TAG, "dialog dismiss");
        super.dismiss();
    }

    private void init() {
        if (VoiceApp.getVoiceApp().mScreenWidth <= 1280) {
            TEXTROBOT_MAX_HEIGHT = 459;
            TEXT_LAYOUT_MIN_HEIGHT = 35;
        }
        //LogUtil.log("dialog init");
        mTextViewOne = (SkyVerticalMarqueeTextview) findViewById(R.id.text_one);
        mTextViewTip = (TextView) findViewById(R.id.txt_guide);
        mLayoutDialog = (LinearLayout) findViewById(R.id.layout_dialog);
        mlayout_stock = (LinearLayout) findViewById(R.id.stock);
        mlayout_weather = (LinearLayout) findViewById(R.id.weather);
        mHeadAnimator = (ImageView) findViewById(R.id.img_paipai);
        mlayoutMain = (LinearLayout) findViewById(R.id.layout_main);
        mVoiceLine = (VoiceLine) findViewById(R.id.voiceline);
        mRecordImg = (ImageView) findViewById(R.id.img_record_bg);
        mRecordImg.setVisibility(View.GONE);
        hideCoversation();
    }

    private void stockShow(@NonNull String result) {
        //LogUtil.log("show stock");
        try {
            View view = getWindow().getDecorView();
            new Stock(result).show(view);
            mTextViewTip.setVisibility(View.GONE);
            mlayout_weather.setVisibility(View.GONE);
            mlayout_stock.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean weatherShow(Weather weather, DuerBean bean) {
        WeatherData weatherData = weather.getmWeatherData();
        if (weatherData != null) {
            List<WeatherInfo> info = weatherData.getWeather_info();
            if (info != null && info.size() > 0) {
                weather.downloadIcons(getWindow().getDecorView());
                Speech speech = bean.getResult().getSpeech();
                if (!speech.getContent().isEmpty() && TextUtils.equals(GlobalVariable.TYPE_TEXT, speech.getType())) {
                    mTextViewOne.setMaxHeight(TEXTROBOT_MAX_HEIGHT);
                    mTextViewOne.setDBCText(speech.getContent());
                }
                try {
                    View view = getWindow().getDecorView();
                    weather.show(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mTextViewTip.setVisibility(View.GONE);
                mlayout_weather.setVisibility(View.VISIBLE);
                mlayout_stock.setVisibility(View.GONE);
                return true;
            }
        }
        return false;
    }

    public void coversationShow(String result) {
        try {
            DuerBean duerBean = GsonUtils.getDuerBean(result);
            if (duerBean == null) {
                return;
            }
            Resource resource = duerBean.getResult().getresource();
            if (resource != null && !TextUtils.isEmpty(resource.getType())) {
                switch (resource.getType()) {
                    case TYPE_STOCK:
                        //LogUtil.log("coversationShow stock");
                        Speech speech = duerBean.getResult().getSpeech();
                        if (!speech.getContent().isEmpty() && TextUtils.equals(GlobalVariable.TYPE_TEXT, speech.getType())) {
                            mTextViewOne.setMaxHeight(TEXTROBOT_MAX_HEIGHT);
                            mTextViewOne.setDBCText(speech.getContent());
                        }

                        stockShow(result);
                        break;
                    case TYPE_WEATHER:
                        //LogUtil.log("coversationShow weather");
                        mWeather = new Weather(result);
                        if (weatherShow(mWeather, duerBean)) {
                            break;
                        }
                        hideCoversation();
                        break;
                    default:
                        hideCoversation();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTextHint(int strid) {
        mTextViewOne.setRobotTextMarquee(false);
        mTextViewOne.setRobotTextMarqueeOver(false);
        mTextViewOne.setMaxHeight(TEXTROBOT_MAX_HEIGHT);
        mTextViewOne.setDBCText(getContext().getString(strid));
    }

    public void setTextHint(String str) {
        mTextViewOne.setRobotTextMarquee(false);
        mTextViewOne.setRobotTextMarqueeOver(false);
        mTextViewOne.setMaxHeight(TEXTROBOT_MAX_HEIGHT);
        mTextViewOne.setDBCText(str);
    }

    private void setTextView(int isRobot, String msg) {
        mTextViewOne.setRobotTextMarquee(false);
        mTextViewOne.setRobotTextMarqueeOver(false);
        mTextViewOne.setMaxHeight(TEXTROBOT_MAX_HEIGHT);

        if (isRobot == ROBOT_INPUT) {
            int id = PaiPaiAnimUtil.getInstance().getSpeakIdByMsg(mContext, msg);
            showPaiPaiByID(id);
            String tips = GuideTip.getInstance().getGuidetips();
            if (tips != null) {
                mTextViewTip.setText(tips);
            }
            if (!VoiceModeAdapter.isAudioBox() && id == PaiPaiAnimUtil.ID_PAIPAI_HELLO) {
                mTextViewOne.setDBCText(getContext().getString(R.string.str_input_note_hint));
            } else {
                mTextViewOne.setDBCText(msg);
            }
        } else {
            mTextViewOne.setDBCText(msg);
        }
    }

    public void showTextContent(int isRobot, String msg) {
        if (isRobot == ROBOT_INPUT) {
            setTextView(isRobot, msg);
        } else {
            if (VoiceModeAdapter.isAudioBox()) {
                String endStr = "，";
                int len = endStr.length();
                if (msg.endsWith(endStr)) {
                    msg = msg.substring(0, msg.length() - len);
                }
            }
            setTextView(isRobot, "\"" + msg + "\"");
        }
    }

    public void hideCoversation() {
        mlayoutMain.setVisibility(View.VISIBLE);
        mlayout_stock.setVisibility(View.GONE);
        mlayout_weather.setVisibility(View.GONE);
        mTextViewTip.setVisibility(View.VISIBLE);
        mLayoutDialog.setMinimumHeight(TEXT_LAYOUT_MIN_HEIGHT);
    }

    public void robotSpeechFinish() {
        showPaiPaiByID(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
//        String words = BdTTS.getInstance().getWords();
//        BdTTS.getInstance().talk(null);
        isRobotTalkError = false;
//        if (words != null) {
            if (VoiceModeAdapter.isAudioBox()) {
                if (mVoiceMode.isRecognizeGoOn()) {
                    Intent intent = new Intent(GlobalVariable.ACTION_VOICE_RECO_GOON);
                    mContext.sendBroadcast(intent);
                } else {
                    BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, 0));
                }
            } else {
                boolean isInSearch = ActivityManager.isSeachActivityOn();//WelcomeTip.getInstance().isSearchPage();
                if (isInSearch) {
                    BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, SEARCH_DISMISS_DEALY));
                } else {
                    BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, 0));
                }
            }
//        }
    }

    public void robotSpeechError() {
        showPaiPaiByID(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
        isRobotTalkError = true;
        mTextViewOne.setReadError();

        BdTTS.getInstance().stopSpeak();
        if ((mJokeUrl != null) || (mWeather != null)) {
            BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, SPEECH_FINISH_DEALY_LONG));
        } else if (mTextViewOne.isRobotTextMarquee() && !mTextViewOne.isRobotTextMarqueeOver()) {//语音播放出错了，textview内容很长并且滚动没有结束
            MLog.i(TAG, "等待滚动结束再消失");
        } else if (VoiceModeAdapter.isAudioBox()) {
            if (mVoiceMode.isRecognizeGoOn()) {
                Intent intent = new Intent(GlobalVariable.ACTION_VOICE_RECO_GOON);
                mContext.sendBroadcast(intent);
            } else {
                BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, SPEECH_FINISH_DEALY));
            }
        } else {
            boolean isInSearch = ActivityManager.isSeachActivityOn();//WelcomeTip.getInstance().isSearchPage();
            if (isInSearch || mRecognizeStatus <= BdController.MSG_REC_PARTIAL) {
                BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, SEARCH_DISMISS_DEALY));
            } else {
                BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, SPEECH_FINISH_DEALY));
            }
        }
    }

    public void setReadProgress(int num) {
        mTextViewOne.setmReadCharNo(num);
    }

    public boolean isRobotTalkError() {
        return isRobotTalkError;
    }

    public void jokeShow() {
        //TODO 暂时不使用百度的笑话,等后期产品决定是否运营我们自己的。
    }

    public void showPaiPaiByID(int id) {
        PaiPaiAnimUtil pai = PaiPaiAnimUtil.getInstance();
        if (pai != null) {
            pai.showPaiPai(id, mHeadAnimator);
        }
    }

    public boolean getRecordAnimStatus() {
        return mRecordStart;
    }

    public void setRecordAnimStatus(boolean status) {
        mRecordStart = status;
    }

    public void recordAnimStart() {
        MLog.i(TAG, "recordAnimStart:" + BdController.getInstance().mDuerState.ordinal());

        if (BdController.getInstance().mDuerState.ordinal() >= VoiceInterface.VoiceState.DUER_RESULT.ordinal()) {
            if (BdController.getInstance().mDuerState == VoiceInterface.VoiceState.EXIT) {
                showPaiPaiByID(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
                MLog.i(TAG, "PaiPai:default");
            }
            return;
        }
        PaiPaiAnimUtil.getInstance().pause();
        mRecordStart = true;
        mHeadAnimator.setVisibility(View.GONE);
        mRecordImg.setVisibility(View.GONE);
        Rotate3DAnimation myYAnimation = new Rotate3DAnimation(-90, 0, mRecordImg.getWidth(), mRecordImg.getHeight());
        mRecordImg.startAnimation(myYAnimation);
        myYAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRecordImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //LogUtil.log("onAnimationEnd");
                if (BdController.getInstance().mDuerState == VoiceInterface.VoiceState.EXIT) {
                    mRecordImg.setVisibility(View.GONE);
                    mHeadAnimator.setVisibility(View.VISIBLE);
                    PaiPaiAnimUtil.getInstance().restart();
                } else {
                    mRecordImg.setVisibility(View.VISIBLE);
                    mHeadAnimator.setVisibility(View.GONE);
                }
            }
        });
    }

    public void recordAnimStop() {
        //LogUtil.log("recordAnimStop");
        mHeadAnimator.setVisibility(View.GONE);
        mRecordImg.setVisibility(View.VISIBLE);
        PaiPaiAnimUtil.getInstance().pause();
        Rotate3DAnimation myYAnimation = new Rotate3DAnimation(0, -90, 128, 128);
        mRecordImg.startAnimation(myYAnimation);
        myYAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                animation.cancel();
                mRecordImg.setVisibility(View.GONE);
                mHeadAnimator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //LogUtil.log("onAnimationEnd");
                mRecordImg.setVisibility(View.GONE);
                mHeadAnimator.setVisibility(View.VISIBLE);
                PaiPaiAnimUtil.getInstance().restart();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        MLog.i(TAG, "key:" + event.getKeyCode());
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case VOLUME_PLUS:
                    VolumeUtils.getInstance(VoiceApp.getInstance()).setAlarmVolumePlus(1);
                    return true;
                case VOLUME_MINUS:
                    VolumeUtils.getInstance(VoiceApp.getInstance()).setAlarmVolumeMinus(1);
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
