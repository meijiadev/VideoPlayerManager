package com.example.videoplayermanager.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.common.GlobalParameter;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;


public class VideoPlayerView extends StandardGSYVideoPlayer {
    public VideoPlayerView(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public VideoPlayerView(Context context) {
        super(context);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public int getLayoutId() {
        return R.layout.empty_control_video;
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

    /**
     * 开始播放视频
     * @param url
     * @return
     */
    private void startPlay(String url){
         setUp(url,true, GlobalParameter.getDownloadFile(),null,false);
         startPlayLogic();
    }


}
