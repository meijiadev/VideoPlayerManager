package com.example.videoplayermanager.other;

import com.bolex.timetask.lib.timed.Task;
import com.bolex.timetask.lib.timed.TimeHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * desc:定时管理工具
 * time：2020/08/13
 */
public class TimeManager implements TimeHandler<TimeManager.MyTask> {
    public static final String START_DOWNLOAD_1="15:24:00";
    public static final String END_DOWNLOAD_1="15:24:30";
    public static final String START_DOWNLOAD_1_NAME="download1";
    public static final String START_DOWNLOAD_2="15:35:00";
    public static final String END_DOWNLOAD_2="15:35:10";
    public static final String START_DOWNLOAD_2_NAME="download2";

    public static final String START_PLAY_VIDEO="09:30:05";
    public static final String END_PLAY_VIDEO="09:30:06";
    public static final String START_PLAY_VIDEO_NAME="startPlayVideo";

    //下一个视频播放的时间
    public static String START_PLAY_NEXT_VIDEO;
    public static String START_PLAY_NEXT_VIDEO_END;
    public static final  String START_PLAY_NEXT_VIDEO_NAME="startPlayNextVideo";


    @Override
    public void exeTask(MyTask myTask) {
        //准时执行
        switch (myTask.name){
            case START_DOWNLOAD_1_NAME:
                Logger.e("定时任务："+START_DOWNLOAD_1_NAME);
                break;
            case START_PLAY_VIDEO_NAME:
                EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.startPlayVideo));
                break;
            case START_PLAY_NEXT_VIDEO_NAME:
                Logger.e("定时播放的时间："+TimeUtils.longToDate(myTask.getStarTime())+"当前时间："+TimeUtils.longToDate(System.currentTimeMillis()));
                //EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.startPlayNextVideo,myTask.getStarTime()));
                break;
        }
    }

    @Override
    public void overdueTask(MyTask myTask) {
        switch (myTask.name){
            case START_DOWNLOAD_1_NAME:
                //Logger.e("定时任务："+START_DOWNLOAD_1_NAME);
                break;
            case START_PLAY_VIDEO_NAME:
                EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.startPlayVideo));
                break;
            case START_PLAY_NEXT_VIDEO_NAME:
                Logger.e("过时播放的时间："+TimeUtils.longToDate(myTask.getStarTime())+"当前时间："+TimeUtils.longToDate(System.currentTimeMillis()));
                //EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.startPlayNextVideo,myTask.getStarTime()));
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
