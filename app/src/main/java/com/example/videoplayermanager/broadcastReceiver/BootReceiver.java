package com.example.videoplayermanager.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.ui.SplashActivity;

/**
 * desc：接收开机广播自动进入app
 * time:2020/08/07
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Intent it=new Intent(context, SplashActivity.class);
            it.setAction("android.intent.action.MAIN");
            it.addCategory("android.intent.category.LAUNCHER");
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(it);
            Logger.e("Boot开机自启动！");
        }else {
            Logger.e("接收的非开机广播");
        }
    }
}
