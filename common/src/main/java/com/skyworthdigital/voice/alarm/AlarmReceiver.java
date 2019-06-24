package com.skyworthdigital.voice.alarm;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.alarm.database.AlarmDbHelper;
import com.skyworthdigital.voice.alarm.database.AlarmDbOperator;
import com.skyworthdigital.voice.common.AbsTTS;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.dingdang.utils.AppUtil;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.dingdang.utils.SPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    private String TAG = AlarmReceiver.class.getSimpleName();

    public static final String ACTION_REMIND_BYUSER = "ACTION_REMIND_BYUSER";//用户设置的提醒
    private static final String ACTION_REMIND_THIRDAPP = "com.skyworthdigital.voiceassisntant.showdialog";// 第三方app发起的提醒广播

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MLog.i(TAG, "AlarmReceiver receive action:"+action);
        if(ACTION_REMIND_BYUSER.equalsIgnoreCase(action)){
            doUserRemind(context, intent);
        }else if(Intent.ACTION_TIME_TICK.equalsIgnoreCase(action)) {// 计时
            doMovieProgramRemind(context);
        }else if(ACTION_REMIND_THIRDAPP.equalsIgnoreCase(action)){
            doThirdAppRemind(intent.getStringExtra("data"));//TODO data key暂定
        }else {
            MLog.i(TAG, "!!! the action haven't been handled.");
        }


    }

    private void doThirdAppRemind(String data) {
        MLog.i(TAG, "AlarmReceiver doThirdAppRemind data:"+data);
        if(TextUtils.isEmpty(data))return;

        try {
            JSONObject dataJson = new JSONObject(data);
            String whenStr = dataJson.optString("when");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSSS", Locale.getDefault());// 按当地时区理解传入的字面，即同样的字面在不同时区将会是不同时间
            Date whenDate = sdf.parse(whenStr);
            AlarmHelper alarm = new AlarmHelper(VoiceApp.getInstance());
            alarm.saveAlarm(dataJson.optString("contentTitle"), dataJson.optString("content"), whenDate.getTime(), "once", AlarmDbHelper.VALUE_SOUND_MODE_DISPLAYONLY);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showDialog(Context context, AlarmDialog.Params dialogParams, AlarmDialog.OnButtonClickListener clickListener){
        AlarmDialog dialog = new AlarmDialog(context);
        dialog.setParams(dialogParams);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setOnButtonClickListener(clickListener);
        dialog.show();
    }
    private void playSound(String soundText){
        AbsTTS.getInstance(null).talkSerial(soundText);
    }

    // 用户设定的提醒
    private void doUserRemind(Context context, Intent intent){
        String mContent = context.getString(R.string.str_alarm_note);
        String mContentTitle = null;// 无则不显示标题
        String repeat = "once";
        final String soundMode;
        Calendar calendar = Calendar.getInstance();
        if (intent.hasExtra("content")) {
            mContent = intent.getStringExtra("content");
            MLog.i(TAG, "onReceiver" + mContent);
        }
        if (intent.hasExtra("contentTitle")) {
            mContentTitle = intent.getStringExtra("contentTitle");
            MLog.i(TAG, "onReceiver" + mContentTitle);
        }

        if (intent.hasExtra("repeat")) {
            repeat = intent.getStringExtra("repeat");
            MLog.i(TAG, "onReceiver repeat:" + repeat);
        }
        soundMode = intent.getStringExtra("sound");

        if (intent.hasExtra("time")) {
            long time = intent.getLongExtra("time", -1);
            calendar.setTimeInMillis(System.currentTimeMillis());
            MLog.i(TAG, "onReceiver time:" + time + " cur:" + System.currentTimeMillis());
            if (TextUtils.equals(repeat, "once")) {
                AlarmDbOperator dbOperator = new AlarmDbOperator(VoiceApp.getInstance());
                dbOperator.delete(time);
            } else if (TextUtils.equals(repeat, "weekday")) {
                boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
                int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                //若一周第一天为星期天，则-1
                if (isFirstSunday) {
                    weekDay = weekDay - 1;
                    if (weekDay == 0) {
                        weekDay = 7;
                    }
                }
                MLog.i(TAG, "weekday:" + weekDay);
            }
            if (System.currentTimeMillis() > 60000 + time) {
                MLog.i(TAG, "outime alarm");
                return;
            }
        }
        final String _content = mContent;
        final String _contentTitle = mContentTitle;
        showDialog(context,
                new AlarmDialog.Params(mContentTitle, mContent),
                new AlarmDialog.OnButtonClickListener() {
                    @Override
                    public void onLeftClick(Dialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onRightClick(Dialog dialog) {
                        // 稍后提醒：5分钟后
                        Calendar mCalendar = Calendar.getInstance();
                        mCalendar.setTimeInMillis(System
                                .currentTimeMillis());
                        mCalendar.add(Calendar.MINUTE, 5);
                        AlarmHelper alarm = new AlarmHelper(VoiceApp.getInstance());
                        alarm.saveAlarm(_contentTitle, _content, mCalendar.getTimeInMillis(), "once", soundMode);
                        dialog.dismiss();
                    }
                });

        if(!AlarmDbHelper.VALUE_SOUND_MODE_DISPLAYONLY.equalsIgnoreCase(soundMode)) {//仅显示模式不播音
            if (intent.hasExtra("content")) {
                playSound(context.getString(R.string.str_alarm_note) + intent.getStringExtra("content"));
            } else {
                playSound(context.getString(R.string.str_alarm_note));
            }
        }
    }
    // 节目单提醒
    private void doMovieProgramRemind(final Context context){
        int PLAYHOUR = 15,PLAYMINUTE = 10;

        Calendar now = Calendar.getInstance();
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        MLog.i(TAG, "AlarmReceiver hour:"+hour+" minute:"+ minute);

        boolean isDemo = SPUtil.getBoolean(SPUtil.KEY_SP_DEMO_SWITCH_ON);
        if(!isDemo && (month!= Calendar.JANUARY || day<13 || day>21))return; // 一月14到21号生效
        if(!isDemo && (hour<12 || hour>=16))return;// 12~16点生效
        if(isDemo){
            if(minute%2!=0)return;//演示2分钟一周期
        }else if(minute!=0 && !(hour==PLAYHOUR && minute==PLAYMINUTE)){
            return;//仅在正点生效
        }

        String remindText = "预告：\n《中国北方硒科普调查纪实--硒望之源》";
        String remindSound = "三点10分有一个很不错的节目，您到时可以看看噢(wo1)~";
        final boolean isTheTime = (hour>PLAYHOUR)||(hour==PLAYHOUR && minute>=PLAYMINUTE);
        if(isTheTime){
            remindText = "正在播放：\n《中国北方硒科普调查纪实--硒望之源》";
            remindSound = "现在正在播放硒望之源，您需要去看看吗(ma1)？";
        }

        AlarmDialog.Params dialogParams = new AlarmDialog.Params(null, remindText);
        if(hour<=15) {
            Calendar videoTime = Calendar.getInstance();
            videoTime.set(Calendar.HOUR_OF_DAY, PLAYHOUR);
            videoTime.set(Calendar.MINUTE, PLAYMINUTE);
            dialogParams.setTitleTime(videoTime.getTime());
        }

        if(isTheTime){
            dialogParams.setLeftBtnText(context.getString(R.string.str_goto_watch));
            dialogParams.setRightBtnText(context.getString(R.string.str_alarm_later_button));
        }else {
            // use default
        }

        showDialog(context, dialogParams,
                new AlarmDialog.OnButtonClickListener() {
                    @Override
                    public void onLeftClick(Dialog dialog) {
                        if(isTheTime){
                            if(AppUtil.isApkInstalled(context, "cn.beevideo.xxx")){
                                AppUtil.startApp(context, "cn.beevideo.xxx");
                            }else {
                                Toast.makeText(context, "尚未安装该app", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onRightClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
        playSound(remindSound);
    }
}
