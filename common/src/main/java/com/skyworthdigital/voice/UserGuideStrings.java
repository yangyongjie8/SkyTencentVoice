package com.skyworthdigital.voice;


import com.skyworthdigital.voice.common.utils.Utils;

import java.util.ArrayList;


public class UserGuideStrings {
    //private static String[] guides1 = "恐怖电影，喜剧电影，悬疑电影，动作电影，最热电影，最新综艺，记录片，偶像电视剧，枪战电影，电视剧".split("，");
    //private static String[][] guides1_all = new String[][]{guides1};

    private static String[] guides2_usermanual = "怎么使用，如何使用，功能介绍，使用说明，功能说明".split("，");
    private static String[] guides2_channel_list = "频道查看，频道推荐，查看频道，推荐频道".split("，");
    private static String[] guides2_video_list = "节目推荐，节目查看，标签推荐，查看标签，电影的标签，综艺的推荐，纪录片的标签".split("，");
    private static String[] guides2_type_list = "类型推荐，类型查看，电影的类型，综艺的类型，纪录片的类型".split("，");
    //private static String[] guides2_wakeup = "怎么唤醒，如何唤醒，唤醒介绍，唤醒说明".split("，");
    private static String[] guides2_my_program = "我的收藏，播放记录".split("，");
    private static String[][] guides2_all = new String[][]{guides2_usermanual, guides2_channel_list,
            guides2_video_list, guides2_type_list, guides2_my_program
    };

    private static String[] guides3 = "动画片，综艺，电视剧，纪录片，电影".split("，");
    //private static String[][] guides3_all = new String[][]{guides3};

    private static String[] guides4_Movie = "枪战片，科幻大片，爆笑喜剧，欧洲经典电影，浪漫爱情电影，动作大片，动画电影，剧情伦理".split("，");
    private static String[] guides4_Zongyi = "金牌综艺，真人选秀综艺".split("，");
    private static String[] guides4_Child = "小猪佩奇，动画片，喜羊羊，迪士尼动画".split("，");
    private static String[] guides4_Dianshiju = "热门网剧，偶像剧，都市情感剧，军旅谍战，古装片，神话仙侠，家庭生活电视剧，搞笑喜剧，悬疑片，年代传奇电视剧，民国往事，乡村生活电视剧，美剧，经典影片".split("，");
    private static String[] guides4_Jilupian = "舌尖美食记录片，自然地理记录片，科学探秘记录片，罪案刑侦记录片，军事记录片，历史记录片，文艺记录片".split("，");
    //private static String[][] guides4_all = new String[][]{guides1, guides4_Movie, guides4_Zongyi,
    //        guides4_Child, guides4_Dianshiju, guides4_Jilupian
    //};
    private static String[] guides2_search_key = "喜剧，恐怖，科幻，动作，周星驰，刘德华，小猪佩奇，喜洋洋".split("，");
    private static String[] guides5_Movie = "喜剧，动作，爱情，悲剧，科幻，动画，恐怖，枪战，犯罪，惊悚，悬疑，家庭，奇幻，魔幻，战争，青春，古装，其他类型".split("，");
    private static String[] guides5_Zongyi = "伦理，音乐，脱口秀，职场，少儿，美食，相亲，曲艺，选秀，情感，晚会，时尚，游戏，旅游，搞笑，真人秀，访谈，其他类型".split("，");
    private static String[] guides5_Child = "动画，少儿综艺，早教益智视频".split("，");
    private static String[] guides5_Dianshiju = "恐怖，历史，偶像剧，科幻，都市剧，谍战剧，网络电视剧，喜剧，武侠，言情电视剧，其他类型，古装，农村电视剧，动作，犯罪，剧情，神话，军旅，宫廷，家庭，穿越，刑侦，悬疑，青春".split("，");
    private static String[] guides5_Jilupian = "军事纪录片，刑侦纪录片，美食纪录片，地理纪录片，科学纪录片，自然纪录片，社会纪录片，文化纪录片，探索纪录片，历史纪录片，旅游纪录片".split("，");
    private static String[][] guides5_all = new String[][]{guides5_Movie, guides5_Zongyi,
            guides5_Child, guides5_Dianshiju, guides5_Jilupian
    };

    private static String[] guides6_play = "第几个，看第几个，播放，播放第几个，放第几个".split("，");
    private static String[] guides6_summary = "第*个简介，第*个介绍，第*部是讲什么的，第*个剧情简介，第*个剧情介绍".split("，");
    private static String[] guides6_volume_add = "声音大点，声音太小了，音量调大，大声点，音量增大，调大声音".split("，");
    private static String[] guides6_volume_lower = "声音小点，小声一点，声音太大了，音量调小，声音减少，音量减小".split("，");
    private static String[] guides6_page = "连续翻页，下一页，还有吗，换一批，翻页，上一页".split("，");
    private static String[] guides6_favourite = "收藏第几个，打开收藏，进入收藏，收藏记录，追剧收藏，我收藏的".split("，");
    private static String[] guides6_history = "观看历史，播放历史，观看记录".split("，");
    private static String[][] guides6_all = new String[][]{guides6_play, guides6_summary, guides6_volume_add, guides6_volume_lower, guides6_page, guides6_favourite, guides6_history};

    private static String[] guides7_quit = "退回主页，返回，返回首页".split("，");
    private static String[] guides7_play = "播放，播放第n集，播放第n期".split("，");
    private static String[] guides7_favorite = "收藏，取消收藏".split("，");
    private static String[] guides7_openvip = "开通VIP，开VIP".split("，");
    private static String[][] guides7_all = new String[][]{guides7_quit, guides7_play, guides7_favorite, guides7_openvip};


    private static String[] guides8_play = "播放，继续看".split("，");
    private static String[] guides8_volume_add = "音量增大，声音小点，大声点".split("，");
    private static String[] guides8_volume_lower = "音量减小，声音大点，小声点".split("，");
    private static String[] guides8_volume_0 = "音量最小，静音，闭嘴".split("，");
    //private static String[] guides8_volume_adjust = "音量x%，声音x%".split("，");
    private static String[] guides8_pause = "暂停，停止播放".split("，");
    private static String[] guides8_quit = "退出，返回，不想看了".split("，");
    //private static String[] guides8_seek_to = "快进到x分钟，前进到x小时".split("，");
    private static String[] guides8_seek = "快进x分钟，前进半分钟".split("，");
    private static String[] guides8_back_to = "后退到x分钟，快退到x小时".split("，");
    private static String[] guides8_back = "后退x分钟，快退半分钟".split("，");
    private static String[] guides8_n_program = "播放下一集，播放第x集".split("，");
    private static String[] guides8_quick_play = "快进，拖着看，快速播放".split("，");
    private static String[] guides8_quick_back_play = "快退，倒退，往回看".split("，");
    private static String[] guides8_play_from_head = "从头播放，重新播放，从头看".split("，");
    //private static String[] getActivity_play_control_seek_to_percent = "快进到x%，进度到x%，拖到x%".split("，");
    private static String[] guides8_play_select_episode = "选集，进入选集，我要选集".split("，");
    private static String[] guides8_play_open_summary = "剧情简介，剧情介绍".split("，");
    private static String[] guides8_play_play_settings = "播放设置，我要设置，设置播放".split("，");
    private static String[] guides8_play_favorite = "收藏，取消收藏".split("，");
    private static String[] guides8_play_open_vip = "开通VIP".split("，");
    private static String[][] guides8_all = new String[][]{guides8_play, guides8_volume_add,
            guides8_volume_lower, guides8_volume_0,
            guides8_pause, guides8_quit, /*guides8_seek_to,*/
            guides8_seek, guides8_back_to, guides8_back,
            guides8_n_program, guides8_quick_play, guides8_quick_back_play,
            guides8_play_from_head, /*getActivity_play_control_seek_to_percent,*/ guides8_play_select_episode,
            guides8_play_open_summary, guides8_play_play_settings, guides8_play_favorite, guides8_play_open_vip
    };

    private static String[] guides_music_type = "经典老歌，伤感的歌，情歌，华语歌，粤语歌，英文歌，日语歌，法语歌，睡前歌曲，来一首神曲，胎教歌曲，旅行歌曲，瑜伽歌曲，动感的歌，广场舞歌曲，酒吧歌曲，婚礼歌曲，洗澡歌曲，聚会歌曲，起床歌曲，休闲歌曲，放松歌曲，怀旧粤语歌曲，零零后歌曲，七零后歌曲，校园歌曲，学习歌曲，欢快的歌曲，甜蜜的歌曲，轻音乐，感动的歌，民谣，摇滚歌曲，婚礼歌曲，洗澡歌曲，爵士歌曲，古典歌曲，民歌歌曲，乡村歌曲，草原风的歌，疗伤歌曲，古筝歌曲，萨克斯歌曲".split("，");

    private static String[] guides_music_singer = "周杰伦的歌，TFBOYS的歌，李荣浩的歌，薛之谦的歌，郁可唯的歌，萧敬腾的歌，王力宏的歌，谭维维的歌，韩红的歌，王菲的歌，王源的歌，林俊杰的歌，张杰的歌，大张伟的歌，邓紫棋的歌，庄心研的歌，凤凰传奇的歌，陈奕迅的歌，刘若英的歌，张靓颖的歌，孙燕姿的歌，莫文蔚的歌，BEYOND的歌".split("，");

    private static String[] guides_music_song = "我想听《我们不一样》，我想听《远走高飞》，西海情歌，告白气球，一千个伤心的理由，邓紫棋的泡沫，外婆的澎湖湾，我想听《潇洒走一回》，我想听《匆匆那年》，我想听《开始懂了》，我想听《至少还有你》，我想听《为你我受冷风吹》，我想听《千年游》，我想听《最熟悉的陌生人》，我想听《影子》，我想听《怒放的生命》，我想听《一人饮酒醉》，我想听《新贵妃醉酒》，我想听《摩天大楼》，我想听朴树的《平凡之路》，我想听《成都》，我想听《阿刁》，我想听《空空如也》，我想听《纸短情长》，周华健的《朋友》，我想听《富士山下》，我想听《江南》，我想听《简单爱》，我想听《后来》，我想听《海阔天空》，我想听《真的爱你》".split("，");
    private static String[] guides_music_control = "暂停/播放，换一首，下一首，快进30秒，单曲循环播放，顺序播放，随机播放，收藏，取消收藏，上一首，再来一首，退出播放，退出ｑｑ音乐，播放下一首".split("，");

    private static String[] guides_music_other = "网络歌曲排行榜，打开内地音乐排行榜，打开欧美音乐排行榜，打开港台音乐排行榜，k歌金曲排行榜，日本公信榜，新歌排行榜".split("，");

    //private static String[] guides_other1 = "，NBA排名，英超最近的比赛，湖人队得分，C罗是谁，梅西百科".split("，");
    //private static String[] guides_other2 = "新闻头条，娱乐新闻，军事新闻，国际新闻".split("，");

    private static String[][] guide_music_control = new String[][]{guides_music_control, guides7_quit,
    };
    private static String[][] guide_music_all = new String[][]{guides_music_type, guides_music_singer,
            guides_music_song, guides_music_other
    };

    private static String[] guide_system = "大盘指数，创维数字股票，7+8=多少，天气，明天会下雨吗，打开购物商城，打开网络连接，打开轮播，诊断网络，媒体中心，今天雾霾严重吗，打开应用市场，打开购物商城，今天几号，联系我们，售后热线，电视淘宝，腾讯视频，打开设置，一键优化，恢复出厂，网络连接，调整显示区域，我的收藏，打开收藏，提醒我明天八点跑步，把贝加尔湖畔设为闹铃，删除所有闹钟，查看闹钟，产品信息".split("，");

    private static String[] guide_miaodong = "周星驰电影，谍中谍，宫心计，朗读者，我想看电视，中央一台，浙江卫视电视节目单，西游记在哪个频道播，NBA最近有什么比赛，熊出没，粉红猪小妹，体育频道，白百合演的电影，播放美人鱼，徐克导演的电影，胡歌演的，佘诗曼演的，王牌对王牌，爱情保卫战，百变马丁，大耳朵图图，延禧攻略，你迟到的许多年，橙红年代，黄渤电影，泡沫之夏，唐人街探案，电影红海行动，陈伟霆的电视剧，跨界歌王，赵丽颖，周星驰百科，红梦楼百科，秋收起义百科，清华大学百科，爱情相关的，赛车的片子，喜剧，科幻电影，机票查询，火车票查询，NBA排名，英超最近的比赛，湖人队得分，C罗是谁，梅西百科，我想听新闻，单田芳的评书，陆游的诗，来几首唐诗，机票查询，火车票查询，收听北京的广播，听广播，我想听京剧，播放《巨齿鲨》，播放《大师兄》，播放《欢乐颂》第一集，播放《琅琊榜》第一集，牛肉可以做什么菜，红烧肉，豆腐的做法，回锅肉，毛血旺，锅包肉".split("，");

    private static String[] guide_recipe = "牛肉的做法，红烧肉的做法，豆腐的做法，酸辣土豆丝，红烧茄子，回锅肉，毛血旺，锅包肉".split("，");

    private static String[][] guide_home_control = new String[][]{guide_system, guides6_volume_lower, guides6_volume_add, guides7_quit,
    };

    private static String[][] guide_home_all = new String[][]{guide_miaodong};

    private static String[] guides_search_cmd = "换一批，第*个剧情介绍，下一页，上一页，播放第*个，播放".split("，");
    private static String[][] guide_search_control = new String[][]{
            guides7_quit, guides_search_cmd};
    private static String[] guides_live = "浙江卫视，中央一台，中央八台，中央新闻频道，东方卫视，CCTV5，财经频道，中央科教频道，中央戏曲频道，中央戏曲频道，江苏频道，北京卫视，中央少儿频道，浙江少儿频道，社会与法频道，四川卫视".split("，");
    private static String[] guides_live_cmd = "换台，上一个台，前一个台，下一个频道，下一个台，CCTV5，财经频道，中央科教频道，中央戏曲频道，中央戏曲频道，江苏频道，北京卫视，中央少儿频道，浙江少儿频道，社会与法频道，四川卫视".split("，");
    private static String[][] guide_live_control = new String[][]{guides_live_cmd, guides6_volume_lower, guides6_volume_add, guides7_quit,
    };
    private static String[][] guide_live_all = new String[][]{guides_live};
    private static String[] guides_media_cmd = "暂停/播放，下一集，快退*分钟，收藏，播放第*集，上一集，快进二十分钟，最后一集，倒数第二集，返回首页，收藏".split("，");
    private static String[][] guide_media_control = new String[][]{guides_media_cmd, guides7_quit};

    private static String[] guides_audio = "收听北京的广播，我想听中国之声，深圳广播，戏剧广播，广播音乐之声，潮州戏曲广播，单田芳的评书，交通广播，北京新闻广播，我想听郭德纲相声，我要听睡前故事，我想听黄梅戏，我想听武侠小说，我想听戏曲，我想听海绵宝宝，我想听京剧，我想听盗墓笔记，我想听小品，我想听红楼梦，我想听小说，我想听百炼成仙，我想听海绵宝宝，讲段评书来听听，我想听言情小说，我想听不差钱，我想听佩奇的故事，我想听三侠五义，我想听水浒传，我想听灰姑娘的故事，我想听图图的故事，我想彼得兔的故事".split("，");
    //public static String[] xxx = "".split("，");
    private static String[] guides_audio_cmd = "停止播放，播放停止，退出播放，退出，暂停/播放，换一个，下一个".split("，");
    private static String[][] guide_audio_control = new String[][]{guides_audio_cmd, guides6_volume_lower, guides6_volume_add, guides7_quit};

    private static String[] getAllTags(String[][] strings) {
        int tagCount = 0;
        int maxLength = strings[0].length;
        for (String[] strings1 : strings) {
            if (strings1 != null) {
                tagCount += strings1.length;
                if (strings1.length > maxLength) {
                    maxLength = strings1.length;//15
                }
            }
        }
        String[] allTags = new String[tagCount];
        int c = 0;
        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < strings.length; j++) {
                if (i < strings[j].length && c < tagCount) {
                    allTags[c] = strings[j][i];
                    //LogUtil.log(c + ":" + strings[j][i] + " " + j + " " + i);
                    c++;
                }
            }
        }
        return allTags;
    }

    public static ArrayList<String> getUserGuide(String[] tags, int n, boolean random) {
        if (tags == null || tags.length == 0 || n <= 0)
            return null;
        if (n >= tags.length) {
            random = false;
        }
        ArrayList<String> list = new ArrayList<>();
        if (random) {
            int[] indexs = Utils.getRandomNumbers(0, tags.length, n);

            for (int i = 0; i < indexs.length; i++) {
                list.add(tags[indexs[i]]);
            }
        } else {
            for (int i = 0; i < (tags.length > n ? n : tags.length); i++) {
                list.add(tags[i]);
            }
        }
        return list;
    }

    public static final int TYPE_MUSIC = 9;
    private static final int TYPE_MUSIC_CONTROL = 10;
    public static final int TYPE_HOME = 11;
    private static final int TYPE_HOME_CONTROL = 12;
    public static final int TYPE_LIVE = 13;
    private static final int TYPE_LIVE_CONTROL = 14;
    public static final int TYPE_SEARCH = 4;
    public static final int TYPE_SEARCH_CONTROL = 16;
    public static final int TYPE_MEDIA_CONTROL = 8;
    private static final int TYPE_MUSIC_SINGER = 19;
    private static final int TYPE_MUSIC_TYPE = 20;
    private static final int TYPE_MUSIC_SONG = 21;
    private static final int TYPE_SEARCH_HIGHWORD = 22;
    //private static final int TYPE_SEARCH_TYPE = 23;
    public static final int TYPE_AUDIO = 30;
    private static final int TYPE_AUDIO_CONTROL = 31;

    public static String[] getTagsFrom(int index) {
        String[] allTags = null;

        if (index == TYPE_MEDIA_CONTROL) {
            allTags = getAllTags(guide_media_control);
        } else if (index == TYPE_SEARCH) {
            allTags = guides3;//getAllTags(guides4_all);
        } else if (index == TYPE_SEARCH_HIGHWORD) {
            allTags = guides2_search_key;
        } else if (index == TYPE_SEARCH_CONTROL) {
            allTags = getAllTags(guide_search_control);
        } /*else if (index == TYPE_SEARCH_TYPE) {
            allTags = guides3;
        } */ else if (index == TYPE_MUSIC) {
            allTags = getAllTags(guide_music_all);
        } else if (index == TYPE_MUSIC_CONTROL) {
            allTags = getAllTags(guide_music_control);
        } else if (index == TYPE_MUSIC_SINGER) {
            allTags = guides_music_singer;
        } else if (index == TYPE_MUSIC_TYPE) {
            allTags = guides_music_type;
        } else if (index == TYPE_MUSIC_SONG) {
            allTags = guides_music_song;
        } else if (index == TYPE_HOME) {
            allTags = guide_miaodong;//getAllTags(guide_home_all);
        } else if (index == TYPE_HOME_CONTROL) {
            allTags = getAllTags(guide_home_control);
        } else if (index == TYPE_LIVE) {
            allTags = getAllTags(guide_live_all);
        } else if (index == TYPE_LIVE_CONTROL) {
            allTags = getAllTags(guide_live_control);
        } else if (index == TYPE_AUDIO) {
            allTags = guides_audio;
        } else if (index == TYPE_AUDIO_CONTROL) {
            allTags = getAllTags(guide_audio_control);
        }
        return allTags;
    }


    public static ArrayList<String> generateUserGuide(int type) {
        ArrayList<String> userGuide = new ArrayList<>();
        switch (type) {
            case TYPE_MUSIC:
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_MUSIC_TYPE), 1, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_MUSIC_SINGER), 1, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_MUSIC_SONG), 1, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_MUSIC_CONTROL), 3, true));
                break;
            case TYPE_LIVE:
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_LIVE), 2, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_LIVE_CONTROL), 4, true));
                break;
            case TYPE_SEARCH:
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_SEARCH_HIGHWORD), 2, true));
                //userGuide.addAll(getUserGuide(getTagsFrom(TYPE_SEARCH_TYPE), 1, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_SEARCH), 1, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_SEARCH_CONTROL), 3, true));
                break;
            case TYPE_AUDIO:
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_AUDIO), 3, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_AUDIO_CONTROL), 3, true));
                break;
            case TYPE_MEDIA_CONTROL:
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_MEDIA_CONTROL), 6, true));
                break;
            case TYPE_HOME:
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_SEARCH), 1, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_MUSIC), 1, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_HOME), 2, true));
                userGuide.addAll(getUserGuide(getTagsFrom(TYPE_HOME_CONTROL), 2, true));
            default:
                break;
        }
        return userGuide;
    }
}
