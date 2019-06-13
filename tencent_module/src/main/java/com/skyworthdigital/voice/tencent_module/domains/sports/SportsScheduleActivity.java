package com.skyworthdigital.voice.tencent_module.domains.sports;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;

import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.List;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class SportsScheduleActivity extends Activity {
    private ListView mListView;
    private SportsScheduleAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);
        TextView textClock = (TextView) findViewById(R.id.date);
        textClock.setText(StringUtils.getDateString());
        TextView textTitle = (TextView) findViewById(R.id.sport_title);
        mListView = (ListView) findViewById(R.id.lv_sports);

        List<SportsDataObj> list = (List<SportsDataObj>) getIntent().getSerializableExtra("list");
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(list.get(0).mCompetition)) {
            sb.append(list.get(0).mCompetition);
        }
        if (!TextUtils.isEmpty(list.get(0).mRoundType)) {
            sb.append(list.get(0).mRoundType);
        }
        sb.append(getResources().getString(R.string.str_sport_schedule));
        textTitle.setText(sb.toString());
        mListAdapter = new SportsScheduleAdapter(this, list);
        mListView.setAdapter(mListAdapter);

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
