package com.skyworthdigital.voice.videosearch.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.skyworthdigital.voice.videoplay.SkyVideoInfo;
import com.skyworthdigital.voice.videosearch.gernalview.MetroRecyclerView;
import com.skyworthdigital.voice.videosearch.gernalview.SkyVideoCellView;
import com.skyworthdigital.voice.videosearch.gernalview.SkyVideoHorizontalCellView;
import com.skyworthdigital.voice.videosearch.manager.IScrollManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MediaListAdapter extends MetroRecyclerView.MetroAdapter<MediaListAdapter.SkyViewHolder> {
    private CopyOnWriteArrayList<SkyVideoInfo> infos = new CopyOnWriteArrayList<>();
    private Context mContext;
    private boolean isStartFromTop = true;
    private boolean isHorizontalModel = false;
    private int mCount = 0;

    public MediaListAdapter(Context context) {
        mContext = context;
    }

    public CopyOnWriteArrayList<SkyVideoInfo> getAllVideo() {
        return infos;
    }

    public void setListModel(boolean isHorizontalModel) {
        this.isHorizontalModel = isHorizontalModel;
    }

    public void setSelection(int selection) {
        notifyDataSetChanged();
    }

    public void replaceAllVideo(CopyOnWriteArrayList<SkyVideoInfo> list) {
        infos = list;

        mCount = (infos != null) ? infos.size() : 0;
        notifyDataSetChanged();
    }

    public void addAllVideo(List<SkyVideoInfo> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        infos.addAll(list);
        mCount = (infos != null) ? infos.size() : 0;
    }

    public void setLoadModel(boolean startFromTop) {
        this.isStartFromTop = startFromTop;
    }

    class SkyViewHolder extends MetroRecyclerView.MetroViewHolder {

        public SkyViewHolder(View view) {
            super(view);
        }

        void initFirst(SkyVideoInfo data) {
            if (data == null || itemView == null) {
                return;
            }
            if (itemView instanceof SkyVideoCellView) {
                ((SkyVideoCellView) itemView).setLoadModel(isStartFromTop);
                ((SkyVideoCellView) itemView).initFirstCellViewLayout(data);
            } else if (itemView instanceof SkyVideoHorizontalCellView) {
                ((SkyVideoHorizontalCellView) itemView).initFirstCellViewLayout(data);
            }
        }

        void initOthers(SkyVideoInfo data, int pos) {
            if (data == null || itemView == null) {
                return;
            }
            if (itemView instanceof SkyVideoCellView) {
                ((SkyVideoCellView) itemView).initDelayCellViewLayout(data, pos);
            } else if (itemView instanceof SkyVideoHorizontalCellView) {
                ((SkyVideoHorizontalCellView) itemView).initDelayCellViewLayout(data);
            }
        }

        void recyclerImage() {
            if (itemView == null) {
                return;
            }
            if (itemView instanceof SkyVideoCellView) {
                ((SkyVideoCellView) itemView).recyclerImage();
            } else if (itemView instanceof SkyVideoHorizontalCellView) {
                ((SkyVideoHorizontalCellView) itemView).recyclerImage();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCount;
        //return infos != null ? infos.size() : 0;
    }

    @Override
    public SkyViewHolder onCreateViewHolder(ViewGroup arg0, int position) {
        View childView;
        if (!isHorizontalModel) {
            childView = new SkyVideoCellView(mContext);
            ((SkyVideoCellView) childView).setScrollManager(mScrollManager);
            ((SkyVideoCellView) childView).resetViewSizeByAbsListView();
        } else {
            childView = new SkyVideoHorizontalCellView(mContext);
            ((SkyVideoHorizontalCellView) childView).setScrollManager(mScrollManager);
            ((SkyVideoHorizontalCellView) childView).resetViewSizeByAbsListView();
        }
//        childView.setFocusable(true);
//        childView.setClickable(true);
        SkyViewHolder viewHolder = new SkyViewHolder(childView);
        return viewHolder;
    }

    private IScrollManager mScrollManager;

    public void setScrollManager(IScrollManager scrollManager) {
        this.mScrollManager = scrollManager;
    }

    @Override
    public void onPrepareBindViewHolder(SkyViewHolder holder, int position) {
        holder.initFirst(infos.get(position));
    }

    @Override
    public void onDelayBindViewHolder(SkyViewHolder holder, int position) {
        holder.initOthers(infos.get(position), position);
    }

    @Override
    public void onUnBindDelayViewHolder(SkyViewHolder holder) {
        holder.recyclerImage();
    }
}