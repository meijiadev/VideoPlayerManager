package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.google.protobuf.GeneratedMessageLite;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;

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
        List<GSYVideoModel> gsyVideoModels=new ArrayList<>();
        for (int i=0;i<videoInfos.size();i++){
            GSYVideoModel gsyVideoModel=new GSYVideoModel(videoInfos.get(i).getUrl(),videoInfos.get(i).getName());
            gsyVideoModels.add(gsyVideoModel);
        }
        VideoResourcesManager.getInstance().setVideoModels(gsyVideoModels);
        Logger.d("接收视频播放列表！");
    }
}
