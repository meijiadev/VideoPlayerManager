package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.google.protobuf.GeneratedMessageLite;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;

/**
 * desc：接收播放的视频列表
 */
public class NotifyCurrentVideoSeqProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRADServiceCmd.notifyCurrentVideoSeq notifyCurrentVideoSeq= (DDRADServiceCmd.notifyCurrentVideoSeq) msg;
        List<DDRADServiceCmd.VideoInfo> videoInfos=notifyCurrentVideoSeq.getVideoInfosList();
        List<VideoModel> videoModels=new ArrayList<>();
        for (int i=0;i<videoInfos.size();i++){
            VideoModel videoModel=new VideoModel(videoInfos.get(i).getUrl(),videoInfos.get(i).getName());
            videoModel.setFloorName(videoInfos.get(i).getFloor());
            videoModel.setFloorNumber(videoInfos.get(i).getNumber());
            videoModel.setBusinessLogo(videoInfos.get(i).getLogo());
            videoModel.setProgramNum(videoInfos.get(i).getProgramNum());
            videoModels.add(videoModel);
            Logger.e("--------:"+videoInfos.get(i).getFloor()+";"+videoInfos.get(i).getNumber()+";"+videoInfos.get(i).getLogo()+";"+videoInfos.get(i).getBusinessInfo()+";"+i);
        }
        VideoResourcesManager.getInstance().setVideoModels(videoModels);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePlayVideos));
        Logger.e("接收视频播放列表！"+videoModels.size());
    }
}
