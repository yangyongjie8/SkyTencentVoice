package com.skyworthdigital.voice.music.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.scene.ISkySceneListener;
import com.skyworthdigital.voice.scene.SceneJsonUtil;
import com.skyworthdigital.voice.scene.SkyScene;


public class MusicCmd implements ISkySceneListener {
    private SkyScene mMusicScene;

    public void register() {
        if (mMusicScene == null) {
            mMusicScene = new SkyScene(VoiceApp.getInstance());//菜单进入前台时进行命令注册
        }
        mMusicScene.init(this);
    }

    public void unregister() {
        if (mMusicScene != null) {
            mMusicScene.release();//不在前台时一定要保证注销
            mMusicScene = null;
        }
    }

    public void executeCmd(int m0) {
        //LogUtil.log("execute qq music Cmd:" + m0);
        Context ctx = VoiceApp.getInstance();
        StringBuilder uri = new StringBuilder();
        Intent intent = new Intent();
        uri.append("musictv://?action=20&pull_from=12121&m0=");
        uri.append(m0);
        intent.setData(Uri.parse(uri.toString()));
        //intent.setComponent(new ComponentName("com.tencent.qqmusictv", "com.tencent.qqmusictv.app.reciver.BroadcastReceiverCenterForThird"));  //（android8.0广播需要）
        ctx.sendBroadcast(intent);
    }

    private void executeCmd(int m0, int offset) {
        StringBuilder uri = new StringBuilder();
        Intent intent = new Intent();
        uri.append("musictv://?action=20&pull_from=12121&m0=");
        uri.append(m0);
        uri.append("&m1=");
        uri.append(offset);
        intent.setData(Uri.parse(uri.toString()));
//        MLog.d("####", "uri:"+uri.toString());
        //intent.setComponent(new ComponentName("com.tencent.qqmusictv", "com.tencent.qqmusictv.app.reciver.BroadcastReceiverCenterForThird"));  //（android8.0广播需要）
        VoiceApp.getInstance().sendBroadcast(intent);
    }

    @Override
    public String onCmdRegister() {
        try {
            return SceneJsonUtil.getSceneJson(VoiceApp.getInstance(), R.raw.musiccmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getSceneName() {
        return "MusicScene";
    }

    /*
        功能名称	m0值
        播放	    0
        暂停  	1
        上一首	2
        下一首	3
        打开MV	4
        收藏 	5
        取消收藏	6
        快进 	7
        快退 	8
        单曲循环	101
        顺序播放	103
        随机播放	105
     */
    @Override
    public void onCmdExecute(Intent intent) {
        //LogUtil.log("voiceCallback intent : " + intent.getExtras().toString());
        MLog.d("MusicCmd", "onCmdExecute, command:"+intent.getStringExtra(DefaultCmds.COMMAND));
        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            String command = intent.getStringExtra(DefaultCmds.COMMAND);
            //LogUtil.log("music command:" + command);
            String action = "";
            Context ctx = VoiceApp.getInstance();
            if (intent.hasExtra(DefaultCmds.INTENT)) {
                action = intent.getStringExtra(DefaultCmds.INTENT);
            }
            MLog.d("MusicCmd", "onCmdExecute, action:"+action);
            switch (command) {
                case "next":
                    executeCmd(3);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                    break;
                case "pre":
                    executeCmd(2);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_pre));
                    break;
                case "stop":
                    executeCmd(1);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_stop));
                    break;
                case "play2":
                    executeCmd(0);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_play));
                    break;
                case "play":
                    int value = 0;
                    if (intent.hasExtra(DefaultCmds.VALUE)) {
                        value = intent.getIntExtra(DefaultCmds.VALUE, 0);
                    }
                    if (DefaultCmds.PLAYER_CMD_PAUSE.equals(action)) {
                        if (0 == value) {
                            executeCmd(0);
                            AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_play));
                        } else {
                            executeCmd(1);
                            AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_stop));
                        }
                    } else if (DefaultCmds.PLAYER_CMD_FASTFORWARD.equals(action)||DefaultCmds.PLAYER_CMD_GOTO.equals(action)) {
                        executeCmd(7, value);
                        AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_fastforward));
                    } else if (DefaultCmds.PLAYER_CMD_BACKFORWARD.equals(action)) {
                        executeCmd(8, value);
                        AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_backforward));
                    } else if (DefaultCmds.PLAYER_CMD_NEXT.equals(action)) {
                        executeCmd(3);
                        AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                    } else if (DefaultCmds.PLAYER_CMD_PREVIOUS.equals(action)) {
                        executeCmd(2);
                        AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_pre));
                    }
                    break;
                case "save":
                    executeCmd(5);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                    break;
                case "unsave":
                    executeCmd(6);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_unsave));
                    break;
                case "singleloop":
                    executeCmd(101);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_single));
                    break;
                case "Sequence":
                    executeCmd(103);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_sequence));
                    break;
                case "random":
                    executeCmd(105);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_music_random));
                    break;
                case "exit":
                    Utils.simulateKeystroke(KeyEvent.KEYCODE_HOME);
                    AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                    break;
                default:
                    break;
            }
        }
    }
}
