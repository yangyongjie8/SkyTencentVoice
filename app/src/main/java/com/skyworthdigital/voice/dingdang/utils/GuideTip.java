package com.skyworthdigital.voice.dingdang.utils;

import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.skyworthdigital.voice.dingdang.SkyAsrDialogControl;
import com.skyworthdigital.voice.dingdang.control.model.UserGuideStrings;
import com.skyworthdigital.voice.dingdang.domains.music.utils.MusicCmd;
import com.skyworthdigital.voice.dingdang.domains.tvlive.TvLiveControl;
import com.skyworthdigital.voice.dingdang.domains.videosearch.model.BeeSearchParams;
import com.skyworthdigital.voice.dingdang.scene.SceneManager;

import java.util.ArrayList;
import java.util.List;

public class GuideTip {
    private static final String TAG = "Guidetip";
    private String mClassname = null;
    private static GuideTip mWelcomeTips = null;
    private String mCurrentClass = null;
    private SkyAsrDialogControl mSkyAsrDialogControl = null;
    //private boolean mIsKaraoke = false;
    private static final int PAGE_HOME = 1;
    private static final int PAGE_MEDIA = 2;
    public static final int PAGE_SEARCH = 3;
    private static final int PAGE_AUDIO = 4;
    private static final int PAGE_TVLIVE = 5;
    private static final int PAGE_MUSIC = 6;
    //private static final int PAGE_INTERACTENTRACE = 7;
    //private static final int PAGE_INTERACT = 8;
    private ArrayList<String> mSearchGuide = null;
    public boolean mIsQQmusic = false;
    private MusicCmd mMusicCmd = new MusicCmd();

    private GuideTip() {
    }

    public static GuideTip getInstance() {
        if (mWelcomeTips == null) {
            mWelcomeTips = new GuideTip();
        }
        return mWelcomeTips;
    }

    public void setDialog(SkyAsrDialogControl dialog) {
        mSkyAsrDialogControl = dialog;
    }

    public void resetSearchGuide(ArrayList<String> tips) {
        mSearchGuide = tips;
        if (null != mSkyAsrDialogControl) {
            mSkyAsrDialogControl.dialogRefreshTips(tips);
        }
    }

    public void setViewText(String text){
        if(mSkyAsrDialogControl!=null){
            mSkyAsrDialogControl.setSpeechTextView(text);
        }
    }

    public void setmCurrentCompenent(String clsname) {
        if (!TextUtils.equals(clsname, "com.skyworthdigital.voice.dingdang.SkyAsrDialog")
                && !TextUtils.equals(clsname, "android.support.v4.widget.DrawerLayout")
                && !TextUtils.equals(clsname, "com.android.packageinstaller.permission.ui.GrantPermissionsActivity")
                && !clsname.contains("com.android.systemui")
                && !clsname.contains("android.widget")
                && !clsname.contains("tv.TvlistDialog")
                && !clsname.contains("com.skyworthdigital.voice.dingdang.GuideDialog")
                && !clsname.contains("com.skyworthdigital.sky2dlauncherv4")
                && !TextUtils.equals(clsname, "com.skyworthdigital.skyvolumecontroll.dialog.VolumeDialog")
                && !TextUtils.equals(clsname, "com.skyworthdigital.skymediacenter.skywidget.LocalDeviceDialog")) {
            mClassname = clsname;
            Log.i(TAG, " cls:" + clsname);
            //karaokeSpecialProcess();
            if(!bindQQMusic()) {
                SceneManager.getInstance().checkInBeeVideoScene();
                TvLiveControl.getInstance().setTvLive(clsname);
            }
        }
    }

    public boolean isVideoPlay() {
        return (TextUtils.equals(mClassname, "com.skyworthdigital.skyallmedia.player.SdkPlayerActivity"));
    }

    public boolean isAudioPlay() {
        return (mClassname != null && mClassname.contains("fm.SkyAudioPlayActivity"));
    }

    public boolean isMediaDetail() {
        return (mClassname != null && mClassname.contains("details.SkyMediaDetailActivity"));
    }

    public String getClassName(){
        return mClassname;
    }

    public boolean isTvlive() {
        return TvLiveControl.getInstance().isTvLive();
        //return (mClassname != null && mClassname.contains("com.linkin.tv"));
    }

    public boolean isPoem() {
        return (mClassname != null && mClassname.contains("SkyPoemActivity"));
    }

    public boolean isMusicPlay() {
        return mIsQQmusic;
    }

    //public boolean isInSearchPage() {
    //    return (mClassname != null &&(mClassname.contains("SkyBeeSearchAcitivity") || mClassname.contains("SkyVoiceSearchAcitivity")));
    //}

    private boolean bindQQMusic() {
        if (mClassname != null && mClassname.contains("com.tencent.qqmusic")) {
            if (!mIsQQmusic) {
                mMusicCmd.register();
            }
            mIsQQmusic = true;
            return true;
        }

        if (mIsQQmusic) {
            mMusicCmd.executeCmd(1);
            mMusicCmd.unregister();
        }
        mIsQQmusic = false;
        return false;
    }

    public void pauseQQMusic() {
        if (mIsQQmusic) {
            mMusicCmd.executeCmd(1);
        }
    }
    public void playQQMusic(){
        if(mIsQQmusic){
            mMusicCmd.executeCmd(0);
        }
    }

    public String getCurrentClass() {
        return mClassname;
    }

    public int getType() {
        if (TextUtils.isEmpty(mClassname)) {
            BeeSearchParams.getInstance().setLastReply(null);
            return PAGE_HOME;
        }

        if (!mClassname.contains("control.domains.videosearch")
                && !mClassname.contains("control.domains.beesearch")
                && !mClassname.contains("com.skyworthdigital.skyallmedia.details")) {
            BeeSearchParams.getInstance().setLastReply(null);
        }

        if (mClassname.contains("com.skyworthdigital.skyallmedia")) {
            return PAGE_MEDIA;
        }

        if (mClassname.contains("com.linkin.tv")) {
            return PAGE_TVLIVE;
        }

        if (mClassname.contains("SkyBeeSearchAcitivity") || mClassname.contains("SkyVoiceSearchAcitivity")) {
            return PAGE_SEARCH;
        }
        if (mClassname.contains("fm.SkyAudioPlayActivity")) {
            return PAGE_AUDIO;
        }
        if (mClassname.contains("com.audiocn.karaoke.tv") || mClassname.contains("com.tencent.qqmusictv")) {
            return PAGE_MUSIC;
        }
        return PAGE_HOME;
    }

    /*public String resetWelcometips() {
        if (TextUtils.equals(mClassname, mCurrentClass)) {
            return null;
        }
        return getWelcometips();
    }*/

    public ArrayList<String> getGuidetips() {
        ArrayList<String> userGuide = new ArrayList<>();
        try {
            mCurrentClass = mClassname;
            if (BeeSearchParams.getInstance().isInSearchPage() && mSearchGuide != null) {
                userGuide.addAll(mSearchGuide);
                return userGuide;
            }
            int type = getType();
            switch (type) {
                case PAGE_SEARCH:
                    if (mSearchGuide != null) {
                        userGuide.addAll(mSearchGuide);
                    } else {
                        userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_SEARCH));
                    }
                    break;
                case PAGE_MEDIA:
                    //mSearchGuide = null;
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_MEDIA_CONTROL));
                    break;
                case PAGE_AUDIO:
                    mSearchGuide = null;
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_AUDIO));
                    break;
                case PAGE_TVLIVE:
                    mSearchGuide = null;
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_LIVE));
                    break;
                case PAGE_MUSIC:
                    mSearchGuide = null;
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_MUSIC));
                    break;
                case PAGE_HOME:
                default:
                    userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_HOME));
                    break;
            }
        } catch (Exception e) {
            userGuide.addAll(UserGuideStrings.generateUserGuide(UserGuideStrings.TYPE_HOME));
            return userGuide;
        }
        return userGuide;
    }

    public void showUserGuide(TextView textView, List<String> guide) {
        Log.i(TAG, "showUserGuide");
        StringBuilder sb = new StringBuilder();
        sb.append("你可以说：");
        for (String item : guide) {
            sb.append(item);
            sb.append(" ");
        }
        textView.setText(sb.toString());//sp);
    }
}
