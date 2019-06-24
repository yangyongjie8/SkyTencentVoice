package com.skyworthdigital.voice.baidu_module.paipaiAnim;

import android.content.Context;
import android.widget.ImageView;

import com.skyworthdigital.voice.baidu_module.R;

import java.util.Arrays;

/**
 * 派派熊动画公共类
 */
public class PaiPaiAnimUtil {
    public static final int ID_PAIPAI_HELLO = 0;
    private static final int ID_PAIPAI_UNKNOWN = 1;
    private static final int ID_PAIPAI_SPEAKING = 2;
    //public static final int ID_PAIPAI_LISTEN = 3;
    public static final int ID_PAIPAI_DEFAULT = 4;
    private static PaiPaiAnimUtil mPaiPaiAnimInstance = null;
    private SkyFramesAnimation mPaiPaiSceneAnim = null;

    /**
     * 获取实例
     */
    public static PaiPaiAnimUtil getInstance() {
        if (mPaiPaiAnimInstance == null) {
            mPaiPaiAnimInstance = new PaiPaiAnimUtil();
        }
        return mPaiPaiAnimInstance;
    }

    public int getSpeakIdByMsg(Context ctx, String msg) {
        String[] unknownArray = ctx.getResources().getStringArray(R.array.array_unknown);

        if (Arrays.asList(unknownArray).contains(msg)) {
            return ID_PAIPAI_UNKNOWN;
        } else {
            return ID_PAIPAI_SPEAKING;
        }
    }

    private static int[] getPaiPaiFrames(int idx) {
        int[] frames = null;
        switch (idx) {
            case ID_PAIPAI_HELLO:
                frames = PaiPaiAnimVars.IMAGE_HELLOS;
                //LogUtil.log("paipai hello");
                break;
            case ID_PAIPAI_DEFAULT:
                frames = PaiPaiAnimVars.IMAGE_DEFAULTS;
                //LogUtil.log("paipai default");
                break;
            case ID_PAIPAI_SPEAKING:
                frames = PaiPaiAnimVars.IMAGE_SPEAKINGS;
                //LogUtil.log("paipai speaking");
                break;
            case ID_PAIPAI_UNKNOWN:
                frames = PaiPaiAnimVars.IMAGE_UNKNOWNS;
                //LogUtil.log("paipai unknown");
                break;
            default:
                break;
        }
        return frames;
    }

    public void showPaiPai(int idx, ImageView frontImage) {
        //LogUtil.log("showPaiPaiAnim:" + idx);
        int[] frams = getPaiPaiFrames(idx);
        if (frams != null) {
            if (mPaiPaiSceneAnim == null) {
                mPaiPaiSceneAnim = new SkyFramesAnimation(frontImage, frams, PaiPaiAnimVars.IMAGES_DURATION_DEFAULT);
            } else {
                if (!Arrays.equals(mPaiPaiSceneAnim.getFrameRess(), frams)) {
                    mPaiPaiSceneAnim.updateFramesWithBg(frontImage, frams, PaiPaiAnimVars.IMAGES_DURATION_DEFAULT);
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
}
