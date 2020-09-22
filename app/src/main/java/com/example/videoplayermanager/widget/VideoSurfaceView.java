package com.example.videoplayermanager.widget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.ProxyCacheManager;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.other.VideoResourcesManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * desc：测试无缝连接 Demo
 */
public class VideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private MediaPlayer firstPlayer;
    private MediaPlayer nextMediaPlayer;
    private MediaPlayer cachePlayer;
    private MediaPlayer currentPlayer;
    private SurfaceHolder surfaceHolder;
    private ArrayList<String> videoListQueue=new ArrayList<>();
    private HashMap<String,MediaPlayer> playersCaches=new HashMap<>();
    private int currentVideoIndex;

    public VideoSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        getUrl();
        initFirstPlayer();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        if (firstPlayer != null) {
            if (firstPlayer.isPlaying()) {
                firstPlayer.stop();
            }
            firstPlayer.release();
        }
        if (nextMediaPlayer != null) {
            if (nextMediaPlayer.isPlaying()) {
                nextMediaPlayer.stop();
            }
            nextMediaPlayer.release();
        }

        if (currentPlayer != null) {
            if (currentPlayer.isPlaying()) {
                currentPlayer.stop();
            }
            currentPlayer.release();
        }
        currentPlayer = null;

    }

    /**
     * 初始化首个播放器
     */
    private void initFirstPlayer(){
        firstPlayer = new MediaPlayer();
        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        firstPlayer.setDisplay(surfaceHolder);
        firstPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Logger.e("第一个视频时长："+firstPlayer.getDuration()+";耗时："+(System.currentTimeMillis()-testTimes));
                        onVideoPlayCompleted(mp);
                    }
                });
        //设置cachePlayer为该player对象
        cachePlayer = firstPlayer;
        initNextPlayer();
        //player对象初始化完成后，开启播放
        startPlayFirstVideo();
    }

    private long testTimes;
    /**
     * 开始播放第一个视频
     */
    private void startPlayFirstVideo() {
        try {
            firstPlayer.setDataSource(videoListQueue.get(currentVideoIndex));
            firstPlayer.prepareAsync();
            firstPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    firstPlayer.start();
                    testTimes=System.currentTimeMillis();
                    Logger.e("开始播放："+ TimeUtils.longToDate(testTimes));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化下一个播放器
     */
    private void initNextPlayer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < videoListQueue.size(); i++) {
                    nextMediaPlayer = new MediaPlayer();
                    nextMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    nextMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    onVideoPlayCompleted(mp);
                                }
                            });

                    try {
                        nextMediaPlayer.setDataSource(videoListQueue.get(i));
                        nextMediaPlayer.prepare();
                    } catch (IOException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                    //设置下一个播放器（当第一个播放完后直接播放这个）
                    cachePlayer.setNextMediaPlayer(nextMediaPlayer);
                    cachePlayer = nextMediaPlayer;
                    playersCaches.put(String.valueOf(i), nextMediaPlayer);
                }
            }
        }).start();
    }


    private void onVideoPlayCompleted(MediaPlayer mp) {
        //将前一个播放器设置Display为空
        mp.setDisplay(null);
        //获取下一个播放器
        currentPlayer = playersCaches.get(String.valueOf(++currentVideoIndex));
        if (currentPlayer != null) {
            Logger.e("------时长："+currentPlayer.getDuration()+";耗时："+(System.currentTimeMillis()-testTimes));
            currentPlayer.setDisplay(surfaceHolder);
        } else {
            firstPlayer.setDisplay(surfaceHolder);
            currentVideoIndex=0;
            //currentVideoIndex=0;
            startPlayFirstVideo();
            Logger.e("播放完毕！");
        }
    }


    /**
     * 获取连接
     */
    public void getUrl(){
        HttpProxyCacheServer proxy = ProxyCacheManager.getProxy(MyApplication.myApplication, GlobalParameter.getDownloadFile());
        List<String> videoUrls= VideoResourcesManager.getInstance().getVideoUrls();
        for (int i=0;i<videoUrls.size();i++){
            String url = proxy.getProxyUrl(videoUrls.get(i));
            videoListQueue.add(url);
        }
    }
}
