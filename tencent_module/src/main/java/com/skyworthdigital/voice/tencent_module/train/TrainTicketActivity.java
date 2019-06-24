package com.skyworthdigital.voice.tencent_module.train;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.AsrResult;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.domains.train.TrainInfoItem;

import java.util.List;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class TrainTicketActivity extends Activity{
    private TrainAdapter mListAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_ticket);
        mListView = (ListView) findViewById(R.id.lv_ticket);
        TextView train_title = (TextView) findViewById(R.id.train_title);
        TextView date = (TextView) findViewById(R.id.date);

        if (getIntent().hasExtra("data")) {
            AsrResult.AsrData data = (AsrResult.AsrData) getIntent().getSerializableExtra("data");
            List<TrainInfoItem> list = data.mTrainInfos;
            train_title.setText(data.mTrainParams.mFrom + "到" + data.mTrainParams.mTo + "的火车票");
            date.setText(data.mTrainParams.mDate);
            mListAdapter = new TrainAdapter(this, list);
            mListView.setAdapter(mListAdapter);
            mListView.setSelection(0);
        } else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    /*@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (action == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    int cur = mListView.getSelectedItemPosition();
                    int height = getResources().getDimensionPixelOffset(R.dimen.train_ticket_item_height);
                    if (cur - 3 >= 0) {
                     mListView.setSelection(cur - 3);
                    }
                    mListView.scrollBy(0, -(height) * 7 / 2);
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    cur = mListView.getSelectedItemPosition();
                    int total = mListAdapter.getCount();
                    height = getResources().getDimensionPixelOffset(R.dimen.train_ticket_item_height);
                    if (cur + 3 < total) {
                        //mListView.smoothScrollToPosition(cur + 3);
                        //mListView.setSelection(cur + 3);
                    }
                    mListView.scrollBy(0, (height) * 7 / 2);
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }*/
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ImagePipeline mImagePipeline = Fresco.getImagePipeline();
        if (mImagePipeline != null) {
            mImagePipeline.clearMemoryCaches();
        }
        MLog.i("Ticket", "onDestroy");
    }
}
