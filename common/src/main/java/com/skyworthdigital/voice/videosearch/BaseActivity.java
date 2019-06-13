package com.skyworthdigital.voice.videosearch;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.skyworthdigital.voice.videosearch.gernalview.CheckDialog;

public class BaseActivity extends Activity {
    private HomeWatcherReceiver mHomeWatcherReceiver = null;
    private CheckDialog mNetworkCheckDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReveiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void registerReveiver() {
        mHomeWatcherReceiver = new HomeWatcherReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mHomeWatcherReceiver, filter);
    }

    /*private void refreshNetStatus() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo == null || netInfo.getState() == NetworkInfo.State.DISCONNECTED) {
//            SkyToast.showToast(getApplicationContext(), R.string.show_net_error);
        }
    }

    public void showNetTips() {
        try {
            if (mNetworkCheckDialog == null) {
                mNetworkCheckDialog = new CheckDialog(this);
                mNetworkCheckDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        BaseActivity.this.finish();
                    }
                });
            }
            mNetworkCheckDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                this.finish();
                System.gc();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mHomeWatcherReceiver != null) {
                unregisterReceiver(mHomeWatcherReceiver);
            }
            ImagePipeline mImagePipeline = Fresco.getImagePipeline();
            if (mImagePipeline != null) {
                mImagePipeline.clearMemoryCaches();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            Log.i(LOG_TAG, "onReceive: action: " + action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                Log.i(LOG_TAG, "reason: " + reason);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    exitHome();
                }
            } /*else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                refreshNetStatus();
            }*/
        }
    }

    public void exitHome() {
        this.finish();
    }
}