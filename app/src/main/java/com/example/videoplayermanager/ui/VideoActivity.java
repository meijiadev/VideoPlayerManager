package com.example.videoplayermanager.ui;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseActivity;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.widget.ListGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import java.util.Collections;
import java.util.List;

public class VideoActivity extends BaseActivity {
    @BindView(R.id.videoPlayer)
    ListGSYVideoPlayer videoPlayer;

    OrientationUtils orientationUtils;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
    }

    @Override
    protected void initData() {
        Intent intent=getIntent();
        List<GSYVideoModel> videoModels = VideoResourcesManager.getInstance().getVideoModels();
        int index=intent.getIntExtra("videoPosition",0);
        videoPlayer.setUp(videoModels,true,index, GlobalParameter.getDownloadFile());
        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });
        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
}