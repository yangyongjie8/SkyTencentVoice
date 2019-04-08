package com.skyworthdigital.voice.dingdang.domains.tv;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.domains.tvlive.TvLiveControl;
import com.skyworthdigital.voice.dingdang.control.model.AsrResult;
import com.skyworthdigital.voice.dingdang.control.tts.MyTTS;
import com.skyworthdigital.voice.dingdang.SkyAsrDialogControl;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.GuideTip;
import com.skyworthdigital.voice.dingdang.utils.IntentUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;

/**
 * Created by SDT03046 on 2018/8/8.
 */

public class TvControl {
    private static final String TAG = "tv";

    public static boolean main(final Context ctx, AsrResult bean, SkyAsrDialogControl dialogControl) {
        try {
            dialogControl.showHeadLoading();
            String mIntent = bean.mSemanticJson.mSemantic.mIntent;
            switch (mIntent) {
                case "select_channel":
                    return TvLiveControl.getInstance().control(bean.mSemanticJson.mSemantic.getTvliveSlots(), bean.mQuery, dialogControl);
                case "switch_off":
                    Utils.simulateKeystroke(KeyEvent.KEYCODE_POWER, 3000);
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_shutoff));
                    break;
                case "switch_on":
                    Utils.simulateKeystroke(KeyEvent.KEYCODE_POWER);
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_shuton));
                    break;
                case "switch_restart":
                case "device_restart":
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_resboot));
                    new Thread(new Runnable() {
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                Thread.sleep(2000);
                                Utils.reboot(ctx);
                            } catch (Exception e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case "current_volume":
                    int cur = VolumeUtils.getInstance(ctx).getVolume();
                    StringBuilder sb =new StringBuilder();
                    sb.append(ctx.getString(R.string.str_volume_now));
                    sb.append(cur);
                    MyTTS.getInstance(null).speakAndShow(sb.toString());
                    break;
                case "turn_up_max":
                    VolumeUtils.getInstance(ctx).setVolume(20);
                    break;
                case "turn_down_min":
                    VolumeUtils.getInstance(ctx).setVolume(0);
                    break;
                case "volumn_down":
                case "turn_down":
                    VolumeUtils.getInstance(ctx).setVolumeMinus(2);
                    break;
                case "volumn_up":
                case "turn_up":
                    VolumeUtils.getInstance(ctx).setVoiceVolumePlus(2);
                    break;
                case "volumn_to":
                case "turn_volume_to":
                    try {
                        if (bean.mSemanticJson.mSemantic.mSlots.get(0).mName != null && TextUtils.equals(bean.mSemanticJson.mSemantic.mSlots.get(0).mName, "volumeto")) {
                            //MLog.d("wyf","type:"+bean.mSemanticJson.mSemantic.mSlots.get(0).mValueList.get(0).mNumber+" per:"+Float.parseFloat(bean.mSemanticJson.mSemantic.mSlots.get(0).mValueList.get(0).mPercent));
                            if (bean.mSemanticJson.mSemantic.mSlots.get(0).mValueList.get(0).mNumber == 1) {
                                String percent = bean.mSemanticJson.mSemantic.mSlots.get(0).mValueList.get(0).mPercent;
                                float fper = Float.parseFloat(percent.substring(0, percent.indexOf("/"))) / Float.parseFloat(percent.substring(percent.indexOf("/") + 1));
                                int volume = (int) (fper * 15);
                                VolumeUtils.getInstance(ctx).setVolume(volume);
                            } else {
                                VolumeUtils.getInstance(ctx).setVolume(Integer.parseInt(bean.mSemanticJson.mSemantic.mSlots.get(0).mValueList.get(0).mInteger));
                            }
                        } else {
                            VolumeUtils.getInstance(ctx).setVolume(bean.mSemanticJson.mSemantic.getIndex());
                        }
                    } catch (Exception e) {
                        MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_volume_set_err));
                    }
                    break;
                case "mute":
                case "volumn_off":
                    VolumeUtils.getInstance(ctx).setMute();
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_volume_mute));
                    break;
                case "volumn_on":
                case "unmute":
                    VolumeUtils.getInstance(ctx).cancelMute();
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_volume_unmute));
                    break;
                case "stop":
                case "exit":
                    AppUtil.killTopApp();
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_exit));
                    break;
                case "back_tvhomepage":
                case "home_on":
                    AppUtil.killTopApp();
                    Utils.simulateKeystroke(KeyEvent.KEYCODE_HOME);
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_backhome));
                    break;
                case "back":
                    back(ctx);
                    break;
                case "set_advance"://设置
                    Intent intent = new Intent();
                    intent.setAction("com.skyworthdigital.settings.MainSettingsActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_ok));
                    break;
                case "set_voice"://声音设置
                    intent = new Intent();
                    intent.setAction("com.skyworthdigital.settings.MainSettingsActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("levelOneTitle", "图像声音");
                    ctx.startActivity(intent);
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_ok));
                    break;
                case "set_systeminfo"://系统信息
                    intent = new Intent();
                    intent.setAction("com.skyworthdigital.settings.MainSettingsActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("levelOneTitle", "关于本机");
                    ctx.startActivity(intent);
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_ok));
                    break;
                case "set_systemupdate"://系统更新
                    /*intent = new Intent();
                    intent.setAction("com.skyworthdigital.updateview");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //intent = getAppIntent(context, action, intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("secure", "关于本机");
                    //LogUtil.log("intent =" + intent.toString());
                    ctx.startActivity(intent);*/
                    IntentUtils.startPackageAction(ctx, "com.skyworthdigital.updateview");
                    MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_ok));
                    break;
                case "speed_play":
                case "episode_select":
                case "page_select":
                case "play_forward":
                case "list":
                case "skip_title":
                    int num = 0xffff;
                    if (bean.mSemanticJson.mSemantic.mSlots != null && bean.mSemanticJson.mSemantic.mSlots.get(0).mName != null && TextUtils.equals(bean.mSemanticJson.mSemantic.mSlots.get(0).mName, "digit")) {
                        num = Integer.parseInt(bean.mSemanticJson.mSemantic.mSlots.get(0).mValueList.get(0).mText);
                        MLog.d(TAG, "idx:" + num);

                    }
                    //intent = DefaultCmds.composePlayControlIntent(mIntent,num);
                    //ctx.startService(intent);
                    break;
                case "network_diagnosis":
                    Intent intent1 = new Intent("com.skyworthdigital.settings.activity.NetDiagnoseActivity");
                    ctx.startActivity(intent1);
                    break;
                case "channellist"://频道列表
                case "page_down"://向前翻页
                case "page_up"://向后翻页
                default:
                    return IntentUtils.appLaunchExecute(ctx, bean.mQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_system_set_err));
            return false;
        }
        return true;
    }

    public static void back(Context ctx) {
        MyTTS.getInstance(null).speakAndShow(ctx.getString(R.string.str_back));
        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                try {
                    //Thread.sleep(2000);
                    Instrumentation inst = new Instrumentation();
                    String top = GuideTip.getInstance().getCurrentClass();//Utils.getTopActivityByExec();
                    if (top != null && (top.contains(GlobalVariable.MEDIAPALY_CLASS) || top.contains("com.mipt.store.activity.MainActivity") || top.contains("SkyMediaDetailActivity")
                            || top.contains(TvLiveControl.TVLIVE_PACKAGENAME))) {
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    }
                    Thread.sleep(100);
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
