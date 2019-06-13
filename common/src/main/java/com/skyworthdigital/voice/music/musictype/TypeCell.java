package com.skyworthdigital.voice.music.musictype;

import java.io.Serializable;
import java.util.List;

public class TypeCell implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> cmds;
    private String type;
    private int id;


    public List<String> getCmds() {
        return cmds;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "cmds:" + cmds + "|intent:" + type + "|app:" + id;
    }
}