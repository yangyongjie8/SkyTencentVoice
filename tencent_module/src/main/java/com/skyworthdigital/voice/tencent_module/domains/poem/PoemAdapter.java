package com.skyworthdigital.voice.tencent_module.domains.poem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skyworthdigital.voice.tencent_module.R;
import com.skyworthdigital.voice.tencent_module.model.DataItem;

import java.util.List;

/**
 * Created by SDT03046 on 2018/7/26.
 */

public class PoemAdapter extends BaseAdapter {

    private List<DataItem> list;
    private Context context;

    public PoemAdapter(Context context, List<DataItem> list) {
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
            view = View.inflate(context, R.layout.item_poem_layout, null);
        }

        TextView item_tv_title = (TextView) view.findViewById(R.id.item_tv_title);

        DataItem newsBean = list.get(position);
        item_tv_title.setText("《" + newsBean.mTitle + "》");
        return view;
    }


}
