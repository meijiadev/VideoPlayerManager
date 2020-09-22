package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;
import android.os.Handler;

import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.VideoPreLoader;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.google.protobuf.GeneratedMessageLite;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;
public class RspVideoAddressProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRADServiceCmd.rspVideoSeq rspVideoSeq= (DDRADServiceCmd.rspVideoSeq) msg;
        VideoResourcesManager.getInstance().setVideoUrls(rspVideoSeq.getUrlList());
        VideoPreLoader.getInstance().setPreLoadUrls(rspVideoSeq.getUrlList());
        Logger.e("返回所有视频下载地址"+rspVideoSeq.getUrlList().size());
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
