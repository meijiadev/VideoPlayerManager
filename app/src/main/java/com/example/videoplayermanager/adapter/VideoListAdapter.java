package com.example.videoplayermanager.adapter;

import com.chad.library.adapter.base.BaseViewHolder;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.bean.VideoMessage;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VideoListAdapter extends BaseAdapter<VideoMessage.TrailersBean> {

    public VideoListAdapter(int layoutResId) {
        super(layoutResId);
    }

    public VideoListAdapter(int layoutResId, @Nullable List<VideoMessage.TrailersBean> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<VideoMessage.TrailersBean> data) {
        super.setNewData(data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, VideoMessage.TrailersBean item) {
        super.convert(helper, item);
        helper.setText(R.id.tvMovieName,item.getMovieName());
    }
}
