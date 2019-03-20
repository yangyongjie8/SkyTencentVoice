package com.skyworthdigital.voice.dingdang;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.forest.bigdatasdk.ForestDataReport;
import com.forest.bigdatasdk.hosttest.IErrorListener;
import com.forest.bigdatasdk.model.ForestInitParam;
import com.forest.bigdatasdk.util.BaseParamUtils;
import com.forest.bigdatasdk.util.SystemUtil;
import com.skyworthdigital.voice.dingdang.IoT.IoTService;
import com.skyworthdigital.voice.dingdang.control.record.PcmRecorder;
import com.skyworthdigital.voice.dingdang.control.record.SkyVoiceProcessor;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.Utils;
import com.tencent.ai.sdk.control.SpeechManager;
import com.tencent.ai.sdk.utils.ISSErrors;
import com.tencent.androidvprocessor.VoiceProcessor;

import org.json.JSONObject;

import okhttp3.OkHttpClient;

import static com.forest.bigdatasdk.app.ForestAdvertCrossAppDataReport.HTTP_PREFIX;

public class VoiceApp extends Application {
    private static VoiceApp sInstance;
    private OkHttpClient mOkHttpClient;
    public MainControler mControler;
    public int mAiType;
    public static VoiceProcessor mVoiceProcessor = null;
    public final static String mModel = Utils.get("ro.product.model");
    private static final int SDK_ID = 10021;
    private static final String SDK_KEY = "d5d2f64047526f4064845a3e964afbf9";

    static {
        if (TextUtils.equals(getModel(), "Q3031")) {
            System.loadLibrary("voiceprocessor");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mAiType = Utils.getAiType();
        Log.i("VoiceApp", "voice type:" + mAiType);
        if (mAiType == GlobalVariable.AI_NONE) {
            System.exit(0);
        }
        setAccessibilityEnable();
        int ret = SpeechManager.getInstance().startUp(sInstance, getAppInfo());
        SpeechManager.getInstance().setAsrDomain(80);
        //SpeechManager.getInstance().aisdkSetConfig(7002, "1");
        //SpeechManager.getInstance().aisdkSetConfig(6007, "1");
        SpeechManager.getInstance().aisdkSetConfig(6011,"2048");

        //SpeechManager.getInstance().setDisplayLog(true);
        Log.i("VoiceApp", "app launch");
        if (ret != ISSErrors.ISS_SUCCESS) {
            System.exit(0);
        }
        if (mAiType == GlobalVariable.AI_REMOTE) {
            SpeechManager.getInstance().setManualMode(true);
        } else {
            //SpeechManager.getInstance().setFullMode(true);
            SpeechManager.getInstance().setManualMode(false);
        }
        String sn = Utils.get("ro.serialno");
        SpeechManager.getInstance().setAiDeviceInfo(sn, "497a7402-7660-4eb6-844f-543b2c2f6777:111d7f7d4dc6460fafce8875efbe0474", null, null, null);
        mOkHttpClient = new OkHttpClient();
        //CrashHandler.getInstance().init(this);
        if (TextUtils.equals(getModel(), "Q3031")) {
            PcmRecorder.copyWcompTable(this);
        }
        Log.d("wyf", "guid = " + SpeechManager.getInstance().getGuidStr());
        mControler = MainControler.getInstance();
        Fresco.initialize(this);
        initBigDataReport();

        startService(new Intent(this, IoTService.class));
    }

    private String getAppInfo() {
        String result = "";
        try {
            final JSONObject info = new JSONObject();
            info.put("appkey", "497a7402-7660-4eb6-844f-543b2c2f6777");
            info.put("token", "111d7f7d4dc6460fafce8875efbe0474");
            /**
             * 如果产品是车机，填入CAR
             * 如果产品是电视，填入TV
             * 如果产品是音箱，填入SPEAKER
             * 如果产品是手机，填入PHONE
             */
            info.put("deviceName", "TV");
            info.put("productName", "ottbox");
            info.put("vendor", "skyworthdigital");

            final JSONObject json = new JSONObject();
            json.put("info", info);

            result = json.toString();
        } catch (Exception e) {
            // do nothing
        }
        Log.d("VoiceApp", "info = " + result);
        return result;
    }

    public static VoiceApp getInstance() {
        return sInstance;
    }

    private void setAccessibilityEnable() {
        //变量enabled_accessibility_services可通过查看/data/system/users/0/settings_secure.xml文件
        Log.i("MyApplication", "setAccessibilityEnable");
        String enabledServicesSetting = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        final String VOICE_SERVICE_CLASS = "com.skyworthdigital.voice.dingdang.service.RecognizeService";
        ComponentName selfComponentName = new ComponentName(getPackageName(),
                VOICE_SERVICE_CLASS);
        String flattenToString = ":" + selfComponentName.flattenToString();
        if (enabledServicesSetting == null ||
                enabledServicesSetting.equalsIgnoreCase("")) {
            enabledServicesSetting = selfComponentName.flattenToString();
        }

        if (enabledServicesSetting.startsWith(selfComponentName.flattenToString())) {
            Log.i("MyApplication", "recognizeService already on");
        } else if (!enabledServicesSetting.contains(flattenToString)) {
            enabledServicesSetting += flattenToString;
        }
        boolean ret = Settings.Secure.putString(getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                enabledServicesSetting);
        Log.i("MyApplication", "set result:" + ret);
        Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED, 1);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static String getModel() {
        return mModel;
    }

    public static VoiceProcessor getVoiceProcessor() {
        if (TextUtils.equals(getModel(), "Q3031")) {
            mVoiceProcessor = SkyVoiceProcessor.getVoiceProcessor();
        }
        return mVoiceProcessor;
    }

    private void initBigDataReport() {
        ForestInitParam skyInitParam = new ForestInitParam();
        String serialNum = BaseParamUtils.getSnno();
        String channel = BaseParamUtils.getCustomerid(serialNum);
        skyInitParam.setAppId(SDK_ID);
        skyInitParam.setAppKey(SDK_KEY);
        skyInitParam.setChannel(channel);
        skyInitParam.setDeviceId(BaseParamUtils.getDeviceNo());
        skyInitParam.setDeviceTypeId(BaseParamUtils.getDeviceTypeId(serialNum));
        skyInitParam.setSkySystemVersion(String.valueOf(BaseParamUtils.getSoftVersion()));
        skyInitParam.setSn(serialNum);
        String ipServer = HTTP_PREFIX + SystemUtil.getSystemProperties("ro.stb.adv.url");
        skyInitParam.setIpurl(ipServer);
        String appupgradeServer = HTTP_PREFIX + SystemUtil.getSystemProperties("ro.stb.appupgrade");
        skyInitParam.setUpdatejarurl(appupgradeServer);
        String bigdataServer = HTTP_PREFIX + SystemUtil.getSystemProperties("ro.stb.bigdata");
        skyInitParam.setBigdataurl(bigdataServer);

        ForestDataReport.getInstance()
                .initDataSdk(this, skyInitParam);
        ForestDataReport.getInstance()
                .setNeedDebug(false);
        ForestDataReport.getInstance()
                .registerErrorListener(new IErrorListener() {
                    @Override
                    public void onErrorOccur(String s, String s1) {
                        Log.i("MyApplication", s + "[=========]" + s1);
                    }
                });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ForestDataReport.getInstance().onTerminate();
    }

}
