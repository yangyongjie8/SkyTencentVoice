package com.skyworthdigital.voice.tv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.db.DataTools;
import com.skyworthdigital.voice.db.DbUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.scene.ISkySceneListener;
import com.skyworthdigital.voice.scene.SceneJsonUtil;
import com.skyworthdigital.voice.scene.SkyScene;
import com.skyworthdigital.voice.videoplay.utils.RequestUtil;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;


public abstract class AbsTvLiveControl implements ISkySceneListener {
    public static final String TAG = "AbsTvLiveControl";
    private boolean mIsTVLive = false;
    protected String mSearchCategoryId = null;
    protected String mSearchChannelId = null;
    protected String mSearchChannelName = null;
    protected String mSpeechChannelName;
    protected static AbsTvLiveControl[] mTvUtilInstance = new AbsTvLiveControl[2];

    public static final String TVLIVE_PACKAGENAME = "com.fengmizhibo.live";//"com.linkin.tv";
    private static final String SWITCH_CHANNEL_ACTION = "cn.beelive.intent.action.PLAY_LIVE_CHANNEL";//"com.linkin.tv.TV_SELECT";
    private static final String NEXT_CHANNEL_ACTION = "com.linkin.tv.TV_PREV_CHANNEL";
    private static final String PRE_CHANNEL_ACTION = "com.linkin.tv.TV_NEXT_CHANNEL";
    protected final static String[] SPEECH_FILTER = {"打开", "我想看", "我要看", "播放", "启动", "切到", "台", "频道", "节目"};
    protected final static String[] UPCHANNEL = {"换台", "换一个台", "换个台", "换频道", "换个频道", "换一频道", "下一个台", "下一个频道", "下个台", "下个频道"};
    protected final static String[] DOWNCHANNEL = {"上一个台", "上一个频道", "上个台", "上个频道", "前一个台", "前一个频道"};
    protected final static String DEFAULT_CHANNEL_ID="ff8080813c1ecddc013c1fd336ba1612";//cctv1

    public static AbsTvLiveControl getInstance() {
        if(VoiceApp.isDuer){
            return mTvUtilInstance[0];
        }else {
            return mTvUtilInstance[1];
        }
    }

    protected void preChannel() {
        if (isTvLive()) {
            try {
                MLog.d(TAG, "prvious channel");
                Intent intent = new Intent(PRE_CHANNEL_ACTION);
                intent.putExtra("eventId", System.currentTimeMillis());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Context ctx = VoiceApp.getInstance();
                ctx.startActivity(intent);
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                IStatus.resetDismissTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void nextChannel() {
        if (isTvLive()) {
            try {
                MLog.d(TAG, "next channel");
                Intent intent = new Intent(NEXT_CHANNEL_ACTION);
                intent.putExtra("eventId", System.currentTimeMillis());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Context ctx = VoiceApp.getInstance();
                ctx.startActivity(intent);
                AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                IStatus.resetDismissTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void jumpToChannel(@NonNull String categoryId, @NonNull String channelId) {
        try {
            if (!mIsTVLive) {
                mIsTVLive = true;
                register();
            }
            if (IStatus.mSceneType != IStatus.SCENE_GIVEN) {
                IStatus.setSceneType(IStatus.SCENE_SHOULD_GIVEN);
            } else {
                IStatus.resetDismissTime();
            }

            Intent intent = new Intent(SWITCH_CHANNEL_ACTION);
            intent.putExtra("category_id", categoryId);
            intent.putExtra("channel_id", channelId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Context ctx = VoiceApp.getInstance();
            ctx.startActivity(intent);
            MLog.d(TAG, "jump category_id:" + categoryId +" channelId:"+channelId);
            AbsTTS.getInstance(null).talk(ctx.getString(R.string.str_tvlive_jump));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTvLive(String clsname) {
        if (!TextUtils.isEmpty(clsname)) {
            boolean islive = clsname.contains(TVLIVE_PACKAGENAME);
            if (islive != mIsTVLive) {
                MLog.d(TAG, "is TVlive:" + islive);
                if (islive) {
                    register();
                } else {
                    unregister();
                }
                mIsTVLive = islive;
            }
        }
    }

    public boolean isTvLive() {
        MLog.d(TAG, "get TVlive:" + mIsTVLive);
        return mIsTVLive;
    }

    public abstract boolean control(final String asrResult, String channelName, String query) ;

    protected static boolean checkApkExist(String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = VoiceApp.getInstance().getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    protected static void tvLiveInstallPage() {
        AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_tvlive_uninstall));
        //TODO
    }

    private boolean updateDb(InputStream inStream) {
        try {
            String content = StringUtils.convertStreamToString(inStream);
            long time = System.currentTimeMillis();
            JSONArray xunmaTvList = new JSONArray(content);
            DbUtils dao = new DbUtils(VoiceApp.getInstance());
            dao.updateDbFromNetwork(xunmaTvList);
            MLog.d(TAG, "update time:" + (System.currentTimeMillis() - time));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void tvLiveOpen() {
        if (checkApkExist(TVLIVE_PACKAGENAME)) {
            MLog.d(TAG, "tvLiveOpen");
            if (!isTvLive()) {
                jumpToChannel("7", "1");//cctv1
            }
        } else {
            tvLiveInstallPage();
        }
    }

    public void updateTvliveDbFromNet() {
        MLog.d(TAG, "updateTvliveDbFromNet");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataTools.copyDbAssets(VoiceApp.getInstance());
                final String url = "http://skyworth.linkinme.com/v3/live/chwei_list";
                RequestUtil.sendRequest(url, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        MLog.d(TAG, "Tvlive request fail");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody body = response.body();
                        if (body != null && body.contentLength() > 0) {
                            updateDb(body.byteStream());
                            MLog.d(TAG, "Tvlive request success");
                        }
                    }
                });
            }
        });

        thread.start();
    }

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
        //LogUtil.log("voiceCallback intent : " + intent.getExtras().toString());
        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            String command = intent.getStringExtra(DefaultCmds.COMMAND);
            //LogUtil.log("music command:" + command);
            String action = "";
            Context ctx = VoiceApp.getInstance();
            if (intent.hasExtra(DefaultCmds.INTENT)) {
                action = intent.getStringExtra(DefaultCmds.INTENT);
            }
            switch (command) {
                case "play":
                    if (DefaultCmds.PLAYER_CMD_NEXT.equals(action)) {
                        nextChannel();
                    } else if (DefaultCmds.PLAYER_CMD_PREVIOUS.equals(action)) {
                        preChannel();
                    }
                    break;
                case "next":
                    nextChannel();
                    break;
                case "prev":
                    preChannel();
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
    /**
     * 功能：数据库生成,用于后续更新数据库调试用
     */
    /*public void generateDb(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = context.getResources().openRawResource(R.raw.duertvlist);
                    String content = StringUtils.convertStreamToString(inputStream);
                    JSONArray tvList = new JSONArray(content);
                    DbUtils dao = new DbUtils(context);
                    LogUtil.log("db:" + DataTools.getBasebasePath());
                    for (int i = 0; i < tvList.length(); i++) {
                        String id = tvList.getJSONObject(i).getString("id");
                        LogUtil.log(i + ":\n" + "saveToDb id:" + id);
                        int number = tvList.getJSONObject(i).getInt("number");
                        String type = tvList.getJSONObject(i).getString("type");
                        String channel = tvList.getJSONObject(i).getString("channel");
                        String channel_name = "NULL", channel_code = "NULL";
                        if (tvList.getJSONObject(i).has("channel_name")) {
                            channel_name = tvList.getJSONObject(i).getString("channel_name");
                        }
                        if (tvList.getJSONObject(i).has("channel_code")) {
                            channel_code = tvList.getJSONObject(i).getString("channel_code");
                        }
                        dao.addItem(id, number, type, channel, channel_name, channel_code);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }).start();
    }*/
}
