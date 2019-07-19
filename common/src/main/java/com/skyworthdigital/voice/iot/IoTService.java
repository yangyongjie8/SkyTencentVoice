package com.skyworthdigital.voice.iot;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.d618.mqttsdk.D618CBInterface;
import com.d618.mqttsdk.D618SDK;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.io.File;

public class IoTService extends Service {

    private D618SDK d618SDK;
    public static final int STATUS_CONNECTING = 1;
    public static final int STATUS_CONNECTED = 2;
    public static boolean d618_gw_connected = false;

    public static final String MSG_FINISH = "action.MSG_FINISH";
    public static final String MSG_VOLUME_CHANGE = "action.VOLUME_CHANGE";
    public static final String MSG_IOT_RESULT = "action.IoT_RESULT";
    public static final String MSG_IOT_START = "action.IoT_START";
    public static final String MSG_IOT_STOP = "action.IoT_STOP";
    public static final String MSG_IOT_CMD = "action.IoT_CMD";
    public static final String MSG_IOT_CMD_CTRL = "action.IoT_CMD_CTRL";
    public static final String MSG_IOT_SLOT_ONOFF = "action.IoT_SLOT_ONOFF";
    public static final String MSG_IOT_RESET="action.Iot_RESET";

    //usb插入
    private final static String USB_ATTACHED = "android.hardware.usb.action.NationalChip_ATTACHED";
    //usb拔出
    private final static String USB_DETACHED = "android.hardware.usb.action.NationalChip_DETACHED";

    public static final String EXTRA_VOLUME = "volume";
    public static final String EXTRA_IoT_TEXT = "asr_text";
    public static final String EXTRA_IS_PARTIAL = "is_partial";

    private static final String TAG = "IoTService";
    private static final long WAKE_UP_TIME = 15 * 1000;

    private static boolean isDataType = true;//true为hotword，false为recognize

    //    private SpeechClient mSpeechClient;
    private int mMusicVolume;
    private int mMaxMusicVolume;
    private AudioManager mAudioManager;
    private boolean mNeedShowVolumeUi;
    private boolean mUseSecondaryVerify = true;
    private int mDetectedCount = 0;

    //    private AudioRecordRunner mAudioRecordRunner;//录音管理类
//    private BlockQueueManager mBlockQueueManager;//音频数据管理类
    private Handler mHandler = new Handler();

    private boolean isUserPause = false;//用户触发了暂停

    private SpeechRecognizer speechRecognizer;

    private void ioTControl(String cmdStr) {
        MLog.d(TAG, "IoT control");
        if(d618_gw_connected){
            d618SDK.sendCommand(cmdStr);
        }
        else{
            MLog.w(TAG,"IoT gateway don't connect");
        }

    }

    private void controlSlot(){
        d618SDK.sendCommand("{\"cmd\":\"remoteCmd\",\"cmdType\":\"slot\",\"oper\":\"onoff\",\"uid\":\"speech\",\"value\":\"on\"}");
    }

    private BroadcastReceiver mUSBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (USB_ATTACHED.equals(action)) {
                MLog.d(TAG, "usb 插入");
            } else if (USB_DETACHED.equals(action)) {
                MLog.d(TAG, "usb 拔出");
            } else if (MSG_IOT_START.equals(action)) {
                MLog.d(TAG, "MSG_IOT_START");
                startIoT();
            }
            else if(MSG_IOT_CMD_CTRL.equals(action)){
                MLog.d(TAG,"got IoT cotrol command");
                String cmdStr = intent.getStringExtra("cmd_str");
                MLog.d(TAG,cmdStr);
                ioTControl(cmdStr);
            }
            else if(MSG_IOT_RESET.equals(action)){
                //if(d618SDK!=null)
                //    d618SDK.cleanup();
                delete("/data/data/com.skyworthdigital.voice.dingdang/files/d618_cache");
                //init618SDK();
            }

        }
    };

    public static boolean isD618_gw_recognized()
    {
        File file = new File("/data/data/com.skyworthdigital.voice.dingdang/files/d618_cache");
        if (file.exists() && file.isFile())
        {
            return true;
        }
        else
            return false;
    }
    private PowerManager.WakeLock mWakeLock;

    private void acquireWakeLock() {
        if(mWakeLock == null) {
            PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    this.getClass().getCanonicalName());
            mWakeLock.acquire();

        }

    }

    private void releaseWakeLock() {
        if(mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();
        init618SDK();
    }

    public void init618SDK()
    {
        d618SDK = new D618SDK();
        //TODO 这个MAC地址，必须按照实际的MAC地址来获取；还要考虑WIFI和有线两种情况。
//        Log.d(TAG, "onCreate:"+ getMachineHardwareAddress());
        String macStr = AppUtil.getMachineHardwareAddress().replace(":","");
        MLog.d(TAG, "onCreate macStr:"+macStr);
        register();
        d618SDK.init("002-SKT-DB000001", macStr);
        d618SDK.setUserCallBack(new D618CBInterface()
        {
            @Override
            public void connectStatus(int status)
            {
                MLog.d(TAG, "connectStatus=" + status);
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
                        d618_gw_connected = true;
//                        d618SDK.sendCommand("{\"cmd\":\"remoteCmd\",\"cmdType\":\"slot\",\"oper\":\"onoff\",\"uid\":\"speech\",\"value\":\"off\"}");
                        break;
                    }
                }
            }

            @Override
            public void receiveMsg(String s)
            {
                MLog.d(TAG, "receiveMsg=" + s);
                if(s.contains("scene")&&s.contains("起床模式"))
                {
                    //TianmaiIntent tianmaiIntent= com.skyworthdigital.voiceassistant.utils.StringUtils.isTianMaiDemoSpeech("起床提醒");
                    //if(tianmaiIntent!=null) {

                    String content= VoiceApp.getInstance().getString(R.string.str_tianmai_tip_wakeup);
                    content=content+"六点半,"+VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_6_half);
                    content=content+VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_8);
                    content=content+"八点半,"+VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_8_half);
                    content=content+"九点,"+VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_9);
                    content=content+"九点半,"+VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_9_half);


                    if(content!=null)
                    {
                        MLog.d(TAG, "receiveMsg end" + content);
                        //Context ctx = MyApplication.getInstance();
                        //DefaultCmds.startTianmaiPlay(ctx,tianmaiIntent);
                        Intent intent_IoT = new Intent("com.skyworthdigital.voice.action.SPEECH_CONTENT");
                        intent_IoT.putExtra("content", content);
                        VoiceApp.getInstance().sendBroadcast(intent_IoT);
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.d(TAG, "onStartCommand " + startId);
        // TODO 考虑多次启动的情况
//        startIoT();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startIoT() {
        MLog.d(TAG, "startIoT call");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "onDestroy");
//        mSpeechClient.stopHotword();
//        mSpeechClient.cancelReconizer(SpeechConstants.CLIENT_NAME);
//        mAudioRecordRunner.stopRecord();
//        mBlockQueueManager.clear();
        LocalBroadcastManager.getInstance(VoiceApp.getInstance()).unregisterReceiver(mUSBReceiver);
//        speechRecognizer.stopListening();
        speechRecognizer.destroy();
        releaseWakeLock();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 注册usb广播
     */
    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(IoTService.USB_ATTACHED);
        filter.addAction(IoTService.USB_DETACHED);
        filter.addAction(IoTService.MSG_IOT_START);
        filter.addAction(IoTService.MSG_IOT_SLOT_ONOFF);
        filter.addAction(IoTService.MSG_IOT_CMD_CTRL);
        filter.addAction(IoTService.MSG_IOT_RESET);
        VoiceApp.getInstance().registerReceiver(mUSBReceiver, filter);
    }

    /** 删除文件，可以是文件或文件夹
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    private boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            Toast.makeText(getApplicationContext(), "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    /** 删除目录及目录下的文件
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Toast.makeText(getApplicationContext(), "删除目录失败：" + filePath + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            Toast.makeText(getApplicationContext(), "删除目录失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()){
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "删除目录：" + filePath + "失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}

