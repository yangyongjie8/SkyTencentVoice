package com.skyworthdigital.voice.videosearch.gernalview;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.skyworthdigital.voice.common.R;


public class SkyAttributeUtil {

    public interface SkyAttribute {
        public boolean needBeSelectedWhenParentIsFocused();
    }
    
    /**
     * you can show animations if the value is win8Formmat and isShowAnimation()
     * is true
     * @param context
     * @param attr
     * @param defaultValue
     * @return
     */
    
    static boolean isWin8Formart(Context context, AttributeSet attr, boolean defaultValue) {
        if (attr != null) {
            TypedArray array = context.obtainStyledAttributes(attr, R.styleable.SkyView);
            boolean ret = array.getBoolean(R.styleable.SkyView_isWin8Formart, defaultValue);
            array.recycle();
            return ret;
        }

        return defaultValue;
    }

    /**
     * whether need set childView select state or not;
     * @param context
     * @param attr
     * @param defaultValue
     * @return
     */

    static boolean needBeSelectedWhenParentIsFocused(Context context, AttributeSet attr, boolean defaultValue) {
        if (attr != null) {
            TypedArray array = context.obtainStyledAttributes(attr, R.styleable.SkyView);
            boolean ret = array.getBoolean(R.styleable.SkyView_needBeSelectedWhenParentIsFocused, defaultValue);
            array.recycle();
            return ret;
        }

        return defaultValue;
    }

    /**
     * You can show the animations if the value is true
     * @param context
     * @param attr
     * @param defaultValue
     * @return
     */
    static boolean isShowAnimation(Context context, AttributeSet attr, boolean defaultValue) {
        if (attr != null) {
            TypedArray array = context.obtainStyledAttributes(attr, R.styleable.SkyView);
            boolean ret = array.getBoolean(R.styleable.SkyView_isShowAnimation, defaultValue);
            array.recycle();
            return ret;
        }

        return defaultValue;
    }

    /**
     * get anination toX and toY ,the values is must > 1.0
     * @param context
     * @param attr
     * @param defaultValue
     * @return
     */

    static int getMagnitudeOfEnlargement(Context context, AttributeSet attr, int defaultValue) {
        if (attr != null) {
            TypedArray array = context.obtainStyledAttributes(attr, R.styleable.SkyView);
            int ret = array.getInt(R.styleable.SkyView_magnitudeOfEnlargement, defaultValue);
            array.recycle();
            return ret;
        }

        return defaultValue;
    }

    /**
     * get animation duration
     * @param context
     * @param attr
     * @param defaultValue
     * @return
     */
    static int getSkyDuration(Context context, AttributeSet attr, int defaultValue) {
        if (attr != null) {
            TypedArray array = context.obtainStyledAttributes(attr, R.styleable.SkyView);
            int ret = array.getInteger(R.styleable.SkyView_skyduration, defaultValue);
            array.recycle();
            return ret;
        }
        return defaultValue;
    }

}
