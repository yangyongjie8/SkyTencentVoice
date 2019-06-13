package com.skyworthdigital.voice.beesearch;

/**
 * Created by SDT03046 on 2018/6/7.
 */

public class BeeCommand {
    public static final int OPT_CODE_SUMMARY = 0;           // 简介查看
    public static final int OPT_CODE_SEEK_FORWARD_TO = 1;   // 快进到
    public static final int OPT_CODE_SEEK_REVERSE_TO = 2;   // 快退到
    public static final int OPT_CODE_SEEK_FORWARD = 3;      // 快进
    public static final int OPT_CODE_SEEK_REVERSE = 4;      // 快退
    public static final int OPT_CODE_VOLUME_0 = 5;          // 静音
    public static final int OPT_CODE_VOLUME_UNMUTE = 40;    // unmute
    public static final int OPT_CODE_VOLUME_ADD = 6;        // 音量增大
    public static final int OPT_CODE_VOLUME_LOWER = 7;      // 音量减小
    public static final int OPT_CODE_VOLUME_ADJUST = 8;     // 音量调整
    public static final int OPT_CODE_VOLUME_100 = 9;        // 音量最大
    public static final int OPT_CODE_PLAY_N = 10;           // 播放第n个
    public static final int OPT_CODE_PLAY_CONTINUE = 11;    // 继续播放
    public static final int OPT_CODE_PLAY_LAST = 12;        // 播放上一集
    public static final int OPT_CODE_PAUSE = 13;            // 暂停
    public static final int OPT_CODE_LAST_PAGE = 14;        // 上一页
    public static final int OPT_CODE_HISTORY = 15;          // 打开历史
    public static final int OPT_CODE_FULLSCREEN = 16;       // 全屏播放
    public static final int OPT_CODE_QUIT = 17;             // 结束对话
    public static final int OPT_CODE_RETURN = 18;           // 退出播放
    public static final int OPT_CODE_QUICK_PLAY = 19;       // 快进播放
    public static final int OPT_CODE_YES = 20;              // 是的
    public static final int OPT_CODE_NO = 21;               // 不是
    public static final int OPT_CODE_QUICK_REVERSE = 22;    // 倒退一下
    public static final int OPT_CODE_FAVOURITE = 23;        // 打开收藏
    public static final int OPT_CODE_SEEK_0 = 24;           // 从头播放
    public static final int OPT_CODE_SEEK_PERCENT_TO = 25;  // 快进到%xx
    public static final int OPT_CODE_CAN_YOU_DO = 26;       // 你能干什么
    public static final int OPT_CODE_CHANNELS = 27;         // 有些什么频道
    public static final int OPT_CODE_PROGRAMS = 28;         // 有些什么好看的节目
    public static final int OPT_CODE_TYPES = 29;            // 你有什么类型可以看
    public static final int OPT_CODE_HOW_TO_WAKEUP = 30;    // 怎么唤醒
    public static final int OPT_CODE_ADD_FAVORITE = 32;     // 收藏
    public static final int OPT_CODE_OPENVIP = 33;      // 开通VIP
    public static final int OPT_CODE_ADD_FAVORITE_N = 36;      // 收藏第n个
    public static final int OPT_CODE_DEL_FAVORITE_N = 37;      // 收藏第n个
    public static final int OPT_CODE_HELP = 41;      // 帮助
    public static final int OPT_CODE_PLAYBYNAME = 42;      // 帮助
    public String action;
    public BeeCommandArgs args;
    public int cmdid = -1;

    public static class BeeCommandArgs {
        public String status;
        public int value = -1;
        public String position;
        public String duration;
        public int percent;
        public String name;

        @Override
        public String toString() {
            return "status=" + status + ", value=" + value + ", position=" + position +
                    ", duration=" + duration + ", percent=" + percent + ", positionpercent=" + position + ", name=" + name;
        }
    }

    public int getCmdid() {
        return cmdid;
    }

    public String getAction() {
        return action;
    }

    public BeeCommandArgs getArgs() {
        return args;
    }
}
