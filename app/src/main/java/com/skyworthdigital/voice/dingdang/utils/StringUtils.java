package com.skyworthdigital.voice.dingdang.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final String TAG = "StringUtils";

    //public static final String RAW_TYPE = "raw";

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
     * 根据用户语音原话，提取是否是下一集指令
     */
    public static boolean isNextCmdFromSpeech(String speech) {
        String regex = "^(下一级|播放下一集)$";
        return Pattern.matches(regex, speech);
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
}
