package com.skyworthdigital.voice.scene;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONObject;

/**
 * 场景注册类。参照讯飞做法
 * Created by SDT03046 on 2017/6/5.
 */

public class SkyScene {
    private Context mContext;
    private ISkySceneListener mISceneListenner;
    private IntentFilter mIntentFilter;

    private int token;
    private static final String TAG = "Scene";
    private static final String SCENE_EXECUTE_ACTION = "com.skyworthdigital.voiceassistant.scenes.EXECUTE";
    private static final String SCENE_QUERY_ACTION = "com.skyworthdigital.voiceassistant.topActivity.QUERY";
    private static final String INTENT_RELEASE_SCENE = "com.skyworthdigital.voiceassistant.topActivity.RELEASE";
    private static final String INTENT_COMMIT = "com.skyworthdigital.voiceassistant.topActivity.COMMIT";
    private static final String SCENE = "_scene";
    private static final String OBJ_HASH = "_objhash";
    private static final String SCENE_NAME = "_sceneName";
    private static final String DUER_VOICE_PACKAGE_NAME = "com.skyworthdigital.voiceassistant";
    private static final String DINGDANG_VOICE_PACKAGE_NAME = "com.skyworthdigital.voice.dingdang";
    private String mVoicePackage = DUER_VOICE_PACKAGE_NAME;

    public SkyScene(Context paramContext) {
        this.mContext = paramContext;
        this.token = hashCode();
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction(SCENE_EXECUTE_ACTION);
        this.mIntentFilter.addAction(SCENE_QUERY_ACTION);
    }

    public void init(ISkySceneListener paramISceneListener) {
        this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        this.mISceneListenner = paramISceneListener;
        Log.i(TAG, "init");
        Intent intent = new Intent(SCENE_QUERY_ACTION);
        mContext.sendBroadcast(intent);
        try {
            ApplicationInfo info = mContext.getPackageManager()
                    .getApplicationInfo(DINGDANG_VOICE_PACKAGE_NAME,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            if (info != null) {
                mVoicePackage = DINGDANG_VOICE_PACKAGE_NAME;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    public void release() {
        Intent paramIntent = new Intent(INTENT_RELEASE_SCENE);
        paramIntent.putExtra(SCENE, mISceneListenner.onCmdRegister());
        paramIntent.putExtra(SCENE_NAME, mISceneListenner.getSceneName());
        paramIntent.putExtra(OBJ_HASH, SkyScene.this.token);
        paramIntent.setPackage(mVoicePackage);
        mContext.startService(paramIntent);

        this.mContext.unregisterReceiver(this.mReceiver);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (SCENE_EXECUTE_ACTION.equals(paramIntent.getAction())) {
                if (paramIntent.hasExtra(OBJ_HASH)) {
                    Log.d(TAG, "skyscene token:" + paramIntent.getIntExtra(OBJ_HASH, -1) + " regitster:" + SkyScene.this.token);
                }
                if ((paramIntent.hasExtra(OBJ_HASH)) && (paramIntent.getIntExtra(OBJ_HASH, -1) == SkyScene.this.token)) {
                    if (paramIntent.hasExtra(SCENE)) {
                        String sceneId = paramIntent.getStringExtra(SCENE);
                        Log.d(TAG, "fromIntent sceneId " + sceneId);
                        try {
                            JSONObject mJsonObject = new JSONObject(SkyScene.this.mISceneListenner.onCmdRegister());
                            String userSceneId = mJsonObject.getString(SCENE);

                            Log.d(TAG, "userSceneId  " + sceneId);
                            if (userSceneId.equals(sceneId)) {
                                SkyScene.this.mISceneListenner.onCmdExecute(paramIntent);
                                Log.d("SCENE_TIME", "EndTime " + System.currentTimeMillis());
                                Log.d(TAG, "FUZZY_SCENE_SERVICE_ACTION exe " + paramIntent.toString());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    } else {
                        SkyScene.this.mISceneListenner.onCmdExecute(paramIntent);
                        Log.d(TAG, "FUZZY_SCENE_SERVICE_ACTION exe " + paramIntent.toString());
                    }
                    Log.d(TAG, "EndTime " + System.currentTimeMillis());
                }
            } else if (SCENE_QUERY_ACTION.equals(paramIntent.getAction())) {
                Log.d(TAG, "ADD SCENECOMMAND_ACTION onReceive " + paramIntent.toString());
                //if (paramIntent.getStringExtra("pkgname") == null) {

                paramIntent.setAction(INTENT_COMMIT);
                paramIntent.putExtra(SCENE, mISceneListenner.onCmdRegister());
                paramIntent.putExtra(SCENE_NAME, mISceneListenner.getSceneName());
                paramIntent.putExtra(OBJ_HASH, SkyScene.this.token);

                try {
                    ApplicationInfo info = mContext.getPackageManager()
                            .getApplicationInfo(mVoicePackage,
                                    PackageManager.GET_UNINSTALLED_PACKAGES);
                    if (info != null) {
                        paramIntent.setPackage(mVoicePackage);
                    }
                    paramContext.startService(paramIntent);
                    Log.d(TAG, "startService:" + mVoicePackage);
                } catch (PackageManager.NameNotFoundException e) {

                }
                //}
            }
        }
    };
}

