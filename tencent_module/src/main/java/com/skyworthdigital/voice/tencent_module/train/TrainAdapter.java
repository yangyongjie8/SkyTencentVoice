package com.skyworthdigital.voice.tencent_module.train;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.domains.train.TrainInfoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDT03046 on 2018/7/26.
 */

public class TrainAdapter extends BaseAdapter {
    private AsyncImageLoader mAsyncImageLoader;
    private List<TrainInfoItem> list;
    private Context context;

    //通过构造方法接受要显示的新闻数据集合
    public TrainAdapter(Context context, List<TrainInfoItem> list) {
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
            view = View.inflate(context, R.layout.item_train_ticket, null);//将一个布局文件转换成一个view对象
        }

        TextView starttime = (TextView) view.findViewById(R.id.starttime);
        TextView origin = (TextView) view.findViewById(R.id.origin);
        TextView arrivetime = (TextView) view.findViewById(R.id.arrivetime);
        TextView destination = (TextView) view.findViewById(R.id.destination);
        TextView price = (TextView) view.findViewById(R.id.txtprice);
        TextView totaltime = (TextView) view.findViewById(R.id.totaltime);
        TextView trainnum = (TextView) view.findViewById(R.id.trainnum);

        List<TextView> info = new ArrayList<>();
        info.add((TextView) view.findViewById(R.id.info1));
        info.add((TextView) view.findViewById(R.id.info2));
        info.add((TextView) view.findViewById(R.id.info3));
        info.add((TextView) view.findViewById(R.id.info4));
        TrainInfoItem newsBean = list.get(position);

        starttime.setText(newsBean.mfromTime);
        arrivetime.setText(newsBean.mtoTime);
        origin.setText(newsBean.mFromStation);
        destination.setText(newsBean.mToStation);
        trainnum.setText(newsBean.mTrainNum);
        StringBuilder usetime = new StringBuilder();
        if (newsBean.mUseTime / 60 >= 1) {
            usetime.append(newsBean.mUseTime / 60);
            usetime.append("时");
        }
        if (newsBean.mUseTime % 60 >= 1) {
            usetime.append(newsBean.mUseTime % 60);
            usetime.append("分");
        }

        totaltime.setText(usetime.toString());
        if (newsBean.mSeats != null && newsBean.mSeats.size() > 0) {
            price.setText(Float.valueOf(newsBean.mSeats.get(0).mPrice) + "起");
            int infoidx = 0;
            int seattotal = 0;
            for (TrainInfoItem.SeatItem item : newsBean.mSeats) {
                seattotal += item.mRemainNum;
            }
            for (TextView txt : info) {
                txt.setText("");
            }
            if (seattotal == 0) {
                info.get(0).setText("暂无余票");
            } else {
                for (int i = 0; i < newsBean.mSeats.size(); i++) {
                    String seatname = newsBean.mSeats.get(i).mSeatName;
                    int remainNum = newsBean.mSeats.get(i).mRemainNum;
                    String strPrice;
                    seatname = seatname.replace("上", "");
                    seatname = seatname.replace("中", "");
                    seatname = seatname.replace("下", "");
                    if (newsBean.mSeats.get(i).mSeatName.contains("上")) {
                        strPrice = "¥" + String.valueOf(newsBean.mSeats.get(i).mPrice) + "起";
                        for (int y = i + 1; y < newsBean.mSeats.size(); y++) {
                            if (newsBean.mSeats.get(y).mSeatName.contains(seatname)) {
                                remainNum += newsBean.mSeats.get(y).mRemainNum;
                                i = y;
                            } else {
                                i = y;
                                break;
                            }
                        }
                    } else {
                        strPrice = "¥" + String.valueOf(newsBean.mSeats.get(i).mPrice);
                    }
                    info.get(infoidx).setText(seatname + " " + remainNum + "张 " + strPrice);
                    infoidx++;
                    if (infoidx > 3) {
                        break;
                    }
                }
            }
        } else {
            price.setText("");
        }
        return view;
    }
}
