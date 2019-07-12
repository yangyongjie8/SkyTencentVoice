package com.skyworthdigital.voice.globalcmd;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.dingdang.utils.IntentUtils;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.io.InputStream;
import java.util.List;

/**
 * 全局语音功能。
 * 实现方式：
 * 解析R.raw.global文件中描述的全局语音的命令列表mListJson。
 * 语音原始输入内容和百度解析后的内容都和mListJson列表做比对，增加准确性，因为有时百度解析不准确。
 * 如果匹配列表中某条item的cmds中其中一个，那么按item中定义的action或package name做跳转
 */
public class GlobalUtil {
    private static String TAG = GlobalUtil.class.getSimpleName();
    private static GlobalUtil mGlobalUtilInstance = null;
    private static List<Cell> mListJson = null;
    private final static String[] START_FILTER = {"我要", "帮我", "我想", "打开", "进入", "开启", "启动"};

    /**
     * 功能：读取R.raw.global文件，转换成GlobalBean类。
     */
    private GlobalBean parseLocalConfig(Context context) {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.global);
            String content = StringUtils.convertStreamToString(inputStream);
            Gson gson = new Gson();
            return gson.fromJson(content, GlobalBean.class);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public static GlobalUtil getInstance() {
        Context context = VoiceApp.getInstance();
        if (mGlobalUtilInstance == null) {
            mGlobalUtilInstance = new GlobalUtil(context);
        }
        return mGlobalUtilInstance;
    }

    /**
     * 功能：读取R.raw.global文件，转换成GlobalBean类。获取到全局语音的命令列表mListJson。
     */
    private GlobalUtil(Context context) {
        GlobalBean globalBean = parseLocalConfig(context);
        if (globalBean != null) {
            mListJson = globalBean.getCellList();
        }
    }

    /**
     * 功能：arg1和arg2都和mListJson列表项做比对，
     * 如果匹配列表中某条item的cmds中其中一个，那么按item中定义的action或package name做跳转
     * 参数：可同时有arg1和arg2，也可只有其中一个，另一个为空。
     * 一般一个参数是原始语音输入内容，另一个为百度解析后的内容，这样匹配更加准确
     */
    public boolean control(Context context, String arg1, String arg2) {
        if ((mListJson == null) || (TextUtils.isEmpty(arg1) && TextUtils.isEmpty(arg2))) {
            return false;
        }

        //arg1 = Utils.filterByStartWith(arg1, START_FILTER);
        //arg2 = Utils.filterByStartWith(arg2, START_FILTER);
        if (TextUtils.isEmpty(arg1) && TextUtils.isEmpty(arg2)) {
            return false;
        }
        MLog.d(TAG, "global:" + arg1 + " " + arg2);

        for (int i = 0; i < mListJson.size(); i++) {
            Cell cell = mListJson.get(i);
            boolean match = false;
            if (TextUtils.equals(cell.getType(), "all")) {
                if (arg1 != null && cell.getCmds().contains(arg1) || (arg2 != null && cell.getCmds().contains(arg2))) {
                    match = true;
                }
            } else if ((!TextUtils.isEmpty(arg1) && isInList(cell.getCmds(), arg1)) || (!TextUtils.isEmpty(arg2) && isInList(cell.getCmds(), arg2))) {
                match = true;
                AbsTTS.getInstance(null).talk(context.getString(R.string.str_ok));
            }

            if (match) {
                Action action = cell.getAction();
                String pkgname = cell.getPkgname();

                if (action != null) {
                    try {
                        IntentUtils.startCellAction(context, cell.getAction());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (pkgname != null) {
                    IntentUtils.startPackageAction(context, pkgname);
                    return true;
                }
            }
        }
        if (appLaunchInstalledPkg(context, arg1)) {
            return true;
        }
        return false;
    }

    private static boolean appLaunchInstalledPkg(Context context, String speech) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        try {
            for (PackageInfo info : packageInfos) {
                String packageName = info.packageName;

                String label = packageManager.getApplicationLabel(info.applicationInfo).toString();

                if (label.equalsIgnoreCase(speech)) {
                    MLog.d("wyf", "appLaunchInstalledPkg:" + label);
                    AbsTTS.getInstance(null).talk(context.getString(R.string.str_ok));
                    IntentUtils.startPackageAction(context, packageName);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isInList(List<String> list, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }

        if (list.size() <= 0) {
            return false;
        }
        for (String temp : list) {
            if (!TextUtils.isEmpty(temp)) {
                if (str.contains(temp)) {
                    return true;
                }
            }
        }
        return false;
    }
}