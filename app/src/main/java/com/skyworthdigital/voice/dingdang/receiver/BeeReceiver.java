package com.skyworthdigital.voice.dingdang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.skyworthdigital.voice.common.AbsController;
import com.skyworthdigital.voice.dingdang.service.BeeRecognizeService;
import com.skyworthdigital.voice.dingdang.utils.MLog;

/**
 * User: yangyongjie
 * Date: 2019-01-17
 * Description: 远场唤醒广播
 */
public class BeeReceiver extends BroadcastReceiver {

    public static final String KEY_ORIGINAL_TXT = "original_txt";

    @Override
    public void onReceive(Context context, Intent intent) {
        MLog.i("BeeReceiver", "onReceive" + intent.getAction());
        if(AbsController.getInstance().isRecognizing() && AbsController.getInstance().isControllerVoice){
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
