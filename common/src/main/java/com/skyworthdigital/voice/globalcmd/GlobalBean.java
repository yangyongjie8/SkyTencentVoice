package com.skyworthdigital.voice.globalcmd;

import java.util.ArrayList;
import java.util.List;

public class GlobalBean {

    private String appname;
    private String pkgname;
    private int version;
    private List<Cell> list = new ArrayList<Cell>();


    public String getAppname() {
        return appname;
    }
    public String getPkgname() {
        return pkgname;
    }
    public List<Cell> getCellList() {
        return list;
    }

}