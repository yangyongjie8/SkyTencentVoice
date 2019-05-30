package com.skyworthdigital.voice.dingdang.domains.tv;

import android.content.Intent;

import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.scene.SceneJsonUtil;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.DefaultCmds;
import com.skyworthdigital.voiceassistant.scene.ISkySceneListener;
import com.skyworthdigital.voiceassistant.scene.SkyScene;


public class TvLiveCmd implements ISkySceneListener {
    private SkyScene mTvScene;

    public void register() {
        if (mTvScene == null) {
            mTvScene = new SkyScene(VoiceApp.getInstance());//菜单进入前台时进行命令注册
        }
        mTvScene.init(this);
    }

    public void unregister() {
        if (mTvScene != null) {
            mTvScene.release();//不在前台时一定要保证注销
            mTvScene = null;
        }
    }

    @Override
    public String onCmdRegister() {
        try {
            return SceneJsonUtil.getSceneJson(VoiceApp.getInstance(), R.raw.tvlivecmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getSceneName() {
        return "com.linkin.tv.IndexActivity";
    }

    @Override
    public void onCmdExecute(Intent intent) {
        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            String command = intent.getStringExtra(DefaultCmds.COMMAND);
            switch (command) {
                case "next":
                    break;
                case "prev":
                    break;
                case "exit":
                    AppUtil.killTopApp();
                    break;
                default:
                    break;
            }
        }
    }
}
