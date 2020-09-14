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

import static com.example.videoplayermanager.other.MessageEvent.Type.updatePlayVideos;

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
        List<DDRADServiceCmd.VideoInfo> videoInfos=notifyCurrentVideoSeq.getVideoInfosList();
        List<VideoModel> videoModels=new ArrayList<>();
        for (int i=0;i<videoInfos.size();i++){
            VideoModel videoModel=new VideoModel(videoInfos.get(i).getUrl(),videoInfos.get(i).getName());
            videoModel.setFloorName(videoInfos.get(i).getFloor());
            videoModel.setFloorNumber(videoInfos.get(i).getNumber());
            videoModel.setBusinessLogo(videoInfos.get(i).getLogo());
            videoModel.setProgramNum(videoInfos.get(i).getProgramNum());
            videoModel.setVideoTimes(videoInfos.get(i).getDuration());
            videoModels.add(videoModel);
            Logger.e("链接："+videoModel.getUrl()+";"+videoModel.getProgramNum());
        }
        VideoResourcesManager.getInstance().setVideoModels(videoModels);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updatePlayVideos));
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.setAlarmTime));
    }
}
