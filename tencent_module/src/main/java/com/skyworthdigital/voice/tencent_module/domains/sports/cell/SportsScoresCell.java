package com.skyworthdigital.voice.tencent_module.domains.sports.cell;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.skyworthdigital.voice.dingdang.utils.AsyncImageLoader;
import com.skyworthdigital.voice.tencent_module.AsrResult;
import com.skyworthdigital.voice.tencent_module.R;

import java.util.List;


/**
 * Created by SDT03046 on 2018/8/1.
 */

public class SportsScoresCell extends FrameLayout {
    private ListView mListview;
    //private SportsScoreAdapter myAdapter = null;
    private AsyncImageLoader mAsyncImageLoader;

    public SportsScoresCell(Context context, Object obj) {
        super(context);
        init(context, obj);
    }

    public void init(Context context, Object obj) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_sports_score, null);

        List<AsrResult.AsrData.SportsScoreObj> sportsScorelist = (List<AsrResult.AsrData.SportsScoreObj>) obj;

        AsrResult.AsrData.SportsScoreObj sportsScoreObj = sportsScorelist.get(0);

        ImageView team1_icon = (ImageView) view.findViewById(R.id.team1_icon);
        TextView name1_txt = (TextView) view.findViewById(R.id.name1_txt);

        ImageView team2_icon = (ImageView) view.findViewById(R.id.team2_icon);
        TextView name2_txt = (TextView) view.findViewById(R.id.name2_txt);

        TextView note_txt = (TextView) view.findViewById(R.id.note_txt);
        TextView time_txt = (TextView) view.findViewById(R.id.time_txt);

        TextView status_txt = (TextView) view.findViewById(R.id.status);

        TextView score1_txt = (TextView) view.findViewById(R.id.score1_txt);
        TextView score2_txt = (TextView) view.findViewById(R.id.score2_txt);

        loadImage(sportsScoreObj.mHomeTeam.teamLogo, team1_icon);
        loadImage(sportsScoreObj.mAwayTeam.teamLogo, team2_icon);
        name1_txt.setText(sportsScoreObj.mHomeTeam.teamName);
        name2_txt.setText(sportsScoreObj.mAwayTeam.teamName);
        score1_txt.setText(sportsScoreObj.mHomeTeam.teamGoal);
        score2_txt.setText(sportsScoreObj.mAwayTeam.teamGoal);
        note_txt.setText(sportsScoreObj.mCompetition + sportsScoreObj.mRoundType);
        time_txt.setText(sportsScoreObj.mSportsStartTime);
        status_txt.setText(sportsScoreObj.mPeriod);
        addView(view);
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
