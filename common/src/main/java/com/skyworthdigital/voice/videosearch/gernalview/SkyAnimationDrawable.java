package com.skyworthdigital.voice.videosearch.gernalview;

import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;

public class SkyAnimationDrawable extends AnimationDrawable {
    private FrameAnimListener mFrameAnimListener;

    public FrameAnimListener getFrameAnimListener() {
        return mFrameAnimListener;
    }

    public void setFrameAnimListener(FrameAnimListener frameAnimListener) {
        this.mFrameAnimListener = frameAnimListener;
    }

    @Override
    public void start() {
        super.start();
        if (mFrameAnimListener != null) {
            mFrameAnimListener.onAnimStart();
        }
        int durationTime = 0;
        for (int i = 0; i < this.getNumberOfFrames(); i++) {
            durationTime += this.getDuration(i);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stop();
                if (mFrameAnimListener != null) {
                    mFrameAnimListener.onAnimEnd();
                }
            }
        }, durationTime);
    }

    @Override
    public void stop() {
        super.stop();
        if (mFrameAnimListener != null) {
            mFrameAnimListener.onAnimEnd();
        }
    }

    public interface FrameAnimListener {
        void onAnimStart();

        void onAnimEnd();
    }
}
