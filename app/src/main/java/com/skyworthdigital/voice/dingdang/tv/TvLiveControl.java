package com.skyworthdigital.voice.dingdang.tv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.SkyAsrDialogControl;
import com.skyworthdigital.voice.dingdang.control.model.ValueItem;
import com.skyworthdigital.voice.dingdang.control.tts.MyTTS;
import com.skyworthdigital.voice.dingdang.domains.tvlive.utils.DataTools;
import com.skyworthdigital.voice.dingdang.domains.tvlive.utils.DbUtils;
import com.skyworthdigital.voice.dingdang.domains.videoplay.utils.RequestUtil;
import com.skyworthdigital.voice.dingdang.scene.SceneJsonUtil;
import com.skyworthdigital.voice.dingdang.utils.DefaultCmds;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.StringUtils;
import com.skyworthdigital.voiceassistant.scene.ISkySceneListener;
import com.skyworthdigital.voiceassistant.scene.SkyScene;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class TvLiveControl implements ISkySceneListener {
    public static final String TAG = "TvLiveControl";
    private boolean mIsTVLive = false;
    private String mSpeechChannelName;
    private static TvLiveControl mTvUtilInstance = null;

    public static final String TVLIVE_PACKAGENAME = "com.linkin.tv";
    private static final String SWITCH_CHANNEL_ACTION = "com.linkin.tv.TV_SELECT";
    private static final String NEXT_CHANNEL_ACTION = "com.linkin.tv.TV_PREV_CHANNEL";
    private static final String PRE_CHANNEL_ACTION = "com.linkin.tv.TV_NEXT_CHANNEL";
    private final static String[] SPEECH_FILTER = {"打开", "我想看", "我要看", "播放", "启动", "切到", "台", "频道", "节目"};
    private final static String[] UPCHANNEL = {"换台", "换一个台", "换个台", "换频道", "换个频道", "换一频道", "下一个台", "下一个频道", "下个台", "下个频道"};
    private final static String[] DOWNCHANNEL = {"上一个台", "上一个频道", "上个台", "上个频道", "前一个台", "前一个频道"};
    private final static String DEFAULT_CHANNEL_ID="ff8080813c1ecddc013c1fd336ba1612";//cctv1

    public static TvLiveControl getInstance() {
        if (mTvUtilInstance == null) {
            mTvUtilInstance = new TvLiveControl();
        }
        return mTvUtilInstance;
    }

    private TvLiveControl() {
    }

    private void preChannel() {
        if (isTvLive()) {
            try {
                MLog.d(TAG, "prvious channel");
                Intent intent = new Intent(PRE_CHANNEL_ACTION);
                intent.putExtra("eventId", System.currentTimeMillis());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Context ctx = VoiceApp.getInstance();
                ctx.startActivity(intent);
                MyTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                IStatus.resetDismissTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void nextChannel() {
        if (isTvLive()) {
            try {
                MLog.d(TAG, "next channel");
                Intent intent = new Intent(NEXT_CHANNEL_ACTION);
                intent.putExtra("eventId", System.currentTimeMillis());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                Context ctx = VoiceApp.getInstance();
                ctx.startActivity(intent);
                MyTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
                IStatus.resetDismissTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void jumpToChannel(String id) {
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
            intent.putExtra("id", id);
            intent.putExtra("eventId", System.currentTimeMillis());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Context ctx = VoiceApp.getInstance();
            ctx.startActivity(intent);
            MLog.d(TAG, "jump id:" + id);
            MyTTS.getInstance(null).talk(ctx.getString(R.string.str_tvlive_jump));
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

    private static boolean checkApkExist(String packageName) {
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

    private static void tvLiveInstallPage() {
        MyTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_tvlive_uninstall));
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
                jumpToChannel(DEFAULT_CHANNEL_ID);//cctv1 id
            }
        } else {
            tvLiveInstallPage();
        }
    }

    public boolean control(final ValueItem valueItem, String query, SkyAsrDialogControl dialogControl) {
        if (valueItem == null && TextUtils.isEmpty(query)) {
            return false;
        }
        MLog.d(TAG, "tvLive control " + query);
        String channelname = null, searchChannelCode = null, searchChannelName = null;
        if (valueItem != null && !TextUtils.isEmpty(valueItem.mText)) {
            channelname = valueItem.mText;
            searchChannelName = valueItem.mText;
        }

        String result;

        //mSearchChannelName = null;
        if (checkApkExist(TVLIVE_PACKAGENAME)) {
            if (!TextUtils.isEmpty(query)) {
                for (String temp : UPCHANNEL) {
                    if (TextUtils.equals(temp, query)) {
                        preChannel();
                        return true;
                    }
                }
                for (String temp : DOWNCHANNEL) {
                    if (TextUtils.equals(temp, query)) {
                        nextChannel();
                        return true;
                    }
                }
                mSpeechChannelName = Utils.filterBy(query, SPEECH_FILTER);
                MLog.d(TAG, "speech name：" + mSpeechChannelName);
            }

            DbUtils dao = new DbUtils(VoiceApp.getInstance());
            result = dao.searchByType(mSpeechChannelName);
            if (result != null) {
                jumpToChannel(result);
                return true;
            }
            result = dao.searchItem(searchChannelName, searchChannelCode, channelname, mSpeechChannelName);
            if (result != null) {
                jumpToChannel(result);
                return true;
            }
        } else {
            tvLiveInstallPage();
        }
        return false;
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
                    MyTTS.getInstance(null).talk(ctx.getString(R.string.str_ok));
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
