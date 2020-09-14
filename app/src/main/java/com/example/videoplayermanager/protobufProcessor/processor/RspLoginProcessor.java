package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.google.protobuf.GeneratedMessageLite;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;


public class RspLoginProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspLogin rspLogin= (BaseCmd.rspLogin) msg;
        Logger.e("登陆回复");
        ToastUtils.show("登陆成功！");
    }
}
