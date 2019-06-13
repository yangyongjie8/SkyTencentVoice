package com.skyworthdigital.voice.globalcmd;
import java.io.Serializable;
import java.util.List;

public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> cmds;
    private Action action;
    private String app;
    private String matchType;


    public List<String> getCmds() {
        return cmds;
    }

    public Action getAction() {
        return action;
    }

    public String getPkgname() {
        return app;
    }

    public String getType(){
        return matchType;
    }

    @Override
    public String toString() {
        return "cmds:" + cmds + "|intent:" + action + "|app:" + app;
    }
}