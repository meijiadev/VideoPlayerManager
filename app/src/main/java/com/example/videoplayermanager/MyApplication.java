package com.example.videoplayermanager;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.ProxyCacheManager;
import com.example.videoplayermanager.ui.CrashActivity;
import com.example.videoplayermanager.ui.SplashActivity;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

import static com.shuyu.gsyvideoplayer.utils.GSYVideoType.SCREEN_TYPE_DEFAULT;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        initSDK();
        //EXOPlayer内核，支持格式更多
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
        //exo缓存模式，支持m3u8，只支持exo
        CacheFactory.setCacheManager(ProxyCacheManager.class);
        GSYVideoType.setShowType(SCREEN_TYPE_DEFAULT);
        ProxyCacheManager.instance().newProxy(context, GlobalParameter.getDownloadFile());
    }


    private void initSDK(){
        // EventBus 事件总线
        EventBusManager.init();
        // Crash 捕捉界面
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
                .enabled(true)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                // 重启的 Activity
                .restartActivity(SplashActivity.class)
                .errorActivity(CrashActivity.class)
                // 设置监听器
                //.eventListener(new YourCustomEventListener())
                .apply();
    }

    public static Context getInstance(){
        return context;
    }
}

