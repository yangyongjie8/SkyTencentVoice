package com.skyworthdigital.voice.alarm;

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

import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.guide.GuideTip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


class AlarmDialog extends Dialog {
    private Context mContext;
    private TextView mContentTitleTxv;
    private TextView mContentTextView;
    private TextView mTimeTxt, mDateTxt;
    private Button mLeftBtn,mRightBtn;
    private long mCurrentTime = System.currentTimeMillis();
    private TimerTask mTimerTask;
    private OnButtonClickListener mListener;
    private Params params;

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
        mContentTitleTxv = view.findViewById(R.id.alarm_note);
        mContentTextView = (TextView) view.findViewById(R.id.event_txtv);
        mTimeTxt = (TextView) view.findViewById(R.id.alarm_time);
        mDateTxt = (TextView) view.findViewById(R.id.alarm_date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");// HH:mm:ss

        Date date = params.titleTime==null?new Date(System.currentTimeMillis()):params.titleTime;
        mTimeTxt.setText(simpleDateFormat.format(date));

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
        mDateTxt.setText(simpleDateFormat.format(date));

        if (!TextUtils.isEmpty(params.content)) {
            mContentTextView.setText(params.content);
        }
        if (TextUtils.isEmpty(params.contentTitle)){//无内容标题则隐藏该控件
            mContentTitleTxv.setVisibility(View.GONE);
        }else {
            mContentTitleTxv.setText(params.contentTitle);
        }

        setContentView(view);
        mLeftBtn = (Button) view.findViewById(R.id.alarm_confirm_btn);
        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onLeftClick(AlarmDialog.this);
                }
            }
        });

        mRightBtn = (Button) view.findViewById(R.id.alarm_later_btn);
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onRightClick(AlarmDialog.this);
                }
            }
        });
        if(!TextUtils.isEmpty(params.getLeftBtnText()))mLeftBtn.setText(params.getLeftBtnText());
        if(!TextUtils.isEmpty(params.getRightBtnText()))mRightBtn.setText(params.getRightBtnText());

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.END);
        window.setWindowAnimations(R.style.BottomDialogAnimation);

        mLeftBtn.setSelected(true);
        mLeftBtn.requestFocus();

        Timer timer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (GuideTip.getInstance().isDialogShowing()) {
                    Log.i("wyf", "voice on,alarm dialog dismiss");
                    dismiss();
                } else if (System.currentTimeMillis() > mCurrentTime + 90000) {
                    Log.i("wyf", "90s,alarm dialog dismiss");
                    dismiss();
                }

            }
        };
        timer.schedule(mTimerTask, 0, 1500);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }

    // 用于填充dialog的内容
    public static class Params {
        private String contentTitle;//内容区域的标题，非整个dialog头部的title，为空时将不占用标题控件空间
        private String content;//正式内容
        private Date titleTime;//标题部分的时间
        private String leftBtnText;
        private String rightBtnText;

        public Params(String contentTitle, String content) {
            this.contentTitle = contentTitle;
            this.content = content;
        }

        public void setLeftBtnText(String leftBtnText) {
            this.leftBtnText = leftBtnText;
        }

        public void setRightBtnText(String rightBtnText) {
            this.rightBtnText = rightBtnText;
        }

        public String getContentTitle() {
            return contentTitle;
        }

        public Date getTitleTime() {
            return titleTime;
        }

        public void setTitleTime(Date titleTime) {
            this.titleTime = titleTime;
        }

        public String getLeftBtnText() {
            return leftBtnText;
        }

        public String getRightBtnText() {
            return rightBtnText;
        }

        public String getContent() {
            return content;
        }
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public void setOnButtonClickListener(OnButtonClickListener mListener) {
        this.mListener = mListener;
    }

    interface OnButtonClickListener {
        /**
         * 点击左按钮
         * @param dialog
         */
        void onLeftClick(Dialog dialog);

        /**
         * 点击右按钮
         * @param dialog
         */
        void onRightClick(Dialog dialog);
    }
}
