package com.skyworthdigital.voice.dingdang.IoT;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.d618.mqttsdk.D618CBInterface;
import com.d618.mqttsdk.D618SDK;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends Activity
{
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTED = 2;

    public static final String TAG = "MainActivity";

    private D618SDK mSdk;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        mSdk = new D618SDK();
        //todo 这个MAC地址，必须按照实际的MAC地址来获取；还要考虑WIFI和有线两种情况。
        Log.d(TAG, "onCreate:"+ getMachineHardwareAddress());
//        TelephonyManager tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);	//获取设备id
//        String deviceId = tm.getDeviceId();
//        final CharSequence cs = deviceId;
//        Log.e(TAG, "Key==deviceId==" + cs);
//        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);	//获取MacID
//		WifiInfo info = wifi.getConnectionInfo();
//        String macID = info.getMacAddress();
//        final CharSequence ms = macID;
//        Log.e(TAG, "Key==macID==" + ms);




        mSdk.init("002-SKT-DB000001", "0242fe0c36f3");
        mSdk.setUserCallBack(new D618CBInterface()
        {
            @Override
            public void connectStatus(int status)
            {
                Log.d(TAG, "connectStatus=" + status);
                switch (status)
                {
                    case STATUS_CONNECTING:
                    {
                        //todo 正在连接网关
                        break;
                    }
                    case STATUS_CONNECTED:
                    {
                        //网关连接成功
                        mSdk.sendCommand("{\"cmd\":\"remoteCmd\",\"cmdType\":\"slot\",\"oper\":\"onoff\",\"uid\":\"speech\",\"value\":\"on\"}");
                        break;
                    }
                }
            }

            @Override
            public void receiveMsg(String s)
            {
                Log.d(TAG, "receiveMsg=" + s);
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //释放资源
        mSdk.cleanup();
    }

    /**
     * android 7.0及以上 （2）扫描各个网络接口获取mac地址
     *
     */
    /**
     * 获取设备HardwareAddress地址
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        String wlan0Addr=null;
        String eth0Addr=null;
        NetworkInterface iF = null;
        if (interfaces == null) {
            return null;
        }
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if(iF.getName().equalsIgnoreCase("wlan0"))
                    wlan0Addr=hardWareAddress;
                if(iF.getName().equalsIgnoreCase("eth0"))
                    eth0Addr=hardWareAddress;
                //if (hardWareAddress != null)
                //    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if(eth0Addr==null)
            if(wlan0Addr!=null)
                hardWareAddress=wlan0Addr;
            else
                hardWareAddress="102030405060";
        else
            hardWareAddress=eth0Addr;
        return hardWareAddress;
    }

    /***
     * byte转为String
     *
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    public static void StartPlay() {
    }
}
