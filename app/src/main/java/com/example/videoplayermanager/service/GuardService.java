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
    private String ip="192.168.1.66";
    private int port=189;
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
            try {
                String formatType="yyyy-MM-dd HH:mm:ss";
                TimeManager.MyTask startPlayVideo=new TimeManager.MyTask();
                String startT=TimeUtils.getCurrentTime2()+" "+TimeManager.START_PLAY_VIDEO;
                String endT=TimeUtils.getCurrentTime2()+" "+TimeManager.END_PLAY_VIDEO;
                startPlayVideo.setStarTime(TimeUtils.stringToLong(startT,formatType));
                startPlayVideo.setEndTime(TimeUtils.stringToLong(endT,formatType));
                startPlayVideo.name=TimeManager.START_PLAY_VIDEO_NAME;
                add(startPlayVideo);
                TimeManager.MyTask myTask=new TimeManager.MyTask();
                String startTime=TimeUtils.getCurrentTime2()+" "+TimeManager.START_DOWNLOAD_1;
                String endTime=TimeUtils.getCurrentTime2()+" "+TimeManager.END_DOWNLOAD_1;
                myTask.setStarTime(TimeUtils.stringToLong(startTime,formatType));
                myTask.setEndTime(TimeUtils.stringToLong(endTime,formatType));
                myTask.name=TimeManager.START_DOWNLOAD_1_NAME;
                add(myTask);
                TimeManager.MyTask myTask1=new TimeManager.MyTask();
                String startTime1=TimeUtils.getCurrentTime2()+" "+TimeManager.START_DOWNLOAD_2;
                String endTime1=TimeUtils.getCurrentTime2()+" "+TimeManager.END_DOWNLOAD_2;
                myTask1.setStarTime(TimeUtils.stringToLong(startTime1,formatType));
                myTask1.setEndTime(TimeUtils.stringToLong(endTime1,formatType));
                myTask1.name=TimeManager.START_DOWNLOAD_2_NAME;
                add(myTask1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        };
    }


}
