package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;

class NotifyCurrentVideoSeqProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRADServiceCmd.notifyCurrentVideoSeq notifyCurrentVideoSeq= (DDRADServiceCmd.notifyCurrentVideoSeq) msg;
    }
}
