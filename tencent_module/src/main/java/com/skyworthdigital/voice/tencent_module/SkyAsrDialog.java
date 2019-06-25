package com.skyworthdigital.voice.tencent_module;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.dingdang.utils.DialogCellType;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.tencent_module.domains.baike.BaikeInfoCell;
import com.skyworthdigital.voice.tencent_module.domains.sports.cell.SportsInfoCell;
import com.skyworthdigital.voice.tencent_module.domains.sports.cell.SportsScoresCell;
import com.skyworthdigital.voice.tencent_module.guide.GuideDialog;
import com.skyworthdigital.voice.tencent_module.model.TemplateItem;
import com.skyworthdigital.voice.tencent_module.view.paipaianim.PaiPaiAnimUtil;
import com.skyworthdigital.voice.view.SkyVerticalMarqueeTextview;

import java.util.List;


/**
 * Created by SDT03046 on 2018/7/19.
 */

public class SkyAsrDialog extends Dialog {
    private static final String TAG = "UI";
    private SkyVerticalMarqueeTextview mTextViewOne;
    private TextView mTextViewTip;
    private RelativeLayout mlayoutMain;
    private ImageView mHeadAnimator;
    private ImageView mRotateHead;
    private ImageView mRotateShadow;
    private FrameLayout mSceneTxtLayout;
    private TextView mTextViewScene;
    private static final int TEXTROBOT_MAX_HEIGHT = 689;
    private static final int MSG_REFRESH_RESULT = 1;
    private static final int MSG_REFRESH_UI = 3;

    private static final int MSG_ANIM_START = 5;
    private static final int MSG_ANIM_STOP = 6;
    private static final int MSG_PAIPAI_DEFAULT = 7;
    private static final int MSG_PAIPAI_SPEAK = 8;
    //private static final int MSG_DIALOG_DISMISS = 9;
    private static final int MSG_SHOW_LAYOUT = 10;
    private static final int MSG_SHOW_TIPS = 11;
    private static final int MSG_CONTAINER_REMOVE = 12;
    private static final int MSG_DIALOG_RESIZE = 13;
    private static final int MSG_DOWNLOAD_BG = 14;
    private static final int MSG_SHOW_EXE_ANIM = 16;
    private static final int MSG_SHOW_GUIDE_DIALOG = 17;

    private PaiPaiAnimUtil mPaiPaiAnimUtil;
    private GuideTip mGuideTip = GuideTip.getInstance();
    private FrameLayout mContainer;
    private Context mContext;
    private View mContainerCell;
    private boolean mTypeBig = true;
    private GuideDialog mGuideDialog = null;

    public SkyAsrDialog(Context context) {
        super(context, R.style.asr_dialog);
        mContext = context;
    }

    @Override
    public void show() {
        super.show();
        mHeadAnimator.setVisibility(View.VISIBLE);
        mContainer.removeAllViews();
        mContainerCell = null;
        if (mGuideTip != null) {
            dialogRefreshTips(mGuideTip.getGuidetips());
        }
        mHandler.removeMessages(MSG_ANIM_START);
        if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
            if (mPaiPaiAnimUtil != null) {
                mPaiPaiAnimUtil.showPaiPai(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
            }
            if (IStatus.mSceneType == IStatus.SCENE_GLOBAL || IStatus.mSceneType == IStatus.SCENE_SEARCHPAGE) {
                dialogResize(false);
            } else {
                dialogResize(true);
            }
            mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_ANIM_START), 1500);
        } else {
            if (mPaiPaiAnimUtil != null) {
                mPaiPaiAnimUtil.showPaiPai(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
            }
            setSpeechTextView(getContext().getString(R.string.str_input_note_hint), 0);
            voiceRecordRefresh();
        }
        Log.i(TAG, "================>show");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        VolumeUtils.getInstance(mContext).setMuteWithNoUi(false);
        IStatus.setDialogSmall(false);
        if (mPaiPaiAnimUtil != null) {
            mPaiPaiAnimUtil.release();
        }
        if (mGuideDialog != null) {
            mGuideDialog.dismiss();
            mGuideDialog.cancel();
            mGuideDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.asr_dialog);
        init();
        super.onCreate(savedInstanceState);
    }


    public void setSpeechTextView(String str, int i) {
        Log.d(TAG, "setSpeechTextView:" + str + " index:" + i);
        if (IStatus.mSceneType == IStatus.SCENE_GIVEN) {
            if (TextUtils.isEmpty(str)) {
                mPaiPaiAnimUtil.restart();
                mSceneTxtLayout.setVisibility(View.INVISIBLE);
                mHeadAnimator.setVisibility(View.VISIBLE);
                mRotateHead.setVisibility(View.INVISIBLE);
            } else {
                mPaiPaiAnimUtil.pause();
                mHeadAnimator.setVisibility(View.INVISIBLE);
                mRotateHead.setVisibility(View.VISIBLE);
                mSceneTxtLayout.setVisibility(View.VISIBLE);
                mTextViewScene.setText(str);
            }
        } else {
            mPaiPaiAnimUtil.restart();
            mHeadAnimator.setVisibility(View.VISIBLE);
            mRotateHead.setVisibility(View.GONE);
            mSceneTxtLayout.setVisibility(View.GONE);
            if (str != null && !TextUtils.isEmpty(str)) {
                Log.i(TAG, "setSpeechTextView:" + str);
                mTextViewOne.setRobotTextMarquee(false);
                mTextViewOne.setRobotTextMarqueeOver(false);
                mTextViewOne.setDBCText(str);
            }
        }
    }

    private void setNluResult(AsrResult bean) {
        try {
            TemplateItem item = null;
            if (bean.mTemplates != null && bean.mTemplates.size() > 0) {
                item = bean.mTemplates.get(0);
            }
            if (item != null && !TextUtils.isEmpty(item.mDescription)) {
                setSpeechTextView(item.mTitle + "\n" + item.mDescription, 1);
            } else if (item != null && !TextUtils.isEmpty(item.mTitle)) {
                setSpeechTextView(item.mTitle, 2);
            } else if (!TextUtils.isEmpty(bean.mAnswer)) {
                setSpeechTextView(bean.mAnswer, 3);
            } else {
                if (mPaiPaiAnimUtil != null) {
                    mPaiPaiAnimUtil.showPaiPai(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init() {
        mSceneTxtLayout = (FrameLayout) findViewById(R.id.scene_txt_layout);
        mTextViewScene = (TextView) findViewById(R.id.scene_txt);
        mTextViewOne = (SkyVerticalMarqueeTextview) findViewById(R.id.text_one);
        mTextViewTip = (TextView) findViewById(R.id.txt_guide);
        mHeadAnimator = (ImageView) findViewById(R.id.layout_head);
        mRotateHead = (ImageView) findViewById(R.id.retote_head);
        mlayoutMain = (RelativeLayout) findViewById(R.id.layout_main);
        mContainer = (FrameLayout) findViewById(R.id.container_layout);
        mPaiPaiAnimUtil = new PaiPaiAnimUtil(mHeadAnimator);
        mRotateShadow = (ImageView) findViewById(R.id.retote_shadow);
        mTextViewOne.setMaxHeight(TEXTROBOT_MAX_HEIGHT);
        mSceneTxtLayout.setVisibility(View.GONE);
        mRotateShadow.setVisibility(View.GONE);
        mRotateHead.setVisibility(View.GONE);
        hideCoversation();
    }

    public void hideCoversation() {
        if (IStatus.mSceneType == IStatus.SCENE_GLOBAL || IStatus.mSceneType == IStatus.SCENE_SEARCHPAGE) {
            mTextViewTip.setVisibility(View.VISIBLE);
        }
    }

    public void dialogRefreshUI(String partialresults, int delay) {
        //Log.i(TAG, "partialresults " + partialresults);
        mHandler.removeMessages(MSG_REFRESH_UI);
        if (delay == 0) {
            mHandler.sendMessage(Message.obtain(mHandler, MSG_REFRESH_UI, partialresults));
        } else {
            mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_REFRESH_UI, partialresults), delay);
        }
    }

    public void dialogRefreshResult(AsrResult bean, int delay) {
        mHandler.removeMessages(MSG_REFRESH_RESULT);
        if (delay == 0) {
            mHandler.sendMessage(Message.obtain(mHandler, MSG_REFRESH_RESULT, bean));
        } else {
            mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_REFRESH_RESULT, bean), delay);
        }
    }

    public void voiceRecordRefresh() {
        mHandler.sendMessage(Message.obtain(mHandler, MSG_ANIM_START));
    }

    public void dialogResize(boolean small) {
        if (small) {
            showGuideDialog(IStatus.mScene);
            mHandler.sendMessage(Message.obtain(mHandler, MSG_DIALOG_RESIZE, 1, 0));
        } else {
            mHandler.sendMessage(Message.obtain(mHandler, MSG_DIALOG_RESIZE, 0, 0));
        }
    }

    public void showGuideDialog(String scene) {
        mHandler.sendMessage(Message.obtain(mHandler, MSG_SHOW_GUIDE_DIALOG, scene));
    }

    public void dialogRefreshDetail(Context ctx, Object obj, int type) {
        Message msg = new Message();
        msg.what = MSG_SHOW_LAYOUT;
        msg.obj = obj;
        msg.arg1 = type;
        mHandler.sendMessage(msg);
    }

    public void paipaiRefresh(int status) {
        if (mPaiPaiAnimUtil != null && mPaiPaiAnimUtil.isRecordAnim()) {
            return;
        }
        if (status == AbsTTS.STATUS_TALKING) {
            mHandler.sendEmptyMessage(MSG_PAIPAI_SPEAK);
        } else {
            mHandler.sendEmptyMessage(MSG_PAIPAI_DEFAULT);
        }
    }

    public void showHeadLoading() {
        if (IStatus.mSceneType == IStatus.SCENE_GIVEN) {
            mHandler.sendEmptyMessage(MSG_SHOW_EXE_ANIM);
        }
    }

    public void dialogRefreshBg(String url) {
        if (!TextUtils.isEmpty(url)) {
            MLog.d(TAG, "weather:" + url);
            Message msg = new Message();
            msg.what = MSG_DOWNLOAD_BG;
            msg.obj = url;
            mHandler.sendMessage(msg);
        }
    }

    private void loadImage(final String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("wind")) {
                mlayoutMain.setBackground(mContext.getResources().getDrawable(R.drawable.wind));
            } else if (url.contains("cloudy")) {
                mlayoutMain.setBackground(mContext.getResources().getDrawable(R.drawable.cloudy));
            } else {
                mlayoutMain.setBackground(mContext.getResources().getDrawable(R.drawable.rain));
            }
        } else {
            mlayoutMain.setBackground(mContext.getResources().getDrawable(R.drawable.bg));
        }
    }

    public void dialogRefreshTips(String tips) {
        if (mGuideTip != null) {
            Message msg = new Message();
            msg.what = MSG_SHOW_TIPS;
            msg.obj = tips;
            mHandler.sendMessage(msg);
        }
    }

    public boolean isGuideDialogShow() {
        if (mGuideDialog != null && mGuideDialog.isShowing()) {
            MLog.d("wyf", "GuideDialog already show");
            return true;
        }
        return false;
    }

    public void closeGuideDialog() {
        if (mGuideDialog != null) {
            mGuideDialog.dismiss();
        }
    }

    public void dialogTxtClear() {
        if (IStatus.mSceneType == IStatus.SCENE_GIVEN) {
            mHandler.sendMessage(Message.obtain(mHandler, MSG_REFRESH_UI, ""));
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mContainerCell != null) {
            if (!mContainerCell.dispatchKeyEvent(event)) {
                return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void animStop() {
        mHandler.sendMessage(Message.obtain(mHandler, MSG_ANIM_STOP));
    }

    public void addCell(int type, Object obj) {
        mContainer = (FrameLayout) findViewById(R.id.container_layout);
        switch (type) {
            case DialogCellType.CELL_SPORT_SCORE:
                mTextViewTip.setVisibility(View.GONE);
                mContainerCell = new SportsScoresCell(mContext, obj);
                mContainer.addView(mContainerCell);
                mContainer.setVisibility(View.VISIBLE);
                break;
            case DialogCellType.CELL_SPORT_INFO:
                mTextViewTip.setVisibility(View.GONE);
                mContainerCell = new SportsInfoCell(mContext, obj);
                mContainer.addView(mContainerCell);
                mContainer.setVisibility(View.VISIBLE);
                break;
            case DialogCellType.CELL_BAIKE_INFO:
                mTextViewTip.setVisibility(View.GONE);
                mContainerCell = new BaikeInfoCell(mContext, obj);
                mContainer.addView(mContainerCell);
                mContainer.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        //MLog.d(TAG, "container child:" + mContainer.getChildCount());
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_UI:
                    setSpeechTextView((String) msg.obj, IStatus.SCENE_SHOULD_STOP);
                    break;
                case MSG_REFRESH_RESULT:
                    setNluResult((AsrResult) msg.obj);
                    break;
                case MSG_PAIPAI_DEFAULT:
                    if (mPaiPaiAnimUtil != null) {
                        mPaiPaiAnimUtil.showPaiPai(mPaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
                    }
                    break;
                case MSG_PAIPAI_SPEAK:
                    if (mPaiPaiAnimUtil != null) {
                        mPaiPaiAnimUtil.showPaiPai(mPaiPaiAnimUtil.ID_PAIPAI_SPEAKING);
                    }
                    break;

                case MSG_ANIM_START:
                    Log.i(TAG, "recordAnimStart");
                    if (IStatus.mRecognizeStatus == IStatus.STATUS_ERROR) {
                        break;
                    }
                    if (mPaiPaiAnimUtil != null) {
                        mPaiPaiAnimUtil.recordAnimStart();
                    }
                    break;
                case MSG_ANIM_STOP:
                    Log.i(TAG, "recordAnimStop");
//                    removeMessages(MSG_ANIM_START);
                    if (mPaiPaiAnimUtil != null) {
                        mPaiPaiAnimUtil.recordAnimStop();
                    }
                    break;
                case MSG_SHOW_LAYOUT:
                    addCell(msg.arg1, msg.obj);
                    break;
                case MSG_SHOW_TIPS:
                    MLog.d(TAG, "scene:" + IStatus.mSceneType);
                    if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
                        mTextViewTip.setVisibility(View.VISIBLE);
                        showUserGuide(mTextViewTip, (String) msg.obj);
                    } else {
                        mTextViewTip.setVisibility(View.GONE);
                    }
                    break;
                case MSG_CONTAINER_REMOVE:
                    mContainer.removeAllViews();
                    mContainerCell = null;
                    if (IStatus.mSceneType == IStatus.SCENE_GLOBAL || IStatus.mSceneType == IStatus.SCENE_SEARCHPAGE) {
                        mTextViewTip.setVisibility(View.VISIBLE);
                    }
                    break;
                case MSG_DIALOG_RESIZE:
                    if (msg.arg1 == 1/* && !BeeSearchParams.getInstance().isInSearchPage()*/) {
                        setDialogSizeSmall();
                    } else {
                        setDialogSizeBig();
                    }
                    break;
                case MSG_DOWNLOAD_BG:
                    loadImage((String) msg.obj);
                    break;
                case MSG_SHOW_EXE_ANIM:
                    mSceneTxtLayout.setVisibility(View.GONE);
                    showExeAnim();
                    break;
                case MSG_SHOW_GUIDE_DIALOG:
                    if (!TextUtils.isEmpty((String) msg.obj)) {
                        if (GuideDialog.checkIfNeedShow((String) msg.obj)) {
                            if (mGuideDialog == null) {
                                mGuideDialog = new GuideDialog(mContext, (String) msg.obj);
                            }
                            mGuideDialog.show();
                        }
                        break;
                    } else {
                        if (mGuideDialog == null) {
                            mGuideDialog = new GuideDialog(mContext, null);
                        }
                        mGuideDialog.show();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private void showUserGuide(TextView textView, String guide) {
        Log.i(TAG, "showUserGuide");
        textView.setText(guide);//sp);
    }

    public void setDialogSizeSmall() {
        IStatus.setDialogSmall(true);
        if (!mTypeBig) {
            return;
        }
        MLog.d(TAG, "Dialog Small");
        mTypeBig = false;
        mlayoutMain.setBackground(mContext.getResources().getDrawable(R.drawable.transparent));
        mPaiPaiAnimUtil.showPaiPai(PaiPaiAnimUtil.ID_PAIPAI_DEFAULT);
        mTextViewOne.setText("");
        mTextViewOne.setVisibility(View.INVISIBLE);
        mTextViewTip.setVisibility(View.INVISIBLE);
        mContainer.removeAllViews();
        mContainer.setVisibility(View.INVISIBLE);
    }

    public void setDialogSizeBig() {
        IStatus.setDialogSmall(false);
        mTypeBig = true;
        mlayoutMain.setBackground(mContext.getResources().getDrawable(R.drawable.bg));
        mTextViewOne.setVisibility(View.VISIBLE);
        mTextViewTip.setVisibility(View.VISIBLE);
        mContainer.removeAllViews();
        mContainer.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mTextViewOne.getText())) {
            mTextViewOne.setText("试试下面的关键词");
        }
    }

    private void showExeAnim() {
        mPaiPaiAnimUtil.pause();
        mHeadAnimator.setVisibility(View.INVISIBLE);
        mRotateHead.setVisibility(View.VISIBLE);
        mRotateShadow.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim);
        if (rotate != null) {
            mRotateShadow.startAnimation(rotate);
        } else {
            mRotateShadow.setAnimation(rotate);
            mRotateShadow.startAnimation(rotate);
        }
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mRotateShadow.clearAnimation();
                mRotateShadow.setVisibility(View.GONE);
                mRotateHead.setVisibility(View.GONE);
                MLog.d("wyf", "scene anim repeat");
                mPaiPaiAnimUtil.restart();
                mHeadAnimator.setVisibility(View.VISIBLE);
                mSceneTxtLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRotateShadow.clearAnimation();
                mRotateShadow.setVisibility(View.GONE);
                mRotateHead.setVisibility(View.GONE);
                MLog.d("wyf", "scene anim end");
                mPaiPaiAnimUtil.restart();
                mHeadAnimator.setVisibility(View.VISIBLE);
                mSceneTxtLayout.setVisibility(View.GONE);
            }
        });
    }
}
