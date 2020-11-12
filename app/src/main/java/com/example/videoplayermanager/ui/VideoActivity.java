package com.example.videoplayermanager.ui;

import butterknife.BindView;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import android.annotation.SuppressLint;
import android.app.smdt.SmdtManager;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseActivity;
import com.example.videoplayermanager.base.BaseThread;
import com.example.videoplayermanager.other.LogcatHelper;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.example.videoplayermanager.widget.SmartPickVideo;
import com.example.videoplayermanager.widget.TestVideoPlayer;
import com.hjq.toast.ToastUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends BaseActivity  {
     @BindView(R.id.videoPlayer)
     SmartPickVideo videoPlayer;
     @BindView(R.id.tvCurrentTime)
     TextView tvCurrentTime;
     private SmdtManager smdtManager;
     private boolean isLive;      //屏幕是否亮起


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceive(MessageEvent messageEvent){
        if (messageEvent.getType().equals(MessageEvent.Type.playOnError)){
            finish();
            TcpClient.getInstance(MyApplication.context, ClientMessageDispatcher.getInstance()).notifyVideoFinish(VideoResourcesManager.getInstance().getProgramUdid());
        }
    }

    @Override
    protected void initData() {
        TcpClient.getInstance(context,ClientMessageDispatcher.getInstance()).notifyService();
        videoPlayer.startPlay();
        //videoPlayer.setUp(VideoResourcesManager.getInstance().getVideoPath());
        smdtManager=SmdtManager.create(context);
    }

    @OnClick(R.id.tvCurrentTime)
    public void onClickViewed(View v){
        if (v.getId()==R.id.tvCurrentTime){
            if (isLive){
                //关闭背光
                if (smdtManager!=null)
                smdtManager.smdtSetLcdBackLight(0);
                isLive=false;
                ToastUtils.show("关闭背光！");
            }else {
                if (smdtManager!=null)
                smdtManager.smdtSetLcdBackLight(1);
                isLive=true;
                ToastUtils.show("开启背光！");
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //打开硬解码
        GSYVideoType.enableMediaCodec();
        GSYVideoType.enableMediaCodecTexture();
        GSYVideoType.setRenderType(GSYVideoType.SUFRACE);
        //new TimeThread().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //videoPlayer.onVideoResume();
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
        //videoPlayer.setVideoAllCallBack(null);
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
                    Thread.sleep(1000);
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