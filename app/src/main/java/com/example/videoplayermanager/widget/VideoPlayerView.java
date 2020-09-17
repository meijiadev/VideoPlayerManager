package com.example.videoplayermanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.shuyu.gsyvideoplayer.GSYVideoBaseManager;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;


public class VideoPlayerView extends StandardGSYVideoPlayer {
    private long nextPlayTime;
    private Context context;
    private long logTime1;
    private long longTime2;
    //private ImageView ivWait;
    private RelativeLayout layoutMessage;
    private TextView tvFloor;
    private TextView tvNumber;
    private ImageView ivIcon;
    private long testTimes;


    public VideoPlayerView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public VideoPlayerView(Context context) {
        super(context);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        EventBusManager.register(this);
        this.context=context;
        //ivWait=findViewById(R.id.ivWait);
        layoutMessage=findViewById(R.id.layoutMessage);
        tvFloor=findViewById(R.id.tvFloor);
        tvNumber=findViewById(R.id.tvNumber);
        ivIcon=findViewById(R.id.ivIcon);
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
    }



    /**
     * 开始准备播放视频
     * @param url
     * @return
     */
    public void startPlay(String url){
         logTime1=System.currentTimeMillis();
         //Logger.e("准备播放视频！");
         setUp(url,true, GlobalParameter.getDownloadFile(),null,false);
         startPlayLogic();
         testTimes=System.currentTimeMillis();
    }



    /**
     * 加载完成
     */
    @Override
    public void onPrepared() {
        super.onPrepared();
        long realStartTime;
        realStartTime =System.currentTimeMillis();
        Logger.e("视频真正开始播放的时间："+TimeUtils.longToDate(realStartTime));
        Logger.e("-----------------------加载完成耗时："+(realStartTime-logTime1));
        //视频真正开始播放的时间
        VideoResourcesManager.getInstance().setRealProgramUdip(VideoResourcesManager.getInstance().getProgramUdid());
        VideoResourcesManager.getInstance().setRealStartPlayTime(realStartTime);
        String floorName= VideoResourcesManager.getInstance().getNextVideoModel().getFloorName();
        String floorNumber=VideoResourcesManager.getInstance().getNextVideoModel().getFloorNumber();
        String imageUrl=VideoResourcesManager.getInstance().getNextVideoModel().getBusinessLogo();
        layoutMessage.setVisibility(VISIBLE);
        tvFloor.setText(floorName);
        tvNumber.setText(floorNumber);
        Glide.with(context).load(imageUrl).into(ivIcon);

    }

    @Override
    public void seekTo(long position) {
        super.seekTo(position);
    }

    /**
     * 取消订阅
     */
    public void unRegister(){
        EventBusManager.unregister(this);
    }


    @Override
    public void onCompletion() {
        releaseNetWorkState();
        super.onCompletion();
    }

    @Override
    public void onAutoCompletion() {
        layoutMessage.setVisibility(GONE);
        Logger.e("当前视频结束！");
        long currentTime=System.currentTimeMillis();
        Logger.e("当前时间："+TimeUtils.longToDate(currentTime)+"该视频时长："+getDuration()+"------播放该视频所需时长："+(currentTime-testTimes));
        TcpClient.getInstance(context, ClientMessageDispatcher.getInstance()).notifyVideoFinish(VideoResourcesManager.getInstance().getProgramUdid());
        super.onAutoCompletion();
    }


    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;
    }
    @Override
    protected void touchDoubleUp() {
        //super.touchDoubleUp();
        //不需要双击暂停
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.e("-----------销毁该View");
        unRegister();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceive(MessageEvent messageEvent){
        if (messageEvent.getType().equals(MessageEvent.Type.startPlayNextVideo)){
            if (nextPlayTime!=(long)messageEvent.getData()){
                nextPlayTime= (long) messageEvent.getData();
                String floorName= VideoResourcesManager.getInstance().getNextVideoModel().getFloorName();
                String floorNumber=VideoResourcesManager.getInstance().getNextVideoModel().getFloorNumber();
                String imageUrl=VideoResourcesManager.getInstance().getNextVideoModel().getBusinessLogo();
                Logger.e("-----------开始定时播放："+ TimeUtils.longToDate(nextPlayTime)+"当前时间："+TimeUtils.longToDate(System.currentTimeMillis())+"视频时长："+VideoResourcesManager.getInstance().getNextVideoModel().getVideoTimes());
                startPlay(VideoResourcesManager.getInstance().getNextVideoModel().getUrl());
                layoutMessage.setVisibility(VISIBLE);
                tvFloor.setText(floorName);
                tvNumber.setText(floorNumber);
                Glide.with(context).load(imageUrl).into(ivIcon);
                Logger.e("--楼层信息:"+floorName+";"+floorNumber+";"+imageUrl);
            }
        }else if (messageEvent.getType().equals(MessageEvent.Type.startPlayNextVideoAtOnce)){
            String floorName= VideoResourcesManager.getInstance().getNextVideoModel().getFloorName();
            String floorNumber=VideoResourcesManager.getInstance().getNextVideoModel().getFloorNumber();
            String imageUrl=VideoResourcesManager.getInstance().getNextVideoModel().getBusinessLogo();
            long videoTimes=VideoResourcesManager.getInstance().getNextVideoModel().getVideoTimes() ;
            Logger.e("当前时间："+TimeUtils.longToDate(System.currentTimeMillis())+"视频时长："+videoTimes);
            startPlay(VideoResourcesManager.getInstance().getNextVideoModel().getUrl());
            //startAfterPrepared();
        }
    }




    public interface VideoStatus{
        void playNext();
    }

}
