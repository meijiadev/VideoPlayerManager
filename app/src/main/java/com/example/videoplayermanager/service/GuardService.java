package com.example.videoplayermanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import com.bolex.timetask.lib.timed.Task;
import com.bolex.timetask.lib.timed.TimeHandler;
import com.bolex.timetask.lib.timed.TimeTask;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.TimeManager;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * desc:
 */
public class GuardService extends Service {
    private String ip="192.168.0.95";
    private int port=88;
    private TcpClient tcpClient;

    private TimeTask<TimeManager.MyTask> myTaskTimeTask;
    private String ACTION="timeTask.action";
    public GuardService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tcpClient=TcpClient.getInstance(MyApplication.context, ClientMessageDispatcher.getInstance());
        myTaskTimeTask=new TimeTask<>(GuardService.this,ACTION);
        myTaskTimeTask.addHandler(new TimeManager());
        List<TimeManager.MyTask> myTasks=createTasks();
        myTaskTimeTask.setTasks(myTasks);
        myTaskTimeTask.startLooperTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!tcpClient.isConnected()){
            tcpClient.createConnect(ip,port);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * 创建定时任务时间列表
     * @return
     */
    private List<TimeManager.MyTask> createTasks(){
        return new ArrayList<TimeManager.MyTask>(){{
         TimeManager.MyTask myTask=new TimeManager.MyTask();
            try {
                String startTime=TimeUtils.getCurrentTime2()+" "+TimeManager.START_DOWNLOAD_1;
                String endTime=TimeUtils.getCurrentTime2()+" "+TimeManager.END_DOWNLOAD_1;
                String formatType="yyyy-MM-dd HH:mm:ss";
                myTask.setStarTime(TimeUtils.stringToLong(startTime,formatType));
                myTask.setEndTime(TimeUtils.stringToLong(endTime,formatType));
                myTask.name="download";
                add(myTask);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        };
    }


}
