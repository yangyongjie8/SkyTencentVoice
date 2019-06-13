package com.skyworthdigital.voice.tencent_module.domains.poem;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import android.util.Log;


class PoemAudioPlayer implements OnBufferingUpdateListener,
        OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
    private MediaPlayer mediaPlayer;

    private SkyPoemActivity.MedioPlayListener mMedioPlayListener;

    PoemAudioPlayer(SkyPoemActivity.MedioPlayListener medioPlayListener) {
        this.mMedioPlayListener = medioPlayListener;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }

    public void play() {
        mediaPlayer.start();
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
        if (!TextUtils.isEmpty(videoUrl)) {
            try {
                Log.e("mediaPlayer", "playUrl:" + videoUrl);
                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                } else {
                    //LogUtil.log("renew mediaPlayer");
                    renewMediaPlayer();
                }
                mediaPlayer.setDataSource(videoUrl);
                mediaPlayer.prepareAsync();//prepare之后自动播放
                //mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        Log.e("mediaPlayer", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        Log.e("mediaPlayer", "onCompletion");

    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        Log.e("mediaPlayer", "duration:" + duration + " position:" + position);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            if (duration > 1000 && position / 1000 == duration / 1000) {
                mMedioPlayListener.onCompetion();
            }
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}