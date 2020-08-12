package com.example.videoplayermanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;

/**
 * desc:
 */
public class GuardService extends Service {
    public GuardService() {
    }

    private String ip="192.168.0.95";
    private int port=88;

    private TcpClient tcpClient;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("------------只会启动一次");
        tcpClient=TcpClient.getInstance(MyApplication.context, ClientMessageDispatcher.getInstance());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e("------------会多次启动");
        if (!tcpClient.isConnected()){
            tcpClient.createConnect(ip,port);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("------------");
    }


}
