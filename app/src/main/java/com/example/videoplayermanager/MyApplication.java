package com.example.videoplayermanager;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.ProxyCacheManager;
import com.example.videoplayermanager.ui.CrashActivity;
import com.example.videoplayermanager.ui.SplashActivity;
import com.google.gson.GsonBuilder;
import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastWhiteStyle;
import com.shuyu.gsyvideoplayer.cache.CacheFactory;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.tencent.bugly.crashreport.CrashReport;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

import static com.example.videoplayermanager.http.Api.APP_AWAIT_SHOW_DOMAIN;
import static com.example.videoplayermanager.http.Api.APP_AWAIT_SHOW_DOMAIN_NAME;
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
        //GlobalParameter.initConfigFile();
        RetrofitUrlManager.getInstance().putDomain(APP_AWAIT_SHOW_DOMAIN_NAME,APP_AWAIT_SHOW_DOMAIN);

    }


    private void initSDK(){
        if (BuildConfig.LOG_DEBUG){
            CrashReport.initCrashReport(context,"d7383637be",true);
        }else {
            CrashReport.initCrashReport(context,"d7383637be",false);
        }
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

        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor() {
            @Override
            public boolean intercept(Toast toast, CharSequence text) {
                boolean intercept = super.intercept(toast, text);
                if (intercept) {
                    Log.e("Toast", "空 Toast");
                } else {
                    Log.i("Toast", text.toString());
                }
                return intercept;
            }
        });
        // 吐司工具类
        ToastUtils.init(this,new ToastWhiteStyle(this));
    }

    public static Context getInstance(){
        return context;
    }
}

