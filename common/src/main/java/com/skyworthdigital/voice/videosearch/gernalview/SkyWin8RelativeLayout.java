package com.skyworthdigital.voice.videosearch.gernalview;



import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class SkyWin8RelativeLayout extends RelativeLayout implements SkyAttributeUtil.SkyAttribute {

    private static final int DELAY_TIME = 500;
    private boolean mIsWin8Formart = false;
    private boolean mNeedBeSelectedWhenParentIsFocused = false;
    private boolean mIsShowAnimation = false;
    private float mMagnitudeOfEnlargement = 1.0f;
    private int mSkyDuration = DELAY_TIME;
    private static final int INVALIDATE_INDEX = -1;
    private static final int PERSENT = 100;
    private static final float PERSENT_F = 100.0f;
    private boolean isFocus = false;

    public SkyWin8RelativeLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context, null);
    }

    public SkyWin8RelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context, attrs);
    }

    public SkyWin8RelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attr) {
        mNeedBeSelectedWhenParentIsFocused = SkyAttributeUtil.needBeSelectedWhenParentIsFocused(context, attr, false);
        mIsWin8Formart = SkyAttributeUtil.isWin8Formart(context, attr, false);
        mIsShowAnimation = SkyAttributeUtil.isShowAnimation(context, attr, false);
        mMagnitudeOfEnlargement = SkyAttributeUtil.getMagnitudeOfEnlargement(context, attr, PERSENT) / PERSENT_F;
        mSkyDuration = SkyAttributeUtil.getSkyDuration(context, attr, DELAY_TIME);
        setChildrenDrawingOrderEnabled(true);
        setClipChildren(false);
    }

    @Override
    public final void setClipChildren(boolean enabled) {
        super.setClipChildren(enabled);
    }

    @Override
    protected final void setChildrenDrawingOrderEnabled(boolean enabled) {
        // TODO Auto-generated method stub
        super.setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected void onFocusChanged(boolean bFocused, int arg1, Rect arg2) {
        // TODO Auto-generated method stub
        super.requestLayout();
        super.onFocusChanged(bFocused, arg1, arg2);
        showAnimation(bFocused);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        showAnimation(selected);
    }

    private void showAnimation(boolean selected) {
        if (isFocus == selected) {
            return;
        }
        if (mIsShowAnimation) {
            if (selected && mIsWin8Formart) {
                SkyAnimationUtil.showAnimation(this, SkyAnimationUtil.SkyAnimation.ZOOM_IN, mMagnitudeOfEnlargement, mSkyDuration);
                isFocus = true;
            }
            else {
                SkyAnimationUtil.showAnimation(this, SkyAnimationUtil.SkyAnimation.ZOOM_OUT, mMagnitudeOfEnlargement, mSkyDuration);
                isFocus = false;
            }
        }
    }

    @Override
    public boolean needBeSelectedWhenParentIsFocused() {
        // TODO Auto-generated method stub
        return mNeedBeSelectedWhenParentIsFocused;
    }

    /**
     * if the values is true ,the format is win8
     * 
     */
    public boolean isWin8Formart() {
        return mIsWin8Formart;
    }

    /**
     * set the win8 format when the value is true
     * 
     * @param //mIsWin8Formart
     */
    public void setIsWin8Formart(boolean isWin8Formart) {
        this.mIsWin8Formart = isWin8Formart;
    }

    /**
     * whether childView is selected or not;
     * 
     */
    public boolean isNeedBeSelectedWhenParentIsFocused() {
        return mNeedBeSelectedWhenParentIsFocused;
    }

    /**
     * whether need set childView select state or not;
     * 
     * @param //mNeedBeSelectedWhenParentIsFocused
     */
    public void setNeedBeSelectedWhenParentIsFocused(boolean needBeSelectedWhenParentIsFocused) {
        this.mNeedBeSelectedWhenParentIsFocused = needBeSelectedWhenParentIsFocused;
    }

    /**
     * whether show the animations or not
     * 
     * @return
     */
    public boolean isShowAnimation() {
        return mIsShowAnimation;
    }

    /**
     * set mIsShowAnimation. If the value is true and mIsWin8Formart is true,you
     * can show the animations;
     * 
     * @param //mIsShowAnimation
     */
    public void setIsShowAnimation(boolean isShowAnimation) {
        this.mIsShowAnimation = isShowAnimation;
    }

    /**
     * set toX and toY ,the values is must > 1.0
     * 
     * @return
     */
    public float getMagnitudeOfEnlargement() {
        return mMagnitudeOfEnlargement;
    }

    /**
     * get values of the toX and toY
     * 
     * @param //mMagnitudeOfEnlargement
     */
    public void setMagnitudeOfEnlargement(float magnitudeOfEnlargement) {
        this.mMagnitudeOfEnlargement = magnitudeOfEnlargement;
    }

    /**
     * get anmations show time;
     * 
     */
    public int getSkyDuration() {
        return mSkyDuration;
    }

    /**
     * set anmations show time;
     * 
     */
    public void setSkyDuration(int skyDuration) {
        this.mSkyDuration = skyDuration;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int index) {
        int focusedChildIndex = getFocusedViewIndex();
        if (focusedChildIndex == INVALIDATE_INDEX) {
            return super.getChildDrawingOrder(childCount, index);
        }

        if ((focusedChildIndex != index) && (index != childCount - 1)) {
            return super.getChildDrawingOrder(childCount, index);
        }

        if (focusedChildIndex == index) {
            return childCount - 1;
        }
        else if (index == childCount - 1) {
            return focusedChildIndex;
        }

        return super.getChildDrawingOrder(childCount, index);
    }

    private int getFocusedViewIndex() {
        int count = this.getChildCount();

        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);
            if (child.isFocused()) {
                return i;
            }
        }

        return INVALIDATE_INDEX;
    }

    @Override
    public View focusSearch(int direction) {
        View nextFocusView = super.focusSearch(direction);
        if (nextFocusView == this || nextFocusView == null) {
            if (!isAnimRunning) {
                SkyAnimationUtil.shake(this, direction, mAnimatorListener);
            }
        }
        return nextFocusView;
    }

    private boolean isAnimRunning = false;
    private AnimatorListener mAnimatorListener = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            isAnimRunning = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {}

        @Override
        public void onAnimationEnd(Animator animation) {
            isAnimRunning = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            isAnimRunning = false;
        }
    };
}
