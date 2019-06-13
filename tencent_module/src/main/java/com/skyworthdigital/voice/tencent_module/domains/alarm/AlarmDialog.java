package com.skyworthdigital.voice.tencent_module.domains.alarm;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.tencent_module.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


class AlarmDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private TextView mContentTextView;
    private TextView mTimeTxt, mDateTxt;
    private String mContentText;
    private long mCurrentTime = System.currentTimeMillis();
    private TimerTask mTimerTask;

    AlarmDialog(Context context, int theme) {
        super(context, R.style.AlarmDialog);
        mContext = context;
    }

    AlarmDialog(Context context) {
        this(context, R.style.AlarmDialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alarm_dialog, null, false);
        mContentTextView = (TextView) view.findViewById(R.id.event_txtv);
        mTimeTxt = (TextView) view.findViewById(R.id.alarm_time);
        mDateTxt = (TextView) view.findViewById(R.id.alarm_date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");// HH:mm:ss

        Date date = new Date(System.currentTimeMillis());
        mTimeTxt.setText(simpleDateFormat.format(date));

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        date = new Date(System.currentTimeMillis());
        mDateTxt.setText(simpleDateFormat.format(date));

        if (!TextUtils.isEmpty(mContentText)) {
            mContentTextView.setText(mContentText);
        }
        setContentView(view);
        Button submitBtn = (Button) view.findViewById(R.id.alarm_confirm_btn);
        submitBtn.setOnClickListener(this);

        Button laterBtn = (Button) view.findViewById(R.id.alarm_later_btn);
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeInMillis(System
                        .currentTimeMillis());
                mCalendar.add(Calendar.MINUTE, 5);
                AlarmHelper alarm = new AlarmHelper(VoiceApp.getInstance());
                alarm.saveAlarm(mContentText, ""+mCalendar.getTimeInMillis(), "once");
                dismiss();
            }
        });

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.END);
        window.setWindowAnimations(R.style.BottomDialogAnimation);

        submitBtn.setSelected(true);
        submitBtn.requestFocus();

        Timer timer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                /*VoiceManager voiceManager = VoiceManager.getInstance();
                if (voiceManager != null && voiceManager.isDialogShow()) {
                    Log.i("wyf", "voice on,alarm dialog dismiss");
                    dismiss();
                } else */
                if (System.currentTimeMillis() > mCurrentTime + 90000) {
                    Log.i("wyf", "90s,alarm dialog dismiss");
                    dismiss();
                }

            }
        };
        timer.schedule(mTimerTask, 0, 1500);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }

    void setContentText(String contentText) {
        mContentText = contentText;
        if (mContentTextView != null) {
            mContentTextView.setText(contentText);
        }
    }
}
