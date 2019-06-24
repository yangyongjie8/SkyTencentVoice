package com.skyworthdigital.voice.music.utils;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.music.musictype.TypeBean;
import com.skyworthdigital.voice.music.musictype.TypeCell;

import java.io.InputStream;
import java.util.List;

/**
 * 全局语音功能。
 * 实现方式：
 * 解析R.raw.music_type文件中描述的全局语音的命令列表mListJson。
 * 语音原始输入内容和百度解析后的内容都和mListJson列表做比对，增加准确性，因为有时百度解析不准确。
 * 如果匹配列表中某条item的cmds中其中一个，那么按item中定义的action或package name做跳转
 */
public class TypeUtil {
    private static TypeUtil mTypeInstance = null;
    private static List<TypeCell> mListJson = null;
    //private final static String[] START_FILTER = {"打开", "进入", "开启", "启动"};

    /**
     * 功能：读取R.raw.global文件，转换成GlobalBean类。
     */
    private TypeBean parseLocalConfig(Context context) {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.music_type);
            String content = StringUtils.convertStreamToString(inputStream);
            Gson gson = new Gson();
            return gson.fromJson(content, TypeBean.class);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public static TypeUtil getInstance(Context context) {
        if (mTypeInstance == null) {
            mTypeInstance = new TypeUtil(context);
        }
        return mTypeInstance;
    }

    /**
     * 功能：读取R.raw.music_type，转换成GlobalBean类。获取到全局语音的命令列表mListJson。
     */
    private TypeUtil(Context context) {
        TypeBean typeBean = parseLocalConfig(context);
        if (typeBean != null) {
            mListJson = typeBean.getCellList();
        }
    }

    /**
     * 功能：arg1和arg2都和mListJson列表项做比对，
     * 如果匹配列表中某条item的cmds中其中一个，那么按item中定义的action或package name做跳转
     * 参数：可同时有arg1和arg2，也可只有其中一个，另一个为空。
     * 一般一个参数是原始语音输入内容，另一个为百度解析后的内容，这样匹配更加准确
     */
    public TypeCell getInfo(String arg1, String speech) {
        if ((mListJson == null) || (TextUtils.isEmpty(arg1) && TextUtils.isEmpty(speech))) {
            return null;
        }

        for (int i = 0; i < mListJson.size(); i++) {
            TypeCell typeCell = mListJson.get(i);
            //LogUtil.log("control:" + typeCell.getCmds() + " " + arg1 + " " + arg2);
            List<String> cmds = typeCell.getCmds();
            if (cmds.size() > 0) {
                for (String tmp : cmds) {
                    //MLog.d("wyf", tmp);
                    if (!TextUtils.isEmpty(speech) && speech.contains(tmp)) {
                        return typeCell;
                    }
                    if (!TextUtils.isEmpty(arg1) && arg1.contains(tmp)) {
                        return typeCell;
                    }
                }
            }
        }

        return null;
    }
}