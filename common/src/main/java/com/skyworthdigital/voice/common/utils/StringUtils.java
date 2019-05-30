package com.skyworthdigital.voice.common.utils;

import com.skyworthdigital.voice.dingdang.utils.MLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ives 2019/5/30
 */
public class StringUtils {

    //去除字符串中的标点
    public static String format(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。， ·、？|-]", "");
        return str;
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
}
