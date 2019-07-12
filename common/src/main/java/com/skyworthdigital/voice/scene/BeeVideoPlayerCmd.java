package com.skyworthdigital.voice.scene;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 蜜蜂视频语音控制场景指令-播放器页
 * Created by Ives 2019/3/15
 */
public class BeeVideoPlayerCmd implements ISkySceneListener {
    private String TAG = "BeeVideoPlayerCmd";
    public static final String SCENE_NAME_BEE_VIDEO_PLAYER = "BeeVideoPlayer";//与json的sceneName保持一致

    public static final String PACKAGE_NAME_BEEVIDEO = "cn.beevideo";//蜜蜂视频包名
    static final String BROADCAST_BEEVIDEO_COMMAND = "cn.beevideo.lib.remote.server.CLIENT_COMMAND_INFO";//蜜蜂视频接收控制语音的广播
    static final String KEY_INTENT_BROADCAST_BEEVIDEO_COMMAND = "tip_info";//广播接收参数

    private SkyScene mVideoScene;

    public void register() {
        if (mVideoScene == null) {
            mVideoScene = new SkyScene(VoiceApp.getInstance());
        }
        mVideoScene.init(this);
    }

    public void unregister() {
        if (mVideoScene != null) {
            mVideoScene.release();//不在前台时一定要保证注销
            mVideoScene = null;
        }
    }

    @Override
    public String onCmdRegister() {
        try {
            return SceneJsonUtil.getSceneJson(VoiceApp.getInstance(), R.raw.beevideo_player_cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getSceneName() {
        return SCENE_NAME_BEE_VIDEO_PLAYER;
    }

    @Override
    public void onCmdExecute(Intent intent) {
        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            MLog.i(TAG, "onCmdExecute intent : " + intent.getExtras().toString());

            String command = intent.getStringExtra(DefaultCmds.COMMAND);// 指令名，_commands的key，比如seek_forward_to
            String sequery = intent.getStringExtra(DefaultCmds.SEQUERY);// 原文
            String category_serv = intent.getStringExtra(DefaultCmds.CATEGORY_SERV);
            MLog.i(TAG, "beeVideo command:" + command+" sequery:"+sequery);
            // intent、value可从dueros解析后传来，暂不使用

            try {
                JSONObject remoteIntents = new JSONObject(SceneJsonUtil.getSceneJson(VoiceApp.getInstance(), R.raw.beevideo_args));
                String remoteArgs = remoteIntents.optString(command);
                MLog.i(TAG, "参数:"+remoteArgs);

                int value = -1;
                if (intent.hasExtra(DefaultCmds.VALUE)) {
                    value = intent.getIntExtra(DefaultCmds.VALUE, 0);
                    MLog.i(TAG, "value:"+value);
                }
                switch (command){
                    case "read_summary"://查看简介
                        break;
                    case "seek_forward_to"://快进到
                    case "seek_reverse_to"://快退到
                    case "seek_forward"://快进
                    case "seek_reverse"://快退
                        if(value!=-1) {
                            remoteArgs = String.format(remoteArgs, value *1000);//单位：s
                        }
                        break;
                    case "volume_min"://音量最小
                        remoteArgs = String.format(remoteArgs, 0);
                        break;
                    case "volume_add"://音量增加百分几
                        //TODO 需要提取到增加值再发广播，否则不发
                        break;
                    case "volume_reduce"://音量减小百分几
                        break;
                    case "volume_adjust"://音量调整到
                        break;
                    case "volume_max"://音量最大
                        remoteArgs = String.format(remoteArgs, 100);
                        break;
                    case "pause"://暂停
                        break;
                    case "history"://查看历史记录
                        break;
                    case "unsave"://取消收藏
                        break;
                    case "save"://收藏
                        break;
                    case "fullscreen"://全屏
                        break;
                    case "login"://登录
                        break;
                    case "openvip"://开通vip+
                        break;
                    case "openpayment"://打开付费
                        break;
                    case "episode"://下一集
                        break;
                    case "play_position"://播放第n个
                        remoteArgs = String.format(remoteArgs, StringUtils.getIndexFromSpeech(sequery));
                        break;
                    case "play"://播放
                        break;
                    case "return"://返回
                        Utils.simulateKeystroke(KeyEvent.KEYCODE_BACK);
                        break;
                }
                Intent beeIntent = new Intent(BROADCAST_BEEVIDEO_COMMAND);
                beeIntent.putExtra(KEY_INTENT_BROADCAST_BEEVIDEO_COMMAND, remoteArgs);
                Log.i(TAG, "broadcast intent command:"+beeIntent.getStringExtra(KEY_INTENT_BROADCAST_BEEVIDEO_COMMAND));
                VoiceApp.getInstance().sendBroadcast(beeIntent);
                AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_ok));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
