package com.skyworthdigital.voice.tencent_module.domains.fm;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.WaveFileReader;
import com.skyworthdigital.voice.videosearch.gernalview.SkyLoadingView;
import com.skyworthdigital.voice.view.Spectrogram;

class AudioPlayer implements OnBufferingUpdateListener,
        OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener {
    private MediaPlayer mediaPlayer = null;
    private TextView mTxtClock;
    private Spectrogram mAudioBarGraph;
    private SkyLoadingView mLoadingView;
    private boolean isPlaying = false;
    private SkyAudioPlayActivity.MedioPlayListener mMedioPlayListener;

    AudioPlayer(Spectrogram audioBarGraph, TextView txtClock, SkyLoadingView loadingView, SkyAudioPlayActivity.MedioPlayListener medioPlayListener) {
        this.mAudioBarGraph = audioBarGraph;
        this.mTxtClock = txtClock;
        this.mLoadingView = loadingView;
        this.mMedioPlayListener = medioPlayListener;
        /*try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }*/
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    private void renewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void play() {
        isPlaying = true;
        mediaPlayer.start();
    }

    boolean seek(int ms) {
        Log.e("mediaPlayer", "seek:" + ms);
        if (mediaPlayer != null) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            Log.e("mediaPlayer", "duration:" + duration);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("mediaPlayer", "playUrl:" + videoUrl);
                    isPlaying = false;
                    //mUrl = videoUrl;
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                    } else {
                        //LogUtil.log("renew mediaPlayer");
                        renewMediaPlayer();
                    }
                    Log.e("mediaPlayer", "renewMediaPlayer ok");
                    mediaPlayer.setDataSource(videoUrl);
                    mediaPlayer.prepareAsync();//prepare之后自动播放
                } catch (IllegalStateException e) {
                    //LogUtil.log("process IllegalStateException");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    void pause() {
        if (mediaPlayer != null) {
            isPlaying = false;
            mediaPlayer.pause();
        }
    }

    void stop() {
        isPlaying = false;
        if (thread != null) {
            isOpenThisActivity = false;
            thread.interrupt();
            thread = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (reader != null) {
            reader.closeReader();
            reader = null;
        }
        data = null;
    }

    int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("mediaPlayer", "onError：" + what + " " + extra);
        Toast.makeText(VoiceApp.getInstance(), "播放出错：" + what, Toast.LENGTH_LONG).show();
        return false;
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
        //////////////
        if (!isOpenThisActivity && thread == null) {
            thread = new Thread(specRun);
            isOpenThisActivity = true;
            thread.start();
        }
        ////////////////
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
        //mSkbProgress.setSecondaryProgress(bufferingProgress * mSkbProgress.getMax() / 100);
        //int currentProgress = mSkbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        //Log.e("mediaPlayer", "onBufferingUpdate:" + "% buffer" + bufferingProgress);
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        isPlaying = true;
        //LogUtil.log("onSeekComplete");
    }

    private static final int HANDLER_SPECTROGRAM = 0;
    private WaveFileReader reader = null;
    private int[] data = null;
    private boolean isOpenThisActivity = false;
    //采样率
    private double samplerate = 0;
    //频谱
    private Thread thread = null;

    private void initWaveData() {
        if (data == null) {
            reader = new WaveFileReader();
            data = reader.initReader("default.wav")[0]; // 获取第一声道
            //获取采样率
            samplerate = reader.getSampleRate();
            mAudioBarGraph.setBitspersample(reader.getBitPerSample());//设置采样点的编码长度
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SPECTROGRAM:
                    if (isPlaying/*mediaPlayer != null && mediaPlayer.isPlaying()*/) {
                        mAudioBarGraph.ShowSpectrogram((int[]) msg.obj, false, samplerate);
                        int position = mediaPlayer.getCurrentPosition();
                        int duration = mediaPlayer.getDuration();
                        Log.e("mediaPlayer", "duration:" + duration + " position:" + position);
                        if (duration > 1000 && position / 1000 == duration / 1000) {
                            isPlaying = false;
                            mMedioPlayListener.onCompetion();
                        }
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Runnable specRun = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            initWaveData();
            while (isOpenThisActivity) {
                long a;
                long T;
                int[] buf;
                int offset = 0;
                if (data != null && reader.isSuccess()) {
                    while (offset < (data.length - Spectrogram.SAMPLING_TOTAL)) {
                        T = System.nanoTime() / 1000000;
                        buf = new int[Spectrogram.SAMPLING_TOTAL];
                        for (int i = 0; i < Spectrogram.SAMPLING_TOTAL; i++) {
                            buf[i] = data[offset + i];
                        }
                        handler.sendMessage(handler.obtainMessage(
                                HANDLER_SPECTROGRAM, buf));
                        offset += (Spectrogram.SAMPLING_TOTAL * 10) / 17;
                        while (true) {
                            a = System.nanoTime() / 1000000;
                            if ((a - T) >= 100)
                                break;
                        }
                        if (!isOpenThisActivity) {
                            return;
                        }
                    }
                }
            }
        }
    };
}