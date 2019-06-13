package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.globalcmd.Action;
import com.skyworthdigital.voice.globalcmd.GlobalUtil;
import com.skyworthdigital.voice.globalcmd.Parameter;
import com.skyworthdigital.voice.music.utils.QQMusicUtils;
import com.skyworthdigital.voice.tv.AbsTvLiveControl;

import java.util.List;

public class IntentUtils {
    private static final String[] START_FILTER = {"我要", "帮我", "我想", "进入", "打开", "进入"};

    public static void startPackageAction(Context context, String packageName) {
        try {
            //Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            Intent intent = new Intent();
            intent = getAppIntent(context, packageName, intent);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void startCellAction(Context context, Action action) throws Exception {
        if (isBadAction(action)) {
            //SkyToast.showToast(context, R.string.show_net_error);
            return;
        }

        if (!TextUtils.isEmpty(action.getValue())) {
            Intent intent = new Intent();
            intent.setAction(action.getValue());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent = getAppIntent(context, action, intent);
            if (action.getValue().contains("sky2dlauncherv4") || action.getValue().contains("skyallmedia")) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            addParameters(intent, action.getParameters());
            //LogUtil.log("intent =" + intent.toString());
            context.startActivity(intent);
        } else if (!TextUtils.isEmpty(action.getLocal())) {
            //LogUtil.log("local action =" + action.getLocal());
            executeLocalAction(action.getLocal());
        }
    }

    private static boolean isBadAction(Action action) {
        return (action == null);
    }

    private static void addParameters(Intent intent, final List<Parameter> parameters) {
        if (intent == null || parameters == null) {
            return;
        }
        try {
            for (Parameter param : parameters) {
                if (isNumeric(param.getValue())) {
                    intent.putExtra(param.getKey(), Integer.valueOf(param.getValue()));
                } else {
                    intent.putExtra(param.getKey(), param.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static Intent getAppIntent(Context context, String pacageName, Intent intent) {
        if (checkApkExist(context, pacageName)) {
            intent = context.getPackageManager().getLaunchIntentForPackage(pacageName);
        } else {
            intent = new Intent();
            intent.setPackage("com.mipt.store");
            intent.setAction("com.mipt.store.intent.APPID");
            intent.setData(Uri.parse("skystore://?packageName=" + pacageName));
        }
        return intent;
    }

    private static boolean appLaunchInstalledPkg(Context context, String speech) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        try {
            for (PackageInfo info : packageInfos) {
                String packageName = info.packageName;

                String label = packageManager.getApplicationLabel(info.applicationInfo).toString();
                /*String name = null;
                Slots slots = nlu.getSlots();
                if (slots != null && slots.getName() != null) {
                    name = slots.getName();
                }*/
                if (/*(name != null && label.equalsIgnoreCase(name)) || */label.equalsIgnoreCase(speech)) {
                    //LogUtil.log("appLaunchInstalledPkg:" + label);
                    AbsTTS.getInstance(null).talk(context.getString(R.string.str_ok));//R.string.str_opensomthing) + label);
                    IntentUtils.startPackageAction(context, packageName);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean appLaunchExecute(Context context, String speech) {
        boolean launchFromApp;
        boolean launchFromGlobal;

        try {
            String name = Utils.filterByStartWith(speech, START_FILTER);
            /*LogUtil.log("name:" + name);
            String slotsname;
            Slots slots = nlu.getSlots();
            if (slots != null && slots.getName() != null) {
                slotsname = slots.getName();
            } else {
                slotsname = null;
            }*/
            //if (nlu.getIntent().equalsIgnoreCase("open_app")
            //        || nlu.getIntent().equalsIgnoreCase("launch_app")) {
            launchFromApp = appLaunchInstalledPkg(context, name);
            if (launchFromApp) {
                return true;
            }

            launchFromGlobal = GlobalUtil.getInstance().control(context, name, null);
            if (launchFromGlobal) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
        return false;
    }

    private static final String CONFIRM = "com.skyworthdigital.voiceassistant.CONFIRM";
    private static final String TOUP = "com.skyworthdigital.voiceassistant.UP";
    private static final String TODOWN = "com.skyworthdigital.voiceassistant.DOWN";
    private static final String TOLEFT = "com.skyworthdigital.voiceassistant.LEFT";
    private static final String TORIGHT = "com.skyworthdigital.voiceassistant.RIGHT";
    private static final String BLUETOOTH_OPEN = "com.skyworth.action.BLUETOOTH_OPEN";
    private static final String BLUETOOTH_CLOSE = "com.skyworth.action.BLUETOOTH_CLOSE";
    private static final String TVLIVE_OPEN = "com.skyworth.voice.action.OPEN_TVLIVE";
    private static final String OPEN_KALAOK = "com.skyworth.voice.action.OPEN_KALAOK";
    private static final String GET_VERSION = "com.skyworth.voice.action.GET_VERSION";
    private static final String INTO_INTERACTION = "com.skyworthdigital.voiceassistant.interaction";
    private static final String MUSIC_CHINA = "com.skyworthdigital.voiceassistant.music.china";
    private static final String MUSIC_UK = "com.skyworthdigital.voiceassistant.music.uk";
    private static final String MUSIC_HK = "com.skyworthdigital.voiceassistant.music.hk";
    private static final String MUSIC_KOREA = "com.skyworthdigital.voiceassistant.music.korea";
    private static final String MUSIC_JAPON = "com.skyworthdigital.voiceassistant.music.japon";
    private static final String MUSIC_JAPON_GX = "com.skyworthdigital.voiceassistant.music.japon_gx";
    private static final String MUSIC_EN = "com.skyworthdigital.voiceassistant.music.en";
    private static final String MUSIC_GOLD = "com.skyworthdigital.voiceassistant.music.gold";
    private static final String MUSIC_NEW = "com.skyworthdigital.voiceassistant.music.new";
    private static final String MUSIC_HOT = "com.skyworthdigital.voiceassistant.music.hot";
    private static final String MUSIC_POPULAR = "com.skyworthdigital.voiceassistant.music.popular";
    private static final String RESOLUTION_SET = "com.skyworthdigital.voice.Resolution";

    private static void executeLocalAction(String local) {
        //LogUtil.log("executeLocalAction:" + local);
        switch (local) {
            case TOUP:
                Utils.simulateKeystroke(KeyEvent.KEYCODE_DPAD_UP);
                break;
            case TODOWN:
                Utils.simulateKeystroke(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case TOLEFT:
                Utils.simulateKeystroke(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case TORIGHT:
                Utils.simulateKeystroke(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
            case CONFIRM:
                Utils.simulateKeystroke(KeyEvent.KEYCODE_DPAD_CENTER);
                break;
            case BLUETOOTH_OPEN:
                //BluetoothUtil.setEnable();
                break;
            case BLUETOOTH_CLOSE:
                //BluetoothUtil.setDisable();
                break;
            case TVLIVE_OPEN:
                AbsTvLiveControl.getInstance().tvLiveOpen();
                break;
            case OPEN_KALAOK:
                //KalaokUtils.kalaokOpen();
                break;
            case GET_VERSION:
                AbsTTS.getInstance(null).talk(Utils.getVersion());
                break;
            case MUSIC_CHINA:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 5);
                break;
            case MUSIC_UK:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 3);
                break;
            case MUSIC_HK:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 6);
                break;
            case MUSIC_KOREA:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 16);
                break;
            case MUSIC_JAPON:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 17);
                break;
            case MUSIC_JAPON_GX:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 105);
                break;
            case MUSIC_EN:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 107);
                break;
            case MUSIC_GOLD:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 36);
                break;
            case MUSIC_NEW:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 27);
                break;
            case MUSIC_HOT:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 26);
                break;
            case MUSIC_POPULAR:
                QQMusicUtils.openRankAction(VoiceApp.getInstance(), 4);
                break;
            case RESOLUTION_SET:
                if (Utils.isQ3031Recoder()) {
                    AbsTTS.getInstance(null).talk(VoiceApp.getInstance().getString(R.string.str_resolution_error));
                } else {
                    Intent intent = new Intent();
                    intent.setAction("com.skyworthdigital.settings.DisplaySetting");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //intent = getAppIntent(context, action, intent);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    VoiceApp.getInstance().startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
}
