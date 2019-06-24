package com.skyworthdigital.voice.dingdang.IoT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.iot.IoTCommand;
import com.skyworthdigital.voice.iot.IoTService;


public class SSRReceiver extends BroadcastReceiver {
    private static final String TAG = "SSRReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MLog.d(TAG, "onReceive action = %s" + action);
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            context.startService(new Intent(context, IoTService.class));
        }
        if(IoTService.MSG_IOT_START.equals(action)){
            Intent intentStart = new Intent(IoTService.MSG_IOT_START);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentStart);

        }
        if(IoTService.MSG_IOT_CMD.equals(action)){
            if(!IoTService.d618_gw_connected) {
                if(IoTService.isD618_gw_recognized()) {
                    context.startService(new Intent(context, IoTService.class));
                    AbsTTS.getInstance(null).talk("正在连接网关，请重试");
                }
                else
                {
                    AbsTTS.getInstance(null).talk("您还没有组网，请先组网");
                }
            }else {
                IoTCommand ioTCommand = (IoTCommand) intent.getSerializableExtra("nlu_data");
                MLog.d(TAG, ioTCommand.toJsonStr());
                Intent intent_iot_cmd = new Intent(IoTService.MSG_IOT_CMD_CTRL);
                intent_iot_cmd.putExtra("cmd_str", ioTCommand.toJsonStr());
                MLog.d(TAG, ioTCommand.getCmd() + ioTCommand.getCmdType() + ioTCommand.getLocation() + ioTCommand.getOper() + ioTCommand.getUid() + ioTCommand.getValue());
                MLog.d(TAG, "##will send cmd to IoT");
                MLog.d(TAG, AppUtil.getMachineHardwareAddress().replace(":", ""));
                VoiceApp.getInstance().sendBroadcast(intent_iot_cmd);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(intent_iot_cmd);
            }
        }
       /* if(IoTService.MSG_IOT_INIT.equals(action)){
            Intent intent_iot_cmd = new Intent(IoTService.MSG_IOT_INIT);
            MyApplication.getInstance().sendBroadcast(intent_iot_cmd);
        }*/
        if(IoTService.MSG_IOT_STOP.equals(action)){
            Intent intentStop = new Intent(IoTService.MSG_IOT_STOP);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentStop);

        }
        if(IoTService.MSG_IOT_SLOT_ONOFF.equals(action)){
            Intent intentSlot = new Intent(IoTService.MSG_IOT_SLOT_ONOFF);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intentSlot);
        }
    }
}
