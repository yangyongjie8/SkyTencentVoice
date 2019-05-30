package com.skyworthdigital.voice.dingdang.control.wakeup;


import com.skyworthdigital.voice.common.AbsWakeup;
import com.skyworthdigital.voice.common.IWakeupResultListener;
import com.skyworthdigital.voice.tencent_module.TxWakeup;

public final class MyWakeup {

    /**
     * 获取实例
     */
    public static AbsWakeup getInstance(IWakeupResultListener wkresultlistener) {
        return TxWakeup.getInstance(wkresultlistener);
    }

}
