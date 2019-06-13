package com.skyworthdigital.voice.tencent_module.domains.fm;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.scene.ISkySceneListener;
import com.skyworthdigital.voice.scene.SceneJsonUtil;
import com.skyworthdigital.voice.scene.SkyScene;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.TxTTS;
import com.skyworthdigital.voice.tencent_module.model.TemplateItem;
import com.skyworthdigital.voice.videosearch.gernalview.SkyLoadingView;
import com.skyworthdigital.voice.view.SkyHorizontalMarqueeText;
import com.skyworthdigital.voice.view.Spectrogram;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SkyAudioPlayActivity extends Activity implements ISkySceneListener, AdapterView.OnItemClickListener {
    private static final String TAG = "SkyAudioPlay";
    private static final int VALUE_PLAY = 0;
    private static final int VALUE_PAUSE = 1;
    private static final int SECONDS = 1000;
    private Spectrogram mAudioBarGraph;
    private SkyHorizontalMarqueeText mTextName;
    private TextView mTextClock;
    private AudioPlayer mAudioPlayer;
    private String mUrl;
    private SkyScene mScene;
    private SkyLoadingView mSkyLoadingView;
    private ListView mFMListView;
    private ArrayList<FMBean> mFmList;
    private FmAdapter mFMAdapter;
    private int mCurPlayPos = 0;
    private boolean isPlay = false;
    private MedioPlayListener mMedioPlayListener = new MedioPlayListener() {
        @Override
        public void onCompetion() {
            playNext();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().setFlags(flags, flags);
        setContentView(R.layout.activity_fm_play);

        mAudioBarGraph = (Spectrogram) findViewById(R.id.spectrogram);
        mTextName = (SkyHorizontalMarqueeText) findViewById(R.id.txtName);
        mTextClock = (TextView) findViewById(R.id.txtTime);
        mSkyLoadingView = (SkyLoadingView) this.findViewById(R.id.fm_play_loading);
        mTextName.setText("");

        mTextClock.setText(StringUtils.getDateString());
        mSkyLoadingView.showLoading();
        mAudioPlayer = new AudioPlayer(mAudioBarGraph, mTextClock, mSkyLoadingView, mMedioPlayListener);
        mCurPlayPos = 0;
        Intent intent = getIntent();
        if (intent.hasExtra(GlobalVariable.FM_URL)) {
            mUrl = intent.getStringExtra(GlobalVariable.FM_URL);
            if (intent.hasExtra(GlobalVariable.FM_NAME)) {
                String name = intent.getStringExtra(GlobalVariable.FM_NAME);
                mTextName.setText(name);
            }
            Log.i(TAG, "audio player activity:" + mUrl);
            mAudioPlayer.playUrl(mUrl);
            isPlay = true;
        }

        List<TemplateItem> list = (List<TemplateItem>) getIntent().getSerializableExtra("list");
        mFmList = FMUtils.getAllNews(this, list);
        //2.找到控件
        mFMListView = (ListView) findViewById(R.id.lv_audio);
        //3.创建一个adapter设置给listview
        mFMAdapter = new FmAdapter(this, mFmList);
        mFMListView.setAdapter(mFMAdapter);
        //4.设置listview条目的点击事件
        mFMListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        //需要获取条目上bean对象中url做跳转
        mCurPlayPos = position;
        FMBean bean = (FMBean) parent.getItemAtPosition(position);
        mUrl = bean.news_url;
        mSkyLoadingView.showLoading();
        if (mAudioPlayer != null) {
            mAudioPlayer.pause();
        } else {
            mAudioPlayer = new AudioPlayer(mAudioBarGraph, mTextClock, mSkyLoadingView, mMedioPlayListener);
        }
        Log.i(TAG, "refresh audio player activity:" + mUrl);
        if (!TextUtils.isEmpty(bean.title)) {
            mTextName.setText(bean.title);
        }
        mAudioPlayer.playUrl(mUrl);
        isPlay = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(GlobalVariable.FM_URL)) {
            String url = intent.getStringExtra(GlobalVariable.FM_URL);
            Log.i(TAG, "onNewIntent:" + url);
            mCurPlayPos = 0;
            if (!TextUtils.equals(url, mUrl)) {
                mUrl = url;
                mSkyLoadingView.showLoading();
                if (mAudioPlayer != null) {
                    mAudioPlayer.pause();
                } else {
                    mAudioPlayer = new AudioPlayer(mAudioBarGraph, mTextClock, mSkyLoadingView, mMedioPlayListener);
                }
                Log.i(TAG, "refresh audio player activity:" + mUrl);
                if (intent.hasExtra(GlobalVariable.FM_NAME)) {
                    String name = intent.getStringExtra(GlobalVariable.FM_NAME);
                    Log.i(TAG, "refresh name:" + name);
                    mTextName.setText(name);
                }
                mAudioPlayer.playUrl(mUrl);
                isPlay = true;
            }
            List<TemplateItem> list = (List<TemplateItem>) intent.getSerializableExtra("list");
            mFmList = FMUtils.getAllNews(this, list);
            //3.创建一个adapter设置给listview
            mFMAdapter = new FmAdapter(this, mFmList);
            mFMListView.setAdapter(mFMAdapter);
            mFMAdapter.notifyDataSetChanged();
        }
    }

    private void playerStop() {
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
        }
        isPlay = false;
        if (mScene != null) {
            mScene.release();
            mScene = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume:");
        if (mScene == null) {
            mScene = new SkyScene(this);//菜单进入前台时进行命令注册
        }
        mScene.init(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mScene != null) {
            mScene.release();//不在前台时一定要保证注销
            mScene = null;
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        playerStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playerStop();
    }

    private boolean playByPos(int pos) {
        if (pos < mFmList.size()) {
            FMBean bean = mFmList.get(pos);
            mUrl = bean.news_url;
            mSkyLoadingView.showLoading();
            if (mAudioPlayer != null) {
                mAudioPlayer.pause();
            } else {
                mAudioPlayer = new AudioPlayer(mAudioBarGraph, mTextClock, mSkyLoadingView, mMedioPlayListener);
            }
            Log.i(TAG, "refresh audio player activity:" + mUrl);
            if (!TextUtils.isEmpty(bean.title)) {
                mTextName.setText(bean.title);
            }

            mAudioPlayer.playUrl(mUrl);
            isPlay = true;
            mFMListView.setSelection(pos);
            return true;
        }
        return false;
    }

    private void playNext() {
        if (mCurPlayPos < (mFmList.size() - 1)) {
            mCurPlayPos += 1;
            playByPos(mCurPlayPos);
        }
    }

    private void playPrevious() {
        if (mCurPlayPos > 0) {
            mCurPlayPos -= 1;
            playByPos(mCurPlayPos);
        }
    }

    @Override
    public String onCmdRegister() {
        try {
            String jsonstr = SceneJsonUtil.getSceneJson(this, R.raw.fmcmd);
            JSONObject jsonObject = new JSONObject(jsonstr);
            Log.i("wyf", jsonObject.toString());
            JSONObject cmd = jsonObject.getJSONObject("_commands");
            JSONArray array = new JSONArray();
            for (FMBean item : mFmList) {
                array.put(item.title);
            }
            cmd.put(DefaultCmds.FUZZYMATCH, array);
            String laststr = jsonObject.toString();//new Gson().toJson(jsonObject);
            Log.i("wyf", laststr);
            return laststr;
            //return SceneJsonUtil.getSceneJson(this, R.raw.fmcmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getSceneName() {
        return "SkyAudioPlayer";
    }

    @Override
    public void onCmdExecute(Intent intent) {
        String action = "";
        int value = 0;
        if (intent.hasExtra(DefaultCmds.VALUE)) {
            value = intent.getIntExtra(DefaultCmds.VALUE, 0);
        }

        if (intent.hasExtra(DefaultCmds.FUZZYMATCH)) {
            int pos = intent.getIntExtra(DefaultCmds.FUZZYMATCH, -1);
            if (playByPos(pos)) {
                mCurPlayPos = pos;
                TxTTS.getInstance(null).talk(getString(R.string.str_videoplay) + mFmList.get(pos).title);
                return;
            }
        }

        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            String command = intent.getStringExtra(DefaultCmds.COMMAND);
            Log.i(TAG, "command:" + command);
            switch (command) {
                case "play":
                    if (intent.hasExtra(DefaultCmds.INTENT)) {
                        action = intent.getStringExtra(DefaultCmds.INTENT);
                    }
                    IStatus.resetDismissTime();
                    Log.i(TAG, "_intent:" + action);
                    if (DefaultCmds.PLAYER_CMD_PAUSE.equals(action)) {
                        if (VALUE_PLAY == value) {
                            isPlay = true;
                            if (mAudioPlayer != null) {
                                TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                                mAudioPlayer.play();
                            }
                        } else if (VALUE_PAUSE == value) {
                            isPlay = false;
                            if (mAudioPlayer != null) {
                                TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                                mAudioPlayer.pause();
                            }
                        }
                    } else if (DefaultCmds.PLAYER_CMD_FASTFORWARD.equals(action)) {
                        if (mAudioPlayer != null) {
                            TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                            mAudioPlayer.seek(value * SECONDS);
                        }
                    } else if (DefaultCmds.PLAYER_CMD_BACKFORWARD.equals(action)) {
                        if (mAudioPlayer != null) {
                            TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                            mAudioPlayer.seek((-SECONDS) * value);
                        }
                    } else if (DefaultCmds.PLAYER_CMD_GOTO.equals(action)) {
                        if (mAudioPlayer != null) {
                            TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                            mAudioPlayer.seekTo(value * SECONDS);
                        }
                    } else if (DefaultCmds.PLAYER_CMD_NEXT.equals(action)) {
                        if (mAudioPlayer != null) {
                            TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                            playNext();
                        }
                    } else if (DefaultCmds.PLAYER_CMD_PREVIOUS.equals(action)) {
                        if (mAudioPlayer != null) {
                            TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                            playPrevious();
                        }
                    } else if (DefaultCmds.COMMAND_LOCATION.equals(action)) {
                        if (mAudioPlayer != null) {
                            TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                            playByPos(value - 1);
                        }
                    } else if (DefaultCmds.PLAYER_CMD_EPISODE.equals(action)) {
                        if (mAudioPlayer != null) {
                            TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                            for (int i = 0; i < mFmList.size(); i++) {
                                if (!TextUtils.isEmpty(mFmList.get(i).title)) {
                                    String strValue = StringUtils.getNumbers(mFmList.get(i).title);
                                    int index = (!TextUtils.isEmpty(strValue)) ? Integer.parseInt(strValue) : 0;
                                    int num = StringUtils.chineseNumber2Int(mFmList.get(i).title);//ChineseToNumber(mFmList.get(i).title);
                                    MLog.d(TAG, "index:" + index + " num:" + num);
                                    if (mFmList.get(i).title.contains("第")) {
                                        if (index != 0 && index == value) {
                                            playByPos(i);
                                            return;
                                        }

                                        if (num != 0 && num == value) {
                                            playByPos(i);
                                            return;
                                        }
                                    }
                                }
                            }
                            TxTTS.getInstance(null).talk(getString(R.string.try_play_note) + mFmList.get(0).title + "\"");
                            //playByPos(value);
                        }
                    }
                    break;
                case "stop":
                    IStatus.resetDismissTime();
                    TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                    playerStop();
                    break;
                default:
                    break;
            }

        }

    }

    public interface MedioPlayListener {
        void onCompetion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ImagePipeline mImagePipeline = Fresco.getImagePipeline();
            if (mImagePipeline != null) {
                mImagePipeline.clearMemoryCaches();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
