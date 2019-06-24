package com.skyworthdigital.voice.baidu_module.duerbean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.skyworthdigital.voice.dingdang.utils.GsonUtils;


/**
 * nlu字段
 * Created by SDT13227 on 2017/5/26.
 */

public class Nlu {
    @SerializedName("domain")
    private String mDomain;

    @SerializedName("intent")
    private String mIntent;

    @SerializedName("slots")
    private Object mSlots;


    public String getDomain() {
        return mDomain;
    }

    public String getIntent() {
        return mIntent;
    }

    public Slots getSlots() {
        Gson gson = new Gson();
        try {
            String gsonstr = gson.toJson(mSlots);
            Slots slot = GsonUtils.parseResult(gsonstr, Slots.class);
            return slot;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSlotsString() {
        Gson gson = new Gson();
        try {
            String gsonstr = gson.toJson(mSlots);
            return gsonstr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "domain:" + mDomain + "|intent:" + mIntent + "|slots:" + mSlots;
    }
}
