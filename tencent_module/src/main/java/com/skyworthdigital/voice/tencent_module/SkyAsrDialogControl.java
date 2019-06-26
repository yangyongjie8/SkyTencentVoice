package com.skyworthdigital.voice.tencent_module;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.skyworthdigital.voice.AbsDialog;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.tencent_module.domains.tv.TvlistDialog;

import java.util.List;


/**
 * Created by SDT03046 on 2018/7/19.
 */

public class SkyAsrDialogControl extends AbsDialog {
    public SkyAsrDialog mAsrDialog = null;
    private static final String TAG = "UI";
    public static final int MSG_DISMISS = 2;
    public static final int MSG_DISMISS_USER_INPUT = 3;
    public static final int MSG_SHOW_TVDIALOG = 154;
    public static final int MSG_HIDE_TVDIALOG = 155;
    private int mTTSStatus = AbsTTS.STATUS_TALKOVER;
    private TvlistDialog mTvdialog = null;
    private boolean isTvDialog = false;//这个赋值似乎并不完全准确
    //public boolean isScene = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DISMISS:
                    dismiss();
                    break;
                case MSG_DISMISS_USER_INPUT:
                    if (mAsrDialog != null && mAsrDialog.isShowing() && IStatus.mSceneType == IStatus.SCENE_GIVEN) {
                        mAsrDialog.dialogRefreshUI("", 0);
                    }
                    break;
                case MSG_SHOW_TVDIALOG:
                    mTvdialog = new TvlistDialog(VoiceApp.getInstance(), (List<AsrResult.AsrData.TvProgramsObj>) msg.obj);
                    mTvdialog.getWindow().setType(WindowManager.LayoutParams.LAST_SYSTEM_WINDOW);
                    int flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
                    mTvdialog.getWindow().setFlags(flags, flags);
                    MLog.d(TAG, "tvdialog show");
                    mTvdialog.show();
                    break;
                case MSG_HIDE_TVDIALOG:
                    if (mTvdialog != null) {
                        mTvdialog.dismiss();
                        mTvdialog = null;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public boolean isTvDialog() {
        return isTvDialog;
    }

    public void setTvDialog(boolean tvDialog) {
        isTvDialog = tvDialog;
    }

    private void dismiss() {
        if (mAsrDialog != null) {
            //SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String dateStr = dateformat.format(IStatus.mSmallDialogDimissTime);
            Context ctx = VoiceApp.getInstance();
            Log.i(TAG, "stopVoiceTriggerDialog ttsstatus:" + mTTSStatus + " check:" + IStatus.mAsrErrorCnt + " dialog:" + IStatus.mSceneType + " " + System.currentTimeMillis() + " " + " detect:" + IStatus.mSceneDetectType);
            if (com.skyworthdigital.voice.VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
                if ((IStatus.mSceneType == IStatus.SCENE_GIVEN || IStatus.mSceneType == IStatus.SCENE_SEARCHPAGE) && System.currentTimeMillis() >= IStatus.mSmallDialogDimissTime) {
                    if (IStatus.mSceneType == IStatus.SCENE_SEARCHPAGE) {
                        TxTTS.getInstance(null).talk(ctx.getString(R.string.exit_note));
                    } else {
                        TxTTS.getInstance(null).talk(ctx.getString(R.string.exit_note), "");
                    }
                    IStatus.setSceneType(IStatus.SCENE_NONE);
                    //Intent intent = new Intent("com.skyworthdigital.action.FORCE_QUIT_ASR");
                    //VoiceApp.getInstance().sendBroadcast(intent);
                    dialogDismiss(3000);
                } else if (mAsrDialog.isShowing() && IStatus.mSceneDetectType != IStatus.SCENE_NONE && (IStatus.mSceneType == IStatus.SCENE_GLOBAL && IStatus.mSceneType != IStatus.SCENE_SHOULD_STOP)) {
                    if (mTTSStatus == AbsTTS.STATUS_TALKING || (IStatus.mRecognizeStatus == IStatus.STATUS_ERROR && IStatus.mAsrErrorCnt < IStatus.getMaxAsrErrorCount())) {
                        dialogDismiss(2000);
                        return;
                    } else if (BeeSearchParams.getInstance().isInSearchPage()) {
                        IStatus.setSceneType(IStatus.SCENE_SEARCHPAGE);
                        mAsrDialog.dialogResize(false);
                    } else {
                        IStatus.setSceneType(IStatus.SCENE_GIVEN);
                        mAsrDialog.dialogResize(true);
                    }
                    Intent tmp = new Intent(IStatus.ACTION_RESTART_ASR);
                    VoiceApp.getInstance().sendBroadcast(tmp);
                    dialogDismiss(IStatus.SMALL_DIALOG_PERIOD);
                } else if (mAsrDialog.isShowing() && IStatus.mSceneType == IStatus.SCENE_SHOULD_GIVEN) {
                    if (mTTSStatus == AbsTTS.STATUS_TALKING) {
                        dialogDismiss(2000);
                    } else {
                        IStatus.setSceneType(IStatus.SCENE_GIVEN);
                        if (IStatus.isInScene()) {
                            Intent tmp = new Intent(IStatus.ACTION_RESTART_ASR);
                            VoiceApp.getInstance().sendBroadcast(tmp);
                        }
                        mAsrDialog.dialogResize(true);
                    }
                } else if (mAsrDialog.isShowing() && (mTTSStatus == AbsTTS.STATUS_TALKING || (IStatus.mRecognizeStatus == IStatus.STATUS_ERROR && IStatus.mAsrErrorCnt < IStatus.getMaxAsrErrorCount()))
                        || ((IStatus.mSceneType == IStatus.SCENE_GIVEN || IStatus.mSceneType == IStatus.SCENE_SEARCHPAGE) && System.currentTimeMillis() < IStatus.mSmallDialogDimissTime)) {
                    dialogDismiss(3000);
                } else {
                    IStatus.setSceneType(IStatus.SCENE_NONE);
                    //Intent tmp = new Intent(IStatus.ACTION_FORCE_QUIT_ASR);
                    //VoiceApp.getInstance().sendBroadcast(tmp);
                    animStop();
                    mAsrDialog.dismiss();
                    mAsrDialog.cancel();
                    mAsrDialog = null;
                }
            } else {
                if (mAsrDialog.isShowing() && mTTSStatus == AbsTTS.STATUS_TALKING) {
                    dialogDismiss(3000);
                } else {
                    IStatus.setSceneType(IStatus.SCENE_NONE);
                    animStop();
                    mAsrDialog.dismiss();
                    mAsrDialog.cancel();
                    mAsrDialog = null;
                }
            }
        }
    }

    public void showTvDialog(List<AsrResult.AsrData.TvProgramsObj> list) {
        Message msg = new Message();
        msg.what = MSG_SHOW_TVDIALOG;
        msg.obj = list;
        mHandler.removeMessages(MSG_SHOW_TVDIALOG);
        mHandler.sendMessage(msg);
    }

    public void hideTvDialog() {
        if (mTvdialog != null) {
            mHandler.removeMessages(MSG_HIDE_TVDIALOG);
            mHandler.sendEmptyMessage(MSG_HIDE_TVDIALOG);
        }
    }

    public void dialogRefresh(final Context ctx, final AsrResult bean, final String partialresults, final int delay) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (mAsrDialog == null) {
                    show(ctx);
//            return;
                }
                //Log.i(TAG, " " + partialresults);
                if (!TextUtils.isEmpty(partialresults)) {
                    mAsrDialog.dialogRefreshUI(partialresults, delay);
                } else if (bean != null) {
                    mAsrDialog.dialogRefreshResult(bean, delay);
                }
            }
        });
    }

    public void dialogRefreshBg(String url) {
        if (mAsrDialog != null) {
            mAsrDialog.dialogRefreshBg(url);
        }
    }

    public void setSpeechTextView(String txt) {
        if (mAsrDialog != null && !TextUtils.isEmpty(txt)) {
            mAsrDialog.setSpeechTextView(txt, 5);
        }
    }

    public void dialogRefreshDetail(Context ctx, Object obj, int type) {
        if (mAsrDialog != null) {
            mAsrDialog.dialogRefreshDetail(ctx, obj, type);
        }
    }

    @Override
    public void dialogRefreshTips(List<String> tips) {
        Log.i(TAG, "dialogRefreshTips");
        if (mAsrDialog != null) {
            mAsrDialog.dialogRefreshTips(GuideTip.getInstance().getGuidetips());
        }
    }

    public void dialogTxtClear() {
        if (mAsrDialog != null) {
            mAsrDialog.dialogTxtClear();
        }
    }

    public void paipaiRefresh(int status) {
        if (mAsrDialog != null) {
            mTTSStatus = status;
            mAsrDialog.paipaiRefresh(status);
        }
    }

    public void showHeadLoading() {
        if (mAsrDialog != null) {
            mAsrDialog.showHeadLoading();
        }
    }

    public void animStop() {
        if (mAsrDialog != null) {
            mAsrDialog.animStop();
        }
    }

    public void voiceRecordRefresh() {
        if (mAsrDialog != null) {
            mAsrDialog.voiceRecordRefresh();
        }
    }

    public void show(final Context ctx) {
        if (mAsrDialog == null) {
            mAsrDialog = new SkyAsrDialog(ctx);
            mAsrDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
            mAsrDialog.getWindow().setFlags(flags, flags);
            //mAsrDialog.getWindow().setWindowAnimations(R.style.Dialog_Anim_Style);
        }
        Log.i(TAG, "dialog show");
        mAsrDialog.show();
    }

    public void dialogDismiss(long time) {
        Log.i(TAG, "dismiss:" + time);
        Message msg = new Message();
        msg.what = MSG_DISMISS;
        mHandler.removeMessages(MSG_DISMISS);
        mHandler.sendMessageDelayed(msg, time);
    }
}
