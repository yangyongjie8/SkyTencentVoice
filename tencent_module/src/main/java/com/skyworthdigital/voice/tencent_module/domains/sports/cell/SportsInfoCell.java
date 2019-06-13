package com.skyworthdigital.voice.tencent_module.domains.sports.cell;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.tencent_module.AsrResult;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDT03046 on 2018/8/1.
 */

public class SportsInfoCell extends FrameLayout {
    private SportsInfoAdapter mListAdapter;
    private ListView mListView;
    private String mLogoPath;
    private AsyncImageLoader mAsyncImageLoader;

    public SportsInfoCell(Context context,Object obj) {
        super(context);
        init(context,obj);
    }

    public void init(Context context, Object obj) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.activity_sports_info, null);
        mListView = (ListView) view.findViewById(R.id.lv_sport_info);
        //if (getIntent().hasExtra("list")) {
            List<AsrResult.AsrData.VStatisticsObj> list = (List<AsrResult.AsrData.VStatisticsObj>)obj;// getIntent().getSerializableExtra("list");
            mListAdapter = new SportsInfoAdapter(context, getList(list));
            mListView.setAdapter(mListAdapter);
        //} else {
        //    finish();
        //}

        ImageView image = (ImageView) view.findViewById(R.id.image_logo);
        if (!TextUtils.isEmpty(mLogoPath)) {
            loadImage(mLogoPath,image);
            //Glide.with(context).load(mLogoPath).error(R.drawable.index_default_postersbg).placeholder(R.drawable.index_default_postersbg).diskCacheStrategy(DiskCacheStrategy.NONE).into(image);
        } else {
            image.setVisibility(View.GONE);
        }
        addView(view);
    }

    public class InfoItem {
        String mCnName1;
        String mValue1;
        String mCnName2;
        String mValue2;
    }

    public List<InfoItem> getList(List<AsrResult.AsrData.VStatisticsObj> list) {
        List<InfoItem> newlist = new ArrayList<>();
        int index = 0;
        InfoItem cell = new InfoItem();
        for (AsrResult.AsrData.VStatisticsObj item : list) {
            if (!TextUtils.isEmpty(item.mValue) && item.mValue.startsWith("http://")) {
                mLogoPath = item.mValue;
            } else {
                if (index == 0) {
                    cell = new InfoItem();
                    cell.mCnName1 = item.mCnName;
                    cell.mValue1 = item.mValue;
                    index += 1;
                } else {
                    cell.mCnName2 = item.mCnName;
                    cell.mValue2 = item.mValue;
                    index = 0;
                    newlist.add(cell);
                }
            }
        }
        if (index > 0) {
            cell.mCnName2 = "";
            cell.mValue2 = "";
            newlist.add(cell);
        }

        return newlist;
    }

    private void loadImage(final String url, final ImageView img) {
        if (mAsyncImageLoader == null) {
            mAsyncImageLoader = new AsyncImageLoader();
        }
        Drawable cacheImage = mAsyncImageLoader.getDrawable(url,
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
