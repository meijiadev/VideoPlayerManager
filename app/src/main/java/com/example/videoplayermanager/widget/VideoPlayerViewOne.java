package com.example.videoplayermanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.google.android.exoplayer2.SeekParameters;
import com.shuyu.gsyvideoplayer.GSYVideoBaseManager;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.codeest.enviews.ENDownloadView;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

public class VideoPlayerViewOne extends StandardGSYVideoPlayer {
    private List<VideoModel> mUriList;
    private long testTimes;
    private long testTimes1;
    private long totalUseTime;
    private long totalVideoTime;
    private double loss;
    public VideoPlayerViewOne(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public VideoPlayerViewOne(Context context) {
        super(context);
    }

    public VideoPlayerViewOne(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video_one;
    }


    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param position      需要播放的位置
     * @param cacheWithPlay 是否边播边缓存
     * @return
     */
    public boolean setUp(List<VideoModel> url, boolean cacheWithPlay, int position) {
        return setUp(url, cacheWithPlay, position, null, new HashMap<String, String>());
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @return
     */
    public boolean setUp(List<VideoModel> url, boolean cacheWithPlay, int position, File cachePath) {
        testTimes=System.currentTimeMillis() ;
        return setUp(url, cacheWithPlay, position, cachePath, new HashMap<String, String>());
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   http header
     * @return
     */
    public boolean setUp(List<VideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData) {
        return setUp(url, cacheWithPlay, position, cachePath, mapHeadData, true);
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param position      需要播放的位置
     * @param cachePath     缓存路径，如果是M3U8或者HLS，请设置为false
     * @param mapHeadData   http header
     * @param changeState   切换的时候释放surface
     * @return
     */
    protected boolean setUp(List<VideoModel> url, boolean cacheWithPlay, int position, File cachePath, Map<String, String> mapHeadData, boolean changeState) {
        mUriList= VideoResourcesManager.getInstance().getVideoModels();
        mPlayPosition = position;
        mMapHeadData = mapHeadData;
        GSYVideoModel gsyVideoModel = new GSYVideoModel(mUriList.get(mPlayPosition).getUrl(),mUriList.get(mPlayPosition).getTitle());
        boolean set = setUp(gsyVideoModel.getUrl(), cacheWithPlay, cachePath, gsyVideoModel.getTitle(), changeState);
        return set;
    }

    @Override
    public void startPlayLogic() {
        super.startPlayLogic();
        //Logger.e("准备播放的时间："+TimeUtils.longToDate(System.currentTimeMillis()));
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        long currentTime=System.currentTimeMillis();
        testTimes1=currentTime;
        Logger.i("---------realStartTime："+ TimeUtils.longToDate(currentTime)+"VideoIndex:"+mPlayPosition+"Times:"+(currentTime-testTimes1));
    }



    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        long currentTime=System.currentTimeMillis();
        //Logger.e("当前时间："+TimeUtils.longToDate(currentTime)+"该视频时长："+getDuration()+"------播放该视频所需时长："+(currentTime-testTimes));
        totalUseTime=currentTime-testTimes;
        totalVideoTime=totalVideoTime+getDuration();
        loss=(double) (totalUseTime-totalVideoTime)/(double) totalVideoTime;
        Logger.e("the Video Time："+getDuration()+"----Play Time："+(currentTime-testTimes1)+"  TotalUserTime:"+totalUseTime+"; TotalVideoTime:"+totalVideoTime+"; loss:"+loss);
        if (playNext()) {
            return;
        }
    }

    @Override
    public void onCompletion() {
        super.onCompletion();

    }

    /**
     * 播放下一集
     *
     * @return true (将原来的列表播放 改为列表循环播放)
     */
    public boolean playNext() {
        mUriList=VideoResourcesManager.getInstance().getVideoModels();
        if (mPlayPosition < (mUriList.size() - 1)) {
            mPlayPosition += 1;
        }else {
            mPlayPosition=0;
        }
        mSaveChangeViewTIme = 0;
        setUp(mUriList, mCache, mPlayPosition, mCachePath, mMapHeadData, false);
        startPlayLogic();
        //startPlayNextVideo(mUriList.get(mPlayPosition).getUrl());
        return true;
    }

    /**
     * 开始状态视频播放，prepare时不执行  addTextureView();
     */
    @Override
    protected void prepareVideo() {
        super.prepareVideo();
        if (mHadPlay && mPlayPosition < (mUriList.size())) {
            setViewShowState(mLoadingProgressBar, VISIBLE);
            if (mLoadingProgressBar instanceof ENDownloadView) {
                ((ENDownloadView) mLoadingProgressBar).start();
            }
        }
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

    private GSYVideoManager mTmpManager;
    private long time1;
    private void startPlayNextVideo(String url){
        mTmpManager = GSYVideoManager.tmpInstance(gsyMediaPlayerListener);
        mTmpManager.initContext(getContext().getApplicationContext());
        mTmpManager.prepare(url, mMapHeadData, false, mSpeed,true,  GlobalParameter.getDownloadFile(), null);
        testTimes1=System.currentTimeMillis();
    }

    private GSYMediaPlayerListener gsyMediaPlayerListener = new GSYMediaPlayerListener() {
        @Override
        public void onPrepared() {
            if (mTmpManager != null) {
                mTmpManager.start();
                Logger.e("准备耗时："+(System.currentTimeMillis()-testTimes1));
                GSYVideoBaseManager manager = GSYVideoManager.instance();
                GSYVideoManager.changeManager(mTmpManager);
                mTmpManager.setLastListener(manager.lastListener());
                mTmpManager.setListener(manager.listener());
                manager.setDisplay(null);
                mTmpManager.setDisplay(mSurface);
                changeUiToPlayingClear();
                manager.releaseMediaPlayer();
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



}
