package com.skyworthdigital.voice.tencent_module.domains.tv;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.AsrResult;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.model.TvProgramsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDT03046 on 2018/9/6.
 */

public class TvlistDialog extends Dialog {
    private TvProgramListAdapter mListAdapter;
    private ListView mListView;
    private List<TvProgramsItem> mTvlist = new ArrayList<>();
    private Context mContext;
    private TextView mTitle, mTime;
    private Button mButtonLeft, mButtonMiddle, mButtonRight;
    private List<TvProgramsItem> mTabs = new ArrayList<>();
    private int mTabIndex = 0;
    private CloseSystemDialogsReceiver mCloseSystemDialogsReceiver;
    private Window mWindow;
    private static final int PAGE_ITEM_COUNT = 7;
    private static final int MAX_TABS = 3;

    public TvlistDialog(Context context, List<AsrResult.AsrData.TvProgramsObj> list) {
        super(context, R.style.asr_dialog);
        for (AsrResult.AsrData.TvProgramsObj tmp : list) {
            if (tmp.mTvProgramsItems != null && tmp.mTvProgramsItems.size() > 0) {
                mTvlist.addAll(tmp.mTvProgramsItems);
            }
        }
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().setFlags(flags, flags);
        setContentView(R.layout.tv_program_list);
        init();
    }

    public void init() {
        mListView = (ListView) findViewById(R.id.lv_tvlist);
        mTitle = (TextView) findViewById(R.id.title);
        mTime = (TextView) findViewById(R.id.time);
        mButtonLeft = (Button) findViewById(R.id.licon);
        mButtonMiddle = (Button) findViewById(R.id.micon);
        mButtonRight = (Button) findViewById(R.id.ricon);

        //List<AsrResult.AsrData.TvProgramsObj> list = (List<AsrResult.AsrData.TvProgramsObj>) obj;// getIntent().getSerializableExtra("list");
        if (mTvlist == null || mTvlist.size() == 0 || mTvlist.size() == 0) {
            dismiss();
            return;
        }

        mWindow = this.getWindow();
        IntentFilter filter = new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mCloseSystemDialogsReceiver = new CloseSystemDialogsReceiver();
        mWindow.getContext().registerReceiver(mCloseSystemDialogsReceiver,
                filter);

        mTabs.add(0, mTvlist.get(0));
        for (int i = 1; i < mTvlist.size(); i++) {
            if (!TextUtils.equals(mTvlist.get(i).mChannelName, mTvlist.get(i - 1).mChannelName)) {
                mTabs.add(mTvlist.get(i));
                MLog.d("wyf", "tab:" + mTvlist.get(i).mChannelName);
                if (mTabs.size() == MAX_TABS) {
                    break;
                }
            }
        }

        if (mTabs.size() == 1) {
            mButtonRight.setText("");
            loadImage(mTvlist.get(0).mChLogo, (ImageView) findViewById(R.id.channel));
            mButtonRight.setVisibility(View.GONE);
            mButtonLeft.setVisibility(View.GONE);
            mButtonMiddle.setVisibility(View.GONE);
        } else if (mTabs.size() == 2) {
            mButtonRight.setText(mTabs.get(1).mChannelName);
            mButtonMiddle.setText(mTabs.get(0).mChannelName);
            mButtonRight.setVisibility(View.VISIBLE);
            mButtonMiddle.setVisibility(View.VISIBLE);
            mButtonLeft.setVisibility(View.GONE);
            mButtonRight.setBackgroundResource(R.drawable.tab_selector);
            mButtonMiddle.setBackgroundResource(R.drawable.tab_selector);
            mButtonMiddle.requestFocus();
        } else {
            mButtonRight.setText(mTabs.get(2).mChannelName);
            mButtonMiddle.setText(mTabs.get(1).mChannelName);
            mButtonLeft.setText(mTabs.get(0).mChannelName);
            mButtonRight.setVisibility(View.VISIBLE);
            mButtonRight.setBackgroundResource(R.drawable.tab_selector);
            mButtonLeft.setBackgroundResource(R.drawable.tab_selector);
            mButtonMiddle.setBackgroundResource(R.drawable.tab_selector);
            mButtonMiddle.setVisibility(View.VISIBLE);
            mButtonLeft.setVisibility(View.VISIBLE);
            mButtonLeft.requestFocus();
        }

        mListAdapter = new TvProgramListAdapter(mContext, getList(mTabs.get(mTabIndex).mChannelName));
        mListView.setAdapter(mListAdapter);
    }


    private List<TvProgramsItem> getList(String channelName) {
        mTitle.setText(mTabs.get(mTabIndex).mChannelName + "电视节目单");
        mTime.setText(mTabs.get(mTabIndex).mDate + mTabs.get(mTabIndex).mWeekPrint);

        List<TvProgramsItem> list = new ArrayList<>();

        for (TvProgramsItem item : mTvlist) {
            if (TextUtils.equals(channelName, item.mChannelName)) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    int cur = mListView.getSelectedItemPosition();
                    if (cur - PAGE_ITEM_COUNT >= 0) {
                        mListView.setSelection(cur - PAGE_ITEM_COUNT);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    cur = mListView.getSelectedItemPosition();
                    int total = mListAdapter.getCount();
                    if (cur + PAGE_ITEM_COUNT < total) {
                        mListView.setSelection(cur + PAGE_ITEM_COUNT);
                    }
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    dismiss();
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mTabIndex > 0) {
                        mTabIndex -= 1;
                        mListAdapter = new TvProgramListAdapter(mContext, getList(mTabs.get(mTabIndex).mChannelName));
                        mListView.setAdapter(mListAdapter);
                        mListAdapter.notifyDataSetChanged();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mTabIndex < (mTabs.size() - 1) && (mTabIndex < MAX_TABS)) {
                        mTabIndex += 1;
                        mListAdapter = new TvProgramListAdapter(mContext, getList(mTabs.get(mTabIndex).mChannelName));
                        mListView.setAdapter(mListAdapter);
                        mListAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private void loadImage(final String url, final ImageView img) {
        AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
        Drawable cacheImage = asyncImageLoader.getDrawable(url,
                new AsyncImageLoader.ImageCallback() {
                    // 请参见实现：如果第一次加载url时下面方法会执行
                    public void imageLoaded(Drawable imageDrawable) {
                        img.setImageDrawable(imageDrawable);
                    }
                });
        if (cacheImage != null) {
            img.setImageDrawable(cacheImage);
        }
    }

    private class CloseSystemDialogsReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    dismiss();
                    mWindow.getContext().unregisterReceiver(mCloseSystemDialogsReceiver);
                }
            }

        }
    }
}
