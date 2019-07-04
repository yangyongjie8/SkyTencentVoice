package com.skyworthdigital.voice.baidu_module.util;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.baidu_module.BdController;
import com.skyworthdigital.voice.baidu_module.R;
import com.skyworthdigital.voice.baidu_module.duerbean.DuerBean;
import com.skyworthdigital.voice.baidu_module.duerbean.FilmSlots;
import com.skyworthdigital.voice.baidu_module.duerbean.Nlu;
import com.skyworthdigital.voice.baidu_module.duerbean.Result;
import com.skyworthdigital.voice.baidu_module.duerbean.Slots;
import com.skyworthdigital.voice.baidu_module.fm.AudioInfo;
import com.skyworthdigital.voice.baidu_module.music.MusicControl;
import com.skyworthdigital.voice.baidu_module.music.MusicInfo;
import com.skyworthdigital.voice.baidu_module.robot.BdTTS;
import com.skyworthdigital.voice.baidu_module.video.VideoInfo;
import com.skyworthdigital.voice.baidu_module.voicemode.VoiceModeAdapter;
import com.skyworthdigital.voice.common.AbsController;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.utils.BluetoothUtil;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.IntentUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.VolumeUtils;
import com.skyworthdigital.voice.globalcmd.GlobalUtil;
import com.skyworthdigital.voice.guide.GuideTip;
import com.skyworthdigital.voice.scene.SkySceneService;
import com.skyworthdigital.voice.sdk.VoiceService;
import com.skyworthdigital.voice.tianmai.TianmaiCommandUtil;
import com.skyworthdigital.voice.tianmai.TianmaiIntent;
import com.skyworthdigital.voice.videoplay.SkyVideoPlayUtils;

import java.util.List;

//import com.google.gson.Gson;


/**
 * action公共类
 * Created by SDT13227 on 2017/5/26.
 */

public class ActionUtils {
    private static String TAG = ActionUtils.class.getSimpleName();

    private static final String APPSTORE_CLASS = "com.mipt.store.activity.MainActivity";
    private static final String ACTION_AUDIO_PLAY = "com.skyworthdigital.voiceassistant.fmplay";
    private static final String[] START_FILTER = {"打开", "进入"};
    private static final String CLOSE_APP = "close_app";

    public static void startSearch(final Context context, final DuerBean duerBean, final String speech /*, OnGetVideoPlayListener listener*/) {
        FilmSlots filmSlots;
        Nlu nlu;
        Slots slots;
        boolean isSearch = true;
        int whepisode = -1;

        if (duerBean != null && duerBean.getResult() != null
                && (nlu = duerBean.getResult().getNlu()) != null && (slots = nlu.getSlots()) != null) {
            filmSlots = slots.getFilmSlots();
        } else {
            return;
        }
        if (filmSlots != null) {
            //if (componentName != null && TextUtils.equals(componentName.getClassName(), SEARCH_ACTIVITY_CLASS_NAME)) {
            filmSlots = filmNameSpecicalProcess(slots, filmSlots, speech);
            String json = new Gson().toJson(filmSlots);
            if (/*(speech.startsWith(context.getString(R.string.str_hopeplay)) && !TextUtils.isEmpty(slots.getFilm()))
                    ||*/ TextUtils.equals(context.getString(R.string.str_videoplay), slots.getActionType())) {
                isSearch = false;
            }
            if (!TextUtils.isEmpty(slots.getWhepisode())) {
                whepisode = Integer.parseInt(slots.getWhepisode());//valueOf(whepisode);
            }
            MLog.i(TAG, "isSearch:" + isSearch);
            //String top = Utils.getTopActivityByExec();
            if (!isSearch) {
                if (!GuideTip.getInstance().isVideoPlay()/*top != null && !top.contains(GlobalVariable.MEDIAPALY_ACTIVITY)*/) {
                    SkyVideoPlayUtils.videoPlayyy(context, json, filmSlots.getFilm(), whepisode);
                    return;
                }
            }
            SkyVideoPlayUtils.jumpToSearch(context, json);
        }
    }

    public static void tvCommandExecute(Context context, @NonNull Nlu nlu, String speech) {
        String name = nlu.getIntent();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        Slots slots = nlu.getSlots();
        switch (name) {
            case DefaultCmds.COMMAND_VOL_UP:
                if (slots == null) {
                    break;
                }
                VolumeUtils.getInstance(context).setVolumePlus((int)(double) slots.getValue());
                BdTTS.getInstance().talk(context.getString(R.string.str_volume_note));
                break;
            case DefaultCmds.COMMAND_VOL_DOWN:
                if (slots == null) {
                    break;
                }
                MLog.d(TAG, "slots value type:"+slots.getValue().getClass().getSimpleName());
                VolumeUtils.getInstance(context).setVolumeMinus((int)(double) slots.getValue());
                BdTTS.getInstance().talk(context.getString(R.string.str_volume_note));
                break;
            case DefaultCmds.COMMAND_VOL_SET:
                if (slots == null) {
                    break;
                }
                if (slots.getValue().toString().equals(GlobalVariable.VOLUME_MAX)
                        || speech.contains("百分之百") || speech.contains("百分之一百")) {
                    VolumeUtils.getInstance(context).setVolumeMax();
                } else {
                    VolumeUtils.getInstance(context).setVolume((double) slots.getValue());
                }
                BdTTS.getInstance().talk(context.getString(R.string.str_volume_note));
                break;
            case DefaultCmds.COMMAND_MUTE:
                if (slots == null) {
                    break;
                }
                try {
                    final double EPSILON = 1e-6;
                    double abs = Math.abs(1.0 - (double) slots.getValue());
                    if (abs < EPSILON) {
                        VolumeUtils.getInstance(context).setMute();
                        BdTTS.getInstance().talk(context.getString(R.string.str_volume_note));
                    } else {
                        VolumeUtils.getInstance(context).cancelMute();
                        BdTTS.getInstance().talk(context.getString(R.string.str_volume_note));
                    }
                } catch (Exception e) {
                    BdTTS.getInstance().talk(context.getString(R.string.str_unknown_note));
                    e.printStackTrace();
                }
                break;
            case DefaultCmds.COMMAND_SLEEP:
            case DefaultCmds.COMMAND_TV_OFF:
                Utils.simulateKeystroke(KeyEvent.KEYCODE_POWER);
                BdTTS.getInstance().talk(context.getString(R.string.str_ok));
                break;
            case DefaultCmds.COMMAND_BACK:
                BdTTS.getInstance().talk(context.getString(R.string.str_back));
                new Thread(new Runnable() {
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(2000);
                            Instrumentation inst = new Instrumentation();
//                            String top = WelcomeTip.getInstance().getCurrentClass();//Utils.getTopActivityByExec();
//                            if (top != null && (top.contains(GlobalVariable.MEDIAPALY_CLASS) || top.contains(APPSTORE_CLASS))) {
//                                LogUtil.log("MEDIAPALY_ACTIVITY");
//                                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
//                            }
//                            Thread.sleep(100);
//                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case DefaultCmds.COMMAND_EXIT:
                AppUtil.killTopApp();
                BdTTS.getInstance().talk(context.getString(R.string.str_exit));
                break;
            case DefaultCmds.COMMAND_GO:
                if (slots == null) {
                    break;
                }
                if (slots.getName() != null) {
                    switch (slots.getName()) {
                        case "主页":
                            AppUtil.killTopApp();
                            Utils.simulateKeystroke(KeyEvent.KEYCODE_HOME);
                            BdTTS.getInstance().talk(context.getString(R.string.str_ok));
                            break;
                        case "分辨率":
                            Intent intent = new Intent();
                            intent.setAction("com.skyworthdigital.settings.DisplaySetting");
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            BdTTS.getInstance().talk(context.getString(R.string.str_ok));
                            break;
                        default:
                            GlobalUtil.getInstance().control(context, slots.getName(), speech);
                            break;
                    }
                }
                break;

            case DefaultCmds.COMMAND_APP_OPEN:
                if (!appLaunchInstalledPkg(context, nlu, speech)) {
                    if (slots == null) {
                        GlobalUtil.getInstance().control(context, speech, null);
                        break;
                    }

                    GlobalUtil.getInstance().control(context, slots.getName(), speech);
                }
                break;

            case DefaultCmds.COMMAND_APP_CLOSE:
                BdTTS.getInstance().talk(context.getString(R.string.str_ok));
                AppUtil.killTopApp();
                break;

            case DefaultCmds.COMMAND_SIGNAL:
                // TODO: 2017/10/11
                /*switch (nlu.getSlots().getName()) {
                    case "tv":
                        LogUtil.log("==>TV");
                        break;
                    case "hdmi1":
                        LogUtil.log("==>HDMI1");
                        break;
                    case "hdmi2":
                        LogUtil.log("==>HDMI2");
                        break;
                    default:
                        break;
                }*/
                break;
            case DefaultCmds.COMMAND_PAGE_NEXT:
                MLog.i(TAG, "==>page.next");
                //Utils.simulateKeystroke(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case DefaultCmds.COMMAND_PAGE_PRE:
                MLog.i(TAG, "==>page.previous");
                //Utils.simulateKeystroke(KeyEvent.KEYCODE_DPAD_UP);
                break;
            case DefaultCmds.COMMAND_LOCATION:
                //BdTTS.getInstance().talk("好的。" + speech);
                break;
            default:
                BdTTS.getInstance().talk(context.getString(R.string.str_unknown_note));
                MLog.i(TAG, "command no execute:" + name);
                break;
        }
    }

    public static boolean appLaunchExecute(Context context, @NonNull Nlu nlu, String speech) {
        boolean launchFromApp;
        boolean launchFromGlobal;

        try {
            String name = Utils.filterByStartWith(speech, START_FILTER);
            MLog.i(TAG, "name:" + name);
            String slotsname;
            Slots slots = nlu.getSlots();
            if (slots != null && slots.getName() != null) {
                slotsname = slots.getName();
            } else {
                slotsname = null;
            }
            if (nlu.getIntent().equalsIgnoreCase(CLOSE_APP)) {
                if (slotsname != null && TextUtils.equals(slotsname, context.getString(R.string.str_bluetooth))) {
                    BluetoothUtil.setDisable();
                }
                MLog.i(TAG, "no handle");
                return true;
            }
            //if (nlu.getIntent().equalsIgnoreCase("open_app")
            //        || nlu.getIntent().equalsIgnoreCase("launch_app")) {
            launchFromApp = appLaunchInstalledPkg(context, nlu, name);
            if (launchFromApp) {
                return true;
            }

            launchFromGlobal = GlobalUtil.getInstance().control(context, speech, slotsname);
            if (launchFromGlobal) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        return false;
    }

    private static boolean appLaunchInstalledPkg(Context context, @NonNull Nlu nlu, String speech) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        try {
            for (PackageInfo info : packageInfos) {
                String packageName = info.packageName;

                String label = packageManager.getApplicationLabel(info.applicationInfo).toString();
                String name = null;
                Slots slots = nlu.getSlots();
                if (slots != null && slots.getName() != null) {
                    name = slots.getName();
                }
                if ((name != null && label.equalsIgnoreCase(name)) || label.equalsIgnoreCase(speech)) {
                    MLog.i(TAG, "appLaunchInstalledPkg:" + label);
                    BdTTS.getInstance().talk(context.getString(R.string.str_ok));//R.string.str_opensomthing) + label);
                    IntentUtils.startPackageAction(context, packageName);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean globalCommandExecute(Context context, String speech) {
        return GlobalUtil.getInstance().control(context, speech, null);
    }


    /*
    *功能：由于搜索的片名中如果带第*季或第*部或战狼2这类时，百度解析得到的film字段会省去季或部的内容，而是填入Whdepart或Whsuffix字段。
    * 而服务器的片名是必须完整的带第*季或第*部才能搜索到。
    * filmNameSpecicalProcess是将带第*季或第*部或战狼2这类影片名特殊处理。
    * 1.首先根据用户语音的内容（speech）查找到有季或部的内容，有则重置影片名。如用户说播放欢乐颂第二季第三集，则影片名为欢乐颂第二季；
    * 2.如果上一步没有查找到，则判断slots中是否有getWhdepart或getWhsuffix描述，有则获取到可能的影片名，重置影片名。
    *   服务器能同时支持多个影片名搜索，例如百度解析filmName：速度与激情 getWhdepart：5，则搜索影片名filmName是：速度与激情5，速度与激情 5，速度与激情五，速度与激情 五
     */
    private static FilmSlots filmNameSpecicalProcess(Slots slots, FilmSlots filmSlots, String speech) {
        String whff = StringUtils.composeNameWithSpeech(slots.getFilm(), speech);
        if (whff != null) {
            filmSlots.setFilm(whff);
        } else if (!TextUtils.isEmpty(slots.getWhdepart())) {
            filmSlots.setFilm(StringUtils.composeNameWithWhdepart(slots.getFilm(), slots.getWhdepart()));
        } else if (!TextUtils.isEmpty(slots.getWhsuffix())) {
            filmSlots.setFilm(StringUtils.composeNameWithWhdepart(slots.getFilm(), slots.getWhsuffix()));
        }
        return filmSlots;
    }

    /**
     * 首先由SkySceneService去处理当前语音结果是否是注册的命令，是的话，则不再进入视频搜索等操作
     */
    public static void skySceneProcess(Context ctx, DuerBean duerBean, String originSpeech) {
        if (DefaultCmds.SystemCmdPatchProcess(ctx,originSpeech)) {
            return;
        }
        if (duerBean != null) {
            Result result = duerBean.getResult();
            if (result != null) {
                Nlu nlu = result.getNlu();
                MLog.i(TAG, "Nlu:" + nlu.toString());
                //String originSpeech = duerBean.getOriginSpeech();
                try {
                    ReportUtils.report2Smartmovie(VoiceApp.deviceId,VoiceApp.lanMac,originSpeech);//上报的deviceId统一用mac
                    Intent intent;
                    if (BdCommands.isPlay(nlu) || DefaultCmds.isMusicCmd(nlu.getIntent())) {
                        intent = BdCommands.composePlayControlIntent(duerBean);
                        ctx.startService(intent);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (originSpeech != null) {
            if (VoiceModeAdapter.isAudioBox()) {
                String endStr = "，";
                int len = endStr.length();
                if (originSpeech.endsWith(endStr)) {
                    originSpeech = originSpeech.substring(0, originSpeech.length() - len);
                    MLog.i(TAG, "originSpeech:" + originSpeech);
                }
            }
            try {
                Intent intent;
                if (!specialCmdProcess(ctx, originSpeech)) {
                    intent = new Intent(SkySceneService.INTENT_TOPACTIVITY_CALL);
                    String strPackage = GlobalVariable.VOICE_PACKAGE_NAME;
                    intent.putExtra(DefaultCmds.SEQUERY, originSpeech);
                    intent.setPackage(strPackage);
                    ctx.startService(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据用户语音原话，特殊处理转化为播放控制命令
     */
    private static boolean specialCmdProcess(Context ctx, String speech) {
        MLog.i(TAG, "######### specialCmdProcess");

        if("切换到叮当".equalsIgnoreCase(speech)||"切换到丁当".equalsIgnoreCase(speech)||"切换到订单".equalsIgnoreCase(speech)){
            VoiceApp.isDuer = false;
            BdController.getInstance().stopVoiceTriggerDialog();
            BdController.getInstance().onDestroy();
            AbsTTS.getInstance(null).talk("我是叮当");
            AbsController.getInstance().dismissDialog(3000);
            return true;
        }
//        if("切换到百度".equalsIgnoreCase(speech)){
//            VoiceApp.isDuer = true;
//            AbsTTS.getInstance(null).talk("我就是百度Duer");
//            return true;
//        }

        try {
            GuideTip tip = GuideTip.getInstance();
            if (tip != null && !tip.isAudioPlay()) {// 不是正在播放页面
                int idx = StringUtils.getEpisodeFromSpeech(speech);
                if (idx > 0) {
                    MLog.i(TAG, "get episode special:" + idx);
                    DefaultCmds.startEpisodeIntent(ctx, idx, speech);
                    return true;
                }

                if(StringUtils.doTwoMinSwitch(speech))return true;

                TianmaiIntent tianmaiIntent;
                if((tianmaiIntent=StringUtils.isTianMaiDemoSpeech(speech))!=null){
                    DefaultCmds.startTianmaiPlay(ctx, tianmaiIntent);
                    MLog.i(TAG, "special tianmai action");
                    return true;
                }

                // 天脉乐龄情志前台运行，所有未能识别的指令都传到乐龄
                if(AppUtil.isForegroundRunning(TianmaiCommandUtil.PACKAGENAME_LELINGQINGZHI)){
                    int number = TianmaiCommandUtil.getQingzhiCommandCode(speech);
                    if(number!=-1){
                        // todo
//                      VoiceService.trySendRecognizeCompletedCommand(originSpeech, 1);//这个1根据sdk接口确定
                        VoiceService.trySendRecognizeCompletedCommand(speech, number);
                        return true;
                    }
                }

                if (StringUtils.isPauseCmdFromSpeech(speech)) {
                    DefaultCmds.startPauseIntent(ctx, true, speech);
                    MLog.i(TAG, "special pause Cmd");
                    return true;
                }

                if (StringUtils.isPlayCmdFromSpeech(speech)) {
                    DefaultCmds.startPauseIntent(ctx, false, speech);
                    MLog.i(TAG, "special play Cmd");
                    return true;
                }
//
            }
            if(StringUtils.isIoTCmdFromSpeech(speech)){
                MLog.i(TAG, "special IoT cmd");
                return true;
            }

            Intent intent = null;
            if ((intent = DefaultCmds.PlayCmdPatchProcess(speech))!=null) {
                MLog.d(TAG, "CmdPatchProcess intent");
                ctx.startService(intent);
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean audioPlayExecute(final Context ctx, final String duerResult) {
        AudioInfo audioInfo = new AudioInfo(duerResult);
        String url = audioInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            try {
                Intent intent = new Intent(ACTION_AUDIO_PLAY);
                intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                if (!TextUtils.isEmpty(audioInfo.getTitle())) {
                    intent.putExtra(GlobalVariable.FM_NAME, audioInfo.getTitle());
                }
                intent.putExtra(GlobalVariable.FM_URL, url);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MLog.i(TAG, "audioPlayExecute open activity");
                ctx.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean musicExecute(final Context ctx, final String duerResult, final String speech) {
        MusicInfo musicInfo = new MusicInfo(duerResult);
        return MusicControl.actionExecute(ctx, musicInfo, speech);
    }

    public static boolean baikeVideoExecute(final Context ctx, final String duerResult) {
        VideoInfo videoInfo = new VideoInfo(duerResult);
        String url = videoInfo.getUrl();
        if (!TextUtils.isEmpty(url)) {
            try {
                Intent intent = new Intent("com.skyworthdigital.voiceassistant.shortvideo");
                intent.setPackage(GlobalVariable.VOICE_PACKAGE_NAME);
                if (!TextUtils.isEmpty(videoInfo.getTitle())) {
                    intent.putExtra("title", videoInfo.getTitle());
                }
                intent.putExtra("video_url", url);
                intent.putExtra("note", videoInfo.getIntroduction());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MLog.i(TAG, "audioPlayExecute open activity");
                ctx.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
}
