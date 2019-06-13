package com.skyworthdigital.voice.videosearch.gernalview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;


public class SkyAnimationUtil {

    private static final float PIVAT_VALUE = 0.5f;
    public static final float FOCUS_MOVE_Y = 30;

    public static final long DEAULT_WAIT_DRUATION = 200;

    public static final long DEAULT_UP_DRUATION = 200;

    public static final long DEAULT_DOWN_DRUATION = 200;

    public static final String NO_ANIMATION = "no_animation";
    public static final int LABLE_SHOW_TIME = 5 * 1000;
    public static final int DISMISS_TIME = 2000;
    public static final int STATEBAR_DISMISS_TIME = 20000;
    public static final int ALPHAL_CHANGE = 255;
    private static final float INTERPOLATOR_RATE = 4;
    private static final int MAX_HEIGHT = 600;
    private static final int MAX_WIDTH = 500;
    private static final float OFFSET = 0.05f;
    private static final int DELTA = 10;

    public enum SkyAnimation {
        ZOOM_IN, ZOOM_OUT
    }

    /**
     * init and show animation
     *
     * @param view
     * @param animation
     * @param mMagnitudeOfEnlargement
     * @param mSkyDuration
     */
    public static void showAnimation(
            View view,
            SkyAnimation animation,
            float mMagnitudeOfEnlargement,
            int mSkyDuration) {
        ScaleAnimation mZoomINAnim;
        ScaleAnimation mZoomOUTAnim;
        if (view.getHeight() >= MAX_HEIGHT || view.getWidth() >= MAX_WIDTH) {
            mMagnitudeOfEnlargement = mMagnitudeOfEnlargement - OFFSET;
        }
        switch (animation) {
            case ZOOM_IN:
                mZoomINAnim =
                        new ScaleAnimation(
                                1.0f,
                                mMagnitudeOfEnlargement,
                                1.0f,
                                mMagnitudeOfEnlargement,
                                Animation.RELATIVE_TO_SELF,
                                PIVAT_VALUE,
                                Animation.RELATIVE_TO_SELF,
                                PIVAT_VALUE);
                mZoomINAnim.setDuration(mSkyDuration);
                mZoomINAnim.setFillAfter(true);
                mZoomINAnim.setInterpolator(new DecelerateInterpolator());
                view.startAnimation(mZoomINAnim);

                break;
            case ZOOM_OUT:
                mZoomOUTAnim =
                        new ScaleAnimation(
                                mMagnitudeOfEnlargement,
                                1.0f,
                                mMagnitudeOfEnlargement,
                                1.0f,
                                Animation.RELATIVE_TO_SELF,
                                PIVAT_VALUE,
                                Animation.RELATIVE_TO_SELF,
                                PIVAT_VALUE);
                mZoomOUTAnim.setDuration(mSkyDuration);
                mZoomOUTAnim.setFillAfter(true);
                mZoomOUTAnim.setInterpolator(new DecelerateInterpolator());
                view.startAnimation(mZoomOUTAnim);
                break;
            default:
                break;
        }
    }

    private static Animator moveY(final View focusView, float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setTarget(focusView);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                focusView.setTranslationY((Float) animation.getAnimatedValue());
            }
        });
        return animator;
    }

    public static void processMove(boolean focus, View view) {
        // TODO Auto-generated method stub
        if (focus) {
            Animator moveUp = moveY(view, 0, -FOCUS_MOVE_Y);
            moveUp.setInterpolator(new AccelerateDecelerateInterpolator());
            moveUp.setDuration(DEAULT_UP_DRUATION).start();
        } else {
            Animator moveDown = moveY(view, -FOCUS_MOVE_Y, 0);
            moveDown.setInterpolator(new DecelerateInterpolator(INTERPOLATOR_RATE));
            moveDown.setDuration(DEAULT_DOWN_DRUATION).start();
        }
    }

    public static Animator changeBackgroundAlphaView(final View focusView, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setTarget(focusView);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (focusView != null) {
                    focusView.setAlpha((int) animation.getAnimatedValue());
                }
            }
        });
        return animator;
    }

    public static Animator changeBackgroundAlpha(final ImageView focusView, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setTarget(focusView);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (focusView != null && focusView.getDrawable() != null) {
                    focusView.getDrawable().setAlpha((int) animation.getAnimatedValue());
                }
            }
        });
        return animator;
    }

    public static int dp2px(float dip, Context context) {
        return (int) (context.getResources().getDisplayMetrics().density * dip);
    }

    public static void shake(View view, int direction) {
        shake(view, direction, null);
    }

    public static void shake(View view, int direction, AnimatorListener listener) {
        int delta = getDeltaValue(direction);
        PropertyValuesHolder pvhTranslateX =
                PropertyValuesHolder.ofKeyframe(
                        getProperty(direction),
                        Keyframe.ofFloat(0f, 0),
                        Keyframe.ofFloat(.10f, -delta),
                        Keyframe.ofFloat(.30f, delta),
                        Keyframe.ofFloat(.50f, -delta),
                        Keyframe.ofFloat(.70f, delta),
                        Keyframe.ofFloat(.90f, -delta),
                        Keyframe.ofFloat(1f, 0f));
        ObjectAnimator shakeAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).setDuration(500);
//        shakeAnimator.setRepeatCount(ValueAnimator.INFINITE);
        if (listener != null) {
            shakeAnimator.addListener(listener);
        }
        shakeAnimator.start();
    }

    private static Property<View, Float> getProperty(int direction) {
        if (View.FOCUS_UP == direction || View.FOCUS_DOWN == direction) {
            return View.TRANSLATION_Y;
        } else if (View.FOCUS_LEFT == direction || View.FOCUS_RIGHT == direction) {
            return View.TRANSLATION_X;
        }
        return View.TRANSLATION_X;
    }

    private static int getDeltaValue(int direction) {
        if (View.FOCUS_RIGHT == direction || View.FOCUS_DOWN == direction) {
            return DELTA;
        } else if (View.FOCUS_LEFT == direction || View.FOCUS_UP == direction) {
            return -DELTA;
        }
        return DELTA;
    }
}
