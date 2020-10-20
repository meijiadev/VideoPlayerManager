package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;

public class NotifyDownloadProgressProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRADServiceCmd.notifyDownloadProgress notifyDownloadProgress= (DDRADServiceCmd.notifyDownloadProgress) msg;
        Logger.e("---："+notifyDownloadProgress.getTotalNum()+";"+notifyDownloadProgress.getCurrentNum()+";"+notifyDownloadProgress.getCurrentProgress());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.downloadProgressService,notifyDownloadProgress));
    }
}
