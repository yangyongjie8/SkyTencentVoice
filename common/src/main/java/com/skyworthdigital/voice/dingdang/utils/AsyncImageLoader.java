package com.skyworthdigital.voice.dingdang.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.drawable.Drawable;
import android.os.Handler;

public class AsyncImageLoader {
    private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
    private ExecutorService mExeCutorService = Executors.newFixedThreadPool(5);
    private final Handler handler = new Handler();

    public Drawable getDrawable(final String imageUrl,
                                final ImageCallback callback) {
        // 如果缓存过就从缓存中取出数据
        if (imageCache.containsKey(imageUrl)) {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            if (softReference.get() != null) {
                //LogUtil.log("AsyncImageLoader exists image");
                return softReference.get();
            }
        }
        // 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
        mExeCutorService.submit(new Runnable() {
            public void run() {
                try {
                    final Drawable drawable = loadImageFromUrl(imageUrl);
                    imageCache.put(imageUrl, new SoftReference<Drawable>(
                            drawable));
                    handler.post(new Runnable() {
                        public void run() {
                            callback.imageLoaded(drawable);
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return null;
    }

    // 从网络上取数据方法
    private Drawable loadImageFromUrl(String imageUrl) {
        InputStream is = null;
        try {
            is = new URL(imageUrl).openStream();
            return Drawable.createFromStream(is,
                    "image.jpg");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 对外界开放的回调接口
    public interface ImageCallback {
        // 注意 此方法是用来设置目标对象的图像资源
        void imageLoaded(Drawable imageDrawable);
    }
}
