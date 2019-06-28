package com.skyworthdigital.voice.tencent_module.domains.sports;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.tencent_module.AsrResult;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class SportsRecordActivity extends Activity {
    private SportsRecordAdapter mListAdapter;
    private ListView mListView;
    Button mButtonRight, mButtonLeft;
    List<AsrResult.AsrData.SportsRecordObj> mTabs = new ArrayList<>();
    private int mTabIndex = 0;
    private static final int MAX_TABS = 2;
    private static final int PAGE_ITEM_COUNT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_record);

        TextView sport_title = (TextView) findViewById(R.id.sport_title);
        TextView textDate = (TextView) findViewById(R.id.date);
        textDate.setText(StringUtils.getDateString());

        mListView = (ListView) findViewById(R.id.lv_sports);
        mButtonRight = (Button) findViewById(R.id.ricon);
        mButtonLeft = (Button) findViewById(R.id.micon);
        if (getIntent().hasExtra("list")) {
            mTabs = (List<AsrResult.AsrData.SportsRecordObj>) getIntent().getSerializableExtra("list");
            if (mTabs.size() == 0) {
                finish();
            }
            if (mTabs.size() >= MAX_TABS) {
                mButtonRight.setText(mTabs.get(1).mGroup);
                mButtonLeft.setText(mTabs.get(0).mGroup);
                mButtonRight.setVisibility(View.VISIBLE);
                mButtonLeft.setVisibility(View.VISIBLE);
                mButtonLeft.requestFocus();
            } else {
                mButtonRight.setVisibility(View.GONE);
                mButtonLeft.setVisibility(View.GONE);
            }

            StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(sb.toString())) {
                sb.append(" ");

            }
            if (!TextUtils.isEmpty(mTabs.get(0).mCompetition)) {
                sb.append(mTabs.get(0).mCompetition);
            }

            if (!TextUtils.isEmpty(mTabs.get(0).mSeason)) {
                sb.append(mTabs.get(0).mSeason);
            }

            sport_title.setText(sb.toString() + "排名");
            freshListByIndex(mTabIndex);
        } else {
            finish();
        }
    }

    private void freshListByIndex(int index) {
        if (index < MAX_TABS && index < mTabs.size()) {
            List<TeamStatVecObj> recordlist = new ArrayList<>();

            TeamStatVecObj title = new TeamStatVecObj();
            title.mTitle = (mTabs.get(index).mCompetition + mTabs.get(index).mGroup);
            recordlist.add(title);
            recordlist.addAll(mTabs.get(index).mTeamStatVec);
            mListAdapter = new SportsRecordAdapter(this, recordlist);
            mListView.setAdapter(mListAdapter);
            mListAdapter.notifyDataSetChanged();

            if (mButtonRight.getVisibility() == View.VISIBLE) {
                if (index > 0) {
                    mButtonRight.setBackgroundColor(getResources().getColor(R.color.tab_focus));
                } else {
                    mButtonRight.setBackgroundColor(getResources().getColor(R.color.tab_unfocus));
                }
            }
            if (mButtonLeft.getVisibility() == View.VISIBLE) {
                if (index == 0) {
                    mButtonLeft.setBackgroundColor(getResources().getColor(R.color.tab_focus));
                } else {
                    mButtonLeft.setBackgroundColor(getResources().getColor(R.color.tab_unfocus));
                }
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    int cur = mListView.getFirstVisiblePosition();
                    int last = mListView.getLastVisiblePosition();
                    Log.i("wyf", "cur:" + cur + " last:" + last);
                    if (cur - PAGE_ITEM_COUNT >= 0) {
                        Log.i("wyf", "cur:" + (cur - PAGE_ITEM_COUNT));
                        mListView.smoothScrollToPosition(cur - PAGE_ITEM_COUNT);
                        mListView.setSelection(cur - PAGE_ITEM_COUNT);
                    } else {
                        mListView.smoothScrollToPosition(0);
                        mListView.setSelection(0);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    cur = mListView.getFirstVisiblePosition();
                    last = mListView.getLastVisiblePosition();
                    Log.i("wyf", "cur:" + cur + " last:" + last);
                    int total = mListAdapter.getCount();
                    if (cur + PAGE_ITEM_COUNT < total) {
                        Log.i("wyf", "cur:" + (cur + PAGE_ITEM_COUNT));
                        mListView.smoothScrollToPosition(cur + PAGE_ITEM_COUNT);
                        mListView.setSelection(cur + PAGE_ITEM_COUNT);
                    } else {
                        mListView.smoothScrollToPosition(total + 1 - PAGE_ITEM_COUNT);
                        mListView.setSelection(total + 1 - PAGE_ITEM_COUNT);
                    }
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mTabIndex > 0) {
                        mTabIndex -= 1;
                        freshListByIndex(mTabIndex);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mTabIndex < (mTabs.size() - 1)) {
                        mTabIndex += 1;
                        freshListByIndex(mTabIndex);
                    }
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
