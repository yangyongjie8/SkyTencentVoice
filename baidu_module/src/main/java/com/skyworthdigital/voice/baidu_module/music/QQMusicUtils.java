package com.skyworthdigital.voice.baidu_module.music;

import android.content.Context;
import android.text.TextUtils;

import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.music.musictype.TypeCell;

/**
 * Created by Ives 2019/6/18
 */
public class QQMusicUtils {
    private static final String QQ_PACKAGENAME = "com.tencent.qqmusictv";

    public static boolean acitonExecute(Context ctx, MusicInfo info) {
        if (com.skyworthdigital.voice.music.utils.QQMusicUtils.checkApkExist(ctx, QQ_PACKAGENAME)) {
            MLog.i("QQMusicUtils", ""+info.toString());
            String song = info.getSong();
            String singer = info.getSinger();
            String type = info.getType();
            String unit = info.getUnit();
            String sort_type = info.getSort_type();

            if (!TextUtils.isEmpty(song) || !TextUtils.isEmpty(singer)) {
                StringBuilder searchword = new StringBuilder();
                if (!TextUtils.isEmpty(singer) && !TextUtils.isEmpty(song)) {
                    searchword.append(singer);
                    searchword.append(" ");
                    searchword.append(song);
                } else if (!TextUtils.isEmpty(singer)) {
                    searchword.append(singer);
                } else {
                    searchword.append(song);
                }
                com.skyworthdigital.voice.music.utils.QQMusicUtils.musicSearchAction(ctx, searchword.toString());
            } else if (TextUtils.equals(unit, "榜单")) {
                TypeCell typeCell = null;
                int id = 34;
                String topname = info.getTop_name();
                if (!TextUtils.isEmpty(topname)) {
                    typeCell = TypeUtil.getInstance(ctx).getInfo(topname);
                    if (typeCell != null) {
                        id = typeCell.getId();
                    }
                }
                if (typeCell == null && TextUtils.isEmpty(sort_type)) {
                    typeCell = TypeUtil.getInstance(ctx).getInfo(sort_type);
                    if (typeCell != null) {
                        id = typeCell.getId();
                    }
                }
                com.skyworthdigital.voice.music.utils.QQMusicUtils.openRankAction(ctx, id);
            } else if (!TextUtils.isEmpty(type) && TextUtils.equals(unit, "歌曲")) {
                MLog.i("QQMusicUtils", "type:" + type);
                TypeCell typeCell = TypeUtil.getInstance(ctx).getInfo(type);
                if (typeCell != null) {
                    com.skyworthdigital.voice.music.utils.QQMusicUtils.openOneAadioStationAction(ctx, typeCell.getId());
                } else {
                    com.skyworthdigital.voice.music.utils.QQMusicUtils.openOneAadioStationAction(ctx, 436);
                }
            } else {
                com.skyworthdigital.voice.music.utils.QQMusicUtils.openOneAadioStationAction(ctx, 436);
            }
        } else {
            MLog.i("QQMusicUtils", "open qqMusicInstallPage");
            com.skyworthdigital.voice.music.utils.QQMusicUtils.qqMusicInstallPage();
        }
        return true;
    }
}
