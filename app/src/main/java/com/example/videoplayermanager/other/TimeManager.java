package com.example.videoplayermanager.other;

import com.bolex.timetask.lib.timed.Task;
import com.bolex.timetask.lib.timed.TimeHandler;
import com.example.videoplayermanager.service.GuardService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * desc:定时管理工具
 * time：2020/08/13
 */
public class TimeManager implements TimeHandler<TimeManager.MyTask> {
    public static final String START_DOWNLOAD_1="15:18:00";
    public static final String END_DOWNLOAD_1="15:18:30";


    @Override
    public void exeTask(MyTask myTask) {
        //准时执行
        Logger.e("定时任务："+myTask.name);
    }

    @Override
    public void overdueTask(MyTask myTask) {

    }

    @Override
    public void futureTask(MyTask myTask) {

    }


    public static class MyTask extends Task {
        public String name;
    }
}
