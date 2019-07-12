package com.skyworthdigital.voice.scene;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.beesearch.BeeSearchUtils;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景注册服务类
 * Created by SDT03046 on 2017/6/5.
 */
public class SkySceneService extends Service {
    private static final String TAG = SkySceneService.class.getSimpleName();
    public static final String INTENT_TOPACTIVITY_COMMIT = "com.skyworthdigital.voiceassistant.topActivity.COMMIT";
    public static final String INTENT_TOPACTIVITY_CALL = "com.skyworthdigital.voiceassistant.app.CALL";
    private static final String INTENT_RELEASE_SCENE = "com.skyworthdigital.voiceassistant.topActivity.RELEASE";
    public static final String SCENE_EXECUTE_ACTION = "com.skyworthdigital.voiceassistant.scenes.EXECUTE";

    private static final String SCENE = "_scene";
    private static final String OBJ_HASH = "_objhash";
    private static final int INVALID_VALUE = -255;
    private static final String SCENE_NAME = "_sceneName";
    private static final List<String> mSceneNameList = new ArrayList<>();
    private List<String> mSceneJson = new ArrayList<>();
    private List<Integer> mToken = new ArrayList<>();
    private IBinder mBinder = new LocalBinder();
    /**
     * 更新进度的回调接口
     */
    private static ISceneCallback mOnSceneListener;


    public class LocalBinder extends Binder {
        public SkySceneService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SkySceneService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //return null;
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        boolean match = false;
        if (intent == null || intent.getAction() == null) {
            //Log.d(TAG, "intent is null");
            return START_STICKY;
        } else if (INTENT_TOPACTIVITY_COMMIT.equals(intent.getAction())) {

            if (intent.hasExtra(SCENE)) {
                String pkg = intent.getStringExtra(SCENE_NAME);
                String scene = intent.getStringExtra(SCENE);
                int token = intent.getIntExtra(OBJ_HASH, -1);
                if(!TextUtils.isEmpty(scene)) {
                    if (!mSceneJson.contains(scene)) {
                        MLog.d(TAG, "regist add:" + scene);
                        mSceneNameList.add(pkg);
                        mSceneJson.add(scene);
                        mToken.add(token);
                    } else {
                        for (int i = 0; i < mSceneJson.size(); i++) {
                            if (TextUtils.equals(scene, mSceneJson.get(i))) {
                                MLog.d(TAG, "regist replace ");
                                mToken.set(i, token);
                                mSceneNameList.set(i, pkg);
                            }
                        }
                    }
                    //LogUtil.log("regist cmd: " + scene + "mToken:" + token + " pkg:" + pkg);
                    if (VoiceApp.getVoiceApp().mAiType == GlobalVariable.AI_VOICE) {
                        if (scene.contains("SkyVoiceSearchAcitivity")) {
                            mOnSceneListener.onSearchPageRegisted();
                        } else {
                            mOnSceneListener.onSceneRegisted(scene);
                        }
                    }
                }
            }
        } else if (INTENT_TOPACTIVITY_CALL.equals(intent.getAction())) {
            MLog.d(TAG, "SkySceneService intent se_query: " + intent.getStringExtra(DefaultCmds.SEQUERY)+" category_serv:"+intent.getStringExtra(DefaultCmds.CATEGORY_SERV));
            String cmd = intent.getStringExtra(DefaultCmds.SEQUERY);
            String categoryServ = intent.getStringExtra(DefaultCmds.CATEGORY_SERV);
            if (BeeSearchUtils.mSpeakSameInfo != null) {
                MLog.d(TAG, "纠错");
            } else {
                for (int i = (mToken.size() - 1); i >= 0; i--) {
                    MLog.d(TAG, "intent: " + mToken.get(i) + " size:" + mToken.size());
                    String key = SceneJsonUtil.isVoiceCmdRegisted(cmd, categoryServ, mSceneJson.get(i));
                    if (key == null) {
                        if (intent.hasExtra(DefaultCmds.PREDEFINE)) {
                            key = SceneJsonUtil.isVoiceCmdRegisted(intent.getStringExtra(DefaultCmds.PREDEFINE), categoryServ, mSceneJson.get(i));
                        }
                    }
                    int index = SceneJsonUtil.isFuzzyMatched(cmd, mSceneJson.get(i));
                    if (key != null || index >= 0) {
                        match = true;
                        Intent executeintent = new Intent(SCENE_EXECUTE_ACTION);

                        if (key != null) {
                            executeintent.putExtra(DefaultCmds.COMMAND, key);
                        }
                        executeintent.putExtra(DefaultCmds.SEQUERY, cmd);// 原文。至此，发这些就够了，下面的参数应该由scene处理，不应该由前面预先处理
                        if (intent.hasExtra(DefaultCmds.CATEGORY_SERV)) {// server解析的指令集，这里只根据server有没解析这类别，json里不一定已使用该类别
                            executeintent.putExtra(DefaultCmds.CATEGORY_SERV, categoryServ);
                        }
                        if (index >= 0) {
                            executeintent.putExtra(DefaultCmds.FUZZYMATCH, index);
                        }

                        if (intent.hasExtra(DefaultCmds.INTENT)) {
                            executeintent.putExtra(DefaultCmds.INTENT, intent.getStringExtra(DefaultCmds.INTENT));
                        }
                        if (intent.hasExtra(DefaultCmds.VALUE)) {
                            executeintent.putExtra(DefaultCmds.VALUE, intent.getIntExtra(DefaultCmds.VALUE, INVALID_VALUE));
                        }
                        executeintent.putExtra(OBJ_HASH, (int) mToken.get(i));
                        sendBroadcast(executeintent);
                    }
                }
            }
            if (match && mOnSceneListener != null) {
                mOnSceneListener.onSceneCheckedOver(true);
            } else if (mOnSceneListener != null) {
                mOnSceneListener.onSceneCheckedOver(false);
            } else {
                MLog.e(TAG, "mOnSceneListener is null, check it please!");
            }
        } else if (INTENT_RELEASE_SCENE.equals(intent.getAction())) {
            try {
                if (intent.hasExtra(SCENE)) {
                    String scene = intent.getStringExtra(SCENE);
                    int size = mSceneJson.size();
                    if (size >= 1) {
                        for (int i = size - 1; i >= 0; i--) {
                            if (TextUtils.equals(scene, mSceneJson.get(i))) {
                                MLog.d(TAG, "release scene");
                                mSceneNameList.remove(i);
                                mSceneJson.remove(i);
                                mToken.remove(i);
                            }
                        }
                    }
                }
                if (mSceneJson.size() <= 0 && mOnSceneListener != null) {
                    mOnSceneListener.onSceneEmpty();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 注册回调接口的方法，供外部调用
     *
     * @param onSceneListener:场景接口
     */
    public void setOnSceneListener(ISceneCallback onSceneListener) {
        //LogUtil.log("setOnProgressListener");
        mOnSceneListener = onSceneListener;
    }

    public boolean isSceneEmpty() {
        if (mSceneJson.size() <= 0) {
            MLog.d(TAG, "SceneEmpty");
            return true;
        } else {
            MLog.d(TAG, "=====");
            for (String tmp : mSceneJson) {
                MLog.d(TAG, tmp);
            }
            MLog.d(TAG, "=====");
        }
        return false;
    }

    /**
     * 判断场景是否在生效中
     * @param sceneName 场景名称，对应json里的scene值
     * @return
     */
    public static boolean containScene(String sceneName){
        return mSceneNameList.contains(sceneName);
    }
}
