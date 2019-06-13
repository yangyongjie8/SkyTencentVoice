package com.skyworthdigital.voice.videosearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.beesearch.BeeSearchParams;
import com.skyworthdigital.voice.beesearch.BeeSearchVideoResult;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.scene.ISkySceneListener;
import com.skyworthdigital.voice.scene.SceneJsonUtil;
import com.skyworthdigital.voice.scene.SkyScene;
import com.skyworthdigital.voice.videoplay.BeeVideoPlayUtils;
import com.skyworthdigital.voice.videoplay.SkyVideoInfo;
import com.skyworthdigital.voice.videoplay.SkyVideoPlayUtils;
import com.skyworthdigital.voice.videosearch.adapter.MediaListAdapter;
import com.skyworthdigital.voice.videosearch.callback.MetroItemClickListener;
import com.skyworthdigital.voice.videosearch.gernalview.MetroRecyclerView;
import com.skyworthdigital.voice.videosearch.gernalview.SkyLoadingView;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SkyBeeSearchAcitivity extends BaseActivity implements MetroRecyclerView.OnScrollEndListener, ISkySceneListener {
    private static final String TAG = "search";
    private static final int VALUE_PLAY = 0;
    private static final int PAGE_SIZE = 12;//60;
    private static final int NUM_COLUMN = 6;
    private static final int MSG_SHOW_VIDEO = 0x01;
    private static final int MSG_ADD_VIDEO = 0x02;
    private static final int MSG_UPDATE_VIDEO = 0x03;
    private static final int MSG_SHOW_FAIL = 0x04;
    private static final int PERPAGE_NUM = 12;
    private static final int LINE_NUM = 6;
    private String mFilmSlots;
    //private int mPageIndex;
    private SkyLoadingView mSkyLoadingView;
    private TextView tipsView;
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
    private int mSelpos = 0;

    private TextView mTxtResult;
    private int mCurPage = 0, mTotalPage = 0;
    private BeeSearchVideoResult mVideoResult = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.voice_search_acitivity);
        mSkyLoadingView = (SkyLoadingView) this.findViewById(R.id.detail_playlist_loading);
        mTxtResult = (TextView) findViewById(R.id.search_result_title);
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
        IntentFilter filter = new IntentFilter(SkyVideoPlayUtils.SEARCH_WORD_BRAODCAST);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mReceiver, filter);
        getVoiceKeyword(getIntent());
        MLog.i(TAG, "onCreate");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLog.i(TAG, "onPause");
        BeeSearchParams.getInstance().setMetasInfo(null);
        if (mScene != null) {
            mScene.release();//不在前台时一定要保证注销
            mScene = null;
        }
        BeeSearchParams.getInstance().setIsInSearch(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BeeSearchParams.getInstance().setIsInSearch(true);
        if (mScene == null) {
            mScene = new SkyScene(this);//菜单进入前台时进行命令注册
        }
        mScene.init(this);
        BeeSearchParams.getInstance().setMetasInfo(mVideoResult);
    }


    protected void handleMyMessage(Message msg) {
        switch (msg.what) {
            case MSG_SHOW_VIDEO: {
                BeeSearchVideoResult mResult = (BeeSearchVideoResult) msg.obj;
                if (mResult != null) {
                    showVideoResult(mResult, false);
                } else {
                    MLog.i(TAG, "show video,but null");
                }
                mSkyLoadingView.clearLoading();
            }
            break;
            case MSG_SHOW_FAIL:
                tipsView.setText(getString(R.string.search_fail_tips));
                tipsView.setVisibility(View.VISIBLE);
                mSkyLoadingView.clearLoading();
                break;
            default:
                break;
        }
    }

    private MetroItemClickListener metroItemClickListener = new MetroItemClickListener() {

        @Override
        public void onItemClick(View parentView, View itemView, int position) {
            try {
                SkyVideoInfo videoInfo = mRecyclerViewAdapter.getAllVideo().get(position);
                MLog.i(TAG, "onItemClick:" + position + " source id:" + videoInfo.getSourceId());
//                if (videoInfo.getSourceId() == GlobalVariable.TENCENT_SOURCE) {
//                    SkyVideoPlayUtils.startToTxVideoDetail(SkyBeeSearchAcitivity.this, videoInfo.getOtherId());
//                } else if (videoInfo.getSourceId() == GlobalVariable.MGTV_SOURCE) {
//                    SkyVideoPlayUtils.startToMangguoVideoDetail(SkyBeeSearchAcitivity.this, videoInfo.getOtherId(), "");
//                } else {
//                    SkyVideoPlayUtils.startToVideoDetail(SkyBeeSearchAcitivity.this, videoInfo.getVideoId());
//                }
                String sourceId = String.valueOf(videoInfo.getSourceId());
                String videoId = String.valueOf(videoInfo.getVideoId());
                Log.d("VOICE play:", "sourceId:" + sourceId + " videoId:" + videoId);
                BeeVideoPlayUtils.startToVideoDetail(SkyBeeSearchAcitivity.this, sourceId, videoId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                MLog.i(TAG, "reason: " + reason);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    exitHome();
                }
            }
            if (action.equals(SkyVideoPlayUtils.SEARCH_WORD_BRAODCAST)) {
                //getVoiceKeyword(intent);
            }
        }
    };

    public void exitHome() {
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mReceiver);
        ImagePipeline mImagePipeline = Fresco.getImagePipeline();
        if (mImagePipeline != null) {
            mImagePipeline.clearMemoryCaches();
        }
        MLog.i(TAG, "searchpage onDestroy");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            //MLog.i(TAG, "search " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (mSelpos >= LINE_NUM) {
                        mSelpos = mSelpos - LINE_NUM;
                        mediaListGridView.setFocusable(true);
                        mediaListGridView.setSelectedItem(mSelpos);
                    } else if (mCurPage >= 1) {
                        jumpToPage(false, mCurPage - 1);
                    }
                    //LogUtil.log("mSelpos " + mSelpos + " mVideoTotalCount:" + mVideoTotalCount + " mResultPageIdx:" + mResultPageIdx);
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (mSelpos >= LINE_NUM) {
                        mSelpos = mSelpos - LINE_NUM;
                        jumpToPage(false, mCurPage + 1);
                    } else if (mSelpos + LINE_NUM < mRecyclerViewAdapter.getAllVideo().size()/* mVideolist.size()*/) {
                        mSelpos = mSelpos + LINE_NUM;
                        mediaListGridView.setFocusable(true);
                        mediaListGridView.setSelectedItem(mSelpos);
                    }
                    //LogUtil.log("1mSelpos " + mSelpos + " mVideoTotalCount:" + mVideoTotalCount + " mResultPageIdx:" + mResultPageIdx);
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mSelpos == (PERPAGE_NUM - 1)) {
                        jumpToPage(false, mCurPage + 1);
                    } else if (mSelpos + 1 < mRecyclerViewAdapter.getAllVideo().size()/* mVideolist.size()*/) {
                        mSelpos = mSelpos + 1;
                        mediaListGridView.setFocusable(true);
                        mediaListGridView.setSelectedItem(mSelpos);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mSelpos == 0) {
                        jumpToPage(false, mCurPage - 1);
                        //prePage(false);
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
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String filmSlots = extras.getString("abnf");
                if (!TextUtils.isEmpty(filmSlots)) {
                    startSearchWord(filmSlots);
                }
                mVideoTotalCount = extras.getInt("total");
                String resultstr = extras.getString("resultstr");
                if (!TextUtils.isEmpty(resultstr)) {
                    highlightResult(mTxtResult, resultstr);

                }
                MLog.i(TAG, "abnf:" + filmSlots + " total:" + mVideoTotalCount);
            }
        }
    }

    private void highlightResult(TextView txtview, String str) {
        Pattern p = Pattern.compile("\\{\\w+\\}");
        Matcher m = p.matcher(str);
        ArrayList<String> keywords = new ArrayList<>();
        try {
            while (m.find()) {
                String key = str.substring(m.start() + 1, m.end() - 1);
                keywords.add(key);
                //LogUtil.log(key + " " + m.group() + "   位置：[" + m.start() + "," + m.end() + "]");
            }

            int[] color = {Color.parseColor("#d94e10"), Color.GREEN, Color.MAGENTA, Color.BLUE};
            int cnt = 0, start, end;
            str = str.replace("{", "");
            str = str.replace("}", "");
            SpannableStringBuilder style = new SpannableStringBuilder(str);
            for (String item : keywords) {
                start = str.indexOf(item);
                end = start + item.length();
                style.setSpan(new ForegroundColorSpan(color[cnt % 4]), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                txtview.setText(style);
                //LogUtil.log("start:" + start + " end:" + end);
                cnt++;
            }
        } catch (Exception e) {
            txtview.setText(str);
        }
    }

    private void showVideoResult(BeeSearchVideoResult result, boolean update) {
        if (result == null || result.getVideolist() == null) {
            mVideoResult = null;
            tipsView.setText(getString(R.string.no_content_tips));
            tipsView.setVisibility(View.VISIBLE);
            return;
        }
        if (mVideoTotalCount > 0) {
            mVideoResult = result;
            tipsView.setVisibility(View.GONE);
        } else {
            mVideoResult = null;
            tipsView.setText(getString(R.string.no_content_tips));
            tipsView.setVisibility(View.VISIBLE);
            //MyTTS.getInstance(null).talk(getString(R.string.no_content_tips));
            return;
        }
        mediaListGridView.setAdapter(mRecyclerViewAdapter);
        mediaListGridView.setFocusable(true);
        CopyOnWriteArrayList cal = new CopyOnWriteArrayList(result.getVideolist().toArray());

        if (result.getVideolist().size() > PERPAGE_NUM) {
            CopyOnWriteArrayList<SkyVideoInfo> list = cal;
            list.subList(0, PERPAGE_NUM);
            mRecyclerViewAdapter.replaceAllVideo(list);
        } else {
            mRecyclerViewAdapter.replaceAllVideo(cal);
        }

        mRecyclerViewAdapter.notifyDataSetChanged();
        mRecyclerViewAdapter.setSelection(0);
        mSelpos = 0;
        MLog.i(TAG, "mVideoTotalCount:" + mVideoTotalCount);
    }

    private void startSearchWord(String abnf) {
        MLog.i(TAG, "startSearchWord2:" + abnf);
        mFilmSlots = abnf;
        mSkyLoadingView.showLoading();
        SkyVideoPlayUtils.startGetBeeSearchVideoList(mFilmSlots, 1, PAGE_SIZE, new OnGetBeeSearchResultListener() {

            @Override
            public void getSearchVideoResult(BeeSearchVideoResult result, String keyword) {
                mCurPage = result.getCurpage();
                mTotalPage = result.getTotalpage();
                BeeSearchParams.getInstance().setMetasInfo(result);
                MLog.i(TAG, "curpage=" + mCurPage + " totalpage:" + mTotalPage);
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
            jumpToPage(false, mCurPage + 1);
        }
    }

    @Override
    public void onScrollToTop(int keyCode) {
        // TODO Auto-generated method stub

    }

    private void searchPlayByNum(int pos) {
        if (pos < 1 || pos > PERPAGE_NUM) {
            AbsTTS.getInstance(null).talk(getString(R.string.str_searchfilm_numerr));
            return;
        }
        try {
            if (pos < mRecyclerViewAdapter.getItemCount()/*getAllVideo().size()*/ + 1) {
                SkyVideoInfo videoInfo = mRecyclerViewAdapter.getAllVideo().get(pos - 1);
                int firstVideoid = videoInfo.getVideoId();
//                if (videoInfo.getSourceId() == GlobalVariable.TENCENT_SOURCE) {
//                    SkyVideoPlayUtils.startToTxVideoDetail(SkyBeeSearchAcitivity.this, videoInfo.getOtherId());
//                    finish();
//                } else if (videoInfo.getSourceId() == GlobalVariable.MGTV_SOURCE) {
//                    SkyVideoPlayUtils.startToMangguoVideoDetail(SkyBeeSearchAcitivity.this, videoInfo.getOtherId(), "");
//                    finish();
//                } else {
//                    SkyVideoPlayUtils.getVideoDetailByID(videoInfo, firstVideoid, -1/*, listener*/);
//                    finish();
//                }
                String sourceId = String.valueOf(videoInfo.getSourceId());
                String videoId = String.valueOf(videoInfo.getVideoId());
                BeeVideoPlayUtils.startToVideoDetail(this, sourceId, videoId);
                finish();
            } else {
                AbsTTS.getInstance(null).talk(getString(R.string.str_searchfilm_noexist));
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

    private void jumpToPage(boolean isVoice, int page) {
        if (page <= mTotalPage && page >= 1) {
            if (isVoice) {
                AbsTTS.getInstance(null).talk(getString(R.string.str_ok));
            }
            MLog.i(TAG, "jumpToPage:" + page + " abnf:" + mFilmSlots);
            mCurPage = page;
            mSkyLoadingView.showLoading();
            SkyVideoPlayUtils
                    .startGetBeeSearchVideoList(mFilmSlots, mCurPage, PAGE_SIZE, new OnGetBeeSearchResultListener() {

                        @Override
                        public void getSearchVideoResult(BeeSearchVideoResult result, String keyword) {
                            mCurPage = result.getCurpage();
                            mTotalPage = result.getTotalpage();
                            //mVideoTotalCount = result.getTotal();
                            MLog.i(TAG, "curpage:" + mCurPage + " total:" + mTotalPage);
                            BeeSearchParams.getInstance().setMetasInfo(result);
                            if (result.getVideolist().size() > 1) {
                                Message msg = new Message();
                                msg.what = MSG_SHOW_VIDEO;
                                msg.obj = result;
                                mHandler.sendMessage(msg);
                            }
                        }

                        @Override
                        public void getSearchFailed() {

                        }
                    });
        }
    }

    @Override
    public void onCmdExecute(Intent intent) {
        if (intent.hasExtra(DefaultCmds.COMMAND)) {
            String command = intent.getStringExtra(DefaultCmds.COMMAND);
            MLog.i(TAG, "command:" + command);
            String action = "";
            if (intent.hasExtra(DefaultCmds.INTENT)) {
                action = intent.getStringExtra(DefaultCmds.INTENT);
            }
            IStatus.mAsrErrorCnt = 0;
            switch (command) {
                case "next":
                    if (mCurPage < mTotalPage) {
                        jumpToPage(true, mCurPage + 1);
                    } else {
                        AbsTTS.getInstance(null).talk(getString(R.string.str_searchfilm_lastpage));
                    }
                    break;
                case "previous":
                    if (mCurPage > 1) {
                        jumpToPage(true, mCurPage - 1);
                    } else {
                        AbsTTS.getInstance(null).talk(getString(R.string.str_searchfilm_firstpage));
                    }
                    break;
                case "play":
                    int value = 0;
                    if (intent.hasExtra(DefaultCmds.VALUE)) {
                        value = intent.getIntExtra(DefaultCmds.VALUE, 0);
                        MLog.i(TAG, "num:" + value + " " + action);
                    }
                    if (DefaultCmds.COMMAND_LOCATION.equals(action)
                            || DefaultCmds.PLAYER_CMD_EPISODE.equals(action)) {
                        searchPlayByNum(value);
                    } else if (DefaultCmds.PLAYER_CMD_PAUSE.equals(action)) {
                        if (VALUE_PLAY == value) {
                            searchPlayByNum((mSelpos + 1) % PERPAGE_NUM);
                        }
                    } else if (DefaultCmds.PLAYER_CMD_NEXT.equals(action)) {
                        if (mCurPage < mTotalPage) {
                            jumpToPage(true, mCurPage + 1);
                        }
                    } else if (DefaultCmds.PLAYER_CMD_PAGE.equals(action)) {
                        if (value < mTotalPage) {
                            jumpToPage(true, value);
                        }
                    } else if (DefaultCmds.CMD_OPEN_DETAILS.equals(action)) {
                        openDetailsByPos(value);
                    } else {
                        AbsTTS.getInstance(null).talk("", getString(R.string.str_try_again));
                    }
                    break;
                case "jianjie":
                    value = -1;
                    if (intent.hasExtra(DefaultCmds.VALUE)) {
                        value = intent.getIntExtra(DefaultCmds.VALUE, 0);
                        MLog.i(TAG, "num:" + value + " " + action);
                    }
                    openDetailsByPos(value);
                    break;
                default:
                    break;
            }
        }
    }

    private void openDetailsByPos(int pos) {
        try {
            MLog.i(TAG, "size:" + mRecyclerViewAdapter.getAllVideo().size() + " " + pos);
            if (pos <= 0 || pos > 12 || pos > mRecyclerViewAdapter.getAllVideo().size()) {
                AbsTTS.getInstance(null).talk(getString(R.string.str_searchfilm_numerr));
            }
            SkyVideoInfo videoInfo = mRecyclerViewAdapter.getAllVideo().get(pos - 1);
            if (videoInfo.getSourceId() == GlobalVariable.TENCENT_SOURCE) {
                SkyVideoPlayUtils.startToTxVideoDetail(SkyBeeSearchAcitivity.this, videoInfo.getOtherId());
            } else if (videoInfo.getSourceId() == GlobalVariable.MGTV_SOURCE) {
                SkyVideoPlayUtils.startToMangguoVideoDetail(SkyBeeSearchAcitivity.this, videoInfo.getOtherId(), "");
            } else {
                SkyVideoPlayUtils.startToVideoDetail(SkyBeeSearchAcitivity.this, videoInfo.getVideoId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


