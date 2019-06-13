
package com.skyworthdigital.voice.videosearch.gernalview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.skyworthdigital.voice.common.R;


public class CheckDialog extends Dialog implements View.OnClickListener {
    private Button mSubmit;
    private Context mContext;
    private TextView mContentTextView;
    private String mContentText;
    private View.OnClickListener mConfirmListener;
    private String mBtnText;

    public CheckDialog(Context context, int theme) {
        super(context, R.style.CustomDialog);
        mContext = context;
    }

    public CheckDialog(Context context) {
        this(context, R.style.CustomDialog);
    }

    public CheckDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, R.style.CustomDialog);
        this.setCancelable(cancelable);
        this.setOnCancelListener(cancelListener);
        mContext = context;
    }

    public CheckDialog(Context context, View.OnClickListener clickListener) {
        super(context, R.style.CustomDialog);
        this.setCancelable(true);
        this.setOnCancelListener(null);
        mConfirmListener = clickListener;
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.confirm_dialog, null, false);
        mContentTextView = (TextView) view.findViewById(R.id.content_txtv);
        if (!TextUtils.isEmpty(mContentText)) {
            mContentTextView.setText(mContentText);
        }
        setContentView(view);
        mSubmit = (Button) view.findViewById(R.id.confirm_btn);
        if (mConfirmListener == null) {
            mSubmit.setOnClickListener(this);
        } else {
            mSubmit.setOnClickListener(mConfirmListener);
        }
        if (mBtnText != null) {
            mSubmit.setText(mBtnText);
        }
        final WindowManager.LayoutParams WMLP = getWindow().getAttributes();
        WMLP.width = (int) mContext.getResources().getDimension(R.dimen.confirm_width);
        WMLP.height = (int) mContext.getResources().getDimension(R.dimen.confirm_height);
        getWindow().setAttributes(WMLP);
        mSubmit.setSelected(true);
        mSubmit.requestFocus();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent("com.skyworthdigital.settings.activity.NetDiagnoseActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        dismiss();
    }

    public void setButtonTect(String text) {
        mBtnText = text;
        if (mSubmit != null) {
            mSubmit.setText(text);
        }
    }

    public void setContentText(String contentText) {
        mContentText = contentText;
        if (mContentTextView != null) {
            mContentTextView.setText(contentText);
        }
    }
}
