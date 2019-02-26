package com.skyworthdigital.voice.dingdang.utils;

import android.content.Context;

import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.control.tts.MyTTS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

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
        MyTTS.getInstance(null).speak("", tip);
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
     * 功能：提取字符串中的数字
     */
    public static String getStringNumbers(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static Map<String, String> getUrl(String str) {
        String reg = "(?i)<a[^>]+href[=\"\']+([^\"\']+)[\"\']?[^>]*>((?!<\\/a>)[\\s\\S]*)<\\/a>";
        Map<String, String> ret = new HashMap<>();
        //String str = "<a href=\"url\">text</a> ";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        while (m.find()) {
            ret.put(m.group(1), m.group(2));
            //MLog.d("geturl", "链接: %s, 内容: %s" + m.group(1) + m.group(2));
        }
        return ret;
    }

    public static String removeUrl(String str) {
        String reg = "(?i)<a[^>]+href[=\"\']+([^\"\']+)[\"\']?[^>]*>((?!<\\/a>)[\\s\\S]*)<\\/a>";
        Map<String, String> ret = new HashMap<>();
        //String str = "<a href=\"url\">text</a> ";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        while (m.find()) {
            ret.put(m.group(1), m.group(2));
            //MLog.d("geturl", "链接: %s, 内容: %s" + m.group(1) + m.group(2));
        }

        int start, end;
        StringBuilder substr = new StringBuilder();
        if (ret.size() > 0) {
            start = str.indexOf("<a");
            end = str.indexOf("</a>");
            //MLog.d("geturl", start + " ~ " + end);
            if (start < end) {
                end += "</a>".length();
            }
            substr.append(str.substring(0, start));
            substr.append(str.substring(end));
        }

        //MLog.d("geturl", substr.toString());
        Set<Map.Entry<String, String>> set = ret.entrySet();
        // 遍历键值对对象的集合，得到每一个键值对对象
        for (Map.Entry<String, String> me : set) {
            // 根据键值对对象获取键和值
            String key = me.getKey();
            String value = me.getValue();
            //MLog.d("geturl", key + "---" + value);
            substr.append(value);
        }
        return substr.toString();
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
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
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

    //去除字符串中的标点
    public static String format(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。， ·、？|-]", "");
        return str;
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


    public static String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 E", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }
}
