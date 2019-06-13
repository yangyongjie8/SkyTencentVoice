package com.skyworthdigital.voice.tencent_module.domains.poem;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.scene.ISkySceneListener;
import com.skyworthdigital.voice.scene.SceneJsonUtil;
import com.skyworthdigital.voice.scene.SkyScene;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.TxTTS;
import com.skyworthdigital.voice.tencent_module.model.DataItem;

import java.util.List;

public class SkyPoemActivity extends Activity implements ISkySceneListener, AdapterView.OnItemClickListener {
    private static final String TAG = "SkyAudioPlay";
    private static final int VALUE_PLAY = 0;
    private static final int VALUE_PAUSE = 1;
    private static final int SECONDS = 1000;

    private TextView mTextTitle, mTextAuthor, mTextContent;
    private TextView mTextName, mTextClock;

    private PoemAudioPlayer mAudioPlayer;
    private String mUrl;
    private SkyScene mScene;
    private ListView mPoemListView;
    private List<DataItem> mPoemList;
    private PoemAdapter mPoemAdapter;
    private ScrollView mPoemScrollbar;
    private RelativeLayout mLvLayout;
    private RelativeLayout mTopLayout;


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
        setContentView(R.layout.activity_poem_play);

        mTextTitle = (TextView) findViewById(R.id.title);
        mTextAuthor = (TextView) findViewById(R.id.author);
        mTextName = (TextView) findViewById(R.id.title_play);
        mTextClock = (TextView) findViewById(R.id.poemtime);
        mTextContent = (TextView) findViewById(R.id.content);
        mLvLayout = (RelativeLayout) findViewById(R.id.lv_layout);
        mPoemScrollbar = (ScrollView) findViewById(R.id.poem_scrollbar);
        mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
        mTextClock.setText(StringUtils.getDateString());
        mAudioPlayer = new PoemAudioPlayer(mMedioPlayListener);
        Intent intent = getIntent();
        if (intent.hasExtra(GlobalVariable.FM_NAME)) {
            String name = intent.getStringExtra(GlobalVariable.FM_NAME);
            mTextName.setText(name);
            mTextTitle.setText(name);
        } else {
            mTextName.setText(getResources().getString(R.string.str_poem));
            mTextTitle.setText(getResources().getString(R.string.str_poem));
        }
        if (intent.hasExtra(GlobalVariable.FM_URL)) {
            mUrl = intent.getStringExtra(GlobalVariable.FM_URL);
            Log.i(TAG, "audio player activity:" + mUrl);
            mAudioPlayer.playUrl(mUrl);
        }

        if (getIntent().hasExtra("list")) {
            mPoemList = (List<DataItem>) getIntent().getSerializableExtra("list");
        } else {
            finish();
        }
        //2.找到控件
        mPoemListView = (ListView) findViewById(R.id.lv_audio);
        //3.创建一个adapter设置给listview
        mPoemAdapter = new PoemAdapter(this, mPoemList);
        mPoemListView.setAdapter(mPoemAdapter);
        //4.设置listview条目的点击事件
        mPoemListView.setOnItemClickListener(this);
        mTextAuthor.setText(mPoemList.get(0).mDynasty + ":" + mPoemList.get(0).mAuthor);
        poemStringPrint(mTextContent, mPoemList.get(0).mContent, mPoemList.get(0).mAppreciation);
        mTextTitle.setVisibility(View.GONE);
        if (mPoemList.size() == 1) {
            mLvLayout.setVisibility(View.GONE);
        } else {
            mPoemListView.setSelection(0);
            mPoemListView.requestFocus();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        //需要获取条目上bean对象中url做跳转
        DataItem bean = (DataItem) parent.getItemAtPosition(position);
        mUrl = bean.mDestURL;
        if (mAudioPlayer != null) {
            mAudioPlayer.pause();
        } else {
            mAudioPlayer = new PoemAudioPlayer(mMedioPlayListener);
        }
        Log.i(TAG, "refresh audio player activity:" + mUrl);
        if (!TextUtils.isEmpty(bean.mTitle)) {
            mTextName.setText(bean.mTitle);
            mTextTitle.setText(bean.mTitle);
        } else {
            mTextName.setText(getResources().getString(R.string.str_poem));
            mTextTitle.setText(getResources().getString(R.string.str_poem));
        }

        mTextAuthor.setText(bean.mDynasty + ":" + bean.mAuthor);
        poemStringPrint(mTextContent, bean.mContent, bean.mAppreciation);
        mAudioPlayer.playUrl(mUrl);
    }

    private void playByPos(int pos) {
        //需要获取条目上bean对象中url做跳转
        if (pos < mPoemAdapter.getCount()) {
            mPoemListView.setSelection(pos);
            DataItem bean = (DataItem) mPoemListView.getItemAtPosition(pos);
            mUrl = bean.mDestURL;
            //mSkyLoadingView.showLoading();
            if (mAudioPlayer != null) {
                mAudioPlayer.pause();
            } else {
                mAudioPlayer = new PoemAudioPlayer(mMedioPlayListener);
            }
            Log.i(TAG, "refresh audio player activity:" + mUrl);
            if (!TextUtils.isEmpty(bean.mTitle)) {
                mTextName.setText(bean.mTitle);
                mTextTitle.setText(bean.mTitle);
            }
            mTextAuthor.setText(bean.mDynasty + ":" + bean.mAuthor);
            poemStringPrint(mTextContent, bean.mContent, bean.mAppreciation);
            mAudioPlayer.playUrl(mUrl);
        } else {
            TxTTS.getInstance(null).talk(getString(R.string.str_not_exist));
        }
    }

    private void playPrevious() {
        int pos = mPoemListView.getSelectedItemPosition();
        if (pos > 0) {
            pos -= 1;
        } else {
            TxTTS.getInstance(null).talk(getString(R.string.str_already_first));
            return;
        }
        playByPos(pos);
    }

    private void playNext() {
        int pos = mPoemListView.getSelectedItemPosition();
        if (pos < (mPoemAdapter.getCount() - 1)) {
            pos += 1;
        } else if (mPoemAdapter.getCount() > 1) {
            TxTTS.getInstance(null).talk(getString(R.string.str_already_last));
            return;
        }
        playByPos(pos);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(GlobalVariable.FM_URL)) {
            String url = intent.getStringExtra(GlobalVariable.FM_URL);
            Log.i(TAG, "onNewIntent:" + url);
            if (!TextUtils.equals(url, mUrl)) {
                mUrl = url;
                if (mAudioPlayer != null) {
                    mAudioPlayer.pause();
                } else {
                    mAudioPlayer = new PoemAudioPlayer(mMedioPlayListener);
                }
                Log.i(TAG, "refresh audio player activity:" + mUrl);
                if (intent.hasExtra(GlobalVariable.FM_NAME)) {
                    String name = intent.getStringExtra(GlobalVariable.FM_NAME);
                    Log.i(TAG, "refresh name:" + name);
                    mTextName.setText(name);
                }
                mAudioPlayer.playUrl(mUrl);
            }
            mPoemList = (List<DataItem>) intent.getSerializableExtra("list");

            mPoemAdapter = new PoemAdapter(this, mPoemList);
            mPoemListView.setAdapter(mPoemAdapter);
            mPoemAdapter.notifyDataSetChanged();
            if (mPoemList.size() == 1) {
                mLvLayout.setVisibility(View.GONE);
            } else {
                mPoemScrollbar.setVerticalScrollBarEnabled(false);
                mTextTitle.setVisibility(View.GONE);
                mLvLayout.setVisibility(View.VISIBLE);
                mTopLayout.setVisibility(View.VISIBLE);
                mPoemListView.setSelection(0);
            }
            mTextTitle.setText(mPoemList.get(0).mTitle);
            mTextAuthor.setText(mPoemList.get(0).mDynasty + ":" + mPoemList.get(0).mAuthor);
            poemStringPrint(mTextContent, mPoemList.get(0).mContent, mPoemList.get(0).mAppreciation);
        }
    }*/

    private void playerStop() {
        if (mAudioPlayer != null) {
            mAudioPlayer.stop();
        }
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mPoemList.size() > 1) {
                        mLvLayout.setVisibility(View.GONE);
                        mTopLayout.setVisibility(View.GONE);
                        mPoemScrollbar.setVerticalScrollBarEnabled(true);
                        mTextTitle.setVisibility(View.VISIBLE);
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mPoemList.size() > 1) {
                        if (mLvLayout.getVisibility() != View.VISIBLE) {
                            mPoemScrollbar.setVerticalScrollBarEnabled(false);
                            mTextTitle.setVisibility(View.GONE);
                            mLvLayout.setVisibility(View.VISIBLE);
                            mTopLayout.setVisibility(View.VISIBLE);
                            mPoemListView.requestFocus();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_BACK:
                case VoiceApp.KEYCODE_TA412_BACK:
                    if (mPoemList.size() > 1) {
                        if (mLvLayout.getVisibility() != View.VISIBLE) {
                            mTextTitle.setVisibility(View.GONE);
                            mPoemScrollbar.setVerticalScrollBarEnabled(false);
                            mLvLayout.setVisibility(View.VISIBLE);
                            mPoemListView.requestFocus();
                            mTopLayout.setVisibility(View.VISIBLE);
                            return true;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public String onCmdRegister() {
        try {
            return SceneJsonUtil.getSceneJson(this, R.raw.poemcmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getSceneName() {
        return "SkyPoemScene";
    }

    @Override
    public void onCmdExecute(Intent intent) {
        String action = "";
        int value = 0;
        if (intent.hasExtra(DefaultCmds.VALUE)) {
            value = intent.getIntExtra(DefaultCmds.VALUE, 0);
        }
        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            String command = intent.getStringExtra(DefaultCmds.COMMAND);
            Log.i(TAG, "command:" + command);
            switch (command) {
                case "play":
                    if (intent.hasExtra(DefaultCmds.INTENT)) {
                        action = intent.getStringExtra(DefaultCmds.INTENT);
                    }
                    Log.i(TAG, "_intent:" + action);
                    if (DefaultCmds.PLAYER_CMD_PAUSE.equals(action)) {
                        if (VALUE_PLAY == value) {
                            if (mAudioPlayer != null) {
                                TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                                mAudioPlayer.play();
                            }
                        } else if (VALUE_PAUSE == value) {
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
                        TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                        playNext();
                    } else if (DefaultCmds.PLAYER_CMD_PREVIOUS.equals(action)) {
                        TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                        playPrevious();
                    } else if (DefaultCmds.COMMAND_LOCATION.equals(action)) {
                        TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                        playByPos(value - 1);
                    }
                    break;
                case "stop":
                    TxTTS.getInstance(null).talk(getString(R.string.str_ok));
                    playerStop();
                    break;
                default:
                    break;
            }

        }

    }

    private void poemStringPrint(TextView txtview, String poem, String appreciation) {
        poem = poem.replace("。", "。\n");
        poem = poem.replace("，", "，\n");
        poem = poem.replace("！", "！\n");
        poem = poem.replace("？", "？\n");
        MLog.d("wyf", poem);
        StringBuilder sb = new StringBuilder();
        sb.append(poem);
        int middle = sb.length();
        if (!TextUtils.isEmpty(appreciation)) {
            sb.append("\n");
            sb.append("\n");
            sb.append("译文：");
            sb.append("\n");
            sb.append(appreciation);
        }

        SpannableStringBuilder style = new SpannableStringBuilder(sb.toString());
        style.setSpan(new ForegroundColorSpan(Color.WHITE), 0, middle, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        txtview.setText(style);
        if (!TextUtils.isEmpty(appreciation)) {
            style.setSpan(new ForegroundColorSpan(Color.parseColor("#80ffffff")), middle + 1, sb.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            txtview.setText(style);
        }
    }

    public interface MedioPlayListener {
        void onCompetion();
    }
}
