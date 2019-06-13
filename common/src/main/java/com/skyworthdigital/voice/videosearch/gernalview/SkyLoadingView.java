package com.skyworthdigital.voice.videosearch.gernalview;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyworthdigital.voice.common.R;

public class SkyLoadingView extends RelativeLayout {

    private Context mContext;
    private ImageView loadingView;
    private static final float FROMDEGREES = 0;
    private static final float TODEGREES = 360.0f;
    private static final float PIVOTX = 0.5f;
    private static final float PIVOTY = 0.5f;
    private static final int REPEATCOUNT = -1;
    private static final int DURATION = 3000;
    private static final int LAST_DURATION = 10000;
    private static final int MSG_HIDE_LOADING = 0x0987;
    private RotateAnimation rotateAnimation;
    private TextView mTextView;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            handleMyMessage(msg);
        }
    };

    public SkyLoadingView(Context context) {
        super(context);
        mContext = context;
        initLoadingLayout();
    }

    private void handleMyMessage(Message msg) {
        if (msg.what == MSG_HIDE_LOADING) {
            this.clearLoading();
        }
    }

    public SkyLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        initLoadingLayout();
    }

    public SkyLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        mContext = context;
        initLoadingLayout();
    }

    private void initLoadingLayout() {
        RelativeLayout loadingLayout = (RelativeLayout) inflate(mContext, R.layout.loading_space, null);
        loadingView = (ImageView) loadingLayout.findViewById(R.id.sky_loading_img);
        mTextView = (TextView) loadingLayout.findViewById(R.id.tv_playlist_loading_msg);
        addView(loadingLayout);
        rotateAnimation = buildAnimation();
        this.setVisibility(View.GONE);
    }

    public boolean isShowing() {
        if (this.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    public void showLoading() {
        mHandler.removeMessages(MSG_HIDE_LOADING);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_LOADING, LAST_DURATION);
        this.setVisibility(View.VISIBLE);
        loadingView.clearAnimation();
        loadingView.startAnimation(rotateAnimation);
    }

    public void clearLoading() {
        this.setVisibility(View.GONE);
        loadingView.clearAnimation();
    }

    @Override
    public void setBackground(Drawable background) {
        // TODO Auto-generated method stub
        setLoadingBackground(background);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        // TODO Auto-generated method stub
        setLoadingBackground(background);
    }

    @Override
    public void setBackgroundResource(int resid) {
        // TODO Auto-generated method stub
        setLoadingBackground(getResources().getDrawable(resid));
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setLoadingBackground(Drawable drawable) {
        if (loadingView == null) {
            return;
        }
        loadingView.setImageDrawable(drawable);
    }

    public void setLoadingTextColor(int color) {
        if (mTextView == null) {
            return;
        }
        mTextView.setTextColor(color);
    }

    private RotateAnimation buildAnimation() {
        if (mContext == null) {
            return null;
        }
        RotateAnimation rotateAnimation =
                new RotateAnimation(
                        FROMDEGREES,
                        TODEGREES,
                        Animation.RELATIVE_TO_SELF,
                        PIVOTX,
                        Animation.RELATIVE_TO_SELF,
                        PIVOTY);
        rotateAnimation.setRepeatCount(REPEATCOUNT);
        rotateAnimation.setDuration(DURATION);
        rotateAnimation.setInterpolator(mContext, android.R.anim.linear_interpolator);
        return rotateAnimation;
    }

}
