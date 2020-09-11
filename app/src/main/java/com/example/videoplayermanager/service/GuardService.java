package com.example.videoplayermanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import com.bolex.timetask.lib.timed.Task;
import com.bolex.timetask.lib.timed.TimeHandler;
import com.bolex.timetask.lib.timed.TimeTask;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.NetWorkUtil;
import com.example.videoplayermanager.other.TimeManager;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * desc:定时运行的服务
 */
public class GuardService extends Service {
    private TcpClient tcpClient;
    private TimeTask<TimeManager.MyTask> myTaskTimeTask;
    private List<TimeManager.MyTask> myTasks=new ArrayList<>();
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
        EventBusManager.register(this);
        myTaskTimeTask=new TimeTask<>(GuardService.this,ACTION);
        myTaskTimeTask.addHandler(new TimeManager());
        createTasks();
        myTaskTimeTask.setTasks(myTasks);
        myTaskTimeTask.startLooperTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (tcpClient.isConnected()){
            tcpClient.disConnect();
        }
        new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tcpClient.createConnect(GlobalParameter.IP,GlobalParameter.PORT);
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myTaskTimeTask.onColse();
        EventBusManager.unregister(this);
    }


    /**
     * 创建定时任务时间列表
     * @return
     */
    private void createTasks(){
        try {
            String formatType="yyyy-MM-dd HH:mm:ss";
            TimeManager.MyTask startPlayVideo=new TimeManager.MyTask();
            String startT=TimeUtils.getCurrentTime2()+" "+TimeManager.START_PLAY_VIDEO;
            String endT=TimeUtils.getCurrentTime2()+" "+TimeManager.END_PLAY_VIDEO;
            startPlayVideo.setStarTime(TimeUtils.stringToLong(startT,formatType));
            startPlayVideo.setEndTime(TimeUtils.stringToLong(endT,formatType));
            startPlayVideo.name=TimeManager.START_PLAY_VIDEO_NAME;
            myTasks.add(startPlayVideo);
            TimeManager.MyTask myTask=new TimeManager.MyTask();
            String startTime=TimeUtils.getCurrentTime2()+" "+TimeManager.START_DOWNLOAD_1;
            String endTime=TimeUtils.getCurrentTime2()+" "+TimeManager.END_DOWNLOAD_1;
            myTask.setStarTime(TimeUtils.stringToLong(startTime,formatType));
            myTask.setEndTime(TimeUtils.stringToLong(endTime,formatType));
            myTask.name=TimeManager.START_DOWNLOAD_1_NAME;
            myTasks.add(myTask);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onReceiveMessage(MessageEvent messageEvent){
        if (messageEvent.getType().equals(MessageEvent.Type.setAlarmTime)){
            myTasks.clear();
            long startPlayTime=System.currentTimeMillis();
            TimeManager.MyTask myTask=new TimeManager.MyTask();
            myTask.setStarTime(startPlayTime+5000);
            myTask.setEndTime(startPlayTime+6000);
            myTask.name=TimeManager.START_PLAY_NEXT_VIDEO_NAME;
            myTasks.add(myTask);
            myTaskTimeTask.setTasks(myTasks);
            myTaskTimeTask.startLooperTask();
        }
    }


}
