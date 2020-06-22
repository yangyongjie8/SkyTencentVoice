package com.skyworthdigital.voice;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.skyworthdigital.voice.dingdang.R;
import com.skyworthdigital.voice.dingdang.service.FileCacheUtils;
import com.skyworthdigital.voice.dingdang.service.InstallUtils;

import java.io.File;

/**
 * User: yangyongjie
 * Date: 2019-01-18
 * Description:
 */
public class TestActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView textView = findViewById(R.id.version_name);
            textView.setText("SkyTencentVoice 当前版本号：" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ((Button)findViewById(R.id.upgrade_btn)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String url = "http://apk.beevideo.bestv.com.cn/apk/mds/yuyin/skyTencentVoice_2.02.05.0413_1414.apk";
        String savePath = FileCacheUtils.getCacheFilePath(this, "myapp", url);
        File file = new File(savePath);
        if(file.exists()) {
            Toast.makeText(this, "install apk:" + savePath, Toast.LENGTH_LONG).show();
            InstallUtils.installSilent(this, file);
        }
    }
}
