package com.example.videoplayermanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import com.bolex.timetask.lib.timed.TimeTask;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.TimeManager;
import com.example.videoplayermanager.other.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * desc:定时运行的服务
 */
public class GuardService extends Service {

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

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myTaskTimeTask=new TimeTask<>(MyApplication.context,ACTION);
        myTaskTimeTask.addHandler(new TimeManager());
        createTasks();
        myTaskTimeTask.setTasks(myTasks);
        myTaskTimeTask.startLooperTask();
        Logger.e("创建定时任务");
        try {
            boolean a=TimeUtils.compareTime("09:30","09:21");
            Logger.e("两数大小："+a);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myTaskTimeTask.onColse();
    }




    /**
     * 创建定时任务时间列表
     * @return
     */
    private void createTasks(){
        try {
            String formatType="yyyy-MM-dd HH:mm";
            TimeManager.MyTask startPlayVideo=new TimeManager.MyTask();
            String startT=TimeUtils.getCurrentTime2()+" "+ GlobalParameter.START_WORK_TIME;
            String endT=TimeUtils.getCurrentTime2()+" "+GlobalParameter.START_WORK_TIME;
            startPlayVideo.setStarTime(TimeUtils.stringToLong(startT,formatType));
            startPlayVideo.setEndTime(TimeUtils.stringToLong(endT,formatType));
            startPlayVideo.name=TimeManager.START_WORK_TIME;
            myTasks.add(startPlayVideo);
            TimeManager.MyTask endPlayVideo=new TimeManager.MyTask();
            String startT1=TimeUtils.getCurrentTime2()+" "+ GlobalParameter.END_WORK_TIME;
            String endT1=TimeUtils.getCurrentTime2()+" "+GlobalParameter.END_WORK_TIME;
            endPlayVideo.setStarTime(TimeUtils.stringToLong(startT1,formatType));
            endPlayVideo.setEndTime(TimeUtils.stringToLong(endT1,formatType));
            endPlayVideo.name=TimeManager.END_WORK_TIME;
            myTasks.add(endPlayVideo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }




}
