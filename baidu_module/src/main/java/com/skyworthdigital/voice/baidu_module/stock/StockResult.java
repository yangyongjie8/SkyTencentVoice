package com.skyworthdigital.voice.baidu_module.stock;

/**
 * 股票result字段
 * Created by SDT13227 on 2017/5/26.
 */

class StockResult {
    private String change;
    private String code;
    private String current_price;
    private String name;
    private Object info;

    String getChange() {
        return change;
    }

    String getCode() {
        return code;
    }

    String getCurrent_price() {
        return current_price;
    }

    Object getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "change:" + change + "|code:" + code;
    }
}
