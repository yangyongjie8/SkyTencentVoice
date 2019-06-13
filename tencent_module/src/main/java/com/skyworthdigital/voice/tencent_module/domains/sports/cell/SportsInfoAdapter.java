package com.skyworthdigital.voice.tencent_module.domains.sports.cell;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skyworthdigital.voice.tencent_module.R;

import java.util.List;

/**
 * Created by SDT03046 on 2018/7/26.
 */

public class SportsInfoAdapter extends BaseAdapter {

    private List<SportsInfoCell.InfoItem> list;
    private Context context;

    //通过构造方法接受要显示的新闻数据集合
    public SportsInfoAdapter(Context context, List<SportsInfoCell.InfoItem> list) {
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
        View view = null;
        //1.复用converView优化listview,创建一个view作为getview的返回值用来显示一个条目
        if (convertView != null) {
            view = convertView;
        } else {
            //方法一：推荐
            //context:上下文, resource:要转换成view对象的layout的id, root:将layout用root(ViewGroup)包一层作为codify的返回值,一般传null
            view = View.inflate(context, R.layout.item_sport_info, null);//将一个布局文件转换成一个view对象
        }
        //2.获取view上的子控件对象
        TextView title1 = (TextView) view.findViewById(R.id.title1);
        //TextView name1 = (TextView) view.findViewById(R.id.name1);
        TextView title2 = (TextView) view.findViewById(R.id.title2);
        //TextView name2 = (TextView) view.findViewById(R.id.name2);
        //3.获取postion位置条目对应的list集合中的新闻数据，Bean对象
        SportsInfoCell.InfoItem newsBean = list.get(position);
        //4.将数据设置给这些子控件做显示

        title1.setText(newsBean.mCnName1 + ":"+newsBean.mValue1);
        if (TextUtils.isEmpty(newsBean.mCnName2)) {
            title2.setText("");
        } else {
            title2.setText(newsBean.mCnName2 + ":"+newsBean.mValue2);
        }
        //name1.setText(newsBean.mValue1);
        //name2.setText(newsBean.mValue2);

        return view;
    }


}
