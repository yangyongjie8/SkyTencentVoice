package com.skyworthdigital.voice.common;

import java.util.HashSet;
import java.util.Set;

/**
 * 管理语音播报tag的类。
 * tag是播放声音或者显示对话框时创建的一个对应的标签，用于在需要取消显示/停止播放时做校验。
 * 这个tag只在需要做校验时才需要创建并返回给调用方，比如第三方应用。app自身的操作暂时全部没有tag.
 * Created by Ives 2019/2/27
 */
public class VoiceTagger {
    private static final Set<String> container = new HashSet<>();

    /**
     * 添加并返回一个新tag，新增一个播放时按需调用，播放完毕后要remove
     * @return
     */
    public static String makeTag(){
        String tag = generateTag();
        while (container.contains(tag)){
            tag = generateTag();
        }
        container.add(tag);
        return tag;
    }
    public static void removeTag(String tag){
        container.remove(tag);
    }

    public static void removeAllTag(){
        container.clear();
    }

    /**
     * 判断该tag是否有效，即表示在需要播放的内容里是否包含该tag对应的内容。
     * @param tag
     * @return
     */
    public static boolean isValid(String tag){
        return container.contains(tag);
    }

    private static String generateTag(){
        return randomStr(8);
    }

    /**
     * 生成任何长度的随机字符串
     * @param length
     * @return
     */
    private static String randomStr(int length) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < length; j++) {
            //生成一个33-126之间的int类型整数
            int intValL = (int) (Math.random() * 93 + 33);
            sb.append((char) intValL);
        }
        return sb.toString();
    }
}
