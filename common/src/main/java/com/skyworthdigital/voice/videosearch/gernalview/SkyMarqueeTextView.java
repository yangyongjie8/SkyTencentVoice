package com.skyworthdigital.voice.videosearch.gernalview;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class SkyMarqueeTextView extends TextView {

    public SkyMarqueeTextView(Context context) {
        super(context);
        SkyMarqueeTextView.this.setEllipsize(TruncateAt.MARQUEE);
    }

    public SkyMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SkyMarqueeTextView.this.setEllipsize(TruncateAt.MARQUEE);
    }

    public SkyMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        SkyMarqueeTextView.this.setEllipsize(TruncateAt.MARQUEE);
    }
    
}
