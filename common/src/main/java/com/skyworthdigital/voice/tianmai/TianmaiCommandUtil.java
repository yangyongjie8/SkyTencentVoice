package com.skyworthdigital.voice.tianmai;

import java.util.regex.Pattern;

/**
 * 天脉应用的命令
 * Created by Ives 2019/3/1
 */
public class TianmaiCommandUtil {
    public static final String PACKAGENAME_LELINGQINGZHI = "com.wisdomaic.hrv";

    // 获取乐龄情志对应指令编号，不匹配其指令则返回-1
    public static int getQingzhiCommandCode(String command){
        if(Pattern.compile("(注册|登记|添加|新增)(账户|账号)?").matcher(command).find())return 2001;
        if(Pattern.compile("(登录|登陆|登入)|((更换|切换|改变)(账户|账号))").matcher(command).find())return 2002;
        if(Pattern.compile("(节律|节拍.?|节奏|速度)(调整|调节|改变|更改)").matcher(command).find())return 3001;
        if(Pattern.compile("(调整|调节|改变|更改)(呼吸|节律|节拍.?|节奏|速度)").matcher(command).find())return 3001;
        if(Pattern.compile("配置|^设置$|设置界面").matcher(command).find())return 4001;
        if(Pattern.compile("(开始|打开)(呼吸)?(训练)").matcher(command).find())return 5001;//5011
        if(Pattern.compile("(反馈|提交).{0,4}(结果|效果|评价|点评|记录|感受)").matcher(command).find())return 6001;//6011
        if(Pattern.compile("(结果|效果|评价|点评|记录|感受)(反馈|提交)").matcher(command).find())return 6001;
        if(Pattern.compile("(修改|更改|变更|更新|编辑)(账号|账户)").matcher(command).find())return 2019;
        if(Pattern.compile("(账号|账户)(修改|更改|变更|更新|编辑)").matcher(command).find())return 2019;
        if(Pattern.compile("^登录|登陆|登入|进入|切换$").matcher(command).find())return 2023;
        if(Pattern.compile("(色调|涉钓|射雕|颜色|皮肤)(调整|设置)").matcher(command).find())return 4011;
        if(Pattern.compile("(调整|设置)(色调|涉钓|射雕|颜色|皮肤)").matcher(command).find())return 4011;
        if(Pattern.compile("(调整|设置|更改|换一个|换)(背景)?(音乐)").matcher(command).find())return 4012;
        if(Pattern.compile("(背景)?(音乐)(调整|设置|换一个)").matcher(command).find())return 4012;
        if(Pattern.compile("(引导语)(调整|设置)").matcher(command).find())return 4014;
        if(Pattern.compile("(调整|设置|换一个).{0,4}(引导语)").matcher(command).find())return 4014;
        if(Pattern.compile("(查询|查找|搜索)报告").matcher(command).find())return 6016;
        if(Pattern.compile("报告(查询|查找|搜索)").matcher(command).find())return 6016;
        if(Pattern.compile("历史报告|以前的报告").matcher(command).find())return 6012;//不需要？
        if(Pattern.compile("打开第.{1,2}个").matcher(command).find())return 6017;

        return -1;
    }

}
