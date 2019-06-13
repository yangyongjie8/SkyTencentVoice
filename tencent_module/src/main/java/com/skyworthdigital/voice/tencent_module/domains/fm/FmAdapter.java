package com.skyworthdigital.voice.tencent_module.domains.fm;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.ArrayList;

/**
 * Created by SDT03046 on 2018/7/26.
 */

public class FmAdapter extends BaseAdapter {

    private ArrayList<FMBean> list;
    private Context context;

    //通过构造方法接受要显示的新闻数据集合
    public FmAdapter(Context context, ArrayList<FMBean> list) {
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
            view = View.inflate(context, R.layout.item_fm_layout, null);
        }
        SimpleDraweeView item_img_icon = (SimpleDraweeView) view.findViewById(R.id.item_img_icon);
        TextView item_tv_title = (TextView) view.findViewById(R.id.item_tv_title);

        FMBean newsBean = list.get(position);

        DraweeController controller =
                Fresco
                        .newDraweeControllerBuilder()
                        .setControllerListener(null)
                        .setAutoPlayAnimations(true)
                        .setUri(newsBean.icon)
                        .build();
        item_img_icon.setController(controller);

        //Glide.with(context).load(newsBean.icon).error(R.drawable.index_default_postersbg).placeholder(R.drawable.index_default_postersbg).diskCacheStrategy(DiskCacheStrategy.NONE).into(item_img_icon);
        item_tv_title.setText(newsBean.title);
        return view;
    }


}
