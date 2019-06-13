package com.skyworthdigital.voice.tencent_module.domains.sports.cell;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.skyworthdigital.voice.tencent_module.AsrResult;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.domains.sports.SportsRecordAdapter;
import com.skyworthdigital.voice.tencent_module.domains.sports.TeamStatVecObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class SportsRecordCell extends FrameLayout{
    private SportsRecordAdapter mListAdapter;
    private ListView mListView;
    private static final int MSG_NEXT_PAGE = 1;
    private static final int MSG_FIRST_PAGE = 2;
    private static final int PER_PAGE_ITEMS=6;
    private static final int PER_PAGE_DELAY=20000;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int cur = mListView.getSelectedItemPosition();
            int total = mListAdapter.getCount();
            if (msg.what == MSG_NEXT_PAGE) {
                if (cur + PER_PAGE_ITEMS < total) {
                    mListView.setSelection(cur + PER_PAGE_ITEMS);
                }
            } else if (msg.what == MSG_FIRST_PAGE) {
                mListView.setSelection(0);
            }
            cur = mListView.getSelectedItemPosition();
            if (cur + PER_PAGE_ITEMS < total) {
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_NEXT_PAGE), PER_PAGE_DELAY);
            } else {
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_FIRST_PAGE), PER_PAGE_DELAY);
            }
        }
    };

    public SportsRecordCell(Context context,Object obj) {
        super(context);
        init(context,obj);
    }

    public void init(Context context, Object obj) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.activity_sports_record, null);

        mListView = (ListView) view.findViewById(R.id.lv_sports);
        // if (getIntent().hasExtra("list")) {
        List<AsrResult.AsrData.SportsRecordObj> list = (List<AsrResult.AsrData.SportsRecordObj>) obj;//getIntent().getSerializableExtra("list");

        mListView.setAdapter(mListAdapter);
        //} else {
        //    finish();
        //}
        addView(view);
        List<TeamStatVecObj> recordlist = new ArrayList<>();
        for (AsrResult.AsrData.SportsRecordObj temp : list) {
            TeamStatVecObj title = new TeamStatVecObj();
            title.mTitle = (temp.mCompetition + temp.mGroup);
            recordlist.add(title);
            recordlist.addAll(temp.mTeamStatVec);
        }
        mListAdapter = new SportsRecordAdapter(context, recordlist);
        mListView.setAdapter(mListAdapter);
        if (mListAdapter.getCount() > PER_PAGE_ITEMS) {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_NEXT_PAGE), PER_PAGE_DELAY);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    int cur = mListView.getSelectedItemPosition();
                    if (cur - 6 >= 0) {
                        mListView.setSelection(cur - 6);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    cur = mListView.getSelectedItemPosition();
                    int total = mListAdapter.getCount();
                    if (cur + 6 < total) {
                        mListView.setSelection(cur + 6);
                    }
                    return true;
                default:
                    break;
            }
        }
        return false;
    }
}
