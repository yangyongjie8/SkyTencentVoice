package com.skyworthdigital.voice.guide;

import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.AbsDialog;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.music.utils.MusicCmd;
import com.skyworthdigital.voice.scene.SceneManager;
import com.skyworthdigital.voice.tv.AbsTvLiveControl;

import java.util.ArrayList;

public class GuideTip {
    private static final String TAG = "Guidetip";
    private String mClassname = null;
    private static GuideTip mWelcomeTips = null;
    private String mCurrentClass = null;
    private AbsDialog mSkyAsrDialogControl = null;
    //private boolean mIsKaraoke = false;
    public static final int PAGE_HOME = 1;
    public static final int PAGE_MEDIA = 2;
    public static final int PAGE_SEARCH = 3;
    public static final int PAGE_AUDIO = 4;
    public static final int PAGE_TVLIVE = 5;
    public static final int PAGE_MUSIC = 6;
    //private static final int PAGE_INTERACTENTRACE = 7;
    //private static final int PAGE_INTERACT = 8;
    public boolean mIsQQmusic = false;
    private MusicCmd mMusicCmd = new MusicCmd();

    private GuideTip() {
    }

    public static GuideTip getInstance() {
        if (mWelcomeTips == null) {
            synchronized (GuideTip.class) {
                if (mWelcomeTips == null) {
                    mWelcomeTips = new GuideTip();
                }
            }
        }
        return mWelcomeTips;
    }

    public void setDialog(AbsDialog dialog) {
        mSkyAsrDialogControl = dialog;
    }

    public boolean isDialogShowing(){
        return AbsGuideAgent.getInstance()!=null && AbsGuideAgent.getInstance().isDialogShowing();
    }
    public void dismissDialog(){
        if(AbsGuideAgent.getInstance()==null)return;
        AbsGuideAgent.getInstance().isDialogShowing();
    }

    public void resetSearchGuide(ArrayList<String> tips) {
        if(AbsGuideAgent.getInstance()!=null){
            AbsGuideAgent.getInstance().resetSearchGuide(tips);
        }
        if (null != mSkyAsrDialogControl) {
            mSkyAsrDialogControl.dialogRefreshTips(tips);
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
                AbsTvLiveControl.getInstance().setTvLive(clsname);
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

    public boolean isLauncherHome(){
        return (mClassname==null || "cn.beevideo.launch.activity.HomeActivity".equalsIgnoreCase(mClassname));
    }

    public String getClassName(){
        return mClassname;
    }

    public boolean isTvlive() {
        return AbsTvLiveControl.getInstance().isTvLive();
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

        if (mIsQQmusic && VolumeUtils.getInstance(VoiceApp.getInstance()).isAudioActive()) {
            mMusicCmd.executeCmd(1);
            mMusicCmd.unregister();
        }
        mIsQQmusic = false;
        return false;
    }

    public void pauseQQMusic() {
        if (mIsQQmusic && VolumeUtils.getInstance(VoiceApp.getInstance()).isAudioActive()) {
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

    public final String getGuidetips(){
        if(TextUtils.equals(mClassname, mCurrentClass))return null;// 类未发生改变，调用应当保持显示不变

        mCurrentClass = mClassname;
        if(AbsGuideAgent.getInstance()!=null){
            return AbsGuideAgent.getInstance().getGuidetips(getType());
        }
        return null;
    }
}
