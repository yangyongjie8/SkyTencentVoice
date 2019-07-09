package com.skyworthdigital.voice.baidu_module.videosearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.baidu_module.BdController;
import com.skyworthdigital.voice.baidu_module.EventMsg;
import com.skyworthdigital.voice.baidu_module.R;
import com.skyworthdigital.voice.baidu_module.robot.BdTTS;
import com.skyworthdigital.voice.baidu_module.util.ActivityManager;
import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.scene.ISkySceneListener;
import com.skyworthdigital.voice.scene.SceneJsonUtil;
import com.skyworthdigital.voice.scene.SkyScene;
import com.skyworthdigital.voice.videoplay.BeeVideoPlayUtils;
import com.skyworthdigital.voice.videoplay.SearchVideoResult;
import com.skyworthdigital.voice.videoplay.SkyVideoInfo;
import com.skyworthdigital.voice.videoplay.SkyVideoPlayUtils;
import com.skyworthdigital.voice.videosearch.BaseActivity;
import com.skyworthdigital.voice.videosearch.adapter.MediaListAdapter;
import com.skyworthdigital.voice.videosearch.callback.MetroItemClickListener;
import com.skyworthdigital.voice.videosearch.callback.OnGetSearchVideoResultListener;
import com.skyworthdigital.voice.videosearch.gernalview.MetroRecyclerView;
import com.skyworthdigital.voice.videosearch.gernalview.SkyLoadingView;

import java.util.concurrent.CopyOnWriteArrayList;


public class SkyVoiceSearchAcitivity extends BaseActivity implements MetroRecyclerView.OnScrollEndListener, ISkySceneListener {
    private static final String TAG = SkyVoiceSearchAcitivity.class.getSimpleName();
    private static final int VALUE_PLAY = 0;
    private static final int PAGE_SIZE = 60;
    private static final int NUM_COLUMN = 6;
    private static final int MSG_SHOW_VIDEO = 0x01;
    private static final int MSG_ADD_VIDEO = 0x02;
    private static final int MSG_UPDATE_VIDEO = 0x03;
    private static final int MSG_SHOW_FAIL = 0x04;
    private static final int MSG_RELEASE_SCENE = 0x05;
    private static final int MSG_REGISTER_SCENE = 0x06;
    private static final int PERPAGE_NUM = 12;
    private static final int LINE_NUM = 6;
    private String mFilmSlots;
    private int mPageIndex;
    private SkyLoadingView mSkyLoadingView;
    private TextView tipsView;
    private TextView mResultTitleTxt;
    private int mVideoTotalCount;
    private MetroRecyclerView mediaListGridView;
    private MediaListAdapter mRecyclerViewAdapter;
    private MetroRecyclerView.MetroGridLayoutManager mMetroGridLayoutManager;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            handleMyMessage(msg);
        }
    };
    private SkyScene mScene;
    CopyOnWriteArrayList<SkyVideoInfo> mVideolist = new CopyOnWriteArrayList<>();
    private int mSelpos = 0;
    private int mResultPageIdx = 0;
    private VoiceModeAdapter mVoiceMode = new VoiceModeAdapter();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.voice_search_acitivity);
        mResultTitleTxt = (TextView) findViewById(R.id.search_result_title);
        mResultTitleTxt.setText(Html.fromHtml("你可以说\"<font color='#0258bb'>换一批</font>\"哟"));
        mSkyLoadingView = (SkyLoadingView) this.findViewById(R.id.detail_playlist_loading);
        mediaListGridView = (MetroRecyclerView) findViewById(R.id.search_content_list);
        tipsView = (TextView) findViewById(R.id.no_content);
        mediaListGridView.setNumColumns(NUM_COLUMN);
        mMetroGridLayoutManager = new MetroRecyclerView.MetroGridLayoutManager(this, NUM_COLUMN, MetroRecyclerView.VERTICAL);
        mediaListGridView.setLayoutManager(mMetroGridLayoutManager
        );
        mediaListGridView.setScrollType(MetroRecyclerView.SCROLL_TYPE_ON_LAST);
        mRecyclerViewAdapter = new MediaListAdapter(this);
        mediaListGridView.setAdapter(mRecyclerViewAdapter);
        mediaListGridView.setOnItemClickListener(metroItemClickListener);
        //mediaListGridView.setOnScrollEndListener(this);
        //AllMediaApplication.getInstance().setSearchActivityFocus(true);
        IntentFilter filter = new IntentFilter(SkyVideoPlayUtils.SEARCH_WORD_BRAODCAST);
        registerReceiver(mReceiver, filter);
        getVoiceKeyword(getIntent());
        ActivityManager.onCreateActivity(this);
        mResultPageIdx = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Message msg = new Message();
        msg.what = MSG_RELEASE_SCENE;
        mHandler.sendMessage(msg);
        /*if (mScene != null) {
            mScene.release();//不在前台时一定要保证注销
            mScene = null;
        }
        ActivityManager.removeActivity(this);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Message msg = new Message();
        msg.what = MSG_REGISTER_SCENE;
        mHandler.sendMessage(msg);
        /*if (mScene == null) {
            mScene = new SkyScene(this);//菜单进入前台时进行命令注册
        }
        mScene.init(this);
        ActivityManager.onCreateActivity(this);*/
    }

    private void showNextPageResult() {
        mediaListGridView.setAdapter(mRecyclerViewAdapter);
        mediaListGridView.setFocusable(true);
        CopyOnWriteArrayList<SkyVideoInfo> rows = mVideolist;//result.getRows();
        int count = rows.size();
        int start = mResultPageIdx * PERPAGE_NUM;
        int end = mResultPageIdx * PERPAGE_NUM + PERPAGE_NUM;
        CopyOnWriteArrayList cal;
        if (count >= end) {
            cal = new CopyOnWriteArrayList(rows.subList(start, end).toArray());
            mRecyclerViewAdapter.replaceAllVideo(cal);
        } else if (count > start) {
            cal = new CopyOnWriteArrayList(rows.subList(start, rows.size()).toArray());
            mRecyclerViewAdapter.replaceAllVideo(cal);
        }

        if (mResultPageIdx * PERPAGE_NUM + mSelpos > count) {
            mSelpos = 0;
        }
        mRecyclerViewAdapter.setSelection(mSelpos);
        mediaListGridView.setSelectedItem(mSelpos);
        //LogUtil.log("update:" + "sixe:" + rows.size() + " start:" + start + " end:" + end);
    }

    protected void handleMyMessage(Message msg) {
        switch (msg.what) {
            case MSG_SHOW_VIDEO: {
                SearchVideoResult mResult = (SearchVideoResult) msg.obj;
                if (mResult != null) {
                    showVideoResult(mResult, false);
                } else {
                    MLog.i(TAG, "show video,but null");
                    //tipsView.setText(getString(R.string.search_fail_tips));
                    //tipsView.setVisibility(View.VISIBLE);
                }
                mSkyLoadingView.clearLoading();
            }
            break;
            case MSG_UPDATE_VIDEO: {
                showNextPageResult();
                mSkyLoadingView.clearLoading();
            }
            break;
            case MSG_ADD_VIDEO: {
                SearchVideoResult mResult = (SearchVideoResult) msg.obj;
                if (mResult != null) {
                    if (mResult.getRows() != null) {
                        mVideolist.addAll(mResult.getRows());
                    }
                    //LogUtil.log("mlist size:" + mVideolist.size());
                }
                mSkyLoadingView.clearLoading();
            }
            break;
            case MSG_SHOW_FAIL:
                tipsView.setText(getString(R.string.search_fail_tips));
                tipsView.setVisibility(View.VISIBLE);
                mSkyLoadingView.clearLoading();
                break;
            case MSG_RELEASE_SCENE:
                if (mScene != null) {
                    mScene.release();//不在前台时一定要保证注销
                    mScene = null;
                }
                ActivityManager.removeActivity(this);
                break;
            case MSG_REGISTER_SCENE:
                if (mScene == null) {
                    mScene = new SkyScene(this);//菜单进入前台时进行命令注册
                }
                mScene.init(this);
                ActivityManager.onCreateActivity(this);
                break;
            default:
                break;
        }
    }

    private MetroItemClickListener metroItemClickListener = new MetroItemClickListener() {

        @Override
        public void onItemClick(View parentView, View itemView, int position) {
            //int start = mResultPageIdx * PERPAGE_NUM;
            try {
                //SkyVideoInfo videoInfo = mVideolist.get(start + position);
                SkyVideoInfo videoInfo = mRecyclerViewAdapter.getAllVideo().get(position);
                MLog.i(TAG, "onItemClick:" + position + " source id:" + videoInfo.getSourceId());
                BdTTS.getInstance().talk(getString(R.string.str_ok));
                mVoiceMode.setRecognizeGoOn(false);
//                if (videoInfo.getSourceId() == GlobalVariable.TENCENT_SOURCE) {
//                    SkyVideoPlayUtils.startToTxVideoDetail(SkyVoiceSearchAcitivity.this, videoInfo.getOtherId());
//                } else if (videoInfo.getSourceId() == GlobalVariable.MGTV_SOURCE) {
//                    SkyVideoPlayUtils.startToMangguoVideoDetail(SkyVoiceSearchAcitivity.this, videoInfo.getOtherId(), "");
//                } else {
//                    SkyVideoPlayUtils.startToVideoDetail(SkyVoiceSearchAcitivity.this, videoInfo.getVideoId());
//                }
                String sourceId = String.valueOf(videoInfo.getSourceId());
                String videoId = String.valueOf(videoInfo.getVideoId());
                Log.d("VOICE play:", "sourceId:" + sourceId + " videoId:" + videoId);
                BeeVideoPlayUtils.startToVideoDetail(SkyVoiceSearchAcitivity.this, sourceId, videoId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals(SkyVideoPlayUtils.SEARCH_WORD_BRAODCAST)) {
                //getVoiceKeyword(intent);
            }
        }
    };

    private void startGetNextPage() {
        MLog.i(TAG, "startGetNextPage mPageIndex:" + mPageIndex);
        mPageIndex = mPageIndex + 1;
        mSkyLoadingView.showLoading();
        SkyVideoPlayUtils
                .startGetVoiceSearchVideoList(mFilmSlots, mPageIndex, PAGE_SIZE, new OnGetSearchVideoResultListener() {

                    @Override
                    public void getSearchVideoResult(SearchVideoResult result, String keyword) {
                        Message msg = new Message();
                        msg.what = MSG_ADD_VIDEO;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void getSearchFailed() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //AllMediaApplication.getInstance().setSearchActivityFocus(false);
        unregisterReceiver(mReceiver);
        //ActivityManager.OnDestroyActivity(this);
        ActivityManager.removeActivity(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            MLog.i(TAG, "search " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mSelpos >= LINE_NUM) {
                        mSelpos = mSelpos - LINE_NUM;
                        mediaListGridView.setFocusable(true);
                        mediaListGridView.setSelectedItem(mSelpos);
                    } else if (mResultPageIdx >= 1) {
                        prePage(false);
                    }
                    //LogUtil.log("mSelpos " + mSelpos + " mVideoTotalCount:" + mVideoTotalCount + " mResultPageIdx:" + mResultPageIdx);
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mSelpos >= LINE_NUM) {
                        mSelpos = mSelpos - LINE_NUM;
                        nextPage(false);
                    } else if (mResultPageIdx * PERPAGE_NUM + mSelpos + LINE_NUM < mVideolist.size()) {
                        mSelpos = mSelpos + LINE_NUM;
                        mediaListGridView.setFocusable(true);
                        mediaListGridView.setSelectedItem(mSelpos);
                    }
                    //LogUtil.log("1mSelpos " + mSelpos + " mVideoTotalCount:" + mVideoTotalCount + " mResultPageIdx:" + mResultPageIdx);
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mSelpos == (PERPAGE_NUM - 1)) {
                        nextPage(false);
                    } else if (mResultPageIdx * PERPAGE_NUM + mSelpos + 1 < mVideolist.size()) {
                        mSelpos = mSelpos + 1;
                        mediaListGridView.setFocusable(true);
                        mediaListGridView.setSelectedItem(mSelpos);
                    }

                    //LogUtil.log("2mSelpos " + mSelpos + " mVideoTotalCount:" + mVideoTotalCount + " mResultPageIdx:" + mResultPageIdx);
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mSelpos == 0) {
                        prePage(false);
                    } else {
                        mSelpos = mSelpos - 1;
                        mediaListGridView.setFocusable(true);
                        mediaListGridView.setSelectedItem(mSelpos);
                    }
                    //LogUtil.log("3mSelpos " + mSelpos + " mVideoTotalCount:" + mVideoTotalCount + " mResultPageIdx:" + mResultPageIdx);
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void getVoiceKeyword(Intent intent) {
        String defaultkey = "{\"type\":\"电影\"}";
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String filmSlots = extras.getString("searchParam");
                MLog.i(TAG, "param:" + filmSlots);
                if (filmSlots != null) {
                    if (TextUtils.equals(filmSlots, "{}")) {
                        filmSlots = defaultkey;
                    }
                    startSearchWord(filmSlots);
                }
            }
        }
    }

    private void showVideoResult(SearchVideoResult result, boolean update) {
        mVideoTotalCount = result.getTotal();

        if (mVideoTotalCount > 0) {
            tipsView.setVisibility(View.GONE);
            mVoiceMode.setRecognizeGoOn(true);
            if (update) {
                BdTTS.getInstance().talk(getString(R.string.str_filmsearch_note2));
            } else {
                BdTTS.getInstance().talk(getString(R.string.str_filmsearch_note1));
            }
        } else {
            tipsView.setText(getString(R.string.no_content_tips));
            mVoiceMode.setRecognizeGoOn(false);
            tipsView.setVisibility(View.VISIBLE);
            //EventBus.getDefault().post(new EventMsg(EventMsg.MSG_GUIDE_SHOW, 1));
            BdTTS.getInstance().talk(getString(R.string.no_content_tips));
        }
        mediaListGridView.setAdapter(mRecyclerViewAdapter);
        mediaListGridView.setFocusable(true);
        CopyOnWriteArrayList cal = new CopyOnWriteArrayList(result.getRows().toArray());

        if (result.getRows().size() > PERPAGE_NUM) {
            CopyOnWriteArrayList<SkyVideoInfo> list = cal;
            list.subList(0, PERPAGE_NUM);
            mRecyclerViewAdapter.replaceAllVideo(list);
        } else {
            mRecyclerViewAdapter.replaceAllVideo(cal);
        }
        mVideolist = cal;
        mRecyclerViewAdapter.notifyDataSetChanged();
        mRecyclerViewAdapter.setSelection(0);
        mSelpos = 0;
        MLog.i(TAG, "mVideoTotalCount:" + mVideoTotalCount);
    }

    private void startSearchWord(String filmSlots) {
        MLog.i(TAG, "startSearchWord2:" + filmSlots);
        mPageIndex = 1;
        mVideoTotalCount = 0;
        mResultPageIdx = 0;
        mFilmSlots = filmSlots;
        mSkyLoadingView.showLoading();
        BdController.getInstance().postEvent(new EventMsg(EventMsg.MSG_DISMISS_DIALOG, 20000));
        Log.d("TAG", "========mFilmSlots...:" + mFilmSlots);
        SkyVideoPlayUtils.startGetVoiceSearchVideoList(mFilmSlots, 1, PAGE_SIZE, new OnGetSearchVideoResultListener() {

            @Override
            public void getSearchVideoResult(SearchVideoResult result, String keyword) {
                mPageIndex = 1;
                mVideoTotalCount = 0;
                mResultPageIdx = 0;
                Message msg = new Message();
                msg.what = MSG_SHOW_VIDEO;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }

            @Override
            public void getSearchFailed() {
                Message msg = new Message();
                msg.what = MSG_SHOW_FAIL;
                mHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onScrollToBottom(int keyCode) {
        if (mVideoTotalCount < 1) {
            return;
        }
        if (mRecyclerViewAdapter.getAllVideo().size() < mVideoTotalCount) {
            startGetNextPage();
        }
    }

    @Override
    public void onScrollToTop(int keyCode) {
        // TODO Auto-generated method stub

    }

    private void searchPlayByNum(int pos) {
        //LogUtil.log("pos:" + pos + " mPageIndex:" + mPageIndex + " mVideoTotalCount:" + mVideoTotalCount + " " + mResult.getRows().size());
        if (pos < 1 || pos > PERPAGE_NUM) {
            BdTTS.getInstance().talk(getString(R.string.str_searchfilm_numerr));
            return;
        }
        try {
            if (pos < mRecyclerViewAdapter.getItemCount()/*getAllVideo().size()*/ + 1) {
                SkyVideoInfo videoInfo = mRecyclerViewAdapter.getAllVideo().get(pos - 1);
                int firstVideoid = videoInfo.getVideoId();
//                if (videoInfo.getSourceId() == GlobalVariable.TENCENT_SOURCE) {
//                    SkyVideoPlayUtils.startToTxVideoDetail(SkyVoiceSearchAcitivity.this, videoInfo.getOtherId());
//                } else if (videoInfo.getSourceId() == GlobalVariable.MGTV_SOURCE) {
//                    SkyVideoPlayUtils.startToMangguoVideoDetail(SkyVoiceSearchAcitivity.this, videoInfo.getOtherId(), "");
//                } else {
//                    SkyVideoPlayUtils.getVideoDetailByID(videoInfo, firstVideoid, -1/*, listener*/);
//                }
                String sourceId = String.valueOf(videoInfo.getSourceId());
                String videoId = String.valueOf(videoInfo.getVideoId());
                BeeVideoPlayUtils.startToVideoDetail(this, sourceId, videoId);
            } else {
                BdTTS.getInstance().talk(getString(R.string.str_searchfilm_noexist));
                //return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String onCmdRegister() {
        //LogUtil.log("search onCmdRegister");
        try {
            return SceneJsonUtil.getSceneJson(this, R.raw.searchcmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getSceneName() {
        return "SkyVoiceSearch";
    }

    private void prePage(boolean isVoice) {
        MLog.i(TAG, "pre");
        if (mResultPageIdx == 0) {
            mVoiceMode.setRecognizeGoOn(false);
            BdTTS.getInstance().talk(getString(R.string.str_searchfilm_firstpage));//"已经是最后一批啦。");
            return;
        }

        if (isVoice) {
            BdTTS.getInstance().talk(getString(R.string.str_filmsearch_note2));
            mVoiceMode.setRecognizeGoOn(true);
        }

        if (mResultPageIdx >= 1) {
            mResultPageIdx = mResultPageIdx - 1;
            Message msg = new Message();
            msg.what = MSG_UPDATE_VIDEO;
            //msg.obj = mResult;
            mHandler.sendMessage(msg);
        }
        //LogUtil.log("mVideoTotalCount:" + mVideoTotalCount);
    }

    private void nextPage(boolean isVoice) {
        if (mVideoTotalCount < PERPAGE_NUM) {
            mVoiceMode.setRecognizeGoOn(false);
            BdTTS.getInstance().talk(getString(R.string.str_searchfilm_lastpage));//"已经是最后一批啦。");
            return;
        }
        int pagetotal = mVideoTotalCount / PERPAGE_NUM;
        if (mVideoTotalCount % PERPAGE_NUM > 0) {
            pagetotal = pagetotal + 1;
        }

        if ((mResultPageIdx + 1) == pagetotal) {
            mVoiceMode.setRecognizeGoOn(false);
            BdTTS.getInstance().talk(getString(R.string.str_searchfilm_lastpage));//"已经是最后一批啦。");
            return;
        }

        if (isVoice) {
            BdTTS.getInstance().talk(getString(R.string.str_filmsearch_note2));
            mVoiceMode.setRecognizeGoOn(true);
        }
        //LogUtil.log("mResult.getTotal():" + mVideolist.size());
        if (mVideolist.size() > (mResultPageIdx + 1) * PERPAGE_NUM) {
            mResultPageIdx = mResultPageIdx + 1;
            Message msg = new Message();
            msg.what = MSG_UPDATE_VIDEO;
            //msg.obj = mResult;
            mHandler.sendMessage(msg);
        }
        //LogUtil.log("mVideoTotalCount:" + mVideoTotalCount);
        if (mVideoTotalCount > 48 && mVideolist.size() < (mResultPageIdx + 3) * PERPAGE_NUM) {
            startGetNextPage();
        }
    }

    @Override
    public void onCmdExecute(Intent intent) {
        //LogUtil.log("voiceCallback intent : " + intent.getExtras().toString());
        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            String command = intent.getStringExtra(DefaultCmds.COMMAND);
            MLog.i(TAG, "command:" + command);
            String action = "";
            int index = 0;
            if (intent.hasExtra(DefaultCmds.INTENT)) {
                action = intent.getStringExtra(DefaultCmds.INTENT);
            }
            switch (command) {
                case "next":
                    nextPage(true);
                    break;
                case "previous":
                    prePage(true);
                    break;
                case "play":
                    int value = 0;
                    if (intent.hasExtra(DefaultCmds.VALUE)) {
                        value = intent.getIntExtra(DefaultCmds.VALUE, 0);
                        MLog.i(TAG, "num:" + value + " " + action);
                    }
                    if (DefaultCmds.COMMAND_LOCATION.equals(action)) {
                        searchPlayByNum(value);
                    } else if (DefaultCmds.PLAYER_CMD_PAUSE.equals(action)) {
                        if (VALUE_PLAY == value) {
                            searchPlayByNum((mSelpos + 1) % PERPAGE_NUM);
                        }
                    }
                    break;
                case "play12":
                    index += 1;
                case "play11":
                    index += 1;
                case "play10":
                    index += 1;
                case "play9":
                    index += 1;
                case "play8":
                    index += 1;
                case "play7":
                    index += 1;
                case "play6":
                    index += 1;
                case "play5":
                    index += 1;
                case "play4":
                    index += 1;
                case "play3":
                    index += 1;
                case "play2":
                    index += 1;
                case "play1":
                    index += 1;
                    searchPlayByNum(index);
                    break;
                default:
                    break;
            }
        }
    }
}


