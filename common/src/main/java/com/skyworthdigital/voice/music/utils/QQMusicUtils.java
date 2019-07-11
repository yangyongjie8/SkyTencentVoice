package com.skyworthdigital.voice.music.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.music.musictype.MusicSlots;
import com.skyworthdigital.voice.music.musictype.TypeCell;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


/**
 * music control
 * Created by SDT03046 on 2017/12/15.
 */

public class QQMusicUtils {
    public static final String QQ_PACKAGENAME = "com.tencent.qqmusictv";
    public static boolean isPauseInRemote;// 因远场唤醒而暂停

    public static void qqMusicInstallPage() {
        AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_qqmusic_uninstall));
        try {
            Intent intent = new Intent();
            intent.setPackage("com.mipt.store");
            intent.setAction("com.mipt.store.intent.APPID");
            intent.setData(Uri.parse("skystore://?packageName=" + QQ_PACKAGENAME));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            VoiceApp.getInstance().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 搜索关键字并选择是否进入播放界面播放
     */
    public static void musicSearchAction(Context ctx, String search_key) {
        MLog.i("QQMusicUtils", "play music:"+search_key);
        try {
            Intent intent = new Intent();
            String uri = "musictv://?action=8&pull_from=12121&mb=true ";
            //String search_key = "发如雪 周杰伦";
            try {
                uri = uri + "&search_key=" + URLEncoder.encode(search_key, "UTF-8") +
                        "&m1=true";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            intent.setData(Uri.parse(uri));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ctx.startActivity(intent);
            getMusicState(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前播放歌曲信息
     */
    private static void getMusicState(Context ctx) {
        try {
            Intent intent = new Intent();
            String uri = "musictv://?action=23&pull_from=12121&m0=control";
            intent.setData(Uri.parse(uri));
            intent.setComponent(new ComponentName("com.tencent.qqmusictv", "com.tencent.qqmusictv.app.reciver.BroadcastReceiverCenterForThird")); //（8.0广播需要）
            ctx.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入具体某个电台
     */
    public static void openOneAadioStationAction(Context ctx, int radioid) {
        try {
            IStatus.resetDismissTime();
            Intent intent = new Intent();
            StringBuilder uri = new StringBuilder();
            uri.append("musictv://?action=15&pull_from=12121&m0=");
            uri.append(radioid);
            uri.append("&mb=true");
            intent.setData(Uri.parse(uri.toString()));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            getMusicState(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开具体某个排行榜
     */
    public static void openRankAction(Context ctx, int rankid) {
        try {
            if (checkApkExist(ctx, QQ_PACKAGENAME)) {
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_rank_search));
                Intent intent = new Intent();
                StringBuilder uri = new StringBuilder();
                uri.append("musictv://?action=19&pull_from=12121&m0=");
                uri.append(rankid);
                //LogUtil.log("rankid:" + rankid);
                uri.append("&mb=true");
                intent.setData(Uri.parse(uri.toString()));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            } else {
                qqMusicInstallPage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean acitonExecute(Context ctx, MusicSlots info, String speech) {
        if (StringUtils.isExitMusicCmdFromSpeech(speech)) {
            if (GuideTip.getInstance().mIsQQmusic) {
                AppUtil.killTopApp();
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
            } else {
                StringUtils.showUnknownNote(ctx, speech);
            }
            return true;
        }
        if (info == null || GuideTip.getInstance().isVideoPlay()
                || GuideTip.getInstance().isAudioPlay() || GuideTip.getInstance().isMediaDetail()) {
            if (info == null || (!StringUtils.isMusicCmdFromSpeech(speech) && TextUtils.isEmpty(info.mSinger))) {
                StringUtils.showUnknownNote(ctx, speech);
                return true;
            }
        }

        if (checkApkExist(ctx, QQ_PACKAGENAME)) {
            String song = info.mSong;
            String singer = info.mSinger;
            String album = info.mAlbum;
            List<String> type = info.mType;

            if (!TextUtils.isEmpty(song) || !TextUtils.isEmpty(singer) || !TextUtils.isEmpty(album)) {
                StringBuilder searchword = new StringBuilder();
                if (!TextUtils.isEmpty(singer) && !TextUtils.isEmpty(song)) {
                    searchword.append(singer);
                    searchword.append(" ");
                    searchword.append(song);
                } else if (!TextUtils.isEmpty(singer)) {
                    searchword.append(singer);
                } else if (!TextUtils.isEmpty(song)) {
                    searchword.append(song);
                }

                if (!TextUtils.isEmpty(searchword.toString()) && !TextUtils.isEmpty(album)) {
                    searchword.append(" ");
                    searchword.append(album);
                } else if (!TextUtils.isEmpty(album)) {
                    searchword.append(album);
                }
                IStatus.resetDismissTime();
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                musicSearchAction(ctx, searchword.toString());
            } else if (type.size() > 0) {
                for (String tmp : type) {
                    MLog.d("wyf", "music type:" + tmp + " " + speech);
                    TypeCell typeCell = TypeUtil.getInstance(ctx).getInfo(tmp, speech);
                    if (typeCell != null) {
                        //MLog.d("wyf", "match type:" + typeCell.getId());
                        AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_search) + typeCell.getCmds().get(0));
                        if (TextUtils.equals(typeCell.getType(), "radio")) {
                            openOneAadioStationAction(ctx, typeCell.getId());
                        } else {
                            openRankAction(ctx, typeCell.getId());
                        }
                        IStatus.resetDismissTime();
                        return true;
                    }
                }
                if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_introduce));
                    openOneAadioStationAction(ctx, 436);
                }
            } else if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
                if (speech.contains("歌") || speech.contains("曲") || speech.contains("音乐")) {
                    TypeCell typeCell = TypeUtil.getInstance(ctx).getInfo(null, speech);
                    if (typeCell != null) {
                        MLog.d("wyf", "match type:" + typeCell.getId());
                        AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_search) + typeCell.getCmds().get(0));
                        if (TextUtils.equals(typeCell.getType(), "radio")) {
                            openOneAadioStationAction(ctx, typeCell.getId());
                        } else {
                            openRankAction(ctx, typeCell.getId());
                        }
                        return true;
                    }
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_introduce));
                    openOneAadioStationAction(ctx, 436);
                } else {
                    StringUtils.showUnknownNote(ctx, speech);
                }
            }
        } else {
            qqMusicInstallPage();
        }
        return true;
    }
}