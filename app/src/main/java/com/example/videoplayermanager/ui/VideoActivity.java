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
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.example.videoplayermanager.widget.ListGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.util.List;

public class VideoActivity extends BaseActivity implements ListGSYVideoPlayer.VideosIndex {
     @BindView(R.id.videoPlayer)
     ListGSYVideoPlayer videoPlayer;
     @BindView(R.id.layoutMessage)
     RelativeLayout layoutMessage;
     @BindView(R.id.tvFloor)
     TextView tvFloor;
     @BindView(R.id.tvNumber)
     TextView tvNumber;
     @BindView(R.id.ivIcon)
     ImageView ivIcon;

    OrientationUtils orientationUtils;
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
        videoPlayer.setVideosIndex(this::currentVideosIndex);  //注册监听播放视频的索引
    }

    @Override
    protected void initData() {
        Intent intent=getIntent();
        List<VideoModel> videoModels = VideoResourcesManager.getInstance().getVideoModels();
        int index=intent.getIntExtra("videoPosition",0);

        videoPlayer.setUp(videoModels,true,index, GlobalParameter.getDownloadFile());
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        videoPlayer.startPlayLogic();
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
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }

    @Override
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


    }
}