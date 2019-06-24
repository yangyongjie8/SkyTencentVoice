package com.skyworthdigital.voice.baidu_module.paipaiAnim;

import android.os.Handler;
import android.widget.ImageView;

import com.skyworthdigital.voice.dingdang.utils.MLog;

/**
 * 该类主要用来实现派派熊的帧动画，可以根据当前语音的
 * 状态更新派派熊的帧动画的前景图片序列和背景图片序列。
 * 通过updateFramesWithBg可以切换帧动画序列内容。
 */
class SkyFramesAnimation {
    //帧动画前景控件
    private ImageView mImageView;

    //前景图片序列
    private int[] mFrameRess;

    //前景图片序列对应的显示时间
    private int[] mDurations;

    private static Handler handler = new Handler();
    private Runnable mRunnable;

    //控制动画是否要继续
    private boolean mShouldRun;

    private boolean mPause;
    //帧动画的图片序列的总图片数
    private int mLastFrameNo;

    //当前显示的帧动画的索引号
    private int mpFrameNo;


    /**
     * 帧动画展示
     * pImageView：前景控件
     * pFrameRess：前景控件显示图片序列
     * pDurations：前景图片序列对应的显示时间
     * bgview：背景控件
     * bgRess：背景控件显示的图片序列
     * showbg：是否显示背景
     */
    SkyFramesAnimation(ImageView pImageView, int[] pFrameRess, int[] pDurations) {
        //LogUtil.log("SkyFramesAnimation");
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDurations = pDurations;
        mLastFrameNo = pFrameRess.length - 1;
        mShouldRun = false;
        mpFrameNo = 0;
        mPause = false;
        mImageView.setBackgroundResource(mFrameRess[0]);
        mRunnable = new Runnable() {
            public void run() {
                //LogUtil.log("SkyFramesAnimation mShouldRun:"+mShouldRun);
                if (mShouldRun) {
                    try {
                        if (mpFrameNo <= mLastFrameNo) {
                            if (!mPause) {
                                //LogUtil.log("SkyFramesAnimation:" + mpFrameNo);
                                //Glide.with(MyApplication.getInstance()).load(mFrameRess[mpFrameNo]).into(mImageView);
                                mImageView.setBackgroundResource(mFrameRess[mpFrameNo]);
                            }
                        }
                    } catch (Exception e) {
                        MLog.e("SkyFramesAnimation",  "error" + e.toString());
                    }
                    if (mpFrameNo >= mLastFrameNo)
                        playWithbg(0);
                    else
                        playWithbg(mpFrameNo + 1);
                }
            }
        };
        mShouldRun = true;
        playWithbg(1);
    }

    int[] getFrameRess() {
        return mFrameRess;
    }

    /**
     * 帧动画更新
     * pImageView：前景控件
     * pFrameRess：前景控件显示图片序列
     * pDurations：前景图片序列对应的显示时间
     * bgview：背景控件
     * bgRess：背景控件显示的图片序列
     */
    void updateFramesWithBg(ImageView pImageView, int[] pFrameRess, int[] pDurations) {
        //LogUtil.log("updateFrameRess");
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDurations = pDurations;
        mLastFrameNo = pFrameRess.length - 1;
        mImageView.setBackgroundResource(mFrameRess[0]);
        mpFrameNo = 0;
    }

    /**
     * 帧动画停止展示
     */
    void stop() {
        //LogUtil.log("anim stop");
        mShouldRun = false;
        handler.removeCallbacksAndMessages(null);
        //handler.removeCallbacks(mRunnable);
    }

    void pause() {
        mPause = true;
    }

    void restart() {
        mPause = false;
    }

    private void playWithbg(final int pFrameNo) {
        mpFrameNo = pFrameNo;
        handler.postDelayed(mRunnable, mDurations[mpFrameNo]);
    }
}
