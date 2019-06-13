package com.skyworthdigital.voice.tencent_module.domains.fm;

import android.content.Context;

import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.tencent_module.model.TemplateItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDT03046 on 2018/7/26.
 */
public class FMUtils {

    //封装新闻的假数据到list中返回,以后数据会从数据库中获取
    public static ArrayList<FMBean> getAllNews(Context context, List<TemplateItem> list) {

        ArrayList<FMBean> arrayList = new ArrayList<FMBean>();

        for(TemplateItem item:list)
        {
            FMBean newsBean = new FMBean();
            newsBean.title =item.mTitle;
            newsBean.des=item.mDescription;// "搜索算法似懂非懂三分得手房贷首付第三方的手";
            newsBean.news_url= item.mDestURL;
            newsBean.icon =item.mContentURL;// ContextCompat.getDrawable(context, R.drawable.head_default_00000);; //通过context对象将一个资源id转换成一个Drawable对象。
            arrayList.add(newsBean);
            MLog.d("audio",newsBean.title);
        }
        return arrayList;
    }

}
