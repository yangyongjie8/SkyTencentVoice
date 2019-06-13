package com.skyworthdigital.voice.videosearch.gernalview;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.videoplay.CollectionVideoInfo;
import com.skyworthdigital.voice.videoplay.SkyVideoInfo;
import com.skyworthdigital.voice.videoplay.SkyVideoSubInfo;
import com.skyworthdigital.voice.videoplay.VideoHistoryInfo;
import com.skyworthdigital.voice.videosearch.manager.IModelManager;
import com.skyworthdigital.voice.videosearch.manager.IScrollManager;
import com.skyworthdigital.voice.videosearch.utils.OtherUtil;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class SkyVideoHorizontalCellView extends SkyWin8RelativeLayout {

    private Context mContext;
    public static final int CELL_WIDTH = 345;
    public static final int CELL_HEIGHT = 194;
    public static final int HORIZONTAL_SPACING = 52;
    public static final int VERTICAL_SPACING = 55;
    private static final int CELL_NAME_HEIGHT = 66;
    private static final float LARGE_SIZE = 1.15f;
    private RelativeLayout mainLayout;
    private SimpleDraweeView mImgPoster;
    private SkyMarqueeTextView mTvVideoName;
    private TextView mTvTimeInfo;
    private TextView mTvDescInfo;
    private TextView mTvVideoScore;
    private ImageView mImgVipIcon;
    private ImageView mImgSourceIcon;
    private RelativeLayout mScoreLayout;
    private boolean isSroceShow = false;
    private boolean isSourceIconShow = false;

    public SkyVideoHorizontalCellView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public SkyVideoHorizontalCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public SkyVideoHorizontalCellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private void initView() {
        initProperty();
        mainLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.media_horizontal_cell_view, null);
        mImgPoster = (SimpleDraweeView) mainLayout.findViewById(R.id.img_simple_h_cell_view);
        mTvVideoName = (SkyMarqueeTextView) mainLayout.findViewById(R.id.tv_h_cell_name);
        mTvTimeInfo = (TextView) mainLayout.findViewById(R.id.tv_h_cell_time_info);
        mTvDescInfo = (TextView) mainLayout.findViewById(R.id.tv_h_cell_desc_info);
        mTvVideoScore = (TextView) mainLayout.findViewById(R.id.tv_video_score);
        mScoreLayout = (RelativeLayout) mainLayout.findViewById(R.id.score_layout);
        mImgVipIcon = (ImageView) mainLayout.findViewById(R.id.img_vip_icon);
        mImgSourceIcon = (ImageView) mainLayout.findViewById(R.id.source_icon_view);
        ViewGroup.LayoutParams lpSPoster = mImgPoster.getLayoutParams();
        if (lpSPoster != null) {
            lpSPoster.width = CELL_WIDTH;
            lpSPoster.height = CELL_HEIGHT;
            mImgPoster.setLayoutParams(lpSPoster);
        }
        mainLayout.setPadding(3, 3, 2, 3);
        this.setBackground(mContext.getResources().getDrawable(R.drawable.video_list_item_selector));
        // this.setBackgroundColor(OtherUtil.getRandomColor(50));
        addView(mainLayout);
    }

    private void initProperty() {
        setGravity(Gravity.CENTER);
        setIsShowAnimation(true);
        setIsWin8Formart(true);
        setMagnitudeOfEnlargement(LARGE_SIZE);
        setSkyDuration(getResources().getInteger(R.integer.views_animation_duration));
        setClipChildren(false);
        setClipToPadding(false);
        // setFocusable(true);
        // setFocusableInTouchMode(true);
    }

    public void recyclerImage() {
        //LogUtil.log("recyclerImage error111");
        GenericDraweeHierarchy hierarchy = mImgPoster.getHierarchy();
        hierarchy.reset();
        hierarchy.setPlaceholderImage(getResources().getDrawable(R.drawable.index_default_postersbg));
        mImgPoster.setImageURI(UriUtil.parseUriOrNull(null));
    }

    public void initFirstCellViewLayout(SkyVideoInfo info) {
        setVideoName(info.getName());
    }

    public void initDelayCellViewLayout(SkyVideoInfo info) {
        if (!TextUtils.isEmpty(info.getPicUrl())) {
            showPoster(info.getPicUrl(), mImgPoster);
        }
        this.setTag(info);
        setSubscriptState(info.getSubscript());
        setScore(info.getDoubanScore(), info.getQmzScore());
        setSourceType(info.getSourceId());
        setIssueTimeInfo(info);
    }

    public void initCellViewLayout(SkyVideoInfo info) {
        setVideoName(info.getName());
        if (!TextUtils.isEmpty(info.getPicUrl())) {
            showPoster(info.getPicUrl(), mImgPoster);
        }
        this.setTag(info);
        setSubscriptState(info.getSubscript());
        setScore(info.getDoubanScore(), info.getQmzScore());
        setSourceType(info.getSourceId());
        setIssueTimeInfo(info);
    }

    public void initCellViewLayout(VideoHistoryInfo info) {
        this.setTag(info);
        if (!TextUtils.isEmpty(info.getPicUrl())) {
            showPoster(info.getPicUrl(), mImgPoster);
        }
        setVideoName(info.getVideoName());
    }

    public void initCellViewLayout(CollectionVideoInfo info) {
        this.setTag(info);
        if (!TextUtils.isEmpty(info.getPicUrl())) {
            showPoster(info.getPicUrl(), mImgPoster);
        }
        setVideoName(info.getVideoName());
    }

    public void initCellViewLayout(SkyVideoSubInfo info, boolean showTimeInfo) {
        this.setTag(info);
        if (!TextUtils.isEmpty(info.getPicUrl())) {
            showPoster(info.getPicUrl(), mImgPoster);
        }
        if (showTimeInfo) {
            setIssueTimeInfo(null, info.getYear());
        }
        setVideoName(info.getName());
    }

    private void setVideoName(String strName) {
        resetVideoNameView(strName);
        if (!TextUtils.isEmpty(strName)) {
            mTvVideoName.setText(strName);
        }
    }

    private void resetVideoNameView(String strName) {
        float newWidth = getBtnTextLength(strName);
        float nameWidth = getResources().getDimension(R.dimen.horizontal_cell_name_width);
        LayoutParams lp = (LayoutParams) mTvVideoName.getLayoutParams();
        if (lp != null) {
            if (newWidth < nameWidth) {
                lp.width = LayoutParams.WRAP_CONTENT;
                mTvVideoName.setSingleLine(false);
            } else {
                lp.width = (int) nameWidth;
                mTvVideoName.setSingleLine(true);
            }
            mTvVideoName.setLayoutParams(lp);
        }
    }

    private float getBtnTextLength(String str) {
        TextPaint textPaint = mTvVideoName.getPaint();
        float textPaintWidth = textPaint.measureText(str);
        return textPaintWidth;
    }

    private void setIssueTimeInfo(SkyVideoInfo info) {
        if (info.getIsSeries() == 0) {
            String strScreenTime = OtherUtil.getTimeSpacing(mContext, info.getIssueTimeStamp());
            String strDuration = OtherUtil.getDurationInfoBySecond(info.getTimeLength());
            setIssueTimeInfo(strScreenTime, strDuration);
        } else {
            setIssueTimeInfo(null, info.getSeriesText());
        }
    }

    private void setIssueTimeInfo(String strIssueTime, String strIssueTimeStamp) {
        if (!TextUtils.isEmpty(strIssueTime)) {
            mTvTimeInfo.setText(strIssueTime);
            OtherUtil.setViewVisibility(mTvTimeInfo, View.VISIBLE);
        } else {
            mTvTimeInfo.setText("");
            OtherUtil.setViewVisibility(mTvTimeInfo, View.GONE);
        }
        if (!TextUtils.isEmpty(strIssueTimeStamp)) {
            mTvDescInfo.setText(strIssueTimeStamp);
            OtherUtil.setViewVisibility(mTvDescInfo, View.VISIBLE);
        } else {
            mTvDescInfo.setText("");
            OtherUtil.setViewVisibility(mTvDescInfo, View.GONE);
        }
    }

    private void setSubscriptState(int state) {
        switch (state) {
            case 0:
                OtherUtil.setViewVisibility(mImgVipIcon, View.GONE);
                break;
            case 1:
                mImgVipIcon.setBackground(mContext.getResources().getDrawable(R.drawable.label_vip));
                OtherUtil.setViewVisibility(mImgVipIcon, View.VISIBLE);
                break;
            case 2:
                mImgVipIcon.setBackground(mContext.getResources().getDrawable(R.drawable.buy));
                OtherUtil.setViewVisibility(mImgVipIcon, View.VISIBLE);
                break;
            default:
                break;
        }

    }

    private void setSourceType(int sourceId) {
        switch (sourceId) {
            case GlobalVariable.IQIYI_SOURCE:
                isSourceIconShow = false;
                mImgSourceIcon.setBackground(null);
                break;
            case GlobalVariable.TENCENT_SOURCE:
                isSourceIconShow = true;
                mImgSourceIcon.setBackground(mContext.getResources().getDrawable(R.drawable.label_yunshiting));
                break;
            case GlobalVariable.SOUHU_SOURCE:
                isSourceIconShow = true;
                mImgSourceIcon.setBackground(mContext.getResources().getDrawable(R.drawable.label_souhu));
                break;
            case GlobalVariable.YOUKU_SOURCE:
                isSourceIconShow = true;
                mImgSourceIcon.setBackground(mContext.getResources().getDrawable(R.drawable.label_youku));
                break;
            case GlobalVariable.MGTV_SOURCE:
                isSourceIconShow = true;
                mImgSourceIcon.setBackground(mContext.getResources().getDrawable(R.drawable.label_mangguo));
                break;
            default:
                break;
        }

    }

    private void setScore(float doubanScore, float qmzScore) {
        if (doubanScore > 0) {
            isSroceShow = true;
            mScoreLayout.setBackground(mContext.getResources().getDrawable(R.drawable.label_doubanscore));
            LayoutParams lp = (LayoutParams) mScoreLayout.getLayoutParams();
            if (lp != null) {
                lp.width = 80;
                mScoreLayout.setLayoutParams(lp);
            }
            OtherUtil.setViewVisibility(mScoreLayout, View.VISIBLE);
            mTvVideoScore.setText("" + doubanScore);
        } else if (qmzScore > 0) {
            isSroceShow = true;
            mScoreLayout.setBackground(mContext.getResources().getDrawable(R.drawable.label_score));
            LayoutParams lp = (LayoutParams) mScoreLayout.getLayoutParams();
            if (lp != null) {
                lp.width = 50;
                mScoreLayout.setLayoutParams(lp);
            }
            OtherUtil.setViewVisibility(mScoreLayout, View.VISIBLE);
            mTvVideoScore.setText("" + qmzScore);
        } else {
            isSroceShow = false;
            OtherUtil.setViewVisibility(mScoreLayout, View.GONE);
        }
    }

    public void resetViewSizeByAbsListView() {
        AbsListView.LayoutParams lpItem =
                new AbsListView.LayoutParams(
                        CELL_WIDTH + HORIZONTAL_SPACING,
                        CELL_HEIGHT + VERTICAL_SPACING + CELL_NAME_HEIGHT);
        this.setLayoutParams(lpItem);
    }

    public void resetViewSizeByGridLayout() {
        GridLayout.LayoutParams lpItem = new GridLayout.LayoutParams();
        lpItem.width = CELL_WIDTH + HORIZONTAL_SPACING;
        lpItem.height = CELL_HEIGHT + VERTICAL_SPACING;
        this.setLayoutParams(lpItem);
    }

    private void showPoster(String url, SimpleDraweeView imgPoster) {
        Uri frescoUri;
        if (url != null && url.trim().startsWith("http")) {
            frescoUri = Uri.parse(url.trim());
        } else {
            int bgid = OtherUtil.getDrawableIdByName(mContext, url);
            frescoUri = Uri.parse("res://com.skyworthdigital.skyallmedia/" + bgid);
        }
        //Glide.with(mContext).load(frescoUri).error(R.drawable.index_default_postersbg).placeholder(R.drawable.index_default_postersbg).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgPoster);
        DraweeController controller =
            Fresco
                .newDraweeControllerBuilder()
                .setControllerListener(null)
                .setAutoPlayAnimations(true)
                .setUri(frescoUri)
                .build();
        imgPoster.setController(controller);
    }

    @Override
    public void onFocusChanged(boolean bFocused, int arg1, Rect arg2) {
        super.onFocusChanged(bFocused, arg1, arg2);
        this.setSelected(bFocused);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        focusEvent(selected);
    }

    private void focusEvent(boolean hasFocused) {
        if (hasFocused) {
            if (isSourceIconShow) {
                OtherUtil.setViewVisibility(mImgSourceIcon, View.VISIBLE);
            }
            if (isSroceShow && isSourceIconShow) {
                OtherUtil.setViewVisibility(mScoreLayout, View.GONE);
            }
        } else {
            OtherUtil.setViewVisibility(mImgSourceIcon, View.GONE);
            if (isSroceShow) {
                OtherUtil.setViewVisibility(mScoreLayout, View.VISIBLE);
            }
        }
    }

    public void setDeleteAble(boolean deleteAble) {
    }

    public void setModelManager(IModelManager modelManager) {
    }

    public void startLoadPoster() {
        Object obj = getTag();
        if (obj == null) {
            return;
        }
        String picUrl = null;
        if (obj instanceof SkyVideoInfo) {
            picUrl = ((SkyVideoInfo) obj).getPicUrl();
        } else if (obj instanceof CollectionVideoInfo) {
            picUrl = ((CollectionVideoInfo) obj).getPicUrl();
        } else if (obj instanceof VideoHistoryInfo) {
            picUrl = ((VideoHistoryInfo) obj).getPicUrl();
        }
        if (!TextUtils.isEmpty(picUrl)) {
            if (mScrollManager != null && mScrollManager.isScrolling()) {
                return;
            }
            showPoster(picUrl, mImgPoster);
        }
    }

    private IScrollManager mScrollManager;

    public void setScrollManager(IScrollManager scrollManager) {
        this.mScrollManager = scrollManager;
    }

}
