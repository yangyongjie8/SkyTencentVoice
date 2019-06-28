package com.skyworthdigital.voice.tencent_module.domains.tv;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.model.TvProgramsItem;

import java.util.List;

/**
 * Created by SDT03046 on 2018/7/26.
 */

public class TvProgramListAdapter extends BaseAdapter {
    private List<TvProgramsItem> list;
    private Context context;
    private AsyncImageLoader mAsyncImageLoader;
    private static final int PAGE_ITEM_COUNT = 7;

    //通过构造方法接受要显示的新闻数据集合
    public TvProgramListAdapter(Context context, List<TvProgramsItem> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        int count;
        if (list.size() % 2 > 0) {
            count = list.size() / 2 + 1;
        } else {
            count = list.size() / 2;
        }
        if (count % PAGE_ITEM_COUNT == 0) {
            return count;
        } else {
            return (count / PAGE_ITEM_COUNT * PAGE_ITEM_COUNT + PAGE_ITEM_COUNT);
        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.item_tvprogram, null);

        TextView programname1 = (TextView) view.findViewById(R.id.programname1);
        TextView programname2 = (TextView) view.findViewById(R.id.programname2);
        ImageView week1 = (ImageView) view.findViewById(R.id.week1);
        ImageView week2 = (ImageView) view.findViewById(R.id.week2);
        ImageView icon1 = (ImageView) view.findViewById(R.id.icon1);
        ImageView icon2 = (ImageView) view.findViewById(R.id.icon2);
        RelativeLayout item = (RelativeLayout) view.findViewById(R.id.item);
        if (position % PAGE_ITEM_COUNT % 2 == 0) {
            item.setBackgroundColor(Color.parseColor("#414b55"));//0,2,4,6
        } else {
            item.setBackgroundColor(Color.parseColor("#2b353f"));//1,3,5,8,10,12
        }
        int pos1 = position / PAGE_ITEM_COUNT * PAGE_ITEM_COUNT*2 + position % PAGE_ITEM_COUNT;
        int pos2 = pos1 + PAGE_ITEM_COUNT;
        icon1.setVisibility(View.INVISIBLE);
        icon2.setVisibility(View.INVISIBLE);
        if (pos1 < list.size()) {
            TvProgramsItem newsBean = list.get(pos1);
            if (newsBean.mIsPlaying == 1) {
                icon1.setVisibility(View.VISIBLE);
            }
            programname1.setText(newsBean.mStartTime + " " + newsBean.mTVName);
            if (pos1 == 0 || (pos1 > 1 && !TextUtils.equals(list.get(pos1 - 1).mWeekPrint, newsBean.mWeekPrint))) {
                week1.setBackground(getWeekIcon(newsBean.mWeekPrint));
            } else {
                week1.setBackgroundResource(R.drawable.transparent);
            }
        } else {
            item.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            programname1.setText("");
            week1.setBackground(getWeekIcon(""));
        }
        if (pos2 < list.size()) {
            TvProgramsItem newsBean = list.get(pos2);
            if (newsBean.mIsPlaying == 1) {
                icon2.setVisibility(View.VISIBLE);
            }
            programname2.setText(newsBean.mStartTime + " " + newsBean.mTVName);
            if (pos2 > 1 && !TextUtils.equals(list.get(pos2 - 1).mWeekPrint, newsBean.mWeekPrint)) {
                week2.setBackground(getWeekIcon(newsBean.mWeekPrint));
            } else {
                week2.setBackgroundResource(R.drawable.transparent);
            }
        } else {
            programname2.setText("");
            week2.setBackground(getWeekIcon(""));
        }
        //}
        return view;
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

    private Drawable getWeekIcon(String week) {
        switch (week) {
            case "周一":
                return context.getResources().getDrawable(R.drawable.monday);
            case "周二":
                return context.getResources().getDrawable(R.drawable.tuesday);
            case "周三":
                return context.getResources().getDrawable(R.drawable.wednesday);
            case "周四":
                return context.getResources().getDrawable(R.drawable.thursday);
            case "周五":
                return context.getResources().getDrawable(R.drawable.friday);
            case "周六":
                return context.getResources().getDrawable(R.drawable.saturday);
            case "周日":
                return context.getResources().getDrawable(R.drawable.sunday);
            default:
                break;
        }
        return context.getResources().getDrawable(R.drawable.transparent);
    }
}
