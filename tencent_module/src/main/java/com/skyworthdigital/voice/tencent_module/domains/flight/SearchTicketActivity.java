package com.skyworthdigital.voice.tencent_module.domains.flight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.AsrResult;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.List;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class SearchTicketActivity extends Activity implements AdapterView.OnItemClickListener {
    private TicketAdapter mListAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_ticket);

        mListView = (ListView) findViewById(R.id.lv_ticket);
        TextView flight_title = (TextView) findViewById(R.id.train_title);
        TextView date = (TextView) findViewById(R.id.date);

        if (getIntent().hasExtra("data")) {
            AsrResult.AsrData data = (AsrResult.AsrData) getIntent().getSerializableExtra("data");
            List<FlightItem> list = data.mFlightList;
            flight_title.setText(data.mTicketParams.mFrom + "到" + data.mTicketParams.mTo + "的航班");
            date.setText(data.mTicketParams.mDate);
            mListAdapter = new TicketAdapter(this, list);
            mListView.setAdapter(mListAdapter);
            //4.设置listview条目的点击事件
            mListView.setOnItemClickListener(this);
        } else {
            finish();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

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
