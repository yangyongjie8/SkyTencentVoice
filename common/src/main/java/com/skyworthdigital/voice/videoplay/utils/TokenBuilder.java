package com.skyworthdigital.voice.videoplay.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


class TokenBuilder {

    private static final Integer MODID = 1281;
    private static final Integer PARTNETERID = 89201951;
    private static final int I71_ID = 11717171;

    String getToken() {
        String src = MODID * PARTNETERID + "i71";
        Long t = new java.util.Date().getTime();// 计算token时的当前时间戳（毫秒），如
        // 1427120889090;
        String secretkey = "i71" + t;
        String iv = I71_ID + "" + (MODID + I71_ID);
        String token0 = encrypt(src, secretkey, iv);// 采用AES和BASE64加密（加密方法参考下面方法）;
        return (t + "" + token0 + "_" + PARTNETERID);
    }

    private String encrypt(String src, String secretkey, String iv) {
        byte[] encrypted;
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(secretkey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivP = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivP);
            encrypted = cipher.doFinal(src.getBytes());
        } catch (Exception e) {
            return null;
        }
        try {
            return URLEncoder.encode(new String(Base64.encode(encrypted, 0)), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

