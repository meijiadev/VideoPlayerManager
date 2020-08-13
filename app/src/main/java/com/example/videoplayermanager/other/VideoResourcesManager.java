package com.example.videoplayermanager.other;

import com.example.videoplayermanager.widget.ListGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:视频资源管理
 * time:2020/08/10
 */
public class VideoResourcesManager {
    private List<GSYVideoModel> videoModels;             //播放的视频资源
    private List<String> videoUrls;                      //即将下载的内容
    public static VideoResourcesManager videoResourcesManager;

    public static VideoResourcesManager getInstance(){
        if (videoResourcesManager==null){
            videoResourcesManager=new VideoResourcesManager();
        }
        return videoResourcesManager;
    }


    private VideoResourcesManager(){

    }

    public void setVideoModels(List<GSYVideoModel> videoModels) {
        this.videoModels = videoModels;

    }

    public List<GSYVideoModel> getVideoModels() {
        return (videoModels==null)?new ArrayList<>():videoModels;
    }

    public void setVideoUrls(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }

    public List<String> getVideoUrls() {
        return (videoUrls==null)?new ArrayList<>():videoUrls;
    }
}
