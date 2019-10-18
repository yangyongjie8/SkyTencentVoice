package com.skyworthdigital.voice.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {

    /**
    * 利用java原生的类实现SHA256加密
    * @param str 加密后的报文
    * @return
    */
    public static byte[] sha256(String str){
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
    * 将byte转为16进制
    * @param bytes
    * @return
    */
    public static String byte2Hex(byte[] bytes){
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
    /**将16进制描述的字符串还原为普通字符串
     * 如"57415a582d4235355359362d5336445435" 还原为："WAZX-B55SY6-S6DT5"
     * */
    public static String hexToStr(String hex){
        byte[] bytes=hexToBytes(hex);
        return new String(bytes);
    }
    /**16进制转byte[]*/
    public static byte[] hexToBytes(String hex){
        int length = hex.length() / 2;
        byte[] bytes=new byte[length];
        for(int i=0;i<length;i++){
            String tempStr=hex.substring(2*i, 2*i+2);//byte:8bit=4bit+4bit=十六进制位+十六进制位
            bytes[i]=(byte) Integer.parseInt(tempStr, 16);
        }
        return bytes;
    }
    public static String strToHex(String str){
        byte[] bytes = str.getBytes();
        return bytesToHex(bytes);
    }
    /**byte[]转16进制*/
    public static String bytesToHex(byte[] bytes){
//        HLog.d("HexUtil", Arrays.toString(bytes));
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<bytes.length;i++){
            int tempI=bytes[i] & 0xFF;//byte:8bit,int:32bit;高位相与.
            String str = Integer.toHexString(tempI);
            if(str.length()<2){
                sb.append(0).append(str);//长度不足两位，补齐：如16进制的d,用0d表示。
            }else{
                sb.append(str);
            }
        }
        return sb.toString();
    }

    public static String byte2Hex2(byte[] bytes){
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length*2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i]&0xFF;
            hexChars[i*2] = hexArray[v>>>4];
            hexChars[i*2+1] = hexArray[v&0x0F];
        }
        return new String(hexChars);
    }/**
     * 字符串转换成十六进制值
     * @param bin String 我们看到的要转换成十六进制的字符串
     * @return
     */
    public static String bin2hex(String bin) {
        return bin2hex(bin.getBytes());
    }
    public static String bin2hex(byte[] bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer();
        byte[] bs = bin;
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }

}
