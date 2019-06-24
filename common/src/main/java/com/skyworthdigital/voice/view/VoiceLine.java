package com.skyworthdigital.voice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.skyworthdigital.voice.VoiceApp;
import com.skyworthdigital.voice.common.R;


public class VoiceLine extends View {
    //private int height;
    private int width;

    private Path path1;//第一条线
    private Path path2;//第二条线

    private Paint paint;

    private float waveHeight_big;//大真毒
    private int mSpeed;

    //private float amplitude_big;//大振幅

    private float frequency;//频率
    private static int mMaxLenth = 0;
    private int mStopIndex = 0;
    private static int STATUS_START = 1;
    private static int STATUS_STOP = 2;
    private static int STATUS_DEFAULT = 0;
    private int mVoiceStatus = STATUS_DEFAULT;
    /**
     * 线的偏移
     */
    private float startAngle1 = (float) (Math.PI / 4);

    private int i = 0;
    private int mTimeDuration = 50;

    private static final String DEFAULT_LENE_COLOR = "#92d5fc";
    private static final String TRAN_LENE_COLOR = "#00bec1cc";
    private int mLineBaseHeight = 100;

    public VoiceLine(Context context) {
        this(context, null);
    }

    public VoiceLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public VoiceLine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.VoiceLine);
        int orange = Color.parseColor(DEFAULT_LENE_COLOR);
        //amplitude_big = ta.getDimension(R.styleable.VoiceLine_amplitude_big, 100);
        frequency = ta.getFloat(R.styleable.VoiceLine_frequency, 100);
        int lineColor = ta.getColor(R.styleable.VoiceLine_lineColor, orange);
        setVoiceLineBaseHeight();

        paint = new Paint();
        paint.setColor(lineColor);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        path1 = new Path();
        path2 = new Path();
        ta.recycle();
    }

    private void setVoiceLineBaseHeight() {
        if (VoiceApp.getVoiceApp().mScreenWidth <= 1280) {
            mLineBaseHeight = 70;
        } else {
            mLineBaseHeight = 100;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        if (width == 0) {
            width = getMeasuredWidth();
        }
    }

    private Runnable mRunable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mVoiceStatus == STATUS_START) {
                i = 0;
            } else if (mVoiceStatus == STATUS_STOP) {
                mMaxLenth -= (10 * mStopIndex * mStopIndex);
                mTimeDuration -= 1;
                if (mMaxLenth < 0) {
                    mMaxLenth = 1;
                }
                if (mStopIndex < 3) {
                    mStopIndex += 1;
                }
            }

            path1.reset();
            path2.reset();
            startAngle1 += (Math.PI / 4);
            postInvalidate();
        }
    };

    /*
    *左边线条渐变
     */
    private LinearGradient getShaderLeft() {
        int orange = Color.parseColor(DEFAULT_LENE_COLOR);
        int back = Color.parseColor(TRAN_LENE_COLOR);
        return new LinearGradient(getMeasuredWidth() / 2, 0, 0, 0, new int[]{orange, back}, new float[]{0.9f, 1.0f}, Shader.TileMode.CLAMP);
    }

    /*
    *右边线条渐变
     */
    private LinearGradient getShaderRight() {
        int orange = Color.parseColor(DEFAULT_LENE_COLOR);
        int back = Color.parseColor(TRAN_LENE_COLOR);
        return new LinearGradient(getMeasuredWidth() / 2, 0, getMeasuredWidth(), 0, new int[]{orange, back}, new float[]{0.9f, 1.0f}, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        if (mVoiceStatus == STATUS_DEFAULT) {
            return;
        }

        if (mVoiceStatus == STATUS_START) {
            // Log.i("wyf", "onDraw:" + "mDirection:" + mDirection + " idx:" + stepcnt + " left:" + getLeft() + " width:" + +width + " padding:" + getPaddingLeft());
            for (i = 0; i <= width / 2 && i <= (mSpeed + mMaxLenth); i += 20) {
                float y = (float) (mLineBaseHeight - waveHeight_big / 2 + waveHeight_big / 2 * Math.sin(i * (2 * Math.PI / frequency) + startAngle1)) + getTop();
                //Log.i("wyf", "i:" + i + " mSpeed:" + mSpeed + " y:" + y);
                if (i == 0) {
                    //设置path的起点
                    path1.moveTo(width / 2, y);
                } else {
                    //连线
                    path1.lineTo(width / 2 - i, y);
                }
                if (i == 0) {
                    //设置path的起点
                    path2.moveTo(width / 2, y);
                } else {
                    //连线
                    path2.lineTo(width / 2 + i, y);
                }
            }
            if (i > mMaxLenth) {
                mMaxLenth = i;
            }
        } else if (mVoiceStatus == STATUS_STOP) {
            for (i = 0; i <= mMaxLenth && mMaxLenth > 0 && i <= width / 2; i += 20) {
                float y = (float) (mLineBaseHeight - waveHeight_big / 2 + waveHeight_big / 2 * Math.sin(i * (2 * Math.PI / frequency) + startAngle1)) + getTop();
                if (i == 0) {
                    //设置path的起点
                    path1.moveTo(width / 2, y);
                } else {
                    //连线
                    path1.lineTo(width / 2 - i, y);
                }
                if (i == 0) {
                    //设置path的起点
                    path2.moveTo(width / 2, y);
                } else {
                    //连线
                    path2.lineTo(width / 2 + i, y);
                }
            }

            if (mMaxLenth <= 1) {
                mVoiceStatus = STATUS_DEFAULT;
            }
        }

        //每隔150毫秒刷新一次界面  将所有的起点增加1/4π   让曲线动起来
        postDelayed(mRunable, mTimeDuration);
        paint.setShader(getShaderLeft());
        canvas.drawPath(path1, paint);

        paint.setShader(getShaderRight());
        canvas.drawPath(path2, paint);
    }

    private void start() {
        if (mVoiceStatus != STATUS_START) {
            mVoiceStatus = STATUS_START;
            mTimeDuration = 50;
            path1.reset();
            path2.reset();
            postInvalidate();
        }
    }

    public void stop() {
        if (mVoiceStatus != STATUS_STOP) {
            mVoiceStatus = STATUS_STOP;
            waveHeight_big = 2;
            mTimeDuration = 10;
            mStopIndex = 1;
            path1.reset();
            path2.reset();
            postInvalidate();
        }
    }

    public void setWaveHeight_big(int height) {
        if (VoiceApp.getVoiceApp().mScreenWidth > 1280) {
            if (height >= 90) {
                waveHeight_big = 45;
            } else if (height >= 0) {
                waveHeight_big = height / 2;
            }
        } else {
            if (height >= 50) {
                waveHeight_big = 25;
            } else if (height >= 0) {
                waveHeight_big = height / 2;
            }
        }
        if (height > 15) {
            mSpeed = 250;
        } else if (height >= 1) {
            mSpeed = 200;
        } else {
            mSpeed = 100;
        }

        start();
    }
}
