package com.example.videoplayermanager.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.shuyu.gsyvideoplayer.GSYVideoBaseManager;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

/**
 * desc:无缝切换视频
 */
public class SmartPickVideo extends StandardGSYVideoPlayer {
    private Context context;
    private List<VideoModel> videoModels;
    private GSYVideoManager mTmpManager;
    private long firstStartTime;
    private long totalUseTime;
    private long totalVideoTime;
    private boolean isCompleted;         //当前视频是否播完
    private boolean isVideoPreparedPlay;  // 是否视频准备好就直接播放，默认不
    private double loss;
    protected Handler mainThreadHandler;
    private RelativeLayout layoutMessage;
    private TextView tvFloor;
    private TextView tvNumber;
    private ImageView ivIcon;
    private VideoModel currentVideoModel;      //当前的播放的视频信息
    private VideoModel nextPlayVideoModel;     //下一个要播放视频的信息
    private String floorName;                    //楼层
    private String floorNumber;                  //门牌号
    private String imageUrl;                    // logo链接

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public SmartPickVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
        initView();
    }

    public SmartPickVideo(Context context) {
        super(context);
        initView();
    }

    public SmartPickVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        this.context=context;
    }

    private void initView(){
        EventBusManager.register(this);
        mainThreadHandler = new Handler();
        layoutMessage=findViewById(R.id.layoutMessage);
        tvFloor=findViewById(R.id.tvFloor);
        tvNumber=findViewById(R.id.tvNumber);
        ivIcon=findViewById(R.id.ivIcon);
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceive(MessageEvent messageEvent){
        if (messageEvent.getType().equals(MessageEvent.Type.startPlayNextVideoAtOnce)){
            videoModels= VideoResourcesManager.getInstance().getVideoModels();
            if (videoModels.size()==2){
                if (videoModels.get(0).getUrl().equals(nextPlayVideoModel.getUrl())){
                    currentVideoModel=videoModels.get(0);
                    nextPlayVideoModel=videoModels.get(1);
                    startNextVideo();
                    prepareNextVideo(nextPlayVideoModel);
                }else {
                    Logger.e("-----------------插播精准视频重新初始化播放器--------------------");
                    //因为插播了一个精准视频，所以需要把之前Z准备的视频播放器释放，重新初始化
                    releaseTmpManager();
                    currentVideoModel=videoModels.get(0);
                    nextPlayVideoModel=videoModels.get(1);
                    //下一个播放的是精准视频，临时设置播放器，设置完直接播放
                    isVideoPreparedPlay=true;
                    prepareNextVideo(currentVideoModel);
                }
                floorName= currentVideoModel.getFloorName();
                floorNumber=currentVideoModel.getFloorNumber();
                imageUrl=currentVideoModel.getBusinessLogo();
                tvFloor.setText(floorName);
                tvNumber.setText(floorNumber);
                Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(ivIcon);
            }

        }
    }

    /**
     *开始播放
     */
    public void startPlay(){
        firstStartTime=System.currentTimeMillis();
        videoModels=VideoResourcesManager.getInstance().getVideoModels();
        if (videoModels.size()>=2){
            currentVideoModel=videoModels.get(0);
            nextPlayVideoModel=videoModels.get(1);
            setUp(currentVideoModel.getUrl());
        }
        Logger.e("快进速度："+1.05f);
    }

    /**
     * 设置连接
     * @param url
     * @return
     */
    private void setUp(String url){
        setUp(url,true,GlobalParameter.getDownloadFile(),null);
        prepareNextVideo(nextPlayVideoModel);
        floorName= currentVideoModel.getFloorName();
        floorNumber=currentVideoModel.getFloorNumber();
        imageUrl=currentVideoModel.getBusinessLogo();
        //layoutMessage.setVisibility(VISIBLE);
        tvFloor.setText(floorName);
        tvNumber.setText(floorNumber);
        Glide.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(ivIcon);
    }

    /**
     * 设置播放速度
     */
    private void setSpeedPlayer(){
        setSpeedPlaying(1.05f,true);
    }



    @Override
    public void onAutoCompletion() {
        //super.onAutoCompletion();
        totalVideoTime=totalVideoTime+getDuration();
        totalUseTime=System.currentTimeMillis()-firstStartTime;
        loss=(double) (totalUseTime-totalVideoTime)/(double)totalVideoTime;
        Logger.e("时长："+totalVideoTime+"耗时："+totalUseTime+"损耗比："+loss);
        TcpClient.getInstance(MyApplication.context, ClientMessageDispatcher.getInstance()).notifyVideoFinish(VideoResourcesManager.getInstance().getProgramUdid());
    }

    @Override
    public void onCompletion() {
        //super.onCompletion();
        //releaseTmpManager();

    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        setSpeedPlayer();

    }

    /**
     * 预先加载下一个视频
     * @param
     */
    private void prepareNextVideo(VideoModel nextPlayVideoModel) {
        mainThreadHandler.post(()->{
            if (nextPlayVideoModel!=null){
                String url=nextPlayVideoModel.getUrl();
                //创建临时管理器执行加载播放
                mTmpManager = GSYVideoManager.tmpInstance(gsyMediaPlayerListener);
                mTmpManager.initContext(getContext().getApplicationContext());
                resolveChangeUrl(mCache, mCachePath, url);
                mTmpManager.prepare(mUrl, mMapHeadData, mLooping, mSpeed, mCache, mCachePath, null);
            }
        });
    }


    /**
     * 开始播放下一个视频
     */
    private void startNextVideo(){
        if (mTmpManager != null) {
            isVideoPreparedPlay=false;
            mTmpManager.start();
            //mTmpManager.seekTo(1500);
            GSYVideoBaseManager manager = GSYVideoManager.instance();
            //替换播放管理器
            GSYVideoManager.changeManager(mTmpManager);
            mTmpManager.setLastListener(manager.lastListener());
            mTmpManager.setListener(manager.listener());
            manager.setDisplay(null);
            mTmpManager.setDisplay(mSurface);
            manager.releaseMediaPlayer();
            setSpeedPlayer();

        }

    }

    private void resolveChangeUrl(boolean cacheWithPlay, File cachePath, String url) {
        if (mTmpManager != null) {
            mCache = cacheWithPlay;
            mCachePath = cachePath;
            mOriginUrl = url;
            this.mUrl = url;
        }
    }

    /**
     * 临时播放器的状态监听
     */
    private GSYMediaPlayerListener gsyMediaPlayerListener = new GSYMediaPlayerListener() {
        @Override
        public void onPrepared() {
            if (isVideoPreparedPlay) {
                startNextVideo();
                prepareNextVideo(nextPlayVideoModel);
            }
        }

        @Override
        public void onAutoCompletion() {

        }

        @Override
        public void onCompletion() {

        }

        @Override
        public void onBufferingUpdate(int percent) {

        }

        @Override
        public void onSeekComplete() {

        }

        @Override
        public void onError(int what, int extra) {
            if (mTmpManager != null) {
                mTmpManager.releaseMediaPlayer();
            }
            post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "change Fail", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onInfo(int what, int extra) {

        }

        @Override
        public void onVideoSizeChanged() {

        }

        @Override
        public void onBackFullscreen() {

        }

        @Override
        public void onVideoPause() {

        }

        @Override
        public void onVideoResume() {

        }

        @Override
        public void onVideoResume(boolean seek) {

        }
    };




    private void releaseTmpManager() {
        if (mTmpManager != null) {
            mTmpManager.releaseMediaPlayer();
            mTmpManager = null;
        }
    }


    @Override
    public boolean onSurfaceDestroyed(Surface surface) {
        //清空释放
        setDisplay(null);
        //同一消息队列中去release
        //todo 需要处理为什么全屏时全屏的surface会被释放了
        //releaseSurface(surface);
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Logger.e("-----------销毁该View");
        unRegister();
    }

    /**
     * 取消订阅
     */
    public void unRegister(){
        EventBusManager.unregister(this);
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
}
