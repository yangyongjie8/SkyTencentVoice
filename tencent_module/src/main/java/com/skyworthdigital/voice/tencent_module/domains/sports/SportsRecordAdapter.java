package com.skyworthdigital.voice.tencent_module.domains.sports;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

public class SportsRecordAdapter extends BaseAdapter {
    private AsyncImageLoader mAsyncImageLoader;
    private List<TeamStatVecObj> list;
    private Context context;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //case MSG_DRAW:
                //Glide.with(context).load(msg.obj).error(R.drawable.index_default_postersbg).placeholder(R.drawable.index_default_postersbg).diskCacheStrategy(DiskCacheStrategy.NONE).into(msg.arg1);
                //    break;
                default:
                    break;
            }
        }
    };

    //通过构造方法接受要显示的新闻数据集合
    public SportsRecordAdapter(Context context, List<TeamStatVecObj> list) {
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
        View view = View.inflate(context, R.layout.item_sports_record, null);
        //2.获取view上的子控件对象
        ImageView team_icon = (ImageView) view.findViewById(R.id.team_icon);
        TextView rank_txt = (TextView) view.findViewById(R.id.rank_txt);
        ImageView rank_img = (ImageView) view.findViewById(R.id.rank_img);
        TextView teamname_txt = (TextView) view.findViewById(R.id.teamname_txt);
        TextView win_txt = (TextView) view.findViewById(R.id.win_txt);
        TextView lost_txt = (TextView) view.findViewById(R.id.lost_txt);
        TextView score_txt = (TextView) view.findViewById(R.id.score_txt);
        RelativeLayout item = (RelativeLayout) view.findViewById(R.id.item);
        //3.获取postion位置条目对应的list集合中的新闻数据，Bean对象
        TeamStatVecObj newsBean = list.get(position);
        if (position % 2 == 0) {
            item.setBackgroundColor(Color.parseColor("#414b55"));//0,2,4,6
        } else {
            item.setBackgroundColor(Color.parseColor("#2b353f"));//1,3,5,8,10,12
        }
        //4.将数据设置给这些子控件做显示
        if (TextUtils.isEmpty(newsBean.mTitle)) {
            //view.setBackgroundColor(context.getResources().getColor(R.color.bg));
            team_icon.setVisibility(View.VISIBLE);
            loadImage(newsBean.mTeamLogo, team_icon);
            //Glide.with(context).load(newsBean.mTeamLogo).error(R.drawable.index_default_postersbg).placeholder(R.drawable.index_default_postersbg).diskCacheStrategy(DiskCacheStrategy.NONE).into(team_icon);
            rank_txt.setText(newsBean.mRank);
            if (TextUtils.equals("1",newsBean.mRank)) {
                rank_img.setBackground(context.getResources().getDrawable(R.drawable.gold));
            } else if (TextUtils.equals("2",newsBean.mRank)) {
                rank_img.setBackground(context.getResources().getDrawable(R.drawable.silver));
            } else if (TextUtils.equals("3",newsBean.mRank)) {
                rank_img.setBackground(context.getResources().getDrawable(R.drawable.copper));
            } else {
                rank_img.setBackground(context.getResources().getDrawable(R.drawable.transparent));
            }
            teamname_txt.setText(newsBean.mTeamName);
            win_txt.setText("" + newsBean.mWinMatchCount);
            lost_txt.setText("" + newsBean.mLostMatchCount);
            score_txt.setText(newsBean.mScore);
            team_icon.setVisibility(View.VISIBLE);
        } else {
            //view.setBackgroundColor(context.getResources().getColor(R.color.gray_black));
            teamname_txt.setText(newsBean.mTitle);
            rank_txt.setText("排名");
            win_txt.setText("胜");
            lost_txt.setText("负");
            score_txt.setText("胜率");
            team_icon.setImageResource(R.drawable.index_default_postersbg);
            team_icon.setVisibility(View.INVISIBLE);
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
