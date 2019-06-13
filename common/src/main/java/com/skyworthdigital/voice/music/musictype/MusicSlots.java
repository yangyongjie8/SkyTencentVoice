package com.skyworthdigital.voice.music.musictype;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频搜索支持的类型，同时格式转换成我们需要的
 */

public class MusicSlots implements Serializable {

    private static final long serialVersionUID = 1L;
    public String mSinger;
    public String mAlbum;
    public String mSong;

    public List<String> mType=new ArrayList<>();
}
