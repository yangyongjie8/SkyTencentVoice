package com.skyworthdigital.voice.tencent_module;

import android.content.Context;
import android.util.Log;

import com.skyworthdigital.voice.VoiceApp;

import java.io.File;

/**
 * Created by cassliu on 2017/10/7.
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    /**
     * 检查目录是否为空
     *
     * @param folder
     * @return
     */
    public static boolean isDirectoryEmpty(File folder) {
        return !folder.exists() || !folder.isDirectory() || folder.list().length <= 0;
    }


    public static void copyModelFiles(Context inContext) {
        //if (isModelFileExist(inContext)) {
        //    return;
        //}
        // 拷贝sdk配置文件到sd卡
        String packagePath = getModelFilePath();
        // 拷贝唤醒模型，先清除原来有的目录
        String sKeywordsPath = packagePath + "/keywords_model";
        File mFile = new File(sKeywordsPath);
        FileUtil.delete(mFile);
        //创建目录
        if (!mFile.exists()) {
            mFile.mkdirs();
        }
        //进行文件拷贝的操作
        AssetsCopyer.releaseAssets(inContext, "keywords_model", packagePath);
        Log.i(TAG, "TVS keywords model config file path: " + sKeywordsPath);

        // 拷贝本地VAD模型，先清除原来有的目录
        String sVadModelPath = packagePath + "/mdl_vtt/vad";
        File vadModelFile = new File(sVadModelPath);
        FileUtil.delete(vadModelFile);
        //创建目录
        if (!vadModelFile.exists()) {
            vadModelFile.mkdirs();
        }
        //进行文件拷贝的操作
        AssetsCopyer.releaseAssets(inContext, "mdl_vtt/vad", packagePath);
        Log.i(TAG, "TVS vad model config file path: " + sVadModelPath);
    }

    /**
     * 递归删除文件及文件夹
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }


    public static String getKeywordsModelDir() {
        File databaseDir = new File(VoiceApp.getInstance().getApplicationInfo().dataDir);// + "/keywords_model/");
        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }
        return databaseDir.getPath();
    }

    public static String getModelFilePath() {
        File databaseDir = new File(VoiceApp.getInstance().getApplicationInfo().dataDir);// + "/keywords_model/");
        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }
        return databaseDir.getPath();
        //String packagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + context.getPackageName() + "/files";
        //return packagePath;
    }

    public static boolean isModelFileExist() {
        String sFilePath = getModelFilePath() + "/keywords_model";
        boolean isExist = false;
        File mFile = new File(sFilePath);
        Log.d(TAG, "Model File Path=" + sFilePath);
        if (mFile.exists()) {
            isExist = true;
        }

        return isExist;
    }
}
