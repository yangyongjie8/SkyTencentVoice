package com.skyworthdigital.voice.tencent_module.domains.baike;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.tencent_module.AsrResult;
import com.skyworthdigital.voice.tencent_module.R;


/**
 * Created by SDT03046 on 2018/8/1.
 */

public class BaikeInfoCell extends FrameLayout {
    public BaikeInfoCell(Context context, Object obj) {
        super(context);
        init(context, obj);
    }

    public void init(Context context, Object obj) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.baike_info, null);

        ImageView image = (ImageView) view.findViewById(R.id.image_logo);

        if (!TextUtils.isEmpty(((AsrResult.AsrData) obj).mPicUrl)) {
            loadImage(((AsrResult.AsrData) obj).mPicUrl, image);
        } else {
            image.setVisibility(View.GONE);
        }

        TextView info = (TextView) view.findViewById(R.id.content);
        if (!TextUtils.isEmpty(((AsrResult.AsrData) obj).mBaikeInfo)) {
            info.setText(((AsrResult.AsrData) obj).mBaikeInfo);
        } else if (!TextUtils.isEmpty(((AsrResult.AsrData) obj).mJokeText)) {
            info.setText(((AsrResult.AsrData) obj).mJokeText);
        }
        addView(view);
    }

    private void loadImage(final String url, final ImageView img) {
        AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
        Drawable cacheImage = asyncImageLoader.getDrawable(url,
                new AsyncImageLoader.ImageCallback() {
                    // 请参见实现：如果第一次加载url时下面方法会执行
                    public void imageLoaded(Drawable imageDrawable) {
                        img.setImageDrawable(imageDrawable);
                    }
                });
        if (cacheImage != null) {
            img.setImageDrawable(cacheImage);
        }
    }
}
