package com.example.videoplayermanager.protobufProcessor.processor;

import android.app.smdt.SmdtManager;
import android.content.Context;

import com.example.videoplayermanager.other.Logger;
import com.google.protobuf.GeneratedMessageLite;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;

/**
 * desc:控制屏幕是否熄屏
 */
public class NotifyScreenActionProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        SmdtManager smdtManager = SmdtManager.create(context);
        DDRADServiceCmd.notifyScreenAction notifyScreenAction= (DDRADServiceCmd.notifyScreenAction) msg;
        if (smdtManager !=null){
            //0：让屏幕熄屏 1：让屏幕点亮
            int value=notifyScreenAction.getActionValue();
            Logger.e("当前屏幕状态："+value);
            smdtManager.smdtSetLcdBackLight(value);
        }
    }
}
