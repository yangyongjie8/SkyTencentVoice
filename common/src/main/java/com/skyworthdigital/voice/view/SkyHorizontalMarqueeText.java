package com.skyworthdigital.voice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * 该类继承TextView，实现内容过长显示不完整的情况下水平自动滚动。
 * 即使控件当前状态不是focus状态，都能自动滚动。
 * Created by SDT03046 on 2017/6/28.
 */


public class SkyHorizontalMarqueeText extends TextView {
    public SkyHorizontalMarqueeText(Context context) {
        super(context);
    }

    public SkyHorizontalMarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkyHorizontalMarqueeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //返回textview是否处在选中的状态
    //而只有选中的textview才能够实现跑马灯效果
    @Override
    public boolean isFocused() {
        return true;
    }
}