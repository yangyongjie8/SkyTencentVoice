package com.skyworthdigital.voice.videoplay;

import android.content.Context;
import android.content.Intent;

import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;

/**
 * User: yangyongjie
 * Date: 2019-01-03
 * Description:蜜蜂视频播放拉起入口
 */
public class BeeVideoPlayUtils {
    public static final String SOURCE_IQIYI_ID = "3"; //爱奇艺
    public static final String SOURCE_4K_ID = "17"; // 4k花园
    public static final String SOURCE_YOUPENG_ID = "18"; // 优朋
    public static final String SOURCE_BESTTV_ID = "19"; // 百视通

    public static void startToVideoDetail(Context context, String sourceId, String videoId) {
        Intent intent = new Intent();
        intent.setPackage("cn.beevideo");
        if (SOURCE_IQIYI_ID.equals(sourceId)) {
            intent.setAction("com.mipt.videohj.intent.action.VOD_PLAY_ACTION");
        } else if (SOURCE_4K_ID.equals(sourceId)) {
            intent.setAction("com.mipt.videohj.intent.action.VOD_DETAIL_4K_ACTION");
        } else if (SOURCE_YOUPENG_ID.equals(sourceId)) {
            intent.setAction("com.mipt.videohj.intent.action.VOD_DETAIL_YOUPENG_ACTION");
        } else if (SOURCE_BESTTV_ID.equals(sourceId)) {
            intent.setAction("com.mipt.videohj.intent.action.VOD_DETAIL_BESTV_ACTION");
        }
        intent.putExtra("videoId", videoId);
        intent.putExtra("invokeFrom", "VOICE_ASSISTANT");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        AbsTTS.getInstance(null).talk(context.getString(R.string.str_ok));
    }
}
