package com.skyworthdigital.voice.dingdang.service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

import static com.skyworthdigital.voice.dingdang.service.DownloadUtils.silentClose;

/**
 * User: yangyongjie
 * Date: 2020-04-13
 * Description:
 */
public class InstallUtils {
    private static final String TAG = "InstallUtils";

    /**
     * install package normal by system intent
     * 注意：针对android7.0需要在AndroidManifest.xml中配置FileProvider
     *
     * @param context
     * @param apkFile
     * @return return true start system install dialog success. otherwise return false.
     */
    public static boolean installNormal(Context context, File apkFile) {
        if (null == apkFile || !apkFile.exists()) {
            return false;
        }

        // check app had install or not
        PackageManager pm = context.getPackageManager();
        PackageInfo fileInfo = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            Uri contentUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".fileprovider", apkFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void installSilent(Context context, File file) {
        file.setReadable(true, false);
        String command = makeInstallCommand(file.getAbsolutePath());
        Log.i(TAG, "=====installSilent command:" + command);
        CommandResult commandResult = execCommand(command, false, true);
        Log.i(TAG, "=====installSilent:" + commandResult.errorMsg);
    }

    private static String makeInstallCommand(String filePath) {
        return String.format("pm install -r '%s'", filePath);
    }


    public static class CommandResult {
        /**
         * 执行的命令
         */
        public String[] commands = null;
        /**
         * 运行情况
         */
        public int result = -1;
        /**
         * 正常终端输出
         */
        public String outMsg = null;
        /**
         * 运行命令异常终端输出
         */
        public String errorMsg = null;

        public CommandResult() {
            // default construction
        }

        public CommandResult(String[] commands) {
            this.commands = commands;
        }

        public boolean containsInstallError(String error) {
            return null != errorMsg && null != error
                    && errorMsg.toLowerCase().contains(error.toLowerCase());
        }

        @Override
        public String toString() {
            return "commands: " + commands + ", result: " + result
                    + ", outMsg: " + outMsg + ", errorMsg: " + errorMsg;
        }
    }

    /**
     * 清空运行信息的线程
     */
    private static class MessageThread extends Thread {
        private static final String TAG = "MessageThread";
        private String type = null;
        private InputStream is = null;
        private StringBuilder outBuilder = null;

        public MessageThread(InputStream is, StringBuilder builder, String type) {
            this.is = is;
            this.outBuilder = builder;
            this.type = type;
        }

        @Override
        public void run() {
            Log.d(TAG, "MessagetThread[" + type + "] start...");
            if (null == is) {
                return;
            }

            InputStreamReader reader = new InputStreamReader(is);
            try {
                int len = 0;
                char[] buf = new char[1024];
                while (-1 != (len = reader.read(buf))) {
                    if (null != outBuilder) {
                        outBuilder.append(buf, 0, len);
                    }
                    Arrays.fill(buf, '\0');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                silentClose(reader);
                reader = null;
            }
            Log.d(TAG, "MessagetThread[" + type + "] finish.");
        }
    }

    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedMessage) {
        String[] commands = {command};
        return execCommand(commands, isRoot, isNeedMessage);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedMessage) {
        CommandResult ret = new CommandResult(commands);

        Process p = null;
        OutputStream out = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("sh");
            pb.redirectErrorStream(! isNeedMessage);
            p = pb.start();
            StringBuilder outBuilder = new StringBuilder();
            StringBuilder errBuilder = new StringBuilder();
            new MessageThread(p.getInputStream(), outBuilder, "STANDARD").start();
            if (isNeedMessage) {
                new MessageThread(p.getErrorStream(), errBuilder, "ERROR").start();
            }

            out = p.getOutputStream();
            for (int i = 0; i < commands.length; i++) {
                out.write(commands[i].getBytes());
                out.write('\n');
                out.flush();
            }

            out.write("exit".getBytes());
            out.write('\n');
            out.flush();

//            D.debug2("commands: " + CommonUtils.dumpArray(commands));
            ret.result = p.waitFor();
            ret.outMsg = outBuilder.toString();
            ret.errorMsg = errBuilder.toString();
//            D.debug2("commands: " + CommonUtils.dumpArray(commands) + ", out: " + outBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            silentClose(out); out = null;
            if (null != p) {
                p.destroy();
            }
        }

        return ret;
    }

    public static synchronized int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
