package com.skyworthdigital.voice.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;




/**
 * 该类继承TextView，实现当text内容过长，高度上显示不完整的情况下，
 * 垂直方向的自动滚动。
 * 该类主要是用来实现语音播报的内容过多时的垂直方向上的滚动显示，同
 * 时滚动的速度要同语音播报的速度相匹配，是一个特殊功能类。
 * Created by SDT03046 on 2017/6/28.
 */
public class SkyVerticalMarqueeTextview extends TextView {
    private final int MGS_START_MARQUEE = 0x01;
    private final int MGS_STOP_MARQUEE = 0x02;
    private final int MAX_LINES = 12;
    private int mCountDelay = 0;
    private int mReadCharNo = 0;
    private int mCurLineOffset = 0;
    private int mCurLineOffsetPre = 0;
    private int mReadLineNo = 0;
    private boolean mReaderror = false;
    private final static int START_MARQUEE_TIME = 20000;
    private int maxScrollY;
    private boolean isFrozenFromWindowFocusChanged = Boolean.FALSE;
    private boolean isFrozenFromVisible = Boolean.FALSE;
    private boolean hasAttachedToWindow;
    private boolean isEnoughToMarquee = false;
    private final int FPS = 1000 / 5;//1000 / 25;
    private final int SPEED = 6;//1;
    private boolean isFrozen = true;
    private static int START_MARQUEE_DELAY = 8000;
    private boolean mRobotTextMarqueeOver = false;
    private boolean mRobotTextNeedMarquee = false;

    public SkyVerticalMarqueeTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
    }

    public SkyVerticalMarqueeTextview(Context context) {
        super(context);
        this.setFocusable(true);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MGS_START_MARQUEE:
                    startMarquee();
                    break;
                case MGS_STOP_MARQUEE:
                    setMarqueeModel(MarqueeModel.MANUAL);
                    break;
                default:
                    break;
            }
        }
    };

    private void removeAllMessage() {
        mHandler.removeMessages(MGS_START_MARQUEE);
        mHandler.removeMessages(MGS_STOP_MARQUEE);
    }

    private void startMarqueeDelay() {
        removeAllMessage();
        mHandler.sendEmptyMessageDelayed(MGS_START_MARQUEE, START_MARQUEE_DELAY);
    }


    private void startMarquee() {
        setMarqueeModel(MarqueeModel.AUTO_ON_VISIBLE);
    }

    private void stopMarquee() {
        removeAllMessage();
        mHandler.sendEmptyMessage(MGS_STOP_MARQUEE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initMaxScrollAndCheckFrezon();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                stopMarquee();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                stopMarquee();

                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                startMarqueeDelay();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                startMarqueeDelay();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void checkEnoughToMarquee() {
        //LogUtil.log("checkEnoughToMarquee height:" + getHeight() + " maxScrollY:" + maxScrollY);
        isEnoughToMarquee = maxScrollY > getHeight();
        if (isEnoughToMarquee) {
            //EventBus.getDefault().post(new EventMsg(EventMsg.MSG_TEXTISMARQUEE));
            mRobotTextNeedMarquee = true;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            if (isFrozenFromWindowFocusChanged) {
                isFrozenFromWindowFocusChanged = false;
                checkFrozen();
            }
        } else {
            isFrozen = true;
            isFrozenFromWindowFocusChanged = true;
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            if (isFrozenFromVisible) {
                isFrozenFromVisible = false;
                checkFrozen();
            }
        } else {
            isFrozen = true;
            isFrozenFromVisible = true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        hasAttachedToWindow = true;
        checkFrozen();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hasAttachedToWindow = false;
        checkFrozen();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        initMaxScrollAndCheckFrezon();
    }

    private void checkFrozen() {
        boolean oldIsFronze = isFrozen;
        isFrozen = !(isEnoughToMarquee && hasAttachedToWindow
                && !isFrozenFromVisible && !isFrozenFromWindowFocusChanged);
        if (oldIsFronze) {
            if (marqueeModel == MarqueeModel.AUTO_ON_VISIBLE) {
                if (isMarquee) {
                    checkScroll();
                } else {
                    doStartMarquee();
                }
            } else if (isMarquee) {
                checkScroll();
            }
        }
    }

    private void checkScroll() {
        if (isMarquee && !isFrozen) {
            postDelayed(scrollTextRunnable, FPS);
        }
    }

    private void initMaxScrollAndCheckFrezon() {
        post(makeMaxScrollAndCheckFrezon);
    }

    final Runnable makeMaxScrollAndCheckFrezon = new Runnable() {

        @Override
        public void run() {
            maxScrollY = getLineCount() * getLineHeight();
            checkEnoughToMarquee();
            checkFrozen();
        }
    };
    final Runnable scrollTextRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMarquee && !isFrozen) {
                if (mCountDelay * FPS > START_MARQUEE_TIME) {
                    //LogUtil.log("mReaderror:" + mReaderror);
                    if (mReaderror) {
                        scrollBy(0, SPEED);
                    } else {
                        mCurLineOffset = getReadLineOffset(mReadCharNo);
                        if (mCurLineOffset == -1 || mCurLineOffsetPre == -1) {
                            scrollBy(0, SPEED);
                        } else {
                            if (mCurLineOffsetPre == 0) {
                                mCurLineOffsetPre = getLineHeight() * mReadLineNo;
                            }
                            int off = mCurLineOffset - mCurLineOffsetPre;
                            //LogUtil.log("cur:" + mCurLineOffset + " Pre:" + mCurLineOffsetPre + " ChNo:" + mReadCharNo);

                            if (off > 0) {
                                scrollBy(0, off);
                            }
                        }
                        mCurLineOffsetPre = mCurLineOffset;
                    }
                }
                mCountDelay = mCountDelay + 1;
                //LogUtil.log("run" + mCountDelay);
                int scrollY = getScrollY();
                if (scrollY > maxScrollY - MAX_LINES * getLineHeight()) {
                    removeAllMessage();
                    doStopMarquee();
                    mRobotTextMarqueeOver = true;
                    //VoiceManager.getInstance().postEvent(new EventMsg(EventMsg.MSG_TEXTMARQUEE_OVER));
                    //scrollTo(0, -getHeight());
                }
                checkScroll();
            }
        }
    };

    private boolean isMarquee = false;

    public void doStartMarquee() {
        if (isMarquee) {
            return;
        }
        isMarquee = true;
        checkScroll();
    }

    public void resetMarqueeLocation() {
        scrollTo(0, 0);
    }

    public void doStopMarquee() {
        isMarquee = false;
        removeCallbacks(scrollTextRunnable);
    }

    public static int STATE_MARQUEE_RUNNING = 1;
    public static int STATE_MARQUEE_STOPED = 2;
    public static int STATE_FROZEN = 3;
    public static int STATE_NO_ENOUGH_LENGTH = 4;

    public int getMarqueeState() {
        if (isFrozen) {
            return STATE_FROZEN;
        }
        if (!isEnoughToMarquee) {
            return STATE_NO_ENOUGH_LENGTH;
        }
        if (isMarquee) {
            return STATE_MARQUEE_RUNNING;
        } else {
            return STATE_MARQUEE_STOPED;
        }
    }

    public boolean isRobotTextMarquee() {
        return mRobotTextNeedMarquee;
    }

    public boolean isRobotTextMarqueeOver() {
        return mRobotTextMarqueeOver;
    }

    public void setRobotTextMarquee(boolean marquee) {
        mRobotTextNeedMarquee = marquee;
    }

    public void setRobotTextMarqueeOver(boolean over) {
        mRobotTextMarqueeOver = over;
    }

    private MarqueeModel marqueeModel = MarqueeModel.AUTO_ON_VISIBLE;

    public void setMarqueeModel(MarqueeModel model) {
        marqueeModel = model;
        if (marqueeModel == null) {
            marqueeModel = MarqueeModel.AUTO_ON_VISIBLE;
        }
        switch (marqueeModel) {
            case AUTO_ON_VISIBLE:
                if (getVisibility() == View.VISIBLE) {
                    doStartMarquee();
                } else {
                    doStopMarquee();
                }
                break;
            case MANUAL:
                doStopMarquee();
                break;
        }
    }

    public enum MarqueeModel {
        AUTO_ON_VISIBLE,
        MANUAL
    }

    public void setDBCText(String text) {
        //LogUtil.log("setDBCText mReaderror:" + mReaderror);
        mReaderror = false;
        mCurLineOffsetPre = 0;
        mCurLineOffset = 0;
        String mTextDBC = changToDBC(text);
        mCountDelay = 0;
        setText(mTextDBC);
    }

    private String changToDBC(String text) {
        char[] c = text.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    private int getReadLineOffset(int charno) {
        if (mReaderror || charno == 0) {
            return -1;
        }
        //得到TextView的布局
        Layout layout = getLayout();
        //得到TextView显示有多少行
        int lines = getLineCount();
        int total = 0;
        for (int i = 0; i < lines; i++) {
            int linecharnum = layout.getLineEnd(i) - layout.getLineStart(i);
            if (charno >= total && charno < total + linecharnum) {
                int off = (charno - total) * getLineHeight() / linecharnum;
                off = getLineHeight() * i + off;
                mReadLineNo = i;
                //LogUtil.log("charno:" + charno + " total:" + total + " linechnum:" + linecharnum + " off:" + off + "LineNo:" + mReadLineNo);
                return off;
            }
            total = total + linecharnum;
        }
        return -1;
    }

    public void setmReadCharNo(int index) {
        mReadCharNo = index;
        //LogUtil.log("ChNo:" + mReadCharNo);
    }

    public void setReadError() {
        //LogUtil.log("setReadError:" + mReaderror);
        mReaderror = true;
    }
}