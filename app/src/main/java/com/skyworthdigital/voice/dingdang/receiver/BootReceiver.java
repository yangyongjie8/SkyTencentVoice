package com.skyworthdigital.voice.dingdang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;


/**
 * 开机广播类
 * Created by SDT03046 on 2017/5/22.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String MUSIC_STATE_ACTION = "com.tencent.qqmusictvforthird";
    private static int mPreVolume = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (action.equals(MUSIC_STATE_ACTION)) {
            try {
            /*
            Code	         对应QQ音乐的状态
            1,2,3,4,61,1001	 播放
            5,501	         暂停
            6,7,601	         停止
            101	             缓存中
            -1	             error*/
                int func;
                int code;
                if (intent.hasExtra("k0")) {
                    func = intent.getIntExtra("k0", 0);

                }
                if (intent.hasExtra("k1")) {
                    code = intent.getIntExtra("k1", 0);

                    if (code == -1) {
                        //BdTTS.getInstance().setWords(context.getString(R.string.str_music_play_error));
                        //QQMusicUtils.musicSearchAction(context, "陈奕迅");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            MLog.d("boot", "android.intent.action.BOOT_COMPLETED");
            mPreVolume = VolumeUtils.getInstance(context).getVolume();
        }
    }
}
