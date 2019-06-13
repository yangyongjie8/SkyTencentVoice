package com.skyworthdigital.voice.tencent_module.domains.baike;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.view.ExpandTextView;

import java.util.List;


public class SkyBaikeVideoActivity extends Activity {
    private final static String TAG = "SkyBaikeVideoActivity";
    private RelativeLayout mPageLayout;
    private VideoView mVideoView;
    private RelativeLayout mVideoLayout;
    private TextView mTitle;
    private ExpandTextView mIntroduction;
    private List<String> mVideoUrls;
    private ImageView mVideoBg, mVideoLoading;
    private boolean fullscreen = false;
    private RelativeLayout mLayoutRight;
    private RelativeLayout mSeekLayout;
    private boolean isPlay = true;
    private int mSeekTime = -1;
    private boolean mIsSeek = false;
    private int mSpeed = 0;
    private int mDownCount = 0;
    private int mCurrentTime = 0;
    private int mCurPlayPos = 0;
    private int mTotalTime = 0;
    private static final int MSG_START_FRESH_SEEKBAR = 105;
    private static final int MSG_DELAY_TO_HIDE = 0x0002;
    private static final int MSG_START_FRESH_TIME = 3;
    private static final long SHOW_SEEK_BAR_DELAY = 1000;
    private static final int INIT_SPEED = 10 * 1000;
    private static final int INIT_COUNT = 3;
    private static final int SPEED_ACCELERATION = 500;
    private static final int HOUR_TIME = 3600;
    private static final int MINUTE_TIME = 60;
    private static final int TEM_NUM = 10;
    private static final int SECONDS = 1000;
    private static final int DELAY_HIDE_TIME = 2 * 1000;
    private SeekBar mSkbProgress;
    private TextView mTextCurTime, mTextTotalTime, mTextSeekTime, mTextDate;
    private ImageView mImagePlay;
    private LinearLayout mVideoBar;
    private LinearLayout mLayoutOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_video);

        mPageLayout = (RelativeLayout) findViewById(R.id.page_layout);
        mVideoBar = (LinearLayout) findViewById(R.id.layout_video_bar);
        mTextCurTime = (TextView) findViewById(R.id.txtTimeCurrent);
        mTextTotalTime = (TextView) findViewById(R.id.txtTimeTotal);
        mTextSeekTime = (TextView) findViewById(R.id.seekbar_time);
        mImagePlay = (ImageView) this.findViewById(R.id.video_play_icon);

        mSkbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
        mSkbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        mSeekLayout = (RelativeLayout) findViewById(R.id.current_time_space);
        mTitle = (TextView) findViewById(R.id.shortvideo_title);
        mIntroduction = (ExpandTextView) findViewById(R.id.introduce);
        mVideoView = (VideoView) this.findViewById(R.id.videoView);
        mVideoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
        mVideoBg = (ImageView) findViewById(R.id.before_play_view);
        mVideoBg.setVisibility(View.VISIBLE);
        mVideoLoading = (ImageView) findViewById(R.id.video_loading);
        mLayoutRight = (RelativeLayout) findViewById(R.id.layout_right);
        mLayoutRight.setVisibility(View.VISIBLE);
        mTextDate = (TextView) findViewById(R.id.date);
        mLayoutOther = (LinearLayout) findViewById(R.id.item1);

        AnimationDrawable d = (AnimationDrawable) mVideoLoading.getDrawable();
        d.start();
        mVideoLayout.requestFocus();
        Intent intent = getIntent();
        if (intent.hasExtra("list")) {
            mVideoUrls = (List<String>) intent.getSerializableExtra("list");
            if (intent.hasExtra("title")) {
                String name = intent.getStringExtra("title");
                mTitle.setText(name);
            }
            if (intent.hasExtra("note")) {
                String name = intent.getStringExtra("note");
                mIntroduction.setText("简介：\n" + name);
                mIntroduction.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
            Log.i(TAG, "mVideoUrl:" + mVideoUrls.get(0));

            mCurPlayPos = 0;
            Uri uri = Uri.parse(mVideoUrls.get(0));
            //设置视频控制器
            mVideoView.setMediaController(new MediaController(this));

            //播放完成回调
            mVideoView.setOnCompletionListener(new MyPlayerOnCompletionListener());

            //设置视频路径
            mVideoView.setVideoURI(uri);
            mVideoView.setOnPreparedListener(new MyPlayerOnPreparedListener());

            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    autoFinish();
                    return false;
                }
            });
            mVideoView.setMediaController(null);
            mHandler.sendEmptyMessageDelayed(MSG_START_FRESH_TIME, SHOW_SEEK_BAR_DELAY);
        } else {
            finish();
        }
        mTextDate.setText(StringUtils.getDateString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("list")) {
            mVideoUrls = (List<String>) intent.getSerializableExtra("list");
            String url = mVideoUrls.get(0);
            Log.i(TAG, "onNewIntent:" + url);

            mVideoBg.setVisibility(View.VISIBLE);
            mVideoLoading = (ImageView) findViewById(R.id.video_loading);
            AnimationDrawable d = (AnimationDrawable) mVideoLoading.getDrawable();
            d.start();
            mCurPlayPos = 0;

            if (intent.hasExtra("title")) {
                String name = intent.getStringExtra("title");
                mTitle.setText(name);
            }
            if (intent.hasExtra("note")) {
                String name = intent.getStringExtra("note");
                mIntroduction.setText("简介：\n" + name);
            }
            Log.i(TAG, "mVideoUrl:" + url);

            Uri uri = Uri.parse(url);
            //设置视频控制器
            mVideoView.setMediaController(new MediaController(this));

            //播放完成回调
            mVideoView.setOnCompletionListener(new MyPlayerOnCompletionListener());

            //设置视频路径
            mVideoView.setVideoURI(uri);
            mVideoView.setOnPreparedListener(new MyPlayerOnPreparedListener());
            //开始播放视频
            //mVideoView.start();
            mVideoView.setOnClickListener(null);

            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    autoFinish();
                    return false;
                }
            });
            mVideoView.setMediaController(null);
        }
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            autoFinish();
        }
    }

    class MyPlayerOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mVideoBg.setVisibility(View.GONE);
            if (mVideoLoading != null) {
                ((AnimationDrawable) mVideoLoading.getDrawable()).stop();
            }
            mVideoLoading.setVisibility(View.GONE);
            mediaPlayer.start();
        }
    }

    private void autoFinish() {
        if (mCurPlayPos >= mVideoUrls.size() - 1) {
            this.finish();
        } else {
            mCurPlayPos += 1;
            String url = mVideoUrls.get(mCurPlayPos);
            mVideoBg.setVisibility(View.VISIBLE);
            mVideoLoading = (ImageView) findViewById(R.id.video_loading);
            AnimationDrawable d = (AnimationDrawable) mVideoLoading.getDrawable();
            d.start();
            mCurPlayPos = 0;

            Log.i(TAG, "mVideoUrl:" + url);

            Uri uri = Uri.parse(url);
            //设置视频控制器
            mVideoView.setMediaController(new MediaController(this));

            //播放完成回调
            mVideoView.setOnCompletionListener(new MyPlayerOnCompletionListener());

            //设置视频路径
            mVideoView.setVideoURI(uri);
            mVideoView.setOnPreparedListener(new MyPlayerOnPreparedListener());
            //开始播放视频
            //mVideoView.start();
            mVideoView.setOnClickListener(null);

            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    autoFinish();
                    return false;
                }
            });
            mVideoView.setMediaController(null);
        }
    }

    @Override
    public void onBackPressed() {
        if (fullscreen) {
            mHandler.sendEmptyMessageDelayed(MSG_DELAY_TO_HIDE, 0);
            mPageLayout.setBackground(this.getResources().getDrawable(R.drawable.review_shortvideo_bg));
            mLayoutRight.setVisibility(View.VISIBLE);
            mVideoLayout.setBackground(this.getResources().getDrawable(R.drawable.short_video_play_space_focus));

            int width = getResources().getDimensionPixelSize(R.dimen.dip_670);
            int height = getResources().getDimensionPixelSize(R.dimen.dip_386);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width,height);//1004, 580);
            int left = getResources().getDimensionPixelSize(R.dimen.dip_113);
            int top = getResources().getDimensionPixelSize(R.dimen.dip_200);
            lp.setMargins(left,top,0,0);//104, 261, 0, 0);
            mVideoLayout.setLayoutParams(lp);
            fullscreen = false;
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    //LogUtil.log("ok key ,fullscreen:" + fullscreen);
                    View rootview = this.getWindow().getDecorView();
                    int focusId = rootview.findFocus().getId();
                    if (focusId == R.id.introduce) {
                        MLog.d(TAG, "curfocus:" + mIntroduction);
                        return super.dispatchKeyEvent(event);
                    }
                    if (focusId != R.id.videoLayout) {
                        //LogUtil.log("invalid" + fullscreen);
                        return true;
                    }

                    if (!fullscreen) {
                        mPageLayout.setBackgroundColor(this.getResources().getColor(R.color.black));
                        mVideoLayout.setBackground(this.getResources().getDrawable(R.drawable.transparent));
                        RelativeLayout.LayoutParams lp =
                                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        lp.setMarginStart(10);
                        lp.setMarginEnd(10);
                        mLayoutRight.setVisibility(View.GONE);
                        lp.setMargins(0, 0, 0, 0);
                        mVideoLayout.setLayoutParams(lp);
                        fullscreen = true;//改变全屏/窗口的标记
                        MLog.d(TAG, "curfocus:" + getCurrentFocus() + " " + mIntroduction);
                        return true;
                    }
                    mHandler.removeMessages(MSG_DELAY_TO_HIDE);
                    mVideoBar.setVisibility(View.VISIBLE);
                    if (isPlay) {
                        isPlay = false;
                        if (mVideoView != null) {
                            mVideoView.pause();
                        }
                        mImagePlay.setBackground(getResources().getDrawable(R.drawable.pause));
                    } else {
                        mHandler.sendEmptyMessageDelayed(MSG_DELAY_TO_HIDE, DELAY_HIDE_TIME);
                        isPlay = true;
                        if (mVideoView != null) {
                            mVideoView.start();
                        }
                        mImagePlay.setBackground(getResources().getDrawable(R.drawable.play));
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (fullscreen) {
                        handleSeekKey(true);
                    } else {
                        mLayoutRight.requestFocus();
                        mVideoLayout.setBackground(this.getResources().getDrawable(R.drawable.short_video_play_space_default));
                        mIntroduction.requestFocus();
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (fullscreen) {
                        handleSeekKey(false);
                    } else {
                        mVideoLayout.requestFocus();
                        mVideoLayout.setBackground(this.getResources().getDrawable(R.drawable.short_video_play_space_focus));
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
                    if (mVideoView != null) {
                        mVideoView.seekTo(mSeekTime);
                    }
                    mHandler.removeMessages(MSG_DELAY_TO_HIDE);
                    mHandler.sendEmptyMessageDelayed(MSG_DELAY_TO_HIDE, DELAY_HIDE_TIME);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void handleSeekKey(boolean flag) {
        seek(flag);
        mHandler.removeMessages(MSG_DELAY_TO_HIDE);
        mVideoBar.setVisibility(View.VISIBLE);
        mSeekLayout.setVisibility(View.VISIBLE);
        Log.i(TAG, "seek show");
    }

    private void seek(boolean up) {
        if (mSeekTime == -1 && mVideoView != null) {
            mSeekTime = mVideoView.getCurrentPosition();
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

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_FRESH_SEEKBAR:
                    showSeekTime();
                    break;
                case MSG_DELAY_TO_HIDE:
                    Log.i(TAG, "seek hide");
                    mSeekTime = -1;
                    mVideoBar.setVisibility(View.INVISIBLE);
                    mSeekLayout.setVisibility(View.INVISIBLE);
                    mHandler.removeMessages(MSG_START_FRESH_SEEKBAR);
                    break;
                case MSG_START_FRESH_TIME:
                    int position = mVideoView.getCurrentPosition() / 1000;
                    int duration = mVideoView.getDuration() / 1000;

                    if (duration > 0) {
                        if (mSkbProgress.getMax() != duration) {
                            mSkbProgress.setMax(duration);
                            mTextTotalTime.setText(getTimeByInt(duration, true));
                        }
                        mTextCurTime.setText(getTimeByInt(position, true));
                        mSkbProgress.setProgress(position);// pos);
                    }
                    mHandler.sendEmptyMessageDelayed(MSG_START_FRESH_TIME, SHOW_SEEK_BAR_DELAY);
                    break;
                default:
                    break;
            }
        }
    };

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
        if (mVideoView != null) {
            mDuration = mVideoView.getDuration();
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
            if (mVideoView != null) {
                mSeekTime = mVideoView.getCurrentPosition();
                secondaryProgressSet(mSeekTime);
                progressSet(mSeekTime);
                mHandler.sendMessageDelayed(msg, SHOW_SEEK_BAR_DELAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "onStopTrackingTouch progress:" + progress);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Log.i(TAG, "onStopTrackingTouch progress:" + progress);
        }
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ImagePipeline mImagePipeline = Fresco.getImagePipeline();
        if (mImagePipeline != null) {
            mImagePipeline.clearMemoryCaches();
        }
        MLog.i("shortvideo", "onDestroy");
    }
}
