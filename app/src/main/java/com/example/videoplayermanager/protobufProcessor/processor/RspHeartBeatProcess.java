package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.tcp.TcpClient;
import com.google.protobuf.GeneratedMessageLite;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;


public class RspHeartBeatProcess extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        TcpClient tcpClient=TcpClient.tcpClient;
        DDRADServiceCmd.ADHeartBeat heartBeat= (DDRADServiceCmd.ADHeartBeat) msg;
        //Logger.d("--------接收心跳"+heartBeat.getIndex());
        if (tcpClient!=null){
            tcpClient.feedDog();
        }
    }
}
