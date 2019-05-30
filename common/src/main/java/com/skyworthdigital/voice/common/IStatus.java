package com.skyworthdigital.voice.common;

import android.content.Intent;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;

/**
 * Created by fujiayi on 2017/6/14.
 */

public class IStatus {

    public static final int STATUS_NONE = 2;

    public static final int STATUS_READY = 3;

    public static final int STATUS_RECOGNITION = 5;
    public static final int STATUS_END = 4;
    public static final int STATUS_FINISHED = 6;
    public static final int STATUS_ERROR = 10;
    public static int mRecognizeStatus = IStatus.STATUS_NONE;

    public static final int STATUS_WAITING_READY = 8001;
    public static final int WHAT_MESSAGE_STATUS = 9001;

    public static final int STATUS_WAKEUP_SUCCESS = 7001;
    public static final int STATUS_WAKEUP_EXIT = 7003;

    public static int mAsrErrorCnt = 0;
    private static final int MAX_REMOTE_ASR_ERROR_COUNT = 1;
    private static final int MAX_VOICE_ASR_ERROR_COUNT = 2;

    public static final int SCENE_NONE = 0;
    public static final int SCENE_SEARCHPAGE = 3;
    public static final int SCENE_GLOBAL = 1;
    public static final int SCENE_GIVEN = 2;//特定的一些具体场景
    public static final int SCENE_SHOULD_GIVEN = 5;//特定的一些具体场景
    public static final int SCENE_SHOULD_STOP = 4;

    public static String mScene = null;
    public static int mSceneDetectType = SCENE_NONE;//service中检测到了场景值
    public static int mSceneType = SCENE_NONE;//0:dismiss 1:full 2:small
    public static final long SMALL_DIALOG_PERIOD = 60000;
    public static long mSmallDialogDimissTime = 0;
    public static final String ACTION_RESTART_ASR = "com.skyworthdigital.action.RESTART_ASR";
    //public static final String ACTION_QUIT_ASR = "com.skyworthdigital.action.QUIT_ASR";
    public static final String ACTION_FORCE_QUIT_ASR = "com.skyworthdigital.action.FORCE_QUIT_ASR";
    public static final String ACTION_TTS = "com.skyworthdigital.action.TTS";
    private static boolean mDialogSmall = false;

    public static int getMaxAsrErrorCount() {
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
            return MAX_VOICE_ASR_ERROR_COUNT;
        } else {
            return MAX_REMOTE_ASR_ERROR_COUNT;
        }
    }

    public static void setSceneType(int status) {
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_REMOTE) {
            mSceneType = SCENE_GLOBAL;
            return;
        }
        if (/*mSceneType != status && */(status == SCENE_GIVEN || status == SCENE_SEARCHPAGE)) {
            mSmallDialogDimissTime = System.currentTimeMillis() + SMALL_DIALOG_PERIOD;
        } else if (status == SCENE_SHOULD_STOP) {
            mSmallDialogDimissTime = System.currentTimeMillis() + 4000;
        } else if (status == SCENE_SHOULD_GIVEN) {
            mSmallDialogDimissTime = System.currentTimeMillis() + 2000;
        }
        mSceneType = status;
        MLog.d("IStatus", "setSceneModel:" + mSceneType + "  Time:" + mSmallDialogDimissTime);
    }

    public static int getDialogStatus() {
        return mSceneType;
    }

    public static boolean isInScene() {
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_REMOTE) {
            return false;
        }
        if (System.currentTimeMillis() < mSmallDialogDimissTime
                && (IStatus.mSceneType == SCENE_GIVEN || IStatus.mSceneType == SCENE_SEARCHPAGE)) {
            MLog.d("IStatus", "isInScene");
            return true;
        }
        return false;
    }

    public static void resetDismissTime() {
        MLog.d("IStatus", "resetDismissTime:" + mSmallDialogDimissTime);
        mSmallDialogDimissTime = System.currentTimeMillis() + SMALL_DIALOG_PERIOD;
        mAsrErrorCnt = 0;
        IStatus.mRecognizeStatus = IStatus.STATUS_FINISHED;
        if (isInScene()) {
            Intent tmp = new Intent(IStatus.ACTION_RESTART_ASR);
            VoiceApp.getInstance().sendBroadcast(tmp);
        }
    }

    public static void setDialogSmall(boolean small) {
        mDialogSmall = small;
    }

    public static boolean getDialogSmall() {
        return mDialogSmall;
    }
}
