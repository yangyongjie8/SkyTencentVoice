package com.skyworthdigital.voice.tencent_module.view.paipaianim;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.lang.ref.WeakReference;


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

    //private Runnable mRunnable;

    //控制动画是否要继续
    private boolean mShouldRun;

    private boolean mPause;
    //帧动画的图片序列的总图片数
    private int mLastFrameNo;

    //当前显示的帧动画的索引号
    private int mpFrameNo;

    //当前显示的帧动画的索引号
    private int mAnimId;

    private static final int MSG_FRESH = 1;
    AnimHandler mHandler = new AnimHandler(this);

    /**
     * 帧动画展示
     * pImageView：前景控件
     * pFrameRess：前景控件显示图片序列
     * pDurations：前景图片序列对应的显示时间
     * bgview：背景控件
     * bgRess：背景控件显示的图片序列
     * showbg：是否显示背景
     */
    SkyFramesAnimation(ImageView pImageView, int id, int[] pFrameRess, int[] pDurations) {
        //LogUtil.log("SkyFramesAnimation");
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDurations = pDurations;
        mLastFrameNo = pFrameRess.length - 1;
        mShouldRun = false;
        mpFrameNo = 0;
        mPause = false;
        mAnimId = id;
        if (mImageView == null) {
            Log.i("wyf", "SkyFramesAnimation is null");
        }

        if (pFrameRess == null) {
            Log.i("wyf", "SkyFramesAnimation is null2");
        }
        Log.i("wyf", "SkyFramesAnimation len:" + pFrameRess.length);
        mImageView.setBackgroundResource(mFrameRess[0]);
        /*mRunnable = new Runnable() {
            public void run() {
                //LogUtil.log("SkyFramesAnimation mShouldRun:"+mShouldRun);
                if (mShouldRun) {
                    try {
                        if (mpFrameNo <= mLastFrameNo) {
                            if (!mPause) {
                                //MLog.d("wyf","SkyFramesAnimation:" + mpFrameNo);
                                //Glide.with(MyApplication.getInstance()).load(mFrameRess[mpFrameNo]).into(mImageView);
                                mImageView.setBackgroundResource(mFrameRess[mpFrameNo]);
                            }
                        }
                    } catch (Exception e) {
                        Log.i("paiAnim", "error" + e.toString());
                    }
                    if (mpFrameNo >= mLastFrameNo && mAnimId == PaiPaiAnimUtil.ID_PAIPAI_RECORE) {
                        playWithbg(12);
                    } else if (mpFrameNo >= mLastFrameNo) {
                        playWithbg(0);
                    } else {
                        playWithbg(mpFrameNo + 1);
                    }
                }
            }
        };*/
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
    void updateFramesWithBg(ImageView pImageView, int id, int[] pFrameRess, int[] pDurations) {
        //LogUtil.log("updateFrameRess");
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDurations = pDurations;
        mLastFrameNo = pFrameRess.length - 1;
        mImageView.setBackgroundResource(mFrameRess[0]);
        mpFrameNo = 0;
        mAnimId = id;
    }

    /**
     * 帧动画停止展示
     */
    void stop() {
        MLog.d("wyf", "anim stop");
        mShouldRun = false;
        mPause = true;
        mHandler.removeCallbacksAndMessages(null);
        //handler.removeCallbacks(mRunnable);
    }

    void pause() {
        if (!mPause) {
            mHandler.removeMessages(MSG_FRESH);
            mPause = true;
        }
    }

    void restart() {
        if (mPause) {
            mPause = false;
            mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_FRESH), mDurations[mpFrameNo]);
        }
    }

    private void playWithbg(final int pFrameNo) {
        if (mShouldRun && !mPause) {
            mpFrameNo = pFrameNo;
            //handler.postDelayed(mRunnable, mDurations[mpFrameNo]);
            mHandler.removeMessages(MSG_FRESH);
            mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_FRESH), mDurations[mpFrameNo]);
        }
    }

    private static class AnimHandler extends Handler {
        private final WeakReference<SkyFramesAnimation> mWeakReference;

        AnimHandler(SkyFramesAnimation anim) {
            mWeakReference = new WeakReference<>(anim);
        }

        @Override
        public void handleMessage(Message msg) {
            final SkyFramesAnimation anim = mWeakReference.get();
            if (anim != null) {
                switch (msg.what) {
                    case MSG_FRESH:
                        try {
                            if (anim.mShouldRun) {
                                if (anim.mpFrameNo <= anim.mLastFrameNo) {
                                    if (!anim.mPause) {
                                        //MLog.d("wyf", "SkyFramesAnimation:" + anim.mpFrameNo + " " + this.toString());
                                        anim.mImageView.setBackgroundResource(anim.mFrameRess[anim.mpFrameNo]);
                                    }
                                }
                                if (anim.mpFrameNo >= anim.mLastFrameNo && anim.mAnimId == PaiPaiAnimUtil.ID_PAIPAI_RECORE) {
                                    anim.playWithbg(12);
                                } else if (anim.mpFrameNo >= anim.mLastFrameNo) {
                                    anim.playWithbg(0);
                                } else {
                                    anim.playWithbg(anim.mpFrameNo + 1);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
