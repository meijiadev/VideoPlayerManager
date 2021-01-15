package com.example.videoplayermanager.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.smdt.SmdtManager;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.videoplayermanager.BuildConfig;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.banner.LocalImageLoader;
import com.example.videoplayermanager.base.BaseDialog;
import com.example.videoplayermanager.base.BaseMvpActivity;
import com.example.videoplayermanager.base.BaseThread;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.contract.MainContract;
import com.example.videoplayermanager.other.ActivityStackManager;
import com.example.videoplayermanager.other.LogcatHelper;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.NetWorkUtil;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.other.download.VideoPreLoader;
import com.example.videoplayermanager.presenter.MainPresenter;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.service.DownloadServer;
import com.example.videoplayermanager.service.GuardService;
import com.example.videoplayermanager.tcp.TcpClient;
import com.example.videoplayermanager.ui.dialog.ProgressDialog;
import com.hjq.toast.ToastUtils;
import com.hjq.xtoast.XToast;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import DDRADServiceProto.DDRADServiceCmd;
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
    private boolean isDownloadApk;              //是否在下载apk
    private boolean isDownloadVideos;          //是否在下载视频
    private List<Integer> imageResource;
    private List<String> pathList;             //测试视频本地连接
    private TcpClient tcpClient;
    private SmdtManager smdtManager ;
    private boolean isCharging;             //是否在充电


    @Override
    protected MainPresenter bindPresenter() {
        mPresenter=new MainPresenter();
        return mPresenter;
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceive(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case startPlayNextVideoAtOnce:
                isReceive=true;
                Activity activity=ActivityStackManager.getInstance().getTopActivity();
                if (activity==null)
                    activity=this;
                if (!isDownloadApk&&!isDownloadVideos&&ActivityStackManager.getInstance().getTopActivity().getLocalClassName().contains("MainActivity")){
                    Logger.e("-----startPlayNextVideoAtOnce启动VideoActivity");
                    Intent intent=new Intent(MainActivity.this,VideoActivity.class);
                    startActivity(intent);
                }
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
            case notifyScreenAction:
                int value= (int) messageEvent.getData();
                if (value == 0) {
                    isCharging=true;
                }else if (value ==1){
                    isCharging=false;
                }
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

        tcpClient=TcpClient.getInstance(MyApplication.context,ClientMessageDispatcher.getInstance());
        if (tcpClient.isConnected()){
            tcpClient.disConnect();
        }
        tcpClient.createConnect(GlobalParameter.IP,GlobalParameter.PORT);
        imageResource=new ArrayList<>();
        imageResource.add(R.mipmap.poster_one);
        imageResource.add(R.mipmap.poster_two);
        imageResource.add(R.mipmap.poster_three);
        imageResource.add(R.mipmap.poster_four);
        useBanner();
        //goTestVideoPlayer();
        Logger.e("------------当前版本时间------------："+BuildConfig.BUILD_TIME);
        new TimeThread().start();

    }

    public void useBanner(){
        LocalImageLoader localImageLoader = new LocalImageLoader();
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

    /**
     * 播放测试视频
     */
    private void goTestVideoPlayer() {
        pathList = new ArrayList<>();
        File videoDir;
        String selection = MediaStore.Video.Media.DATA + " like ?";
        String dirPath;
        if (GlobalParameter.VIDEO_TIME == 5) {
            dirPath = GlobalParameter.VIDEO_TEST_5S_FOLDER;
            Logger.e("播放5秒视频！");
        } else if (GlobalParameter.VIDEO_TIME == 10) {
            dirPath = GlobalParameter.VIDEO_TEST_10S_FOLDER;
            Logger.e("播放10秒视频！");
        } else {
            ToastUtils.show("请检查配置参数");
            return;
        }
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Video.Media.DATA},
                selection, new String[]{dirPath+"%"}, MediaStore.MediaColumns.DATE_MODIFIED + " DESC");
        if (cursor != null) {
            int dataindex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            while (cursor.moveToNext()) {
                String path = cursor.getString(dataindex);
                pathList.add(path);
                Logger.e("----------视频数量："+pathList.size()+";"+path);
            }
            cursor.close();
            if (pathList.size() > 0 && !isDownloadApk) {
                VideoResourcesManager.getInstance().setVideoPath(pathList);
                startActivity(VideoActivity.class);
            }
        }
    }




    @OnClick({R.id.tvTest,R.id.tvClear,R.id.tvTest2})
    public void onViewClicked(View view){
        if (view.getId()==R.id.tvTest){
            isDownloadApk();
        }else if (view.getId()==R.id.tvClear){
            ToastUtils.show("正在清空所有缓存文件");
            GSYVideoManager.instance().clearDefaultCache(MyApplication.context,GlobalParameter.getDownloadFile(),null);
            Logger.e("清空所有缓存视频！");
        }else if(view.getId()==R.id.tvTest2){
            ToastUtils.show("点亮屏幕！");
            smdtManager = SmdtManager.create(MyApplication.context);
            smdtManager.smdtSetLcdBackLight(1);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
            isDownloadApk=true;
        }
    }


    @Override
    protected void onDestroy() {
        isRunning=false;
        TcpClient.getInstance(context, ClientMessageDispatcher.getInstance()).disConnect();
        VideoResourcesManager.getInstance().setVideoUrls(new ArrayList<>());
        VideoPreLoader.getInstance().onDestroy();
        destroyXToast();
        LogcatHelper.getInstance(context).stop();
        Logger.e("---------------onDestroy-----------------");
        ToastUtils.show("退出广告播放管理器");
        if (smdtManager == null) {
            smdtManager.smdtSetLcdBackLight(1);
            Logger.e("退出播放程序前，让屏幕点亮");
        }
        super.onDestroy();
    }


    private TextView tvShowDownloadCounts,tvText;
    /**
     * 全局下载视频浮窗
     * @param
     */
    @SuppressLint("SetTextI18n")
    private void showXToast(){
        if (xToast==null){
            xToast=new XToast(MyApplication.getInstance())
                    .setView(R.layout.xtoast_layout)
                    .setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
                    .setAnimStyle(android.R.style.Animation_Dialog)
                    .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP)
                    .show();
            tvShowDownloadCounts= (TextView) xToast.findViewById(R.id.tvDownloadTimes);
            tvText=(TextView)xToast.findViewById(R.id.tvHasDownload);
            Logger.e("显示浮窗！");
        }
    }

    /**
     * 销毁弹窗
     */
    private void destroyXToast(){
        if (xToast!=null){
            xToast.cancel();
            xToast=null;
            Logger.e("销毁提示浮窗！");
        }
    }

    private boolean isRunning;

    public  class TimeThread extends BaseThread {
        public TimeThread(){
            isRunning=true;
            smdtManager = SmdtManager.create(MyApplication.context);
        }
        @Override
        public void run() {
            super.run();
            while (isRunning){
                String currentTime= TimeUtils.getCurrentTime();
                if (!isCharging){
                    try {
                        //如果当前时间大于工作起始时间并且在工作结束时间前面则让屏幕点亮
                        if(!GlobalParameter.START_WORK_TIME.equals("00:00")){
                            if(TimeUtils.compareTime(GlobalParameter.START_WORK_TIME,GlobalParameter.END_WORK_TIME)){
                                if (TimeUtils.compareTime(GlobalParameter.START_WORK_TIME,currentTime)&&TimeUtils.compareTime(currentTime,GlobalParameter.END_WORK_TIME)){
                                    Logger.e("工作时间----"+currentTime);
                                    smdtManager.smdtSetLcdBackLight(1);
                                }
                                if (TimeUtils.compareTime(GlobalParameter.END_WORK_TIME,currentTime)|TimeUtils.compareTime(currentTime,GlobalParameter.START_WORK_TIME)){
                                    Logger.e("休息时间----"+currentTime);
                                    smdtManager.smdtSetLcdBackLight(0);
                                }
                            }else {
                                if (TimeUtils.compareTime(GlobalParameter.END_WORK_TIME,currentTime)&&TimeUtils.compareTime(currentTime,GlobalParameter.START_WORK_TIME)){
                                    Logger.e("休息时间----"+currentTime);
                                    smdtManager.smdtSetLcdBackLight(0);
                                }
                                if (TimeUtils.compareTime(GlobalParameter.START_WORK_TIME,currentTime)|TimeUtils.compareTime(currentTime,GlobalParameter.END_WORK_TIME)){
                                    Logger.e("工作时间----"+currentTime);
                                    smdtManager.smdtSetLcdBackLight(01);
                                }
                            }
                        }else{
                            Logger.e("未设置休眠唤醒时间！");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void updateVideos(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case allPlayVideos:
                destroyXToast();
                showXToast();
                tvShowDownloadCounts.setText(0+"/"+VideoPreLoader.getInstance().getUrls().size());
                Logger.e("获取今日播放视频下载链接！");
                isDownloadVideos=true;
                break;
            case downloadIndex:
                if (xToast!=null){
                    int hasDownload= (int) messageEvent.getData()+1;
                    int totalCount=VideoPreLoader.getInstance().getUrls().size();
                    tvShowDownloadCounts.setText(hasDownload+"/"+totalCount);
                }
                break;
            case downloadFinish:
                ToastUtils.show("已全部下载！");
                new Handler().postDelayed(this::destroyXToast,1000);
                isDownloadVideos=false;
                // 视频下载完，如果当前不是出于更新apk或者当前界面是处于MainActivity 则直接进入视频播放界面
                Activity activity=ActivityStackManager.getInstance().getTopActivity();
                if (activity==null){
                    activity=this;
                }
                Logger.e("当前栈顶activity："+activity.getLocalClassName());
                if (isReceive&&!isDownloadApk&&activity.getLocalClassName().contains("MainActivity")){
                    Logger.e("--------downloadFinish启动VideoActivity");
                    Intent intent=new Intent(MainActivity.this,VideoActivity.class);
                    startActivity(intent);
                }else {
                    //如果不是处于MainActivity或者处于未接受到播放列表的情况下
                    TcpClient.getInstance(context,ClientMessageDispatcher.getInstance()).notifyService();
                }
                break;
            case downloadFailed:
                new Handler().postDelayed(this::destroyXToast,1000);
                ToastUtils.show("下载出错，请检查当前网络！");
                TcpClient.getInstance(context,ClientMessageDispatcher.getInstance()).requestVideoAddress();
                break;
            case downloadProgressService:
                DDRADServiceCmd.notifyDownloadProgress notifyDownloadProgress= (DDRADServiceCmd.notifyDownloadProgress) messageEvent.getData();
                int total=notifyDownloadProgress.getTotalNum();
                int currentNum=notifyDownloadProgress.getCurrentNum();
                float currentProgress=notifyDownloadProgress.getCurrentProgress();
                if (total==currentNum&&currentProgress==1.0f){
                    destroyXToast();
                }else {
                    showXToast();
                    tvText.setVisibility(View.GONE);
                    int value=(int)(currentProgress*100);
                    tvShowDownloadCounts.setText("总文件数："+total+" 当前下载："+currentNum+" 当前进度："+value+"%");
                }
                break;
        }
    }

}