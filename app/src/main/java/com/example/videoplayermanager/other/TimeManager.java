package com.example.videoplayermanager.other;

import android.app.smdt.SmdtManager;

import com.bolex.timetask.lib.timed.Task;
import com.bolex.timetask.lib.timed.TimeHandler;
import com.example.videoplayermanager.MyApplication;

import org.greenrobot.eventbus.EventBus;

/**
 * desc:定时管理工具
 * time：2020/08/13
 */
public class TimeManager implements TimeHandler<TimeManager.MyTask> {
    public static final String START_WORK_TIME ="startPlayVideo";
    public static final String END_WORK_TIME="endPlayVideo";

    @Override
    public void exeTask(MyTask myTask) {
        //准时执行
        switch (myTask.name){
            case START_WORK_TIME:
                Logger.e("------开始工作："+TimeUtils.longToDate1(myTask.getStarTime()));
                if(!TimeUtils.longToDate1(myTask.getStarTime()).contains("00:00")){
                    SmdtManager smdtManager = SmdtManager.create(MyApplication.context);
                    smdtManager.smdtSetLcdBackLight(1);
                }
                break;
            case END_WORK_TIME:
                Logger.e("------结束工作："+TimeUtils.longToDate1(myTask.getEndTime()));
                if(!TimeUtils.longToDate1(myTask.getStarTime()).contains("00:00")){
                    SmdtManager smdtManager1 = SmdtManager.create(MyApplication.context);
                    smdtManager1.smdtSetLcdBackLight(0);
                }
                break;
        }
    }

    @Override
    public void overdueTask(MyTask myTask) {
        switch (myTask.name){
            case START_WORK_TIME:
                Logger.e("------开始工作："+TimeUtils.longToDate1(myTask.getStarTime()));
                if(!TimeUtils.longToDate1(myTask.getStarTime()).contains("00:00")){
                    SmdtManager smdtManager = SmdtManager.create(MyApplication.context);
                    smdtManager.smdtSetLcdBackLight(1);
                }
                break;
            case END_WORK_TIME:
                Logger.e("------结束工作："+TimeUtils.longToDate1(myTask.getEndTime()));
                if(!TimeUtils.longToDate1(myTask.getStarTime()).contains("00:00")){
                    SmdtManager smdtManager1 = SmdtManager.create(MyApplication.context);
                    smdtManager1.smdtSetLcdBackLight(0);
                }
                break;

        }
    }

    @Override
    public void futureTask(MyTask myTask) {

    }


    public static class MyTask extends Task {
        public String name;
    }
}
