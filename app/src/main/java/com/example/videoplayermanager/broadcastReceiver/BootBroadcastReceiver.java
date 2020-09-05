package com.example.videoplayermanager.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.service.GuardService;
import com.example.videoplayermanager.ui.SplashActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * desc：接收开机广播自动进入app
 * time:2020/08/07
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private Context context;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent messageEvent){
        if (messageEvent.getType().equals(MessageEvent.Type.startApp)){
            Intent it=new Intent(context, SplashActivity.class);
            it.setAction("android.intent.action.MAIN");
            it.addCategory("android.intent.category.LAUNCHER");
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(it);
            Logger.e("Boot开机自启动！");

            //启动服务
            Intent serviceIntent=new Intent(context, GuardService.class);
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        EventBus.getDefault().register(this);
        this.context=context;
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
           new Thread(()->{
               try {
                   Thread.sleep(3*60000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.startApp));
           }).start();
        }else {
            Logger.e("接收的非开机广播");
        }
    }

}
