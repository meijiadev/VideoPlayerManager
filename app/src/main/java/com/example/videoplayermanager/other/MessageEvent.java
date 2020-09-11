package com.example.videoplayermanager.other;

/**
 * desc: EvenBus的信息传递类
 */
public class MessageEvent {
    private Type type;
    private Object data;
    private String bitmapPath;
    public enum Type{
        updatePlayVideos,   //更新视频播放列表
        allPlayVideos,      //所有的视频地址
        startPlayVideo,        //开始播放视频
        startApp,             //启动APP
        setAlarmTime,         //设置下一次播放视频的时间
        startPlayNextVideo,     //播放下一个视频

        updateProgress,
        apkDownloadSucceed,
        apkDownloadFailed,
    }

    public MessageEvent(Type type) {
      this.type=type;
    }

    public MessageEvent(Type type, Object object){
        this.type=type;
        this.data=object;
    }


    public Type getType() {
        return type;
    }


    public Object getData(){
        return data;
    }
}
