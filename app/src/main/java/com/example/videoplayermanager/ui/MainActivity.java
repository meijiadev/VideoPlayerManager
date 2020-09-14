package com.example.videoplayermanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.banner.LocalImageLoader;
import com.example.videoplayermanager.base.BaseDialog;
import com.example.videoplayermanager.base.BaseMvpActivity;
import com.example.videoplayermanager.base.BaseThread;
import com.example.videoplayermanager.contract.MainContract;
import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.other.VideoPreLoader;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.presenter.MainPresenter;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.service.DownloadServer;
import com.example.videoplayermanager.service.GuardService;
import com.example.videoplayermanager.tcp.TcpClient;
import com.example.videoplayermanager.ui.dialog.ProgressDialog;
import com.hjq.toast.ToastUtils;
import com.hjq.xtoast.XToast;
import com.tencent.bugly.crashreport.CrashReport;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static com.example.videoplayermanager.http.Api.APP_DEFAULT_DOMAIN;
import static com.example.videoplayermanager.http.Api.VIDEO_LIST_DOMAIN_NAME;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContract.View {
    private boolean isReceive;       //接收到播放列表
    private boolean isStartPlay;     //开始播放
    private BaseDialog progressDialog;
    private XToast xToast;
    @BindView(R.id.tvTest)
    TextView tvTest;
    @BindView(R.id.banner)
    Banner banner;
    private LocalImageLoader localImageLoader;
    private List<Integer> imageResource;

    @Override
    protected MainPresenter bindPresenter() {
        mPresenter=new MainPresenter();
        return mPresenter;
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceive(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updatePlayVideos:
                Logger.e("--------------updatePlayVideos");
                if (VideoResourcesManager.getInstance().getVideoModels().size()>0){
                    if (!isReceive&&isStartPlay){
                        Intent intent=new Intent(MainActivity.this,VideoActivity.class);
                        startActivity(intent);
                    }
                    isReceive=true;
                }
                break;
            case startPlayVideo:
                Logger.e("------------startPlayVideo");
                if (!isStartPlay&&isReceive){
                    Toast.makeText(MainActivity.this,"播放时间到",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this,VideoActivity.class);
                    startActivity(intent);
                }
                isStartPlay=true;
                break;
            case apkDownloadSucceed:
                progressDialog=null;
                File file= (File) messageEvent.getData();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                    Uri uri=Uri.fromFile(file);
                    intent.setDataAndType(uri,"application/vnd.android.package-archive");
                    startActivity(intent);
                }else {
                    Logger.e("---------"+getPackageName());
                    Uri uriFile= FileProvider.getUriForFile(context,"com.example.videoplayermanager.fileprovider",file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uriFile,"application/vnd.android.package-archive");
                    startActivity(intent);
                }
                break;
            case apkDownloadFailed:
                progressDialog=null;
                ToastUtils.show("下载失败!");
                break;
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }




    @Override
    protected void initView() {
        setStatusBarEnabled(true);
        RetrofitUrlManager.getInstance().putDomain(VIDEO_LIST_DOMAIN_NAME,APP_DEFAULT_DOMAIN);
    }

    @Override
    protected void initData() {
        //启动服务
        Intent serviceIntent=new Intent(context, GuardService.class);
        startService(serviceIntent);
        imageResource=new ArrayList<>();
        imageResource.add(R.mipmap.poster_one);
        imageResource.add(R.mipmap.poster_two);
        imageResource.add(R.mipmap.poster_three);
        imageResource.add(R.mipmap.poster_four);
        useBanner();
    }

    public void useBanner(){
        localImageLoader=new LocalImageLoader();
        //样式
       // banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        //加载器
        banner.setImageLoader(localImageLoader);
        //动画效果
        banner.setBannerAnimation(Transformer.ZoomOutSlide);
        //间隔时间
        banner.setDelayTime(3000);
        //是否为自动轮播
        banner.isAutoPlay(true);
        //图片显示的位置
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //图片加载地址
        banner.setImages(imageResource);
        banner.start();
    }



    @OnClick(R.id.tvTest)
    public void onViewClicked(View view){
        if (view.getId()==R.id.tvTest){
            //EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.setAlarmTime));
            //Logger.e("----------------设置测试时间闹钟:"+ TimeUtils.longToDate(System.currentTimeMillis()));
            //startActivity(WebActivity.class);
            isDownloadApk();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        EventBusManager.register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        banner.startAutoPlay();
        EventBusManager.unregister(this);
        if (xToast!=null){
            xToast.cancel();
        }
    }



    /**
     * 下载更新最新版本apk
     */
    private void isDownloadApk(){
        if (progressDialog==null){
            progressDialog=new ProgressDialog.Builder(this)
                    .setTitle("下载进度：")
                    .show();
            Intent intent=new Intent(MainActivity.this, DownloadServer.class);
            startService(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TcpClient.getInstance(context, ClientMessageDispatcher.getInstance()).disConnect();
        VideoPreLoader.getInstance().onDestroy();
        if (xToast!=null){
            xToast.cancel();
            xToast=null;
        }
        ToastUtils.show("退出广告播放！");
    }


    private TextView tvShowDownloadCounts;
    /**
     * 全局浮窗
     * @param
     */
    private void showXToast(){
        if (xToast==null){
            xToast=new XToast(MyApplication.getInstance())
                    .setView(R.layout.xtoast_layout)
                    .setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
                    .setAnimStyle(android.R.style.Animation_Dialog)
                    .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP)
                    .show();
            tvShowDownloadCounts= (TextView) xToast.findViewById(R.id.tvDownloadTimes);
            tvShowDownloadCounts.setText(0+"/"+VideoPreLoader.getInstance().getUrls().size());
            Logger.e("显示浮窗！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void updateVideos(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case allPlayVideos:
                showXToast();
                break;
            case downloadIndex:
                int hasDownload= (int) messageEvent.getData()+1;
                int totalCount=VideoPreLoader.getInstance().getUrls().size();
                tvShowDownloadCounts.setText(hasDownload+"/"+totalCount);
                break;
            case downloadFinish:
                ToastUtils.show("已全部下载！");
                new Handler().postDelayed(()->{
                    if (xToast!=null)
                        xToast.cancel();
                        xToast=null;
                },1000);
                break;

        }
    }

}