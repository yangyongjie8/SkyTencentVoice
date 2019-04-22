package com.skyworthdigital.voice.dingdang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.skyworthdigital.voice.dingdang.MainControler;
import com.skyworthdigital.voice.dingdang.service.BeeRecognizeService;
import com.skyworthdigital.voice.dingdang.utils.MLog;

/**
 * User: yangyongjie
 * Date: 2019-01-17
 * Description:
 */
public class BeeReceiver extends BroadcastReceiver {

    public static final String KEY_ORIGINAL_TXT = "original_txt";

    @Override
    public void onReceive(Context context, Intent intent) {
        MLog.i("BeeReceiver", "onReceive" + intent.getAction());
        if(MainControler.getInstance().isRecognizing()){
            MLog.i("BeeReceiver", "recognizing with controller, ignore broadcast.");
            return;
        }

        String action = intent.getAction();
        String txt = intent.getStringExtra(KEY_ORIGINAL_TXT);
        Intent newIntent = new Intent(context, BeeRecognizeService.class);
        newIntent.setAction(action);
        newIntent.putExtra(KEY_ORIGINAL_TXT, txt);
        context.startService(newIntent);
    }
}
