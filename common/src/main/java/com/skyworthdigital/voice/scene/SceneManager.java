package com.skyworthdigital.voice.scene;

import com.skyworthdigital.voice.guide.GuideTip;

/**
 * 场景实例的容器（仅非activity场景）
 * Created by Ives 2019/3/20
 */
public class SceneManager {
    private BeeVideoPlayerCmd mBeeVideoPlayerCmd = new BeeVideoPlayerCmd();

    private SceneManager(){}
    private static SceneManager mInstance;
    public static SceneManager getInstance(){
        if(mInstance==null){
            synchronized (SceneManager.class){
                if(mInstance==null){
                    mInstance = new SceneManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 检入蜜蜂视频的场景
     * @return true 启用本场景
     */
    public boolean checkInBeeVideoScene(){
        // 百视通、蜜蜂视频、爱奇艺、4k花园
        if("cn.beevideo.bestvplayer2.acticity.BestvVideoActivity".equals(GuideTip.getInstance().getClassName())||
                "cn.beevideo.iqyplayer.activity.VideoDetailActivity".equals(GuideTip.getInstance().getClassName())||
                "cn.beevideo.iqiyiplayer2.acticity.IQIYIVideoActivity".equals(GuideTip.getInstance().getClassName())||
                "cn.beevideo.iqiyiplayer2.acticity.IQIYIVideoFullscreenActivity".equals(GuideTip.getInstance().getClassName())||
                "cn.beevideo.skgardenplayer2.activity.SKVideoDetailActivity".equals(GuideTip.getInstance().getClassName())){
            if(!SkySceneService.containScene(BeeVideoPlayerCmd.SCENE_NAME_BEE_VIDEO_PLAYER)){
                mBeeVideoPlayerCmd.register();
            }
            return true;
        }else {
            mBeeVideoPlayerCmd.unregister();
        }
        return false;
    }
}
