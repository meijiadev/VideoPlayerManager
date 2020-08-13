package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import DDRCommProto.BaseCmd;


public class RspLoginProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspLogin rspLogin= (BaseCmd.rspLogin) msg;
        Logger.e("登陆回复");
        switch (rspLogin.getYourRoleValue()){
            case 2:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.LoginSuccess));
                break;
            case 0:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.LoginSuccess));
                break;
        }
    }
}
