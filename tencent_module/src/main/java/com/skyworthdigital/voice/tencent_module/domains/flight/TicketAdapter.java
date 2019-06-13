package com.skyworthdigital.voice.tencent_module.domains.flight;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyworthdigital.voice.common.utils.StringUtils;
import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.List;

/**
 * Created by SDT03046 on 2018/7/26.
 */

public class TicketAdapter extends BaseAdapter {
    private AsyncImageLoader mAsyncImageLoader;
    private List<FlightItem> list;
    private Context context;

    //通过构造方法接受要显示的新闻数据集合
    public TicketAdapter(Context context, List<FlightItem> list) {
        this.list = list;
        this.context = context;
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
        View view;
       if (convertView != null) {
            view = convertView;
        } else {
            view = View.inflate(context, R.layout.item_flight_ticket, null);//将一个布局文件转换成一个view对象
        }
        //2.获取view上的子控件对象
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView starttime = (TextView) view.findViewById(R.id.starttime);
        TextView origin = (TextView) view.findViewById(R.id.origin);
        TextView arrivetime = (TextView) view.findViewById(R.id.arrivetime);
        TextView destination = (TextView) view.findViewById(R.id.destination);
        TextView price = (TextView) view.findViewById(R.id.txtprice);

        TextView price1 = (TextView) view.findViewById(R.id.price1);
        TextView price2 = (TextView) view.findViewById(R.id.price2);
        TextView totaltime = (TextView) view.findViewById(R.id.totaltime);
        TextView info = (TextView) view.findViewById(R.id.info);

        //3.获取postion位置条目对应的list集合中的新闻数据，Bean对象
        FlightItem newsBean = list.get(position);

        //4.将数据设置给这些子控件做显示
        loadImage(newsBean.mIconUrl, icon);
        //Glide.with(context).load(newsBean.mTeamLogo).error(R.drawable.index_default_postersbg).placeholder(R.drawable.index_default_postersbg).diskCacheStrategy(DiskCacheStrategy.NONE).into(team_icon);
        starttime.setText(StringUtils.stampToDate(newsBean.mDepartTimestamp));
        arrivetime.setText(StringUtils.stampToDate(newsBean.mArriveTimestamp));
        Long lt = newsBean.mArriveTimestamp - newsBean.mDepartTimestamp;
        long hour = lt / 3600;
        long min = (lt - hour * 3600) / 60;
        totaltime.setText(hour + "小时" + min + "分钟");
        origin.setText(newsBean.mOrigin.mAirport.mAirportName + newsBean.mOrigin.mTerminal);
        destination.setText(newsBean.mDestination.mAirport.mAirportName + newsBean.mDestination.mTerminal);
        if (newsBean.mPolicyInfoList != null && newsBean.mPolicyInfoList.size() > 0) {
            //MLog.d("wyf", "price:" + newsBean.mPolicyInfoList.get(0).mPrice);
            price.setText("¥" + String.valueOf(newsBean.mPolicyInfoList.get(0).mPrice) + "起");
            StringBuilder sb = new StringBuilder();
            sb.append("¥");
            sb.append(String.valueOf(newsBean.mPolicyInfoList.get(0).mPrice));
            if (newsBean.mPolicyInfoList.get(0).mClassInfoList != null && newsBean.mPolicyInfoList.get(0).mClassInfoList.size() > 0) {
                sb.append("(");
                sb.append(newsBean.mPolicyInfoList.get(0).mClassInfoList.get(0).mClassName);
                sb.append("/");
                sb.append(newsBean.mPolicyInfoList.get(0).mRemainCount + "张");
                sb.append(") ");
            }
            sb.append(newsBean.mPolicyInfoList.get(0).mDiscountRate);
            sb.append("折 ");
            price1.setText(sb.toString());
        } else {
            price.setText("");
            price1.setText("");
        }

        if (newsBean.mPolicyInfoList != null && newsBean.mPolicyInfoList.size() > 1 && newsBean.mPolicyInfoList.get(1).mClassInfoList != null && newsBean.mPolicyInfoList.get(1).mClassInfoList.size() > 0) {
            MLog.d("wyf", "price1:" + newsBean.mPolicyInfoList.get(1).mPrice);
            StringBuilder sb = new StringBuilder();
            sb.append("¥");
            sb.append(String.valueOf(newsBean.mPolicyInfoList.get(1).mPrice));
            sb.append("(");
            sb.append(newsBean.mPolicyInfoList.get(1).mClassInfoList.get(0).mClassName);
            sb.append("/");
            sb.append(newsBean.mPolicyInfoList.get(1).mRemainCount + "张");
            sb.append(") ");
            sb.append(newsBean.mPolicyInfoList.get(1).mDiscountRate);
            sb.append("折");
            price2.setText(sb.toString());
        } else {
            price2.setText("");
        }

        StringBuilder strinfo = new StringBuilder();
        strinfo.append(newsBean.mCompanyName);
        strinfo.append(newsBean.mFlightNo);
        if (newsBean.mCraftInfo != null && !TextUtils.isEmpty(newsBean.mCraftInfo.mKind)) {
            strinfo.append(newsBean.mCraftInfo.mKind);
            strinfo.append("型机");
        }
        info.setText(strinfo.toString());
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
