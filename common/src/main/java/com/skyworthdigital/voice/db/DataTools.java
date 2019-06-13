package com.skyworthdigital.voice.db;


import android.content.Context;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.NetUtils;

import java.io.File;
import java.io.InputStream;

public class DataTools {
    static final String TABLE_TVLIST = "tvlist_fm";
    private static final String DB_NAME = "tvlist.db";

    public static String getBasebasePath() {
        return getDatabaseDir() + "/" + DataTools.DB_NAME;
    }

    private static String getDatabaseDir() {
        File databaseDir = new File(VoiceApp.getInstance().getApplicationInfo().dataDir + "/databases/");
        if (!databaseDir.exists()) {
            databaseDir.mkdir();
        }

        return databaseDir.getPath();
    }

    /**
     * 功能：拷贝assert下面的tvlist.db到应用databases目录下。
     */
    public static void copyDbAssets(Context context) {
        try {
            String newPath = DataTools.getBasebasePath();
            InputStream is = context.getAssets().open(DataTools.DB_NAME);
            File file = new File(newPath);
            if (!file.exists()) {
                file.createNewFile();
                MLog.i("DataTools","copyDbAssets write to " + newPath);
                NetUtils.writeContentToFile(is, file);
            }

            is.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

