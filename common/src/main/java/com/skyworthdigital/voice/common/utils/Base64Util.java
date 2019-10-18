package com.skyworthdigital.voice.common.utils;

import android.util.Base64;

import java.nio.charset.Charset;

public class Base64Util {

    /**
     * <p>
     * BASE64字符串解码为普通字符串
     * </p>
     *
     * @param base64
     * @return
     */
    public static String decode(String base64) {
        return new String(Base64.decode(base64, Base64.DEFAULT), Charset.forName("UTF-8"));
    }

    /**
     * <p>
     * 普通字符串编码为BASE64字符串
     * </p>
     *
     * @param string
     * @return
     */
    public static String encode(String string) {
        return new String(Base64.encode(string.getBytes(Charset.forName("UTF-8")), Base64.DEFAULT), Charset.forName("UTF-8"));
    }

}
