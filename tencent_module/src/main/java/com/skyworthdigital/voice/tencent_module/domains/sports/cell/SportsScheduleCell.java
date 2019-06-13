package com.skyworthdigital.voice.tencent_module.domains.sports.cell;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.domains.sports.SportsDataObj;
import com.skyworthdigital.voice.tencent_module.domains.sports.SportsScheduleAdapter;

import java.util.List;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class SportsScheduleCell extends FrameLayout {
    private ListView mListView;
    private SportsScheduleAdapter mListAdapter;
    private static final int MSG_NEXT_PAGE = 1;
    private static final int MSG_FIRST_PAGE = 2;
    private static final int PER_PAGE_ITEMS = 3;
    private static final int PER_PAGE_DELAY = 20000;

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

    public SportsScheduleCell(Context context, Object obj) {
        super(context);
        init(context, obj);
    }

    public void init(Context context, Object obj) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.activity_sports, null);

        mListView = (ListView) view.findViewById(R.id.lv_sports);
        List<SportsDataObj> list = (List<SportsDataObj>) obj;
        mListAdapter = new SportsScheduleAdapter(context, list);
        mListView.setAdapter(mListAdapter);
        addView(view);
        if (mListAdapter.getCount() > PER_PAGE_ITEMS) {
            //mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_NEXT_PAGE), PER_PAGE_DELAY);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            Log.i("wyf", "sportsrecord: " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    int cur = mListView.getSelectedItemPosition();
                    Log.i("wyf", "cur: " + cur);
                    if (cur - 3 >= 0) {
                        mListView.setSelection(cur - 3);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    cur = mListView.getSelectedItemPosition();
                    int total = mListAdapter.getCount();
                    if (cur + 3 < total) {
                        mListView.setSelection(cur + 3);
                    }
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
