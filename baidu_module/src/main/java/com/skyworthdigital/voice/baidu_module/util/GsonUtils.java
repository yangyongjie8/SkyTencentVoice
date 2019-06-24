package com.skyworthdigital.voice.baidu_module.util;

import com.google.gson.Gson;
import com.skyworthdigital.voice.baidu_module.duerbean.DuerBean;

/**
 * Created by Ives 2019/6/14
 */
public class GsonUtils {

    public static DuerBean getDuerBean(String json) {
        DuerBean duerBean = null;
        Gson gson = new Gson();
        try {
            duerBean = gson.fromJson(json, DuerBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duerBean;
    }
}
