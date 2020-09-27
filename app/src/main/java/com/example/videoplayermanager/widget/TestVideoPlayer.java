package com.example.videoplayermanager.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Surface;
import android.widget.Toast;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.other.Logger;
import com.hjq.toast.ToastUtils;
import com.shuyu.gsyvideoplayer.GSYVideoBaseManager;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.util.List;

/**
 * desc：播放本地视频，测试播放损耗
 */
public class TestVideoPlayer extends StandardGSYVideoPlayer {
    private GSYVideoManager mTmpManager;
    private int mSourcePosition;
    private long firstStartTime;
    private long totalUseTime;
    private long totalVideoTime;
    private float loss;
    private List<String> mUrls;
    protected Handler mainThreadHandler;

    public TestVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
        initView();
    }

    public TestVideoPlayer(Context context) {
        super(context);
        initView();
    }

    public TestVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
    }

    private void initView(){
        mainThreadHandler = new Handler();
    }


    /**
     * 开始播放列表内容
     * @param urls
     */
    public void setUp(List<String> urls){
        firstStartTime=System.currentTimeMillis();
        this.mUrls=urls;
        setUp(urls.get(0),false,null);
        startPlayLogic();
        preparePlayNext(mSourcePosition+1);
        Logger.e("开始播放列表内容");
    }

    /**
     * 设置播放速度
     */
    private void setSpeedPlayer(){
        setSpeedPlaying(GlobalParameter.PLAY_SPEED,true);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        setSpeedPlayer();
    }


    @Override
    public void onAutoCompletion() {
        //super.onAutoCompletion();
        totalUseTime=System.currentTimeMillis()-firstStartTime;
        totalVideoTime=getDuration()+totalVideoTime;
        loss=(float) (totalUseTime-totalVideoTime)/(float) totalVideoTime;
        Logger.e("TotalUseTime:"+totalUseTime+";------TotalVideoTime:"+totalVideoTime+";---------loss:"+loss);
        startNextVideo();
        preparePlayNext(mSourcePosition+1);
    }

    @Override
    public void onCompletion() {
        //super.onCompletion();
    }

    /**
     * 准备下一个视频
     * @param position
     */
    private void preparePlayNext(int position) {
        mainThreadHandler.post(()->{
            if (mSourcePosition != position) {
                if (position<mUrls.size()){
                    mSourcePosition = position;
                }else {
                    mSourcePosition=0;
                }
                final String url = mUrls.get(mSourcePosition);
                Logger.e("播放的视频连接："+url);
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


    private GSYMediaPlayerListener gsyMediaPlayerListener = new GSYMediaPlayerListener() {
        @Override
        public void onPrepared() {

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
                    ToastUtils.show("onError");
                    Logger.e("-------------onError-------------");
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
