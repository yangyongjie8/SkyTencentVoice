package com.skyworthdigital.voice.dingdang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.skyworthdigital.voice.dingdang.service.BeeRecognizeService;

/**
 * User: yangyongjie
 * Date: 2019-01-17
 * Description:
 */
public class BeeReceiver extends BroadcastReceiver {

    public static final String KEY_ORIGINAL_TXT = "original_txt";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "onReceive" + intent.getAction(), Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        String txt = intent.getStringExtra(KEY_ORIGINAL_TXT);
        Intent newIntent = new Intent(context, BeeRecognizeService.class);
        newIntent.setAction(action);
        newIntent.putExtra(KEY_ORIGINAL_TXT, txt);
        context.startService(newIntent);
    }
}
