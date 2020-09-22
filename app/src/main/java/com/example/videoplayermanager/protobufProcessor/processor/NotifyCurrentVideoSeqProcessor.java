package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.google.protobuf.GeneratedMessageLite;

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
        VideoResourcesManager.getInstance().setProgramUdid(notifyCurrentVideoSeq.getProgramUdid());
        VideoResourcesManager.getInstance().setTimeTickToPlay(notifyCurrentVideoSeq.getTimeTickToPlay());
        //EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.setAlarmTime));
        List<DDRADServiceCmd.VideoInfo> videoIfs=notifyCurrentVideoSeq.getVideoInfosList();
        List<VideoModel> videoModels=new ArrayList<>();
        for (int i=0;i<videoIfs.size();i++){
            VideoModel videoModel=new VideoModel(videoIfs.get(i).getUrl(),videoIfs.get(i).getName());
            videoModel.setFloorName(videoIfs.get(i).getFloor());
            videoModel.setFloorNumber(videoIfs.get(i).getNumber());
            videoModel.setBusinessLogo(videoIfs.get(i).getLogo());
            videoModel.setProgramNum(videoIfs.get(i).getProgramNum());
            videoModel.setVideoTimes(videoIfs.get(i).getDuration());
            videoModels.add(videoModel);
        }
        VideoResourcesManager.getInstance().setVideoModels(videoModels);
        //EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePlayVideos));
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.startPlayNextVideoAtOnce));
        Logger.e("接收播放列表");
    }
}
