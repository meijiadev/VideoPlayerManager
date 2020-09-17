package com.example.videoplayermanager.ui;

import butterknife.BindView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseActivity;
import com.example.videoplayermanager.base.BaseThread;
import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.example.videoplayermanager.widget.VideoPlayerView;
import com.example.videoplayermanager.widget.VideoPlayerViewOne;
import com.hjq.toast.ToastUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;


import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends BaseActivity  {
     @BindView(R.id.videoPlayer)
     VideoPlayerView videoPlayer;
     @BindView(R.id.tvCurrentTime)
     TextView tvCurrentTime;
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

    }

    @Override
    protected void initData() {
        List<VideoOptionModel> list=new ArrayList<>();
        /*VideoOptionModel videoOptionModel=new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"framedrop",2);
        list.add(videoOptionModel);*/
       /* VideoOptionModel videoOptionMode04 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1);//是否开启缓冲
        list.add(videoOptionMode04);
        VideoOptionModel videoOptionMode13 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 10);//最大缓存时长
        list.add(videoOptionMode13);*/
        VideoOptionModel videoOptionMode05 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);//丢帧,太卡可以尝试丢帧
        list.add(videoOptionMode05);
        VideoOptionModel videoOptionMode12 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 25);//默认最大帧数25
        list.add(videoOptionMode12);
        GSYVideoManager.instance().setOptionModelList(list);
        TcpClient.getInstance(context,ClientMessageDispatcher.getInstance()).notifyService();

    }


    @Override
    protected void onStart() {
        super.onStart();
        //打开硬解码
        GSYVideoType.enableMediaCodec();
        GSYVideoType.enableMediaCodecTexture();
        new TimeThread().start();
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
    protected void onStop() {
        super.onStop();
        isRunning=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /// 关闭硬解码
        GSYVideoType.disableMediaCodec();
        GSYVideoManager.releaseAllVideos();
        ToastUtils.show("退出广告播放！");

    }

    @Override
    public void onBackPressed() {
        //释放所有
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed();
    }



    private boolean isRunning;
    public  class TimeThread extends BaseThread {
        public TimeThread(){
            isRunning=true;
        }
        @Override
        public void run() {
            super.run();
            while (isRunning){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message=new Message();
                message.what=1;
                mHandler.sendMessage(message);
            }
        }
    }
    //在主线程里面处理消息并更新UI界面
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                long sysTime = System.currentTimeMillis();//获取系统时间
                if (tvCurrentTime!=null){
                    tvCurrentTime.setText(TimeUtils.longToDate(sysTime));
                }
            }
        }
    };

}