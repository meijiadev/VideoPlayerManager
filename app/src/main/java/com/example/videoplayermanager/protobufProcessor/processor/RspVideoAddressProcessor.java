package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;

public class RspVideoAddressProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);

    }
}
