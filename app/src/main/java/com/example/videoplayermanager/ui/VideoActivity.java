package com.example.videoplayermanager.ui;

import butterknife.BindView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseActivity;
import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.example.videoplayermanager.widget.VideoPlayerView;
import com.shuyu.gsyvideoplayer.GSYVideoManager;


import java.util.List;

public class VideoActivity extends BaseActivity implements VideoPlayerView.VideoStatus {
     @BindView(R.id.videoPlayer)
     VideoPlayerView videoPlayer;
 /*    @BindView(R.id.layoutMessage)
     RelativeLayout layoutMessage;
     @BindView(R.id.tvFloor)
     TextView tvFloor;
     @BindView(R.id.tvNumber)
     TextView tvNumber;
     @BindView(R.id.ivIcon)
     ImageView ivIcon;*/

    private TcpClient tcpClient;
    private List<VideoModel> videoModels;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
        tcpClient=TcpClient.getInstance(context, ClientMessageDispatcher.getInstance());
        videoModels=VideoResourcesManager.getInstance().getVideoModels();
        videoPlayer.setVideoStatus(this);
    }

    @Override
    protected void initData() {
        //增加封面
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.mipmap.poster_one);
        videoPlayer.setThumbImageView(imageView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.unRegister();
        GSYVideoManager.releaseAllVideos();

    }

    @Override
    public void onBackPressed() {
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }

    @Override
    public void playNext() {
        runOnUiThread(()->{
           /* tvFloor.setText(VideoResourcesManager.getInstance().getNextVideoModel().getFloorName());
            tvNumber.setText(VideoResourcesManager.getInstance().getNextVideoModel().getFloorNumber());
            Glide.with(context).load(VideoResourcesManager.getInstance().getNextVideoModel().getBusinessLogo()).into(ivIcon);*/
        });
    }

   /* @Override
    public void currentVideosIndex(int index,boolean hasNext,String name) {
        if (index<videoModels.size()){
            long timeDuration=videoPlayer.getDuration();
            String programNum=videoModels.get(index).getProgramNum();
            Logger.e("--------当前："+index+";当前视频总长："+timeDuration);
            tcpClient.setIndex(index,hasNext,name,programNum,(int)(timeDuration/1000));
            runOnUiThread(()->{
                tvFloor.setText(videoModels.get(index).getFloorName());
                tvNumber.setText(videoModels.get(index).getFloorNumber());
                Glide.with(context).load(videoModels.get(index).getBusinessLogo()).into(ivIcon);
            });
        }
    }*/


}