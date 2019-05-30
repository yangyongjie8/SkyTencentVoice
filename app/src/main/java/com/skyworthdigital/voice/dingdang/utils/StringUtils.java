package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.skyworthdigital.voice.dingdang.IoT.IoTService;
import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.VoiceApp;
import com.skyworthdigital.voice.dingdang.control.tts.MyTTS;
import com.skyworthdigital.voice.dingdang.domains.iot.IoTParserResult;
import com.skyworthdigital.voice.dingdang.domains.tianmai.TianmaiIntent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class StringUtils {
    private static final String TAG = "StringUtils";

    //public static final String RAW_TYPE = "raw";

    public static String convertStreamToString(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder sBuild = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sBuild.append(line);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            throw new IOException("convertStreamToString failed");
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sBuild.toString();
    }

    /*
  *功能：根据语音输入获取可能的片名。对带季或部的片名做特殊处理，提高搜索结果准确度。
  * 例如film:速度与激情，speech：速度与激情第五部，
  * 返回：速度与激情第五季，速度与激情 第五季，速度与激情第五部，速度与激情 第五部，速度与激情五，速度与激情 五，速度与激情第5季，速度与激情 第5季，速度与激情第5部，速度与激情 第5部，速度与激情5，速度与激情 5，
   */
    static String composeNameWithSpeech(String film, String speech) {
        String regex = "第([0~9,一二三四五六七八九十]{1,2})(季|部)";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(speech);
        if (m.find()) {
            StringBuilder whff = new StringBuilder();
            whff.append(film + "第" + m.group(1) + "季");
            whff.append(",");
            whff.append(film + " 第" + m.group(1) + "季");
            whff.append(",");
            whff.append(film + "第" + m.group(1) + "部");
            whff.append(",");
            whff.append(film + " 第" + m.group(1) + "部");
            whff.append(",");
            whff.append(film + m.group(1));
            whff.append(",");
            whff.append(film + " " + m.group(1));
            whff.append(",");
            String num = chineseToRome(m.group(1));
            if (num != null) {
                whff.append(film + "第" + num + "季");
                whff.append(",");
                whff.append(film + " 第" + num + "季");
                whff.append(",");
                whff.append(film + "第" + num + "部");
                whff.append(",");
                whff.append(film + " 第" + num + "部");
                whff.append(",");
                whff.append(film + num);
                whff.append(",");
                whff.append(film + " " + num);
            }
            //LogUtil.log(whff.toString());
            return whff.toString();
        }
        return null;
    }

    /*
    *功能：重新组合可能的影片名。对片名做特殊处理。
    * 例如film:速度与激情，Whdepart：5，
    * 返回：速度与激情5，速度与激情 5，速度与激情五，速度与激情 五
     */
    static String composeNameWithWhdepart(String film, String Whdepart) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(film);
            sb.append(Integer.parseInt(Whdepart));
            sb.append(",");
            sb.append(film);
            sb.append(Integer.parseInt(Whdepart));
            String chinanum = numToChinese(Whdepart);
            if (chinanum != null) {
                sb.append(",");
                sb.append(film);
                sb.append(chinanum);
                sb.append(",");
                sb.append(film);
                sb.append(chinanum);
            }
            //LogUtil.log(sb.toString());
            return sb.toString();
        } catch (Exception e) {
            return film;
        }
    }

    //去除字符串中的标点
    public static String format(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。， ·、？|-]", "");
        return str;
    }

    /**
     * 功能：提取字符串中的数字
     */
    public static String getStringNumbers(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 功能：中文转为罗马数字，暂只处理千以内的。
     */
    public static int chineseNumber2Int(String chineseNumber) {
        int result = 0;
        int temp = 0;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        char[] cnArr = new char[]{'一', '二', '三', '四', '五', '六', '七', '八', '九'};
        char[] chArr = new char[]{'十', '百'};
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if (0 != count) {//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if (b) {//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                if (temp == 0) {
                                    temp = 1;
                                }
                                temp *= 10;
                                break;
                            case 1:
                                if (temp == 0) {
                                    temp = 1;
                                }
                                temp *= 100;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNumber.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }

    /**
     * 根据用户语音原话，提取里面的数值，如播放第5集中的数字5
     */
    public static int getIndexFromSpeech(String speech) {
        String regex = "^(第|播放第|播放的|我想看第|我要看第)(([一二三四五六七八九十]{1,5})|[0-9]{1,4})(集|季|期|级|课|极|步|部|个)$";

        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(speech);
            if (m.find()) {
                String strnum = m.group(2);
                MLog.d("wyf", "getEpisodeFromSpeech:" + strnum);
                if (chineseNumber2Int(strnum) != 0) {
                    return chineseNumber2Int(strnum);
                } else {
                    return Integer.valueOf(strnum);
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    /**
     * 根据用户语音原话，提取是否是上一集指令
     */
    public static boolean isPrevCmdFromSpeech(String speech) {
        String regex = "^(播放上一级|上一级|播放上一集|上一集)$";
        return Pattern.matches(regex, speech);
    }

    /**
     * 根据用户语音原话，提取是否是上一集指令
     */
    public static boolean isReplayCmdFromSpeech(String speech) {
        String regex = "^(从头开始|重新开始|重播|重新播|从头播).*";
        return Pattern.matches(regex, speech);
    }

    /**
     * 根据用户语音原话，提取是否是下一集指令
     */
    public static boolean isNextCmdFromSpeech(String speech) {
        String regex = "^(下一级|播放下一集)$";
        return Pattern.matches(regex, speech);
    }

    /**
     * 根据用户语音原话，提取是否是下一集指令
     */
    public static boolean isMusicCmdFromSpeech(String speech) {
        String regex = ".*(听|歌|曲|音乐|唱|首).*";
        MLog.d("wyf", "isMusicCmdFromSpeech");
        return Pattern.matches(regex, speech);
    }

    /**
     * 根据用户语音原话，提取是否消息静音
     */
    public static boolean isUnmuteCmdFromSpeech(String speech) {
        String regex = ".*(取消静音|恢复音量|音量恢复|静音取消).*";
        MLog.d("wyf", "isUnmuteCmdFromSpeech");
        return Pattern.matches(regex, speech);
    }

    /**
     * 根据用户语音原话，提取是否请求帮助指令
     */
    public static boolean isHelpCmdFromSpeech(String speech) {
        String regex = ".*(怎么使用|如何使用|功能介绍|使用说明|功能说明|使用帮助).*";
        MLog.d("wyf", "isExitCmdFromSpeech");
        return Pattern.matches(regex, speech);
    }

    public static String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 E", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 根据用户语音原话，提取是否退出指令
     */
    public static boolean isExitCmdFromSpeech(String speech) {
        String regex = ".*(退下).*";
        MLog.d("wyf", "isExitCmdFromSpeech");
        return Pattern.matches(regex, speech);
    }

    /**
     * 根据用户语音原话，提取是否退出指令
     */
    public static boolean isHomeCmdFromSpeech(String speech) {
        String regex = "^(退出).*";
        MLog.d("wyf", "isExitCmdFromSpeech");
        return Pattern.matches(regex, speech);
    }
    /**
     * 根据用户语音原话，提取是否消息静音
     */
    public static boolean isExitMusicCmdFromSpeech(String speech) {
        String regex = ".*(不想听|不听了|停止播放|播放停止|不想听了|不听呢|不听啦).*";
        MLog.d("wyf", "isExitMusicCmdFromSpeech");
        return Pattern.matches(regex, speech);
    }

    public static boolean isDingdangInvalidBack(String word) {
        String regex = ".*(我这里还没有这个词条|试试问点别的|没找到|查不到它的相关信息|没学到|还没有).*";
        return Pattern.matches(regex, word);
    }

    public static void showUnknownNote(Context ctx, String speech) {
        String tip = ctx.getString(R.string.unknow_tip);
        tip = String.format(tip, speech);
        MyTTS.getInstance(null).talk("", tip);
    }

    /**
     * 根据用户语音原话，判断是否是播放命令
     */
    static boolean isPlayCmdFromSpeech(String speech) {
        String regex = "^(播放|继续播放|回复播放)$";

        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(speech);
            if (m.find()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 根据用户语音原话，判断是否是暂停命令
     */
    static boolean isPauseCmdFromSpeech(String speech) {
        String regex = "^(暂停|暂停播放|播放暂停)$";

        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(speech);
            if (m.find()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 功能：数字转为中文，暂只处理1~10的。
     */
    private static String numToChinese(String num) {
        Map<String, String> map = new HashMap<>();
        map.put("1", "一");
        map.put("2", "二");
        map.put("3", "三");
        map.put("4", "四");
        map.put("5", "五");
        map.put("6", "六");
        map.put("7", "七");
        map.put("8", "八");
        map.put("9", "九");
        map.put("10", "十");

        return map.get(num);
    }

    /**
     * 功能：中文转为罗马数字，暂只处理1~10的。
     */
    private static String chineseToRome(String num) {
        Map<String, String> map = new HashMap<>();
        map.put("一", "1");
        map.put("二", "2");
        map.put("三", "3");
        map.put("四", "4");
        map.put("五", "5");
        map.put("六", "6");
        map.put("七", "7");
        map.put("八", "8");
        map.put("九", "9");
        map.put("十", "10");
        map.put("十一", "11");
        map.put("十二", "12");
        return map.get(num);
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(Long lt) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date(lt * 1000);
        res = simpleDateFormat.format(date);
        return res;
    }

    // 判断一个字符串是否含有数字
    public static boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    // 判断一个字符串是否含有中文数字
    public static boolean hasChineseDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*[一二三四五六七八九十]{1,5}.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    //截取数字  【读取字符串中第一个连续的字符串，不包含后面不连续的数字】
    public static String getChineseNumbers(String content) {
        Pattern pattern = Pattern.compile("[一二三四五六七八九十]{1,5}");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    private static String sectionTOChinese(int section, String chineseNum) {
        String setionChinese = new String();//小节部分用独立函数操作
        int unitPos = 0;//小节内部的权值计数器
        boolean zero = true;//小节内部的制零判断，每个小节内只能出现一个零

        final String[] chnNumChar = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final char[] chnNumChinese = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};
        //节权位
        final String[] chnUnitSection = {"", "万", "亿", "万亿"};
        //权位
        final String[] chnUnitChar = {"", "十", "百", "千"};
        HashMap intList = new HashMap();

        for (int i = 0; i < chnNumChar.length; i++) {
            intList.put(chnNumChinese[i], i);
        }

        intList.put('十', 10);
        intList.put('百', 100);
        intList.put('千', 1000);

        while (section > 0) {
            int v = section % 10;//取当前最末位的值
            if (v == 0) {
                if (!zero) {
                    zero = true;//需要补零的操作，确保对连续多个零只是输出一个
                    chineseNum = chnNumChar[0] + chineseNum;
                }
            } else {
                zero = false;//有非零的数字，就把制零开关打开
                setionChinese = chnNumChar[v];//对应中文数字位
                setionChinese = setionChinese + chnUnitChar[unitPos];//对应中文权位
                chineseNum = setionChinese + chineseNum;
            }
            unitPos++;
            section = section / 10;
        }

        return chineseNum;
    }

    public static String numberToChinese(int num) {//转化一个阿拉伯数字为中文字符串
        if (num == 0) {
            return "零";
        }

        final String[] chnNumChar = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final char[] chnNumChinese = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};
        //节权位
        final String[] chnUnitSection = {"", "万", "亿", "万亿"};
        //权位
        final String[] chnUnitChar = {"", "十", "百", "千"};
        HashMap intList = new HashMap();

        for (int i = 0; i < chnNumChar.length; i++) {
            intList.put(chnNumChinese[i], i);
        }

        intList.put('十', 10);
        intList.put('百', 100);
        intList.put('千', 1000);


        int unitPos = 0;//节权位标识
        String All = new String();
        String chineseNum = new String();//中文数字字符串
        boolean needZero = false;//下一小结是否需要补零
        String strIns = new String();
        while (num > 0) {
            int section = num % 10000;//取最后面的那一个小节
            if (needZero) {//判断上一小节千位是否为零，为零就要加上零
                All = chnNumChar[0] + All;
            }
            chineseNum = sectionTOChinese(section, chineseNum);//处理当前小节的数字,然后用chineseNum记录当前小节数字
            if (section != 0) {//此处用if else 选择语句来执行加节权位
                strIns = chnUnitSection[unitPos];//当小节不为0，就加上节权位
                chineseNum = chineseNum + strIns;
            } else {
                strIns = chnUnitSection[0];//否则不用加
                chineseNum = strIns + chineseNum;
            }
            All = chineseNum + All;
            chineseNum = "";
            needZero = (section < 1000) && (section > 0);
            num = num / 10000;
            unitPos++;
        }
        return All;
    }

    //截取数字  【读取字符串中第一个连续的字符串，不包含后面不连续的数字】
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 演示用，是否开启2分钟弹节目单提示
     * @param speech
     * @return
     */
    public static boolean doTwoMinSwitch(String speech){
        if(TextUtils.isEmpty(speech))return false;
        if(speech.matches("^1234567$")){//启用2分钟周期演示
            SPUtil.putBoolean(SPUtil.KEY_SP_DEMO_SWITCH_ON, true);
            MyTTS.getInstance(null).talk("已启用");
            return true;
        }else if (speech.matches("^7654321$")){//关闭
            SPUtil.putBoolean(SPUtil.KEY_SP_DEMO_SWITCH_ON, false);
            MyTTS.getInstance(null).talk("已关闭");
            return true;
        }
        return false;
    }
    private static String buildTianmaiTimeScheduleRegex(String time){
        // .{0,2}表示可以中间有任意两个以内的字符，但半小时的应当放在整点的前面，以让半小时的优先匹配，否则会被整点的匹配掉。晚上的也是一样道理，原则是：更长的模板放在更前面。
        String regex = time+".{0,4}行程|"+time+".{0,4}形成|"+time+".{0,4}星辰|"+time+"要((干|做)(什么|啥))|"+time+"有(什么|啥)要(干|做)";
        return regex;
    }
    /**
     * 是否天脉演示场景指令
     * @param speech
     * @return
     */
    public static TianmaiIntent isTianMaiDemoSpeech(String speech){
        MLog.i("StringUtils", "speech:"+speech);
        List<TianmaiIntent> intentList = Arrays.asList(new TianmaiIntent("开始演示", "开始.{0,3}(演示|掩饰|也是)"),
                new TianmaiIntent("十三点半行程", buildTianmaiTimeScheduleRegex("十三点半")+"|"+ buildTianmaiTimeScheduleRegex("下午一点半")+"|"+ buildTianmaiTimeScheduleRegex("下午1:30")),
                new TianmaiIntent("十三点行程", buildTianmaiTimeScheduleRegex("十三点")+"|"+ buildTianmaiTimeScheduleRegex("下午一点")+"|"+ buildTianmaiTimeScheduleRegex("13:00")),
                new TianmaiIntent("十四点半行程", false, buildTianmaiTimeScheduleRegex("十四点半")+"|"+ buildTianmaiTimeScheduleRegex("下午两点半")+"|"+ buildTianmaiTimeScheduleRegex("下午2:30")),
                new TianmaiIntent("十四点行程", buildTianmaiTimeScheduleRegex("十四点")+"|"+ buildTianmaiTimeScheduleRegex("下午两点")+"|"+ buildTianmaiTimeScheduleRegex("下午2:00")),
                new TianmaiIntent("十五点半行程", false, buildTianmaiTimeScheduleRegex("十五点半")+"|"+ buildTianmaiTimeScheduleRegex("下午三点半")+"|"+ buildTianmaiTimeScheduleRegex("下午3:30")),
                new TianmaiIntent("十五点行程", false, buildTianmaiTimeScheduleRegex("十五点")+"|"+ buildTianmaiTimeScheduleRegex("下午三点")+"|"+ buildTianmaiTimeScheduleRegex("下午3:00")),
                new TianmaiIntent("十六点半行程", buildTianmaiTimeScheduleRegex("十六点半")+"|"+ buildTianmaiTimeScheduleRegex("下午四点半")+"|"+ buildTianmaiTimeScheduleRegex("下午4:30")),
                new TianmaiIntent("十六点行程", buildTianmaiTimeScheduleRegex("十六点")+"|"+ buildTianmaiTimeScheduleRegex("下午四点")+"|"+ buildTianmaiTimeScheduleRegex("下午4:00")),
                new TianmaiIntent("十七点半行程", buildTianmaiTimeScheduleRegex("十七点半")+"|"+ buildTianmaiTimeScheduleRegex("下午五点半")+"|"+ buildTianmaiTimeScheduleRegex("下午5:30")),
                new TianmaiIntent("十七点行程", false, buildTianmaiTimeScheduleRegex("十七点")+"|"+ buildTianmaiTimeScheduleRegex("下午五点")+"|"+ buildTianmaiTimeScheduleRegex("下午5:00")),
                new TianmaiIntent("十八点半行程", false, buildTianmaiTimeScheduleRegex("十八点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上六点半")+"|"+ buildTianmaiTimeScheduleRegex("今晚六点半")+"|"+ buildTianmaiTimeScheduleRegex("下午6:30")+"|"+ buildTianmaiTimeScheduleRegex("晚上6:30")),
                new TianmaiIntent("十八点行程", buildTianmaiTimeScheduleRegex("十八点")+"|"+ buildTianmaiTimeScheduleRegex("下午六点")+"|"+ buildTianmaiTimeScheduleRegex("晚上六点")+"|"+ buildTianmaiTimeScheduleRegex("今晚六点")+"|"+ buildTianmaiTimeScheduleRegex("晚上6:00")+"|"+ buildTianmaiTimeScheduleRegex("下午6:00")),
                new TianmaiIntent("十九点半行程", false, buildTianmaiTimeScheduleRegex("十九点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上七点半")+"|"+ buildTianmaiTimeScheduleRegex("今晚七点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上7:30")),
                new TianmaiIntent("十九点行程", buildTianmaiTimeScheduleRegex("十九点")+"|"+ buildTianmaiTimeScheduleRegex("晚上七点")+"|"+ buildTianmaiTimeScheduleRegex("今晚七点")+"|"+ buildTianmaiTimeScheduleRegex("晚上7:00")),
                new TianmaiIntent("二十点半行程", buildTianmaiTimeScheduleRegex("二十点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上八点半")+"|"+ buildTianmaiTimeScheduleRegex("今晚八点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上8:30")),
                new TianmaiIntent("二十点行程", false, buildTianmaiTimeScheduleRegex("二十点")+"|"+ buildTianmaiTimeScheduleRegex("晚上八点")+"|"+ buildTianmaiTimeScheduleRegex("今晚八点")+"|"+ buildTianmaiTimeScheduleRegex("晚上8:00")),
                new TianmaiIntent("二十一点半行程", false, buildTianmaiTimeScheduleRegex("二十一点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上九点半")+"|"+ buildTianmaiTimeScheduleRegex("今晚九点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上9:30")),
                new TianmaiIntent("二十一点行程", buildTianmaiTimeScheduleRegex("21点")+"|"+ buildTianmaiTimeScheduleRegex("二十一点")+"|"+ buildTianmaiTimeScheduleRegex("晚上九点")+"|"+ buildTianmaiTimeScheduleRegex("今晚九点")+"|"+ buildTianmaiTimeScheduleRegex("晚上9:00")),
                new TianmaiIntent("二十二点半行程", false, buildTianmaiTimeScheduleRegex("22点半")+"|"+ buildTianmaiTimeScheduleRegex("二十二点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上十点半")+"|"+ buildTianmaiTimeScheduleRegex("今晚十点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上10:30")),
                new TianmaiIntent("二十二点行程", false, buildTianmaiTimeScheduleRegex("22点")+"|"+ buildTianmaiTimeScheduleRegex("二十二点")+"|"+ buildTianmaiTimeScheduleRegex("晚上十点")+"|"+ buildTianmaiTimeScheduleRegex("晚上时点")+"|"+ buildTianmaiTimeScheduleRegex("今晚十点")+"|"+ buildTianmaiTimeScheduleRegex("今晚时点")+"|"+ buildTianmaiTimeScheduleRegex("晚上10:00")),
                new TianmaiIntent("二十三点半行程", false, buildTianmaiTimeScheduleRegex("23点半")+"|"+ buildTianmaiTimeScheduleRegex("二十三点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上十一点半")+"|"+ buildTianmaiTimeScheduleRegex("今晚十一点半")+"|"+ buildTianmaiTimeScheduleRegex("晚上11:30")),
                new TianmaiIntent("二十三点行程", false, buildTianmaiTimeScheduleRegex("23点")+"|"+ buildTianmaiTimeScheduleRegex("二十三点")+"|"+ buildTianmaiTimeScheduleRegex("晚上十一点")+"|"+ buildTianmaiTimeScheduleRegex("晚上时一点")+"|"+ buildTianmaiTimeScheduleRegex("今晚十一点")+"|"+ buildTianmaiTimeScheduleRegex("今晚时一点")+"|"+ buildTianmaiTimeScheduleRegex("晚上11:00")),
                new TianmaiIntent("六点半行程", buildTianmaiTimeScheduleRegex("六点半")+"|"+ buildTianmaiTimeScheduleRegex("6:30")),
                new TianmaiIntent("六点行程", buildTianmaiTimeScheduleRegex("六点")+"|"+ buildTianmaiTimeScheduleRegex("6:00")+"|我起床了|我起床啦"),
                new TianmaiIntent("起床提醒", "起床.{0,4}提醒"),
                new TianmaiIntent("七点半行程", false, buildTianmaiTimeScheduleRegex("七点半")+"|"+ buildTianmaiTimeScheduleRegex("7:30")),
                new TianmaiIntent("七点行程", false, buildTianmaiTimeScheduleRegex("七点")+"|"+ buildTianmaiTimeScheduleRegex("7:00")),
                new TianmaiIntent("八点半行程", buildTianmaiTimeScheduleRegex("八点半")+"|"+ buildTianmaiTimeScheduleRegex("8:30")),
                new TianmaiIntent("八点行程", buildTianmaiTimeScheduleRegex("八点")+"|"+ buildTianmaiTimeScheduleRegex("8:00")),
                new TianmaiIntent("九点半行程", buildTianmaiTimeScheduleRegex("九点半")+"|"+ buildTianmaiTimeScheduleRegex("9:30")),
                new TianmaiIntent("九点行程", buildTianmaiTimeScheduleRegex("九点")+"|"+ buildTianmaiTimeScheduleRegex("9:00")),
                new TianmaiIntent("十点半行程", false, buildTianmaiTimeScheduleRegex("十点半")+"|"+ buildTianmaiTimeScheduleRegex("时点半")+"|"+ buildTianmaiTimeScheduleRegex("10:30")),
                new TianmaiIntent("十点行程", buildTianmaiTimeScheduleRegex("十点")+"|"+ buildTianmaiTimeScheduleRegex("时点")+"|"+ buildTianmaiTimeScheduleRegex("10:00")),//十点的，时点的，十点钟的...
                new TianmaiIntent("十一点半行程", false, buildTianmaiTimeScheduleRegex("十一点半")+"|"+ buildTianmaiTimeScheduleRegex("11:30")),
                new TianmaiIntent("十一点行程", false, buildTianmaiTimeScheduleRegex("十一点")+"|"+ buildTianmaiTimeScheduleRegex("11:00")),
                new TianmaiIntent("十二点半行程", buildTianmaiTimeScheduleRegex("十二点半")+"|"+ buildTianmaiTimeScheduleRegex("12:30")),
                new TianmaiIntent("十二点行程", buildTianmaiTimeScheduleRegex("十二点")+"|"+ buildTianmaiTimeScheduleRegex("12:00")),
                new TianmaiIntent("睡眠注意事项", "^(睡觉|睡眠).{0,4}注意.{0,4}事项$"));

        // 能正则匹配到的指令，则添加响应
        for (TianmaiIntent intent : intentList) {
            if(Pattern.compile(intent.getMatchRegex()).matcher(speech).find()) {
                intent.setVoiceContent(getTianmaiActionContent(intent));
                MLog.i("StringUtils", "found match time");
                return intent;
            }
        }

        sortToNormalTime(intentList);

        if(isAskforTianmaiNextSchedule(speech)){
            String nextScheduleKey = getNextScheduleName();
            MLog.i("StringUtils", "nextScheduleKey:"+nextScheduleKey);
            // 直接返回最近对应的有效的行程
            boolean hasFoundTime = false;
            for (TianmaiIntent intent : intentList) {
                if(hasFoundTime){
                    // 过滤出有效匹配的下一个行程
                    if(intent.isTurnOn() && !"六点行程".equals(intent.getName())) {
                        intent.setVoiceContent(intent.getName().replace("行程","，") + getTianmaiActionContent(intent));
                        return intent;
                    }else {
                        continue;
                    }
                }
                if(intent.getName().equalsIgnoreCase(nextScheduleKey)) {
                    hasFoundTime = true;
                    MLog.i("StringUtils", "found latest time");
                    if(intent.isTurnOn()) {//当前intent有效
                        intent.setVoiceContent(intent.getName().replace("行程","，") + getTianmaiActionContent(intent));
                        return intent;
                    }
                }
            }
        }

        return null;
    }

    // 将意图列表按正常的时间顺序排列，以便查找最近的行程
    private static void sortToNormalTime(List<TianmaiIntent> intentList) {
        final List<String> sortIndexTemplete = Arrays.asList("六点行程","六点半行程","七点行程","七点半行程","八点行程","八点半行程","九点行程","九点半行程","十点行程","十点半行程",
                "十一点行程","十一点半行程","十二点行程","十二点半行程","十三点行程","十三点半行程","十四点行程","十四点半行程","十五点行程","十五点半行程","十六点行程",
                "十六点半行程","十七点行程","十七点半行程","十八点行程","十八点半行程","十九点行程","十九点半行程","二十点行程","二十点半行程","二十一点行程","二十一点半行程",
                "二十二点行程","二十二点半行程","二十三点行程","二十三点半行程","开始演示","起床提醒","睡眠注意事项");
        // 先按时间顺序排齐
        Collections.sort(intentList, new Comparator<TianmaiIntent>() {
            @Override
            public int compare(TianmaiIntent o1, TianmaiIntent o2) {
                return sortIndexTemplete.indexOf(o1.getName()) - sortIndexTemplete.indexOf(o2.getName());
            }
        });
//        for (TianmaiIntent ti : intentList) {
//            LogUtil.log("name:" + ti.getName());
//            LogUtil.log("voice content:" + ti.getVoiceContent());
//        }
    }

    // 是否询问接下来的行程（天脉演示）
    private static boolean isAskforTianmaiNextSchedule(String speech){
        return Pattern.compile("(最近|接下来|今天|等一下|等下|现在|等一会|等一等|稍后|下一个)(有什么|的|还有什么)(行程|形成)").matcher(speech).find();
    }
    // 返回最近一个整半点的行程名称
    private static String getNextScheduleName(){
        Calendar c = Calendar.getInstance();
        int hour,minute;
        if(c.get(Calendar.MINUTE)>=30){// 超过半小时，取最近一个整点
            hour = c.get(Calendar.HOUR_OF_DAY)+1;
            minute = 0;
        }else {// 未超过半小时，将时间设为30分
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = 30;
        }
        String[] hourList = new String[]{"零","一","二","三","四","五","六","七","八","九","十","十一","十二","十三","十四","十五","十六","十七","十八","十九","二十",
                "二十一","二十二","二十三","二十四"};
        String name = hourList[hour]+"点"+(minute==0?"行程":"半行程");
        return name;
    }
    // 设置天脉演示播放的内容
    private static String getTianmaiActionContent(TianmaiIntent intent){
        if(!intent.isTurnOn()){
            return intent.getName().replace("行程", "没有行程");
        }
        switch (intent.getName()) {
            case "开始演示":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_start_play);
            case "六点行程":
                // 六点行程的天气播放放在外部设置，因为最好使用异步线程取值
                // 这里将只设置全部逐条播放的内容
                return getTianmaiScheduleAllContent();
            case "起床提醒":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_tip_wakeup);
            case "六点半行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_6_half);
            case "八点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_8);
            case "八点半行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_8_half);
            case "九点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_9);
            case "九点半行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_9_half);
            case "十点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_10);
            case "十二点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_12);
            case "十二点半行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_12_half);
            case "十三点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_13);
            case "十三点半行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_13_half);
            case "十四点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_14);
            case "十六点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_16);
            case "十六点半行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_16_half);
            case "十七点半行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_17_half);
            case "十八点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_18);
            case "十九点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_19);
            case "二十点半行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_20_half);
            case "二十一点行程":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_schedule_21);
            case "睡眠注意事项":
                return VoiceApp.getInstance().getString(R.string.str_tianmai_sleep_notice);
            default:MLog.i("StringUtils", "有漏掉的意图，请检查并补充");
        }
        return null;
    }
    // 起床时需要播放今日行程的所有组合。
    private static String getTianmaiScheduleAllContent(){
        StringBuilder sb = new StringBuilder();
        sb.append("六点半行程，").append(getTianmaiActionContent(new TianmaiIntent("六点半行程",null))).append("#");
        sb.append("八点行程，").append(getTianmaiActionContent(new TianmaiIntent("八点行程",null))).append("#");
        sb.append("八点半行程，").append(getTianmaiActionContent(new TianmaiIntent("八点半行程",null))).append("#");
        sb.append("九点行程，").append(getTianmaiActionContent(new TianmaiIntent("九点行程",null))).append("#");
        sb.append("九点半行程，").append(getTianmaiActionContent(new TianmaiIntent("九点半行程",null))).append("#");
        sb.append("十点行程，").append(getTianmaiActionContent(new TianmaiIntent("十点行程",null))).append("#");
        sb.append("十二点行程，").append(getTianmaiActionContent(new TianmaiIntent("十二点行程",null))).append("#");
        sb.append("十二点半行程，").append(getTianmaiActionContent(new TianmaiIntent("十二点半行程",null))).append("#");
        sb.append("十三点行程，").append(getTianmaiActionContent(new TianmaiIntent("十三点行程",null))).append("#");
        sb.append("十三点半行程，").append(getTianmaiActionContent(new TianmaiIntent("十三点半行程",null))).append("#");
        sb.append("十四点行程，").append(getTianmaiActionContent(new TianmaiIntent("十四点行程",null))).append("#");
        sb.append("十六点行程，").append(getTianmaiActionContent(new TianmaiIntent("十六点行程",null))).append("#");
        sb.append("十六点半行程，").append(getTianmaiActionContent(new TianmaiIntent("十六点半行程",null))).append("#");
        sb.append("十七点半行程，").append(getTianmaiActionContent(new TianmaiIntent("十七点半行程",null))).append("#");
        sb.append("十八点行程，").append(getTianmaiActionContent(new TianmaiIntent("十八点行程",null))).append("#");
        sb.append("十九点行程，").append(getTianmaiActionContent(new TianmaiIntent("十九点行程",null))).append("#");
        sb.append("二十点半行程，").append(getTianmaiActionContent(new TianmaiIntent("二十点半行程",null))).append("#");
        sb.append("二十一点行程，").append(getTianmaiActionContent(new TianmaiIntent("二十一点行程",null))).append("#");
        return sb.toString();
    }

    public static boolean isIoTCmdFromSpeech(String speech) {
        try {
            if(speech.equals("重新入网"))
            {
                Intent intent_IoT = new Intent(IoTService.MSG_IOT_RESET);
                VoiceApp.getInstance().sendBroadcast(intent_IoT);
                MyTTS.getInstance(null).talk("正在清除网关数据,请重启设备并重新组网");
                return true;
            }
            MLog.i("StringUtils", "check IoT Cmd" + speech);

            // 关闭前退出所有应用
            if(Pattern.compile("关.?电视").matcher(speech).find() && !GuideTip.getInstance().isMusicPlay()){
                MLog.i("StringUtils", "发送home键，关闭电视");
                AppUtil.killTopApp();
            }

            Gson gson = new Gson();
            // String url = "http://119.23.12.86/SmartJX/api/parse?words=";
            String url = "http://smartmovie.skyworthbox.com/SmartTianmai/api/isiot?txt=";
            //translate some word from Hanzi to decimal
            //speech= Util.transferNumber(speech);
            //speech=speech.replace("鱼缸","浴缸");
            //speech=speech.replace("玉刚","浴缸");
            //speech=speech.replace("与缸","浴缸");
            url = url + URLEncoder.encode(speech, "utf-8");
            MLog.i("StringUtils", "" + url);
//            String response = "";
//            String response_jni = SSRService.httpGet(url, response);
            Call call = VoiceApp.getVoiceApp().getOkHttpClient().newCall(new Request.Builder().url(url).build());
            Response response = call.execute();
            if(response==null || !response.isSuccessful() || response.body()==null)return false;
            String resText = response.body().string();
            MLog.i("StringUtils", "response_jni:"+resText);
            // SSRJXGDResultBean ssrjxgdResultBean = gson.fromJson(response_jni,SSRJXGDResultBean.class);
            //
            // LogUtil.log(ssrjxgdResultBean.getDomainName() + ssrjxgdResultBean.getPayLoad() +
            // ssrjxgdResultBean.getTts() + ssrjxgdResultBean.getCode());
            //NluResult nluResult=gson.fromJson(response_jni,NluResult.class);
            //if(nluResult.getOperationCode().equals(DomainOpCode.IOT.getOperationCodeStr()))
            //{
            //todo:IOT things here
            //  IoTParserResult rslt=IoTParser.parser(nluResult);
            //LogUtil.log(rslt.iotCommand.toJsonStr());

            //todo:call iot command here
            IoTParserResult rslt=gson.fromJson(resText,IoTParserResult.class);
            if(rslt!=null&&rslt.iotCommand!=null) {
                if(rslt.iotCommand.isValid()) {
                    MLog.i("StringUtils", "2send broadcast:" + IoTService.MSG_IOT_CMD);
                    Intent intent_IoT = new Intent("action.IoT_CMD");
                    intent_IoT.putExtra("nlu_data", rslt.iotCommand);
                    VoiceApp.getInstance().sendBroadcast(intent_IoT);
                }
                MyTTS.getInstance(null).talk(rslt.replyWord);
                return true;
            }

            //}

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public static boolean isMatches(String text, String regex){
        return Pattern.compile(regex).matcher(text).find();
    }
    public static boolean equals(String text, String text2){
        if(text==null)return text2==null;
        return text.equals(text2);
    }
}
