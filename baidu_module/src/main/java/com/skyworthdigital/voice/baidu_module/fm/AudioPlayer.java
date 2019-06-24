package com.skyworthdigital.voice.baidu_module.fm;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skyworthdigital.voice.baidu_module.R;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.videosearch.gernalview.SkyLoadingView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

class AudioPlayer implements OnBufferingUpdateListener,
        OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
    private MediaPlayer mediaPlayer;
    private SeekBar mSkbProgress;
    private TextView mTxtCurTime, mTxtTotalTime, mTxtClock;
    private ImageView mImageBg;
    private SkyLoadingView mLoadingView;
    private static final int HOUR_TIME = 3600;
    private static final int MINUTE_TIME = 60;
    private static final int TEM_NUM = 10;
    private boolean isPlaying = false;

    AudioPlayer(@NonNull SeekBar skbProgress, @NonNull TextView txtCurtime, @NonNull TextView txtTotaltime, ImageView bg, TextView txtClock, SkyLoadingView loadingView) {
        this.mSkbProgress = skbProgress;
        this.mTxtCurTime = txtCurtime;
        this.mTxtTotalTime = txtTotaltime;
        this.mImageBg = bg;
        this.mTxtClock = txtClock;
        this.mLoadingView = loadingView;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer == null)
                    return;
                if (isPlaying/*mediaPlayer.isPlaying() && !mSkbProgress.isPressed()*/) {
                    handleProgress.removeMessages(0);
                    handleProgress.sendEmptyMessage(0);
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/

    private Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            if (mediaPlayer != null) {
                int position = mediaPlayer.getCurrentPosition() / 1000;
                int duration = mediaPlayer.getDuration() / 1000;

                if (duration > 0) {
                    if (mSkbProgress.getMax() != duration) {
                        mSkbProgress.setMax(duration);
                        mTxtTotalTime.setText(getTimeByInt(duration, true));
                    }
                    mTxtCurTime.setText(getTimeByInt(position, true));
                    mSkbProgress.setProgress(position);// pos);
                    if (mImageBg != null) {
                        if (position % 2 == 0) {
                            mImageBg.setBackgroundResource(R.drawable.fm02);
                        } else {
                            mImageBg.setBackgroundResource(R.drawable.fm01);
                        }
                    }
                }
            }
            if (mTxtClock != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINA);
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String str = formatter.format(curDate);
                mTxtClock.setText(str);
            }
        }
    };

    private void renewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }

    public void play() {
        mediaPlayer.start();
    }

    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    boolean seek(int ms) {
        Log.e("mediaPlayer", "seek:" + ms);
        if (mediaPlayer != null) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (position + ms >= 0 && position + ms <= duration) {
                mediaPlayer.seekTo((position + ms));
                return true;
            }
        }
        return false;
    }

    boolean seekTo(int ms) {
        Log.e("mediaPlayer", "seekTo:" + ms);
        if (mediaPlayer != null) {
            int duration = mediaPlayer.getDuration();
            Log.e("mediaPlayer", "duration:" + duration);
            if (ms >= 0 && ms <= duration) {
                Log.e("mediaPlayer", "seekTo start:" + ms);
                mediaPlayer.seekTo(ms);
                return true;
            }
        }
        return true;
    }

    void playUrl(final String videoUrl) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            } else {
                MLog.i("AudioPlayer", "renew mediaPlayer");
                renewMediaPlayer();
            }
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepareAsync();//prepare之后自动播放
            //mediaPlayer.start();
        } catch (IllegalStateException e) {
            MLog.i("AudioPlayer", "process IllegalStateException");
        } catch (Exception e) {
            e.printStackTrace();
            MLog.i("AudioPlayer", "playUrl exception");
        }
    }


    void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer arg0) {
        arg0.start();
        if (mLoadingView != null) {
            mLoadingView.clearLoading();
        }
        isPlaying = true;
        Log.e("mediaPlayer", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        isPlaying = false;
        Log.e("mediaPlayer", "onCompletion");
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        mSkbProgress.setSecondaryProgress(bufferingProgress * mSkbProgress.getMax() / 100);
        //int currentProgress = mSkbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        //Log.e("mediaPlayer", "onBufferingUpdate:" + "% buffer" + " max:" + mSkbProgress.getMax() + " cur:" + mSkbProgress.getProgress());
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
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        isPlaying = true;
        MLog.i("AudioPlayer", "onSeekComplete");
    }
}