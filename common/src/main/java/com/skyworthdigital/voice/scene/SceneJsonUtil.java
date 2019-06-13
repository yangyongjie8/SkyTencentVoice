package com.skyworthdigital.voice.scene;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.DefaultCmds;
import com.skyworthdigital.voice.common.IStatus;
import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.common.utils.Utils;
import com.skyworthdigital.voice.dingdang.utils.MLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;


public class SceneJsonUtil {
    private static final String TAG = "SceneJsonUtil";
    private static final String COMMANDS = "_commands";

    /**
     * 将场景注册命令的json格式的文件转化成字符串
     */
    public static String getSceneJson(Context context, int fileName) {
        String sceneJson = "";
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(fileName);
            sceneJson = inputStreamToString(is);
            //LogUtil.d("buildSceneJson : " + sceneJson);
            return sceneJson;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                //is = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sceneJson;
    }

    private static String inputStreamToString(InputStream is) {
        String string = "";
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new InputStreamReader(is, "UTF-8");
            bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
                buffer.append("\n");
            }
            string = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                //reader = null;
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                //bufferedReader = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return string;
    }

    /**
     * 功能：判断当前语音输入voice是否是scene中注册的命令
     * 参数：voice:语音原始输入的内容，如：快进五分钟
     * 参数：categoryServ:由server识别出的指令集类别，如：#Play
     * scene:格式是json字符串，通过onCmdRegister注册的命令，举例如下：res/raw/searchcmd
     */
    static String isVoiceCmdRegisted(String voice, @Nullable String categoryServ, String scene) {
        if (voice == null || scene == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(scene);
            JSONObject jsonCmd = jsonObject.getJSONObject(COMMANDS);
            //JSONObject jsonActivity = jsonObject.getJSONObject("_scene");
            MLog.i(TAG, "cmds: " + jsonCmd.toString());

            if (jsonCmd.length() > 0) {
                Iterator keys = jsonCmd.keys();
                String valueTemp = null;
                while (keys.hasNext()) {
                    String key = keys.next().toString();
                    String value = jsonCmd.optString(key);
//                    LogUtil.log("key: " + key + " value:" + value);
                    JSONArray jsonArray = new JSONArray(value);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        valueTemp = jsonArray.optString(i);
                        if(StringUtils.isMatches(voice, valueTemp)){
//                        if (voice.equalsIgnoreCase(jsonArray.optString(i))) {
                            MLog.i(TAG, "matched "+voice);
                            return key;
                        }
                        if(StringUtils.equals(categoryServ, valueTemp)){
                            MLog.i(TAG, "matched "+categoryServ);
                            return key;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static int isFuzzyMatched(String voice, String scene) {
        if (voice == null || scene == null || IStatus.mSceneType == IStatus.SCENE_GIVEN) {
            return -1;
        }
        try {
            JSONObject jsonObject = new JSONObject(scene);
            JSONObject jsonCmd = jsonObject.getJSONObject(COMMANDS);
            if (jsonCmd.length() > 0) {
                Iterator keys = jsonCmd.keys();
                while (keys.hasNext()) {
                    String key = keys.next().toString();

                    if (key.startsWith(DefaultCmds.FUZZYMATCH)) {
                        String value = jsonCmd.optString(key);
                        //LogUtil.log("key: " + key + " value:" + value);
                        JSONArray jsonArray = new JSONArray(value);
                        return fuzzyMatch(voice, jsonArray);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    private static int fuzzyMatch(String name, JSONArray jsonArray) {
        String[] filter = {"播放", "打开", "我想听", "我要听"};
        name = Utils.filterBy(name, filter);
        String name_chinese = "", name_int = "";
        int common2 = -1;
        if (name.contains("第")) {
            if (StringUtils.hasDigit(name)) {
                String str_number = StringUtils.getNumbers(name);
                String str_chinnese = StringUtils.numberToChinese(Integer.parseInt(str_number));
                name_chinese = name.replace(str_number, str_chinnese);
                //Log.i("wyf", "int2china:" + str_number + " chine:" + str_chinnese + " last:" + name_chinese);
            }

            if (StringUtils.hasChineseDigit(name)) {
                String str_chinnese = StringUtils.getChineseNumbers(name);
                if (!TextUtils.isEmpty(str_chinnese)) {
                    int number = StringUtils.chineseNumber2Int(str_chinnese);
                    name_int = name.replace(str_chinnese, String.valueOf(number));
                    //Log.i("wyf", "china2int:" + number + " chine:" + str_chinnese + " last:" + name_int);
                }
            }
        }
        ArrayList<String> commons = new ArrayList<>();
        //int index;
        int rate_max = -1, index_pre = -1;

        for (int i = 0; i < jsonArray.length(); i++) {
            String str_format = StringUtils.format(jsonArray.optString(i)).trim();
            int common = (int) (Utils.levenshtein(str_format, name) * 100);

            if (!TextUtils.isEmpty(name_chinese)) {
                common2 = (int) (Utils.levenshtein(str_format, name_chinese) * 100);
            } else if (!TextUtils.isEmpty(name_int)) {
                common2 = (int) (Utils.levenshtein(str_format, name_int) * 100);
            }

            if (common2 > common) {
                common = common2;
            }
            if (common > 0 && common > rate_max) {
                rate_max = common;
                index_pre = i;
                commons.add("{\"index\":" + i + ",\"common\":" + common + "}");
                Log.i("wyf", "{\"index\":" + i + ",\"common\":" + common + "}");
            }
            if (common == 100) {
                return i;
            }
        }

        int len = jsonArray.optString(index_pre).length();
        if (!TextUtils.isEmpty(name_chinese) || !TextUtils.isEmpty(name_int)) {
            if (rate_max == 100) {//带数字需要完全匹配
                return index_pre;
            } else {
                return -1;
            }
        } else if (len > 20 && rate_max >= 15) {
            return index_pre;//字数多的字符匹配度要求低一些
        } else if (len > 15 && rate_max >= 20) {
            return index_pre;//字数多的字符匹配度要求低一些
        } else if (len > 10 && rate_max >= 25) {
            return index_pre;//字数多的字符匹配度要求低一些
        } else if (rate_max >= 50) {
            return index_pre;
        }
        return -1;
    }
}
