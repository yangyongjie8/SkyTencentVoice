package com.skyworthdigital.voice.baidu_module.fm;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.baidu_module.R;
import com.skyworthdigital.voice.baidu_module.robot.BdTTS;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.scene.ISkySceneListener;
import com.skyworthdigital.voice.scene.SceneJsonUtil;
import com.skyworthdigital.voice.scene.SkyScene;
import com.skyworthdigital.voice.videosearch.gernalview.SkyLoadingView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 播放纯音频的activity，比如远场技能有声读物。
 */
public class SkyAudioPlayActivity extends Activity implements ISkySceneListener {
    private static final String TAG = "SkyAudioPlay";
    private static final int VALUE_PLAY = 0;
    private static final int VALUE_PAUSE = 1;
    private static final int MSG_START_FRESH_SEEKBAR = 105;
    private static final int MSG_DELAY_TO_HIDE = 0x0002;
    private static final long SHOW_SEEK_BAR_DELAY = 1000;
    private static final int INIT_SPEED = 10 * 1000;
    private static final int INIT_COUNT = 3;
    private static final int SPEED_ACCELERATION = 500;
    private static final int HOUR_TIME = 3600;
    private static final int MINUTE_TIME = 60;
    private static final int TEM_NUM = 10;
    private static final int SECONDS = 1000;
    private static final int DELAY_HIDE_TIME = 2 * 1000;

    private ImageView mImageBg, mImagePlay;
    private SeekBar mSkbProgress;
    private TextView mTextName, mTextCurTime, mTextTotalTime, mTextClock, mTextSeekTime;
    private AudioPlayer mAudioPlayer;
    private String mUrl;
    private SkyScene mScene;
    private RelativeLayout mSeekLayout;
    private SkyLoadingView mSkyLoadingView;

    private boolean isPlay = true;
    private int mSeekTime = -1;
    private boolean mIsSeek = false;
    private int mSpeed = 0;
    private int mDownCount = 0;
    private int mCurrentTime = 0;
    private int mTotalTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().setFlags(flags, flags);
        setContentView(R.layout.activity_fm_play);
        this.setTitle("在线音乐播放---hellogv编写");

        mImagePlay = (ImageView) this.findViewById(R.id.btnPlayUrl);
        mImageBg = (ImageView) findViewById(R.id.image_bg);
        mTextName = (TextView) findViewById(R.id.txtName);
        mSeekLayout = (RelativeLayout) findViewById(R.id.current_time_space);
        mTextCurTime = (TextView) findViewById(R.id.txtTimeCurrent);
        mTextTotalTime = (TextView) findViewById(R.id.txtTimeTotal);
        mTextSeekTime = (TextView) findViewById(R.id.seekbar_time);
        mTextClock = (TextView) findViewById(R.id.txtTime);
        mSkyLoadingView = (SkyLoadingView) this.findViewById(R.id.fm_play_loading);
        mSkbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
        mSkbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        mTextName.setText("");

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        mTextClock.setText(str);
        mSkyLoadingView.showLoading();
        mAudioPlayer = new AudioPlayer(mSkbProgress, mTextCurTime, mTextTotalTime, mImageBg, mTextClock, mSkyLoadingView);
        mIsSeek = false;
        mSpeed = 0;
        mDownCount = 0;
        Intent intent = getIntent();
        if (intent.hasExtra(GlobalVariable.FM_URL)) {
            mUrl = intent.getStringExtra(GlobalVariable.FM_URL);
            if (intent.hasExtra(GlobalVariable.FM_NAME)) {
                String name = intent.getStringExtra(GlobalVariable.FM_NAME);
                mTextName.setText(name);
            }
            Log.i(TAG, "audio player activity:" + mUrl);
            mAudioPlayer.playUrl(mUrl);
            isPlay = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(GlobalVariable.FM_URL)) {
            String url = intent.getStringExtra(GlobalVariable.FM_URL);
            Log.i(TAG, "onNewIntent:" + url);
            if (!TextUtils.equals(url, mUrl)) {
                mUrl = url;
                mSkyLoadingView.showLoading();
                if (mAudioPlayer != null) {
                    mAudioPlayer.pause();
                } else {
                    mAudioPlayer = new AudioPlayer(mSkbProgress, mTextCurTime, mTextTotalTime, mImageBg, mTextClock, mSkyLoadingView);
                    mIsSeek = false;
                    mSpeed = 0;
                    mDownCount = 0;
                }
                Log.i(TAG, "refresh audio player activity:" + mUrl);
                if (intent.hasExtra(GlobalVariable.FM_NAME)) {
                    String name = intent.getStringExtra(GlobalVariable.FM_NAME);
                    Log.i(TAG, "refresh name:" + name);
                    mTextName.setText(name);
                }
                mAudioPlayer.playUrl(mUrl);
                isPlay = true;
            }
        }
    }

    private void playerStop() {
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
        }
        isPlay = false;
        if (mScene != null) {
            mScene.release();
            mScene = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume:");
        if (mScene == null) {
            mScene = new SkyScene(this);//菜单进入前台时进行命令注册
        }
        mScene.init(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mScene != null) {
            mScene.release();//不在前台时一定要保证注销
            mScene = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        playerStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playerStop();
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            //this.progress = progress * player.mediaPlayer.getDuration()
            //        / seekBar.getMax();
            //Log.i(TAG, "progress:" + this.progress + " pro:" + progress + " duration:" + player.mediaPlayer.getDuration());
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "onStopTrackingTouch progress:" + progress);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            //player.mediaPlayer.seekTo(progress);
            //Log.i(TAG, "onStopTrackingTouch progress:" + progress);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (isPlay) {
                        isPlay = false;
                        if (mAudioPlayer != null) {
                            mAudioPlayer.pause();
                        }
                        mImagePlay.setBackground(getResources().getDrawable(R.drawable.pause));
                    } else {
                        isPlay = true;
                        if (mAudioPlayer != null) {
                            mAudioPlayer.play();
                        }
                        mImagePlay.setBackground(getResources().getDrawable(R.drawable.play));
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mAudioPlayer != null) {
                        handleSeekKey(true);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mAudioPlayer != null) {
                        handleSeekKey(false);
                    }
                    return true;
                default:
                    break;
            }
        } else if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mIsSeek) {
                    mIsSeek = false;
                    mSpeed = 0;
                    mDownCount = 0;
                    if (mAudioPlayer != null) {
                        mAudioPlayer.seekTo(mSeekTime);
                    }
                    mHandler.removeMessages(MSG_DELAY_TO_HIDE);
                    mHandler.sendEmptyMessageDelayed(MSG_DELAY_TO_HIDE, DELAY_HIDE_TIME);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public String onCmdRegister() {
        try {
            return SceneJsonUtil.getSceneJson(this, R.raw.fmcmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getSceneName() {
        return "SkyAudioPlayer";
    }

    @Override
    public void onCmdExecute(Intent intent) {
        String action = "";
        int value = 0;
        if (intent.hasExtra(DefaultCmds.VALUE)) {
            value = intent.getIntExtra(DefaultCmds.VALUE, 0);
        }
        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            String command = intent.getStringExtra(DefaultCmds.COMMAND);
            Log.i(TAG, "command:" + command);
            switch (command) {
                case "play":
                    if (intent.hasExtra(DefaultCmds.INTENT)) {
                        action = intent.getStringExtra(DefaultCmds.INTENT);
                    }
                    Log.i(TAG, "_intent:" + action);
                    if (DefaultCmds.PLAYER_CMD_PAUSE.equals(action)) {
                        if (VALUE_PLAY == value) {
                            isPlay = true;
                            if (mAudioPlayer != null) {
                                BdTTS.getInstance().talk(this.getString(R.string.str_ok));
                                mAudioPlayer.play();
                            }
                            mImagePlay.setBackground(getResources().getDrawable(R.drawable.play));
                        } else if (VALUE_PAUSE == value) {
                            isPlay = false;
                            if (mAudioPlayer != null) {
                                BdTTS.getInstance().talk(this.getString(R.string.str_ok));
                                mAudioPlayer.pause();
                            }
                            mImagePlay.setBackground(getResources().getDrawable(R.drawable.pause));
                        }
                    } else if (DefaultCmds.PLAYER_CMD_FASTFORWARD.equals(action)) {
                        if (mAudioPlayer != null) {
                            BdTTS.getInstance().talk(this.getString(R.string.str_ok));
                            mAudioPlayer.seek(value * SECONDS);
                        }
                    } else if (DefaultCmds.PLAYER_CMD_BACKFORWARD.equals(action)) {
                        if (mAudioPlayer != null) {
                            BdTTS.getInstance().talk(this.getString(R.string.str_ok));
                            mAudioPlayer.seek((-SECONDS) * value);
                        }
                    } else if (DefaultCmds.PLAYER_CMD_GOTO.equals(action)) {
                        if (mAudioPlayer != null) {
                            BdTTS.getInstance().talk(this.getString(R.string.str_ok));
                            mAudioPlayer.seekTo(value * SECONDS);
                        }
                    }
                    break;
                case "stop":
                    BdTTS.getInstance().talk(this.getString(R.string.str_ok));
                    playerStop();
                    break;
                default:
                    break;
            }

        }

    }

    private void handleSeekKey(boolean flag) {
        seek(flag);
        mSeekLayout.setVisibility(View.VISIBLE);
        Log.i(TAG, "seek show");
    }

    private void seek(boolean up) {
        if (mSeekTime == -1 && mAudioPlayer != null) {
            mSeekTime = mAudioPlayer.getCurrentPosition();
        }

        if (!mIsSeek) {
            mHandler.removeMessages(MSG_START_FRESH_SEEKBAR);
            mIsSeek = true;
        }
        advanceAndRetreat(KeyEvent.ACTION_DOWN, up);
        progressSet(mSeekTime);
        mHandler.removeMessages(MSG_START_FRESH_SEEKBAR);
        mHandler.sendEmptyMessageDelayed(MSG_START_FRESH_SEEKBAR, SHOW_SEEK_BAR_DELAY);
    }

    public void advanceAndRetreat(int keyAction, boolean isAdvance) {
        Log.i(TAG, "advanceAndRetreat..,mSeekTime:" + mSeekTime);
        switch (keyAction) {
            case KeyEvent.ACTION_DOWN:
                if (mDownCount < INIT_COUNT) {
                    mSpeed = INIT_SPEED;
                } else {
                    mSpeed = (mDownCount - INIT_COUNT + 1) * SPEED_ACCELERATION;//
                }
                mDownCount++;
                break;
            case KeyEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP lastPos = " + mSeekTime);
                mSpeed = 0;
                mDownCount = 0;
                break;
            default:
                break;
        }

        if (isAdvance) {
            mSeekTime += mSpeed;
        } else {
            mSeekTime -= mSpeed;
        }
        int mDuration = 0;
        if (mAudioPlayer != null) {
            mDuration = mAudioPlayer.getDuration();
            mTotalTime = mDuration / SECONDS;
        }
        Log.e(TAG, " mDuration = " + mDuration);

        if (mSeekTime > mDuration) {
            mSeekTime = mDuration;
        } else if (mSeekTime <= 0) {
            mSeekTime = 0;
        }

        Log.i(TAG, "mSeekTime:" + mSeekTime);
    }

    private void showSeekTime() {
        Message msg = mHandler.obtainMessage(MSG_START_FRESH_SEEKBAR);
        try {
            if (mAudioPlayer != null) {
                mSeekTime = mAudioPlayer.getCurrentPosition();
                secondaryProgressSet(mSeekTime);
                progressSet(mSeekTime);
                mHandler.sendMessageDelayed(msg, SHOW_SEEK_BAR_DELAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_FRESH_SEEKBAR:
                    showSeekTime();
                    break;
                case MSG_DELAY_TO_HIDE:
                    Log.i(TAG, "seek hide");
                    mSeekTime = -1;
                    mSeekLayout.setVisibility(View.INVISIBLE);
                    mHandler.removeMessages(MSG_START_FRESH_SEEKBAR);
                    break;
                default:
                    break;
            }
        }
    };

    private void updateCurrentTimeView(int currentTime) {
        int startmargin = this.getResources().getDimensionPixelOffset(R.dimen.progress_time_l_margin);
        Log.i(TAG, "startmargin:" + startmargin);
        if (mTotalTime <= 0) {
            return;
        }
        int marginLeft = startmargin;
        marginLeft =
                marginLeft
                        + (mSkbProgress.getWidth() - (mSkbProgress.getPaddingLeft() + mSkbProgress.getPaddingLeft()))
                        * currentTime
                        / mTotalTime;
        RelativeLayout.LayoutParams currentTimeLp = (RelativeLayout.LayoutParams) mSeekLayout.getLayoutParams();
        if (currentTimeLp != null) {
            currentTimeLp.setMarginStart(marginLeft);
            Log.i(TAG, "TimeView:" + marginLeft + " curTime:" + currentTime + " total:" + mTotalTime + " Width:" + mSkbProgress.getWidth() + " left:" + mSkbProgress.getPaddingLeft());
            mSeekLayout.setLayoutParams(currentTimeLp);
        }
        mTextSeekTime.setText(getTimeByInt(currentTime, true));
    }

    public void progressSet(int progress) {
        int currentProgress = progress / SECONDS;
        mCurrentTime = currentProgress;
        if (currentProgress > mSkbProgress.getSecondaryProgress()) {
            mSkbProgress.setSecondaryProgress(currentProgress);
        } else {
            mSkbProgress.setProgress(currentProgress);
        }
        updateCurrentTimeView(currentProgress);
    }

    public void secondaryProgressSet(int progress) {
        int secondaryProgress = progress / SECONDS;
        if (secondaryProgress <= 0) {
            mSkbProgress.setSecondaryProgress(0);
        } else if (secondaryProgress > mSkbProgress.getMax()) {
            mSkbProgress.setSecondaryProgress(mSkbProgress.getMax());
        } else if (mCurrentTime > secondaryProgress) {
            mSkbProgress.setProgress(secondaryProgress);
        }
    }

    private String getTimeByInt(int time, boolean showSeconds) {
        int hour = time / HOUR_TIME;
        int minute = (time - HOUR_TIME * hour) / MINUTE_TIME;
        String strHour;
        if (hour >= TEM_NUM) {
            strHour = "" + hour;
        } else {
            strHour = "0" + hour;
        }
        String strMinute;
        if (minute >= TEM_NUM) {
            strMinute = "" + minute;
        } else {
            strMinute = "0" + minute;
        }
        String strTotalTime = strHour + ":" + strMinute;
        if (showSeconds) {
            int seconds = time % MINUTE_TIME;
            String strSeconds;
            if (seconds >= TEM_NUM) {
                strSeconds = "" + seconds;
            } else {
                strSeconds = "0" + seconds;
            }
            strTotalTime = strTotalTime + ":" + strSeconds;
        }
        return strTotalTime;
    }
}
