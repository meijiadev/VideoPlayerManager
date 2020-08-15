package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.google.protobuf.GeneratedMessageLite;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;

public class RspVideoAddressProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);

        Logger.e("返回所有视频下载地址");
        DDRADServiceCmd.rspVideoSeq rspVideoSeq= (DDRADServiceCmd.rspVideoSeq) msg;
        VideoResourcesManager.getInstance().setVideoUrls(rspVideoSeq.getUrlList());
    }
}
