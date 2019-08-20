package com.skyworthdigital.voice.common.utils;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class Utils {
    //public static final String TAG = "Utils";

    /**
     * 按键操作
     **/
    public static void simulateKeystroke(final int KeyCode) {

        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                try {
                    //Thread.sleep(2000);
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void simulateKeystroke(final int KeyCode, final int delay) {

        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(delay);
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * get properties by android.os.SystemProperties.
     * <p>
     * param key
     */
    public static String get(String key) {
        Class<?> clazz;
        try {
            clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getDeclaredMethod("get", String.class);
            return (String) method.invoke(clazz.newInstance(), key);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 功能：字符串过滤掉filter开头的字符
     * 例如：src:打开应用商店 filter：{"打开", "进入"}
     * 返回值：应用商店
     */
    public static String filterByStartWith(String src, String[] filter) {
        if (src == null) {
            return null;
        }
        if (filter == null || filter.length == 0) {
            return src;
        }
        for (String temp : filter) {
            if (src.startsWith(temp)) {
                int len = temp.length();
                //LogUtil.log("filter:" + src + " " + filter[i]);
                src = src.substring(len);
            }
        }
        return src;
    }

    /**
     * 功能：字符串过滤掉filter字符
     * 例如：src:打开应用商店 filter：{"打开", "进入"}
     * 返回值：应用商店
     */
    public static String filterBy(String src, String[] filter) {
        if (src == null) {
            return null;
        }
        if (filter == null || filter.length == 0) {
            return src;
        }
        for (String temp : filter) {
            int tempPos = src.indexOf(temp);
            int len = temp.length();
            if (tempPos != -1) {
                if (tempPos == 0) {
                    src = src.substring(len);
                } else if (src.endsWith(temp)) {
                    src = src.substring(0, src.length() - len);
                } else {
                    src = src.substring(0, tempPos) + src.substring(tempPos + len, src.length());
                }
            }
        }
        return src;
    }

    public static String getVersion() {
        Context ctx = VoiceApp.getInstance();
        try {
            PackageManager manager = ctx.getPackageManager();
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";//ctx.getString(R.string.str_unknown_note);
        }
    }

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     *
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n   随机数个数
     */
    public static int[] getRandomNumbers(int min, int max, int n) {
        if (n > (max - min)) {
            int[] result = new int[max - min];
            for (int i = min; i < (max - min); i++) {
                result[i - min] = i;
            }
            return result;
        }
        if (max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    public static void reboot(Context ctx) {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        pm.reboot("normal");
    }

    public static float levenshtein(String str1, String str2) {
        //计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        //建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
            }
        }
        Log.i("wyf", "字符串\"" + str1 + "\"与\"" + str2 + "\"的比较");
        //取数组右下角的值，同样不同位置代表不同字符串的比较
        //Log.i("wyf", "差异步骤：" + dif[len1][len2]);
        //计算相似度
        float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
        //Log.i("wyf", "相似度：" + similarity);
        return similarity;
    }


    //得到最小值
    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    public static int getAiType() {
        String type = Utils.get("ro.config.voice.type");
        if (isQ3031Recoder()) {
            return GlobalVariable.AI_VOICE;
        } else if (!TextUtils.isEmpty(type)) {
            if (TextUtils.equals(type, "dingdang_remote")) {
                return GlobalVariable.AI_REMOTE;
            } else if (TextUtils.equals(type, "dingdang_voice")) {
                return GlobalVariable.AI_VOICE;
            }
        }
        return GlobalVariable.AI_REMOTE;
    }

    public static boolean isQ3031Recoder() {
        return (TextUtils.equals(VoiceApp.getModel(), "Q3031"));
    }

    public static boolean isP201IPtv() {
        return (TextUtils.equals(VoiceApp.getModel(), "p201_iptv"));
    }

    public static void setWakeupProperty(int on) {
        //0:closed 1:opened 2:opening 3:closing
        Log.i("wakeup", "setWakeupProperty:" + on);
        Settings.System.putInt(VoiceApp.getInstance().getContentResolver(), "voiceopen", on);
    }

    public static int getWakeupProperty() {
        return Settings.System.getInt(VoiceApp.getInstance().getContentResolver(), "voiceopen", 1);
    }

    // 切换hdmi/音箱输出
    public static void openHdmi(boolean open){
        Class<?> clazz = null;
        try {
            clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("set", String.class, String.class);
            if(open){
                Settings.Secure.putInt(VoiceApp.getInstance().getContentResolver(), "sound_hdmi_soundbox", 0);
                method.invoke(null,"persist.sys.hdmi.soundbox.mute", "0");
            }else {
                Settings.Secure.putInt(VoiceApp.getInstance().getContentResolver(), "sound_hdmi_soundbox", 1);
                method.invoke(null,"persist.sys.hdmi.soundbox.mute", "1");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void openScreen(boolean open){
        if(open){
            rootExec("echo -1 > /sys/class/amhdmitx/amhdmitx0/avmute");
        }else {
            rootExec("echo 1 > /sys/class/amhdmitx/amhdmitx0/avmute");
        }
    }

    // 执行一段普通的linux命令
    private static String exec(String[] args) {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
    // 执行一段root命令
    private static String rootExec(String cmd) {
        if (cmd.startsWith("su")|cmd.startsWith("ping")) {
            return "不允许执行的命令";
        }
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process ps = Runtime.getRuntime().exec("sh");
            dos = new DataOutputStream(ps.getOutputStream());
            dis = new DataInputStream(ps.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine())!=null) {
                result += "\n"+line;
                //   Message ms = new Message();
                //   ms.obj = line;
                //   handler.sendMessageDelayed(ms,1000);
            }

            ps.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
