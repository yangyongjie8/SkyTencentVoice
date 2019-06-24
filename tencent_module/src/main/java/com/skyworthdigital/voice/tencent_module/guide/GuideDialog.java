package com.skyworthdigital.voice.tencent_module.guide;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.PrefsUtils;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class GuideDialog extends Dialog {
    private Context mContext;
    private LinearLayout mCmdListLayout;
    private String mScene;
    private Timer mTimer;
    private ArrayList<GuideItem> mGuideList = new ArrayList<>();
    private static final int[][] mVideoPageCommand = {
            {R.drawable.nextset, R.string.guide_next},
            {R.drawable.preset, R.string.guide_previous},
            {R.drawable.guide_play, R.string.guide_playnum},
            {R.drawable.speed, R.string.guide_speed},
            {R.drawable.guide_pause, R.string.guide_pause},
            {R.drawable.collection, R.string.guide_collect},
            {R.drawable.voice, R.string.guide_volume_up},
            {R.drawable.fullscreen, R.string.guide_fullscreen},
            {R.drawable.back, R.string.guide_exit},
            {R.drawable.meun, R.string.guide_help},

    };

    private static final int[][] mMusicPageCommand = {
            {R.drawable.nextset, R.string.guide_nextsong},
            {R.drawable.preset, R.string.guide_presong},
            {R.drawable.guide_play, R.string.guide_playsong},
            {R.drawable.guide_pause, R.string.guide_pause},
            {R.drawable.voice, R.string.guide_volume_down},
            {R.drawable.singleloop, R.string.guide_songloop},
            {R.drawable.sequence, R.string.guide_sequence},
            {R.drawable.random, R.string.guide_random},
            {R.drawable.back, R.string.guide_exit},
            {R.drawable.meun, R.string.guide_help},
    };

    private static final int[][] mFmPageCommand = {
            {R.drawable.nextset, R.string.guide_nextnum},
            {R.drawable.preset, R.string.guide_prenum},
            {R.drawable.guide_play, R.string.guide_play},
            {R.drawable.guide_pause, R.string.guide_pause},
            {R.drawable.voice, R.string.guide_volume_up},
            {R.drawable.voice, R.string.guide_volume_down},
            {R.drawable.back, R.string.guide_exit},
            {R.drawable.meun, R.string.guide_help},
    };

    private static final int[][] mTvlivePageCommand = {
            {R.drawable.nextset, R.string.guide_nextchannel},
            {R.drawable.preset, R.string.guide_prechannel},
            {R.drawable.meun, R.string.guide_tvplay1},
            {R.drawable.meun, R.string.guide_tvplay2},
            {R.drawable.voice, R.string.guide_volume_up},
            {R.drawable.voice, R.string.guide_volume_down},
            {R.drawable.back, R.string.guide_exit},
            {R.drawable.meun, R.string.guide_help},
    };

    private static final int[][] mPoemPageCommand = {
            {R.drawable.nextset, R.string.guide_nextnum},
            {R.drawable.preset, R.string.guide_prenum},
            {R.drawable.guide_play, R.string.guide_play},
            {R.drawable.guide_pause, R.string.guide_pause},
            {R.drawable.voice, R.string.guide_volume_up},
            {R.drawable.voice, R.string.guide_volume_down},
            {R.drawable.back, R.string.guide_exit},
            {R.drawable.meun, R.string.guide_help},
    };

    public GuideDialog(Context context, String scene) {
        super(context, R.style.GuideDialog);//R.style.CustomDialog);
        mContext = context;
        mScene = scene;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().setFlags(flags, flags);
        View view = LayoutInflater.from(mContext).inflate(R.layout.guide_dialog, null, false);
        setContentView(view);
        mCmdListLayout = (LinearLayout) findViewById(R.id.cmd_list);
        setList();
    }

    public void setList() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        int[][] guidelist = getlist();
        if (guidelist == null) {
            dismiss();
            return;
        }
        for (int i = 0; i < guidelist.length; i++) {
            View view = layoutInflater.inflate(R.layout.guideitem, null);
            ImageView mIconImage = (ImageView) view.findViewById(R.id.icon);
            TextView mtxt = (TextView) view.findViewById(R.id.txt);
            mIconImage.setBackground(mContext.getResources().getDrawable(guidelist[i][0]));
            mtxt.setText(mContext.getResources().getString(guidelist[i][1]));
            mCmdListLayout.addView(view);
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
        super.onBackPressed();
    }

    private int[][] getlist() {
        int[][] guidelist = null;
        if (GuideTip.getInstance().isAudioPlay() || (!TextUtils.isEmpty(mScene) && mScene.contains("SkyAudioPlayActivity"))) {
            guidelist = mFmPageCommand;
            PrefsUtils.setFmGuideShow(mContext, true);
        } else if (GuideTip.getInstance().isVideoPlay()
                || GuideTip.getInstance().isMediaDetail()
                || (!TextUtils.isEmpty(mScene) && (mScene.contains("SdkPlayerActivity") || mScene.contains("SkyMediaDetailActivity")))) {
            guidelist = mVideoPageCommand;
            PrefsUtils.setVideoGuideShow(mContext, true);
        } else if (GuideTip.getInstance().isTvlive()
                || (!TextUtils.isEmpty(mScene) && mScene.contains("com.linkin.tv"))) {
            guidelist = mTvlivePageCommand;
            PrefsUtils.setTvliveGuideShow(mContext, true);
        } else if (GuideTip.getInstance().isPoem()
                || (!TextUtils.isEmpty(mScene) && mScene.contains("SkyPoemActivity"))) {
            guidelist = mPoemPageCommand;
            PrefsUtils.setPoemGuideShow(mContext, true);
        } else if (GuideTip.getInstance().isMusicPlay()
                || (!TextUtils.isEmpty(mScene) && mScene.contains("com.tencent.qqmusic"))) {
            guidelist = mMusicPageCommand;
            PrefsUtils.setMusicGuideShow(mContext, true);
        } else {
            MLog.d("wyf", "guide dialog dismiss");
            dismiss();
        }
        return guidelist;
    }

    public static boolean checkIfNeedShow(String scene) {
        boolean ret = false;
        if (!TextUtils.isEmpty(scene) && scene.contains("SkyAudioPlayActivity")) {
            ret = PrefsUtils.getFmGuideShow(VoiceApp.getInstance());
        } else if (!TextUtils.isEmpty(scene) && (scene.contains("SdkPlayerActivity") || scene.contains("SkyMediaDetailActivity"))) {
            ret = PrefsUtils.getVideoGuideShow(VoiceApp.getInstance());
        } else if (!TextUtils.isEmpty(scene) && scene.contains("SkyMusicPlayActivity")) {
            ret = PrefsUtils.getMusicGuideShow(VoiceApp.getInstance());
        } else if (!TextUtils.isEmpty(scene) && scene.contains("com.linkin.tv")) {
            ret = PrefsUtils.getTvliveGuideShow(VoiceApp.getInstance());
        } else if (!TextUtils.isEmpty(scene) && scene.contains("SkyPoemActivity")) {
            ret = PrefsUtils.getPoemGuideShow(VoiceApp.getInstance());
        }
        MLog.d("wyf", "check guide show:" + scene + "\n" + ret);
        return !ret;
    }

    @Override
    public void show() {
        super.show();
        MLog.d("wyf", "timer start");
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public void run() {
                dismiss();
            }
        }, 20000);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        MLog.d("wyf", "timer stop");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}

