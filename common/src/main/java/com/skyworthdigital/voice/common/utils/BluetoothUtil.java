package com.skyworthdigital.voice.common.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.dingdang.utils.MLog;

/**
 * 蓝牙工具类
 */

public class BluetoothUtil {
    private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String BLUETOOTH_DISCOVERABLE_ACTION = "android.bluetooth.a2dpsink.profile.action.discoverable";

    /**
     * 判断蓝牙是否开启
     *
     * @return 如果蓝牙开启，返回true，否则，返回false
     */
    private static boolean isEnabled() {
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());//需要权限:android.permission.BLUETOOTH_ADMIN
    }

    /**
     * 蓝牙开启
     */
    public static void setEnable() {
        if (!isEnabled()) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.enable();//需要权限：android.permission.BLUETOOTH_ADMIN
            }
        }
        Intent intent = new Intent(BLUETOOTH_DISCOVERABLE_ACTION);
        VoiceApp.getInstance().sendBroadcast(intent);
        MLog.i("BluetoothUtil", "send broadcast DISCOVERABLE");
    }

    public static void setDisable() {
        if (isEnabled()) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.disable();
            }
        }
        MLog.i("BluetoothUtil","BLUETOOTH_CLOSE");
        AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_bluetooth_disconnect));
    }

}
