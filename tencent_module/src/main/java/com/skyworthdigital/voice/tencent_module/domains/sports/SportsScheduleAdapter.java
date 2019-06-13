package com.skyworthdigital.voice.tencent_module.domains.sports;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.List;

/**
 * Created by SDT03046 on 2018/7/26.
 */

public class SportsScheduleAdapter extends BaseAdapter {
    private AsyncImageLoader mAsyncImageLoader;
    private List<SportsDataObj> list;
    private Context context;

    //通过构造方法接受要显示的新闻数据集合
    public SportsScheduleAdapter(Context context, List<SportsDataObj> list) {
        this.list = list;
        this.context = context;

        Log.d("wyf", "size:" + list.size());
        for (SportsDataObj tmp : list)
            Log.d("wyf", "item:" + tmp.mHomeTeam.teamName);
    }

    @Override
    public int getCount() {
        return list.size();
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

        //方法一：推荐
        //context:上下文, resource:要转换成view对象的layout的id, root:将layout用root(ViewGroup)包一层作为codify的返回值,一般传null
        View view = View.inflate(context, R.layout.item_sport_schedule, null);//将一个布局文件转换成一个view对象

        RelativeLayout item = (RelativeLayout) view.findViewById(R.id.item);
        if (position % 2 == 0) {
            item.setBackgroundColor(Color.parseColor("#414b55"));//0,2,4,6
        } else {
            item.setBackgroundColor(Color.parseColor("#2b353f"));//1,3,5,8,10,12
        }
        //2.获取view上的子控件对象
        ImageView team1_icon = (ImageView) view.findViewById(R.id.team1_icon);
        TextView name1_txt = (TextView) view.findViewById(R.id.name1_txt);

        ImageView team2_icon = (ImageView) view.findViewById(R.id.team2_icon);
        TextView name2_txt = (TextView) view.findViewById(R.id.name2_txt);

        TextView clock_txt = (TextView) view.findViewById(R.id.time_txt);
        TextView date_txt = (TextView) view.findViewById(R.id.date_txt);

        //3.获取postion位置条目对应的list集合中的新闻数据，Bean对象
        SportsDataObj newsBean = list.get(position);
        //4.将数据设置给这些子控件做显示

        loadImage(newsBean.mHomeTeam.teamLogo, team1_icon);
        loadImage(newsBean.mAwayTeam.teamLogo, team2_icon);
        name1_txt.setText(newsBean.mHomeTeam.teamName);
        name2_txt.setText(newsBean.mAwayTeam.teamName);

        String date = newsBean.mSportsStartTime.substring(0, newsBean.mSportsStartTime.indexOf(" "));
        String clock = newsBean.mSportsStartTime.substring(newsBean.mSportsStartTime.indexOf(" "));
        clock_txt.setText(clock);
        date_txt.setText(date);
        if (position > 0) {
            String datepre = newsBean.mSportsStartTime.substring(0, list.get(position - 1).mSportsStartTime.indexOf(" "));
            if (TextUtils.equals(date, datepre)) {
                date_txt.setText("");
            }
        }

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
}
