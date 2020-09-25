package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.download.VideoPreLoader;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;
public class RspVideoAddressProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRADServiceCmd.rspVideoSeq rspVideoSeq= (DDRADServiceCmd.rspVideoSeq) msg;
        VideoResourcesManager.getInstance().setVideoUrls(rspVideoSeq.getInfosList());
        VideoPreLoader.getInstance().setPreLoadUrls(rspVideoSeq.getInfosList());
        Logger.e("返回所有视频下载地址"+rspVideoSeq.getInfosList().size());
        EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.allPlayVideos));
       /* List<String> urls=rspVideoSeq.getUrlList();
        List<VideoModel> videoModels=new ArrayList<>();
        for (int i=0;i<urls.size();i++){
            VideoModel videoModel=new VideoModel(urls.get(i),"测试");
            videoModels.add(videoModel);
        }
        VideoResourcesManager.getInstance().setVideoModels(videoModels);*/

    }
}
