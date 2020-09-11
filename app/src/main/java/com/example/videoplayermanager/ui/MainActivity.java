package com.example.videoplayermanager.ui;

import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseDialog;
import com.example.videoplayermanager.base.BaseMvpActivity;
import com.example.videoplayermanager.base.BaseThread;
import com.example.videoplayermanager.contract.MainContract;
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
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
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

    @BindView(R.id.tvTest)
    TextView tvTest;

    @Override
    protected MainPresenter bindPresenter() {
        mPresenter=new MainPresenter();
        return mPresenter;
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceive(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updatePlayVideos:
                if (VideoResourcesManager.getInstance().getVideoModels().size()>0){
                    if (!isReceive&&isStartPlay){
                        Intent intent=new Intent(MainActivity.this,VideoActivity.class);
                        intent.putExtra("videoPosition",0);
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
                    intent.putExtra("videoPosition",0);
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
        ToastUtils.show("退出广告播放！");
    }

}