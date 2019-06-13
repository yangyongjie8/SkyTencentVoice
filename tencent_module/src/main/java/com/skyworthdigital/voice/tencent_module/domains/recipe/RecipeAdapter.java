package com.skyworthdigital.voice.tencent_module.domains.recipe;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

public class RecipeAdapter extends BaseAdapter {

    private ArrayList<RecipeListItem> list;
    private Context context;
    private int mPlayPos = 0;
    LayoutInflater inflater;

    //通过构造方法接受要显示的新闻数据集合
    RecipeAdapter(Context context, ArrayList<RecipeListItem> list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (list.size() > 5) ? 5 : list.size();
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
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recipe_item, null);
            viewHolder = new ViewHolder();
            viewHolder.item_img_icon = (SimpleDraweeView) convertView.findViewById(R.id.img);
            viewHolder.item_tv_title = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RecipeListItem newsBean = list.get(position);
        if (TextUtils.isEmpty(newsBean.icon)) {
            viewHolder.item_img_icon.setVisibility(View.GONE);
        } else {

            DraweeController controller =
                    Fresco
                            .newDraweeControllerBuilder()
                            .setControllerListener(null)
                            .setAutoPlayAnimations(true)
                            .setUri(newsBean.icon)
                            .build();
            viewHolder.item_img_icon.setController(controller);
        }
        viewHolder.item_tv_title.setText(newsBean.title);

        if (position == mPlayPos) {
            viewHolder.item_tv_title.setBackgroundColor(context.getResources().getColor(R.color.new_gold));
        }
        return convertView;
    }

    void setPlayPos(int pos) {
        mPlayPos = pos;
    }

    int getPlayPos() {
        return mPlayPos;
    }

    //辅助类
    class ViewHolder {
        SimpleDraweeView item_img_icon;
        TextView item_tv_title;
    }
}
