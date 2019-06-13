package com.skyworthdigital.voice.tencent_module.view.paipaianim;

import android.util.Log;
import android.widget.ImageView;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;

import java.util.Arrays;

/**
 * 派派熊动画公共类
 */
public class PaiPaiAnimUtil {
    //public static final int ID_PAIPAI_HELLO = 0;
    private static final int ID_PAIPAI_UNKNOWN = 1;
    public static final int ID_PAIPAI_SPEAKING = 2;
    //public static final int ID_PAIPAI_LISTEN = 3;
    public static final int ID_PAIPAI_DEFAULT = 4;
    public static final int ID_PAIPAI_RECORE = 5;
    private SkyFramesAnimation mPaiPaiSceneAnim = null;
    private ImageView mHeadAnimator;
    private boolean mIsRecording = false;

    /**
     * 获取实例
     */
    /*public static PaiPaiAnimUtil getInstance(ImageView headAnimator,  ImageView recordImage) {
        if (mPaiPaiAnimInstance == null) {
            mPaiPaiAnimInstance = new PaiPaiAnimUtil(headAnimator,recordImage);
        }
        return mPaiPaiAnimInstance;
    }*/
    public PaiPaiAnimUtil(ImageView headAnimator) {
        mHeadAnimator = headAnimator;
    }

    private static int[] getPaiPaiFrames(int idx) {
        int[] frames = null;
        switch (idx) {
            //case ID_PAIPAI_HELLO:
            case ID_PAIPAI_DEFAULT:
            case ID_PAIPAI_SPEAKING:
            case ID_PAIPAI_UNKNOWN:
                frames = PaiPaiAnimVars.IMAGE_DEFAULTS;
                //LogUtil.log("paipai default");
                break;
            case ID_PAIPAI_RECORE:
                if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_REMOTE) {
                    frames = PaiPaiAnimVars.IMAGE_RECORDS;
                } else if (IStatus.mSceneType == IStatus.SCENE_GIVEN) {
                    frames = PaiPaiAnimVars.IMAGE_DEFAULTS;
                }
                break;
            default:
                frames = PaiPaiAnimVars.IMAGE_DEFAULTS;
                break;
        }
        return frames;
    }

    public void showPaiPai(int idx) {
        //Log.i("wyf", "showPaiPaiAnim:" + idx);
        int[] frams = PaiPaiAnimVars.IMAGE_DEFAULTS;
        if (mIsRecording) {
            if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_REMOTE) {
                frams = PaiPaiAnimVars.IMAGE_RECORDS;
            } else if (IStatus.mSceneType == IStatus.SCENE_GIVEN) {
                frams = PaiPaiAnimVars.IMAGE_DEFAULTS;
            }
        } else {
            frams = getPaiPaiFrames(idx);
        }
        if (frams != null) {
            if (mPaiPaiSceneAnim == null) {
                mPaiPaiSceneAnim = new SkyFramesAnimation(mHeadAnimator, idx, frams, PaiPaiAnimVars.IMAGES_DURATION_DEFAULT);
            } else {
                if (!Arrays.equals(mPaiPaiSceneAnim.getFrameRess(), frams)) {
                    mPaiPaiSceneAnim.updateFramesWithBg(mHeadAnimator, idx, frams, PaiPaiAnimVars.IMAGES_DURATION_DEFAULT);
                }
            }
        }
    }

    public void release() {
        if (mPaiPaiSceneAnim != null) {
            mPaiPaiSceneAnim.stop();
            mPaiPaiSceneAnim = null;
        }
    }

    public void pause() {
        if (mPaiPaiSceneAnim != null) {
            mPaiPaiSceneAnim.pause();
        }
    }

    public void restart() {
        if (mPaiPaiSceneAnim != null) {
            mPaiPaiSceneAnim.restart();
        }
    }

    public void recordAnimStart() {
        Log.i("wyf", "recordAnimStart:");
        mIsRecording = true;
        showPaiPai(ID_PAIPAI_RECORE);
    }

    public void recordAnimStop() {
        mIsRecording = false;
        showPaiPai(ID_PAIPAI_DEFAULT);
    }

    public boolean isRecordAnim() {
        return mIsRecording;
    }
}
