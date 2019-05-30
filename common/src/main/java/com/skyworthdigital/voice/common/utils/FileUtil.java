package com.skyworthdigital.voice.common.utils;

import android.content.Context;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FileUtil {

    /**
     * 检查目录是否为空
     *
     * @param folder
     * @return
     */
    public static boolean isDirectoryEmpty(File folder) {
        return !folder.exists() || !folder.isDirectory() || folder.list().length <= 0;
    }

    //复制方法
    public static void copy(String src, String des) throws Exception {
        //初始化文件复制
        File file1 = new File(src);
        //把文件里面内容放进数组
        File[] fs = file1.listFiles();
        //初始化文件粘贴
        File file2 = new File(des);
        //判断是否有这个文件有不管没有创建
        if (!file2.exists()) {
            file2.mkdirs();
        }
        //遍历文件及文件夹
        for (File f : fs) {
            if (f.isFile()) {
                //文件
                fileCopy(f.getPath(), des + "\\" + f.getName()); //调用文件拷贝的方法
            } else if (f.isDirectory()) {
                //文件夹
                copy(f.getPath(), des + "\\" + f.getName());//继续调用复制方法      递归的地方,自己调用自己的方法,就可以复制文件夹的文件夹了
            }
        }

    }

    /**
     * 文件复制的具体方法
     */
    private static void fileCopy(String src, String des) throws Exception {
        //io流固定格式
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
        int i = -1;//记录获取长度
        byte[] bt = new byte[2014];//缓冲区
        while ((i = bis.read(bt)) != -1) {
            bos.write(bt, 0, i);
        }
        bis.close();
        bos.close();
        //关闭流
    }

    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context Context 使用CopyFiles类的Activity
     * @param oldPath String  原文件路径  如：/aa
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public static void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            for (String tmp : fileNames)
                Log.i("wyf", "file:" + tmp);
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    Log.i("wyf", "copy:" + newPath + fileName);
                    copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    /**
     * 获取单个文件的MD5值！

     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}
