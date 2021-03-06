package com.example.videoplayermanager.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import moe.codeest.enviews.ENDownloadView;

/**
 * desc:列表循环播放
 * time:2020/08/10
 */
public class ListGSYVideoPlayer extends StandardGSYVideoPlayer {

    protected List<VideoModel> mUriList = new ArrayList<com.example.videoplayermanager.bean.VideoModel>();
    protected int mPlayPosition;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public ListGSYVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public ListGSYVideoPlayer(Context context) {
        super(context);
    }

    public ListGSYVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
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
        mUriList=VideoResourcesManager.getInstance().getVideoModels();
        mPlayPosition = position;
        mMapHeadData = mapHeadData;
        GSYVideoModel gsyVideoModel = new GSYVideoModel(mUriList.get(mPlayPosition).getUrl(),mUriList.get(mPlayPosition).getTitle());
        Logger.e("正在播放："+mPlayPosition+"----列表长度："+mUriList.size()+"视频地址："+gsyVideoModel.getUrl()+"视频名字："+gsyVideoModel.getTitle());
        boolean set = setUp(gsyVideoModel.getUrl(), cacheWithPlay, cachePath, gsyVideoModel.getTitle(), changeState);
      /*  if (!TextUtils.isEmpty(gsyVideoModel.getTitle())) {
            mTitleTextView.setText(gsyVideoModel.getTitle());
        }*/
        return set;
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
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        ListGSYVideoPlayer sf = (ListGSYVideoPlayer) from;
        ListGSYVideoPlayer st = (ListGSYVideoPlayer) to;
        st.mPlayPosition = sf.mPlayPosition;
        st.mUriList = sf.mUriList;
    }


    @Override
    public void onCompletion() {
        releaseNetWorkState();
        if (mPlayPosition < (mUriList.size())) {
            return;
        }
        super.onCompletion();
    }

    @Override
    public void onAutoCompletion() {
        if (playNext()) {
            return;
        }
        super.onAutoCompletion();
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
    public void onPrepared() {
        super.onPrepared();

    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        if (mHadPlay && mPlayPosition < (mUriList.size())) {
            setViewShowState(mThumbImageViewLayout, GONE);
            setViewShowState(mTopContainer, INVISIBLE);
            setViewShowState(mBottomContainer, INVISIBLE);
            setViewShowState(mStartButton, GONE);
            setViewShowState(mLoadingProgressBar, VISIBLE);
            setViewShowState(mBottomProgressBar, INVISIBLE);
            setViewShowState(mLockScreen, GONE);
            if (mLoadingProgressBar instanceof ENDownloadView) {
                ((ENDownloadView) mLoadingProgressBar).start();

            }
        }
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
        setUp(mUriList, mCache, mPlayPosition, null, mMapHeadData, false);
        startPlayLogic();
        return true;
    }






}
