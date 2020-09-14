package com.example.videoplayermanager.other;

import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.widget.ListGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * desc:视频资源管理
 * time:2020/08/10
 */
public class VideoResourcesManager {
    private List<VideoModel> videoModels;             //播放的视频资源
    private List<String> videoUrls;                      //即将下载的内容
    public static VideoResourcesManager videoResourcesManager;
    private long programUdid;
    private long timeTickToPlay;                         //预计最近播放的时间点
    private long realStartPlayTime;                      //真正开始播放的时间
    private long realProgramUdip;


    public static VideoResourcesManager getInstance(){
        if (videoResourcesManager==null){
            videoResourcesManager=new VideoResourcesManager();
        }
        return videoResourcesManager;
    }


    private VideoResourcesManager(){

    }

    public void setVideoModels(List<VideoModel> videoModels) {
        this.videoModels = videoModels;

    }

    public List<VideoModel> getVideoModels() {
        return (videoModels==null)?new ArrayList<>():videoModels;
    }


    public VideoModel getNextVideoModel(){
        if (videoModels!=null){
            int size=videoModels.size();
            if (size>0){
                return videoModels.get(size-1);
            }
        }
        return null;
    }

    public void setVideoUrls(List<String> videoUrls) {
        this.videoUrls = videoUrls;
        for (String url:videoUrls){
            Logger.e("下载链接："+url);
        }
    }

    public List<String> getVideoUrls() {
        return (videoUrls==null)?new ArrayList<>():videoUrls;
    }

    public void setProgramUdid(long programUdid) {
        this.programUdid = programUdid;
    }

    public long getProgramUdid() {
        return programUdid;
    }

    public void setTimeTickToPlay(long timeTickToPlay) {
        this.timeTickToPlay = timeTickToPlay;
    }

    public long getTimeTickToPlay() {
        return timeTickToPlay;
    }

    public void setRealStartPlayTime(long realStartPlayTime) {
        this.realStartPlayTime = realStartPlayTime;
    }

    public long getRealStartPlayTime() {
        return realStartPlayTime;
    }

    public void setRealProgramUdip(long realProgramUdip) {
        this.realProgramUdip = realProgramUdip;
    }

    public long getRealProgramUdip() {
        return realProgramUdip;
    }
}
