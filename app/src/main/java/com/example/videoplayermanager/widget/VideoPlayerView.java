package com.example.videoplayermanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;


public class VideoPlayerView extends StandardGSYVideoPlayer {
    private VideoStatus videoStatus;
    private long nextPlayTime;
    private Context context;
    private boolean currentVideoFinish;           //当前视频是否播完
    private long logTime1;
    private long longTime2;
    private ImageView ivWait;
    RelativeLayout layoutMessage;
    TextView tvFloor;
    TextView tvNumber;
    ImageView ivIcon;
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
        ivWait=findViewById(R.id.ivWait);
        layoutMessage=findViewById(R.id.layoutMessage);
        tvFloor=findViewById(R.id.tvFloor);
        tvNumber=findViewById(R.id.tvNumber);
        ivIcon=findViewById(R.id.ivIcon);
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
    }

    public void setVideoStatus(VideoStatus videoStatus) {
        this.videoStatus = videoStatus;
    }

    /**
     * 开始准备播放视频（先加载再播放）
     * @param url
     * @return
     */
    public void startPlay(String url){
         currentVideoFinish=false;
         layoutMessage.setVisibility(VISIBLE);
         tvFloor.setText(VideoResourcesManager.getInstance().getNextVideoModel().getFloorName());
         tvNumber.setText(VideoResourcesManager.getInstance().getNextVideoModel().getFloorNumber());
         Glide.with(context).load(VideoResourcesManager.getInstance().getNextVideoModel().getBusinessLogo()).into(ivIcon);
         logTime1=System.currentTimeMillis();
         Logger.e("准备播放视频！");
         setUp(url,true, GlobalParameter.getDownloadFile(),null,false);
         startPlayLogic();
    }

    /**
     * 加载完成
     */
    @Override
    public void onPrepared() {
        super.onPrepared();
        ivWait.setVisibility(GONE);
        long currentTime=System.currentTimeMillis();
        videoStatus.playNext();
        Logger.e("-----------------------加载完成耗时："+(currentTime-logTime1));
        //视频真正开始播放的时间
        long realStartTime;
        if (currentTime>nextPlayTime&&nextPlayTime>0){
            //seekTo(currentTime-nextPlayTime);
           // realStartTime =2*currentTime-nextPlayTime;
            realStartTime=System.currentTimeMillis();
            Logger.e("-------------------------seek的时间："+(currentTime-nextPlayTime));
        }else {
            realStartTime =System.currentTimeMillis();
        }
        VideoResourcesManager.getInstance().setRealProgramUdip(VideoResourcesManager.getInstance().getProgramUdid());
        VideoResourcesManager.getInstance().setRealStartPlayTime(realStartTime);
        nextPlayTime=0;
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
        ivWait.setVisibility(VISIBLE);
        Logger.e("当前视频播放完！");
        currentVideoFinish=true;
        if (System.currentTimeMillis()>=nextPlayTime&&nextPlayTime>0){
            startPlay(VideoResourcesManager.getInstance().getNextVideoModel().getUrl());
            Logger.e("开始播放下一个视频" );
        }
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceive(MessageEvent messageEvent){
        if (messageEvent.getType().equals(MessageEvent.Type.startPlayNextVideo)){
            nextPlayTime= (long) messageEvent.getData();
            Logger.e("------开始定时播放！");
            startPlay(VideoResourcesManager.getInstance().getNextVideoModel().getUrl());
        }
    }

    public interface VideoStatus{
        void playNext();
    }

}
