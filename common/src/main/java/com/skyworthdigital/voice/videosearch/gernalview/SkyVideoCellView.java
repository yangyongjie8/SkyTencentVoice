package com.skyworthdigital.voice.videosearch.gernalview;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.skyworthdigital.voice.common.R;
import com.skyworthdigital.voice.dingdang.utils.GlobalVariable;
import com.skyworthdigital.voice.dingdang.utils.MLog;
import com.skyworthdigital.voice.videoplay.CollectionVideoInfo;
import com.skyworthdigital.voice.videoplay.SkyDetailExtensionCellInfo;
import com.skyworthdigital.voice.videoplay.SkyVideoInfo;
import com.skyworthdigital.voice.videoplay.VideoHistoryInfo;
import com.skyworthdigital.voice.videosearch.manager.IModelManager;
import com.skyworthdigital.voice.videosearch.manager.IScrollManager;
import com.skyworthdigital.voice.videosearch.utils.OtherUtil;


public class SkyVideoCellView extends SkyWin8RelativeLayout {

    private Context mContext;
    public int CELL_WIDTH = 240;
    public int CELL_HEIGHT = 346;//336;//332;
    public int HORIZONTAL_SPACING = 46;
    public int VERTICAL_SPACING = 35;//55;
    private final float LARGE_SIZE = 1.0f;
    private static int CELL_NAME_HEIGHT = 54;
    private final int NO_CORNER = 99;
    private RelativeLayout mainLayout;
    private SimpleDraweeView mImgPoster;
    private ImageView mImgVipIcon;
    private ImageView mImgSourceIcon;
    private ImageView mImgIconNum;
    private SkyMarqueeTextView mTvVideoName;
    private SkyMarqueeTextView mTvSeriesInfo;
    private TextView mTvVideoScore;
    private ImageView mImgUpdateState;
    private RelativeLayout mScoreLayout;
    private RelativeLayout mDeleteLayout;
    private ProgressBar mHistoryProgress;
    private boolean isDeleteAble = false;
    private IModelManager mIModelManager;
    private boolean isReStartToLoad = true;
    private boolean isSroceShow = false;
    private boolean isSourceIconShow = false;
    private SkyVideoInfo mSkyVideoInfo;
    private boolean isFocusedShowSeries = false;
    private ImageView mImgSpecialBg;

    public SkyVideoCellView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public SkyVideoCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public SkyVideoCellView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    /*private void initScreenParam() {
        if (MyApplication.getInstance().mScreenWidth <= 1280) {
            CELL_WIDTH = 160;//240;
            CELL_HEIGHT = 230;//346;//336;//332;
            HORIZONTAL_SPACING = 30;//46;
            VERTICAL_SPACING = 23;//35;//55;
        }
    }*/

    private void initView() {
        //initScreenParam();
        initProperty();
        initDimensValue();
        mainLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.media_cell_view, null);
        mImgPoster = (SimpleDraweeView) mainLayout.findViewById(R.id.img_simple_cell_view);
        mImgIconNum = (ImageView) mainLayout.findViewById(R.id.source_icon_num);
        mImgVipIcon = (ImageView) mainLayout.findViewById(R.id.img_vip_icon);
        mImgSourceIcon = (ImageView) mainLayout.findViewById(R.id.source_icon_view);
        mTvVideoName = (SkyMarqueeTextView) mainLayout.findViewById(R.id.tv_cell_name);
        mTvSeriesInfo = (SkyMarqueeTextView) mainLayout.findViewById(R.id.tv_series_info);
        mTvVideoScore = (TextView) mainLayout.findViewById(R.id.tv_video_score);
        mScoreLayout = (RelativeLayout) mainLayout.findViewById(R.id.score_layout);
        mDeleteLayout = (RelativeLayout) mainLayout.findViewById(R.id.delete_model_layout);
        mImgUpdateState = (ImageView) mainLayout.findViewById(R.id.img_update_state);
        mHistoryProgress = (ProgressBar) mainLayout.findViewById(R.id.history_progress);
        mImgSpecialBg = (ImageView) mainLayout.findViewById(R.id.img_special_bg);
        ViewGroup.LayoutParams lpSPoster = mImgPoster.getLayoutParams();
        lpSPoster.width = CELL_WIDTH;
        lpSPoster.height = CELL_HEIGHT;
        mImgPoster.setLayoutParams(lpSPoster);
        //int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.video_cell_focus_padding);
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
        setClipChildren(false);
        setClipToPadding(false);
        // setFocusable(true);
        // setFocusableInTouchMode(true);
    }

    private void initDimensValue() {
        CELL_WIDTH = mContext.getResources().getDimensionPixelOffset(R.dimen.v_video_cell_width);
        CELL_HEIGHT = mContext.getResources().getDimensionPixelOffset(R.dimen.v_video_cell_height);
        HORIZONTAL_SPACING = mContext.getResources().getDimensionPixelOffset(R.dimen.video_cell_h_spacing);
        VERTICAL_SPACING = mContext.getResources().getDimensionPixelOffset(R.dimen.video_cell_v_spacing);
        CELL_NAME_HEIGHT = mContext.getResources().getDimensionPixelOffset(R.dimen.video_name_height);
    }

    public void recyclerImage() {
        //LogUtil.log("recyclerImage");
        GenericDraweeHierarchy hierarchy = mImgPoster.getHierarchy();
        hierarchy.reset();
        hierarchy.setPlaceholderImage(getResources().getDrawable(R.drawable.index_default_postersbg));
        mImgPoster.setImageURI(UriUtil.parseUriOrNull(null));
    }

    public void setLoadModel(boolean reStartToLoad) {
//        this.isReStartToLoad = reStartToLoad;
    }

    public void initFirstCellViewLayout(SkyVideoInfo info) {
        //LogUtil.log("initFirstCellViewLayout info.getName() : " + info.getName());
        this.mSkyVideoInfo = info;
        setVideoName(info.getName());
        this.setTag(info);
        setSubscriptState(info.getSubscript());
        setScore(info.getDoubanScore(), info.getQmzScore());
        setSourceType(info.getSourceId());
//        setSeriesInfo(info.getSeriesText(), info.getIsFinish());
        initSeriesInfoShow(info);
    }

    public void initDelayCellViewLayout(SkyVideoInfo info, int pos) {
        //LogUtil.log("initDelayCellViewLayout info.getName() : " + info.getName());
        if (!isReStartToLoad) {
            if (getTag() != null) {
                mImgPoster.setController(null);
            }
        } else {
            if (!TextUtils.isEmpty(info.getPicUrl())) {
                showPoster(info.getPicUrl(), mImgPoster);
                showNumIcon(pos);
            }
        }
    }

    public void initCellViewLayout(SkyVideoInfo info) {
        if (!TextUtils.isEmpty(info.getPicUrl())) {
            showPoster(info.getPicUrl(), mImgPoster);
        }
        this.setTag(info);
        setSubscriptState(info.getSubscript());
        setVideoName(info.getName());
        setScore(info.getDoubanScore(), info.getQmzScore());
        setSourceType(info.getSourceId());
        initSeriesInfoShow(info);
    }

    public void initCellViewLayout(VideoHistoryInfo info) {
        this.setTag(info);
        if (!TextUtils.isEmpty(info.getPicUrl())) {
            showPoster(info.getPicUrl(), mImgPoster);
        }
        setVideoName(info.getVideoName());
        setSubscriptState(info.getSubscript());
        setScore(info.getDoubanScore(), info.getScore());
        initHistoryProgress(info.getDuration(), info.getPlayedDuration());
        setSourceType(info.getSourceId());
        refreshUpdateInfoShow(info);
        initSeriesInfoShow(info);
    }

    public void initCellViewLayout(CollectionVideoInfo info) {
        this.setTag(info);
        if (!TextUtils.isEmpty(info.getPicUrl())) {
            showPoster(info.getPicUrl(), mImgPoster);
        }
        setVideoName(info.getVideoName());
        setScore(info.getDoubanScore(), info.getScore());
        setSourceType(info.getSourceId());
        setSubscriptState(info.getSubscript());
        refreshUpdateInfoShow(info);
        initSeriesInfoShow(info);
    }

    public void initCellViewLayout(SkyDetailExtensionCellInfo info) {
        this.setTag(info);
        if (!TextUtils.isEmpty(info.getImgUrl())) {
            showPoster(info.getImgUrl(), mImgPoster);
        }
        setVideoName(info.getTitle());
        setSubscriptState(info.getCornerMarkType());
    }

    public void setSpecialBackground(int rid) {
        if (rid > 0) {
            try {
                mImgSpecialBg.setVisibility(View.VISIBLE);
                mImgSpecialBg.setBackground(mContext.getResources().getDrawable(rid));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initSeriesInfoShow(Object tagInfo) {
        if (tagInfo == null) {
            tagInfo = getTag();
        }
        if (tagInfo instanceof SkyVideoInfo) {
            SkyVideoInfo info = (SkyVideoInfo) tagInfo;
            if (((SkyVideoInfo) tagInfo).getIsSummary() != 1) {
                setSeriesInfo(info.getSeriesText(), info.getIsFinish());
            } else {
                // 电影一句话描述，暂不显示
            }
        } else if (tagInfo instanceof CollectionVideoInfo) {
            CollectionVideoInfo info = (CollectionVideoInfo) tagInfo;
            this.setTag(info);
            setSeriesInfo(info.getSeriesText(), info.getIsFinish());
        } else if (tagInfo instanceof VideoHistoryInfo) {
            VideoHistoryInfo info = (VideoHistoryInfo) tagInfo;
            this.setTag(info);
            setSeriesInfo(info.getEpisodeDesc(), 0);
        }
    }

    public void refreshUpdateInfoShow(Object tagInfo) {
        if (tagInfo == null) {
            tagInfo = getTag();
        }
        refreshUpdateInfo(tagInfo);
    }

    private void refreshUpdateInfo(Object tagInfo) {
        boolean hasUpdate = false;
        if (tagInfo instanceof VideoHistoryInfo) {
            hasUpdate = ((VideoHistoryInfo) tagInfo).isHasUpdate();
        } else if (tagInfo instanceof CollectionVideoInfo) {
            hasUpdate = ((CollectionVideoInfo) tagInfo).isHasUpdate();
        }
        if (hasUpdate) {
            OtherUtil.setViewVisibility(mImgUpdateState, View.VISIBLE);
        } else {
            OtherUtil.setViewVisibility(mImgUpdateState, View.GONE);
        }
    }

    private void initHistoryProgress(int duration, int playedDuration) {
        if (playedDuration < duration) {
            OtherUtil.setViewVisibility(mHistoryProgress, View.VISIBLE);
            mHistoryProgress.setMax(duration);
            mHistoryProgress.setProgress(playedDuration);
        } else {
            mHistoryProgress.setMax(duration);
            mHistoryProgress.setProgress(duration);
            OtherUtil.setViewVisibility(mHistoryProgress, View.VISIBLE);
            // OtherUtil.setViewVisibility(mHistoryProgress, View.GONE);
        }
    }

    private void setVideoName(String strName) {
        if (!TextUtils.isEmpty(strName)) {
            OtherUtil.setViewVisibility(mTvVideoName, View.VISIBLE);
            mTvVideoName.setText(strName);
        } else {
        }
    }

    private void showNumIcon(int pos) {
        int[] imgs = {R.drawable.index_list_label_1,
                R.drawable.index_list_label_2,
                R.drawable.index_list_label_3,
                R.drawable.index_list_label_4,
                R.drawable.index_list_label_5,
                R.drawable.index_list_label_6,
                R.drawable.index_list_label_7,
                R.drawable.index_list_label_8,
                R.drawable.index_list_label_9,
                R.drawable.index_list_label_10,
                R.drawable.index_list_label_11,
                R.drawable.index_list_label_12};
        //LogUtil.log("setVideoNum:"+pos);
        mImgIconNum.setImageResource(imgs[pos % imgs.length]);
    }

    private void setScore(float doubanScore, float qmzScore) {
        if (doubanScore > 0) {
            isSroceShow = true;
            mScoreLayout.setBackground(mContext.getResources().getDrawable(R.drawable.label_doubanscore));
            LayoutParams lp = (LayoutParams) mScoreLayout.getLayoutParams();
            lp.width = mContext.getResources().getDimensionPixelOffset(R.dimen.video_douban_sroce_width);
            mScoreLayout.setLayoutParams(lp);
            OtherUtil.setViewVisibility(mScoreLayout, View.VISIBLE);
            mTvVideoScore.setText("" + doubanScore);
        } else if (qmzScore > 0) {
            isSroceShow = true;
            mScoreLayout.setBackground(mContext.getResources().getDrawable(R.drawable.label_score));
            LayoutParams lp = (LayoutParams) mScoreLayout.getLayoutParams();
            lp.width = mContext.getResources().getDimensionPixelOffset(R.dimen.video_sroce_width);
            mScoreLayout.setLayoutParams(lp);
            OtherUtil.setViewVisibility(mScoreLayout, View.VISIBLE);
            mTvVideoScore.setText("" + qmzScore);
        } else {
            isSroceShow = false;
            OtherUtil.setViewVisibility(mScoreLayout, View.GONE);
        }
    }

    public void resetViewSizeByAbsListView() {
        AbsListView.LayoutParams lpItem = null;
        lpItem = new AbsListView.LayoutParams(CELL_WIDTH + HORIZONTAL_SPACING, CELL_HEIGHT + VERTICAL_SPACING + CELL_NAME_HEIGHT);
        this.setLayoutParams(lpItem);
        //if (V.mScreenWidth > 1280) {
        int left = mContext.getResources().getDimensionPixelOffset(R.dimen.dip_20);
        int top = mContext.getResources().getDimensionPixelOffset(R.dimen.dip_4);
        int right = mContext.getResources().getDimensionPixelOffset(R.dimen.dip_20);
        int bottom = mContext.getResources().getDimensionPixelOffset(R.dimen.dip_4);
        this.setPadding(left, top, right, bottom);//27, 6, 29, 6);
        //} else {
        //    this.setPadding(18, 4, 20, 4);
        //}
    }

    public void resetViewSizeByGridLayout() {
        GridLayout.LayoutParams lpItem = new GridLayout.LayoutParams();
        lpItem.width = CELL_WIDTH + HORIZONTAL_SPACING;
        lpItem.height = CELL_HEIGHT + VERTICAL_SPACING + CELL_NAME_HEIGHT;
        this.setLayoutParams(lpItem);
    }

    private void setSubscriptState(int state) {
        if (state <= 0 || state >= NO_CORNER) {
            OtherUtil.setViewVisibility(mImgVipIcon, View.GONE);
        } else {
            String label = "label_" + state;
            int id = OtherUtil.getDrawableIdByName(mContext, label);
            if (id > 0) {
                try {
                    mImgVipIcon.setImageResource(id);
                    mImgVipIcon.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            case GlobalVariable.MGTV_SOURCE:
                isSourceIconShow = true;
                mImgSourceIcon.setBackground(mContext.getResources().getDrawable(R.drawable.label_mangguo));
                break;
            case GlobalVariable.YOUKU_SOURCE:
                isSourceIconShow = true;
                mImgSourceIcon.setBackground(mContext.getResources().getDrawable(R.drawable.label_youku));
                break;
            case GlobalVariable.BESTV_SOURCE:
                isSourceIconShow = true;
                mImgSourceIcon.setBackground(mContext.getResources().getDrawable(R.drawable.label_bestv));
                break;
            default:
                break;
        }

    }

    private void setSeriesInfo(String strSeriesInfo, int isFinish) {
        if (!TextUtils.isEmpty(strSeriesInfo)) {
            mTvSeriesInfo.setText(strSeriesInfo);
            if (isFinish != 1) {
                isFocusedShowSeries = true;
                OtherUtil.setViewVisibility(mTvSeriesInfo, View.VISIBLE);
            } else {
                isFocusedShowSeries = false;
                OtherUtil.setViewVisibility(mTvSeriesInfo, View.GONE);
            }
        } else {
            isFocusedShowSeries = false;
            mTvSeriesInfo.setText("");
            OtherUtil.setViewVisibility(mTvSeriesInfo, View.GONE);
        }
    }

    private void showSeriesInfo(boolean isSelected) {
        String seriesText = (String) mTvSeriesInfo.getText();
        if (getTag() != null && !TextUtils.isEmpty(seriesText)) {
            if (isFocusedShowSeries) {
                OtherUtil.setViewVisibility(mTvSeriesInfo, View.VISIBLE);
            } else {
                OtherUtil.setViewVisibility(mTvSeriesInfo, isSelected ? View.VISIBLE : View.GONE);
            }
        } else {
            OtherUtil.setViewVisibility(mTvSeriesInfo, View.GONE);
        }
    }


    private void showPoster(String url, SimpleDraweeView imgPoster) {
        Uri frescoUri;
        if (url != null && url.trim().startsWith("http")) {
            frescoUri = Uri.parse(url.trim());
        } else {
            int bgid = OtherUtil.getDrawableIdByName(mContext, url);
            frescoUri = Uri.parse("res://com.skyworthdigital.skyallmedia/" + bgid);
        }
        //int width = CELL_WIDTH - mContext.getResources().getDimensionPixelOffset(R.dimen.video_cell_focus_padding);
        //int height = CELL_HEIGHT;
        Glide.with(mContext).load(frescoUri).error(R.drawable.index_default_postersbg).placeholder(R.drawable.index_default_postersbg).diskCacheStrategy(DiskCacheStrategy.NONE).into(imgPoster);
//        int width = CELL_WIDTH - mContext.getResources().getDimensionPixelOffset(R.dimen.video_cell_focus_padding);
//        int height = CELL_HEIGHT;
//        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(frescoUri)
//                .setResizeOptions(new ResizeOptions(width, height)).build();
//        DraweeController controller =
//                Fresco
//                        .newDraweeControllerBuilder()
//                        .setControllerListener(null)
//                        .setAutoPlayAnimations(true)
//                        .setUri(frescoUri)
//                        .setImageRequest(request)
//                        .build();
//        imgPoster.setController(controller);
    }

    @Override
    protected void onFocusChanged(boolean bFocused, int arg1, Rect arg2) {
        super.onFocusChanged(bFocused, arg1, arg2);
        // mTvVideoName.setSelected(bFocused);
        this.setSelected(bFocused);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        showSeriesInfo(selected);
        //showSummaryInfo(selected);
        if (isDeleteAble && mIModelManager != null) {
            if (selected && mIModelManager.isDeleteModel()) {
                OtherUtil.setViewVisibility(mDeleteLayout, View.VISIBLE);
            } else {
                OtherUtil.setViewVisibility(mDeleteLayout, View.GONE);
            }
        }
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
        this.isDeleteAble = deleteAble;
    }

    /*public void setModelManager(IModelManager modelManager) {
        this.mIModelManager = modelManager;
    }*/

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

    public void updateDeleteState(boolean showDelete) {
        try {
            OtherUtil.setViewVisibility(mDeleteLayout, showDelete ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IScrollManager mScrollManager;

    public void setScrollManager(IScrollManager scrollManager) {
        this.mScrollManager = scrollManager;
    }

}
