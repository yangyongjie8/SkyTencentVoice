package com.skyworthdigital.voice.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.skyworthdigital.voice.dingdang.utils.MLog;


/**
 * 自定义Y轴旋转动画
 * Created by Administrator on 2017/2/10.
 */

public class Rotate3DAnimation extends Animation {
    // 3d rotate
    private float mFromDegrees;
    private float mToDegrees;
    private float mCenterX;
    private float mCenterY;
    private boolean mOver = false;
    private Camera mCamera;

    public Rotate3DAnimation(float fromDegrees, float toDegrees, int width, int height) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = width / 2;
        mCenterY = height / 2;
        MLog.i("Rotate3DAnimation", "3D anim:x" + mCenterX + " y" + mCenterY);
    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCenterX = 64;//width / 2;
        mCenterY = 64;//height / 2;
        mCamera = new Camera();
        setDuration(250);
        setRepeatCount(1);
        setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + (mToDegrees - mFromDegrees) * interpolatedTime;

        final Matrix matrix = t.getMatrix();
        if (degrees == mToDegrees) {
            mOver = true;
        }
        if (mOver) {
            degrees = mToDegrees;
        }
        mCamera.save();

        mCamera.rotateY(degrees);
        mCamera.getMatrix(matrix);
        mCamera.restore();
        //
        //LogUtil.log(" degrees:" + degrees);
        matrix.preTranslate(-mCenterX, -mCenterY);
        matrix.postTranslate(mCenterX, mCenterY);
    }
}