package com.skyworthdigital.voice.dingdang.control.recognization;

import com.skyworthdigital.voice.common.AbsRecognizer;
import com.skyworthdigital.voice.common.IRecogListener;

/**
 * Created by Ives 2019/5/29
 */
public class MyRecognizer {

    public static AbsRecognizer getInstance(IRecogListener recogListener) {
        return TxRecognizer.getInstance(recogListener);
    }
}
