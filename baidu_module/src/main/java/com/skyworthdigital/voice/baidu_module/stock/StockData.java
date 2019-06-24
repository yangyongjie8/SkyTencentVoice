package com.skyworthdigital.voice.baidu_module.stock;

import java.util.ArrayList;
import java.util.List;

/**
 * 股票data字段
 * Created by SDT13227 on 2017/5/26.
 */

class StockData {
    private String open_resource_name;
    private List<StockResult> result = new ArrayList<StockResult>();


    String getOpen_resource_name() {
        return open_resource_name;
    }

    StockResult getResultFirst() {
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }
}
