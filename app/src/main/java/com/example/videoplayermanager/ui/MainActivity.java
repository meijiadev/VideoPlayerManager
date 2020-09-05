package com.example.videoplayermanager.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.example.videoplayermanager.R;
import com.example.videoplayermanager.adapter.VideoListAdapter;
import com.example.videoplayermanager.base.BaseMvpActivity;
import com.example.videoplayermanager.contract.MainContract;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.TimeUtils;
import com.example.videoplayermanager.other.VideoPreLoader;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.presenter.MainPresenter;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.service.GuardService;
import com.example.videoplayermanager.tcp.TcpClient;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import butterknife.BindView;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static com.example.videoplayermanager.http.Api.APP_DEFAULT_DOMAIN;
import static com.example.videoplayermanager.http.Api.VIDEO_LIST_DOMAIN_NAME;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContract.View {
    @BindView(R.id.tv_time)
    TextView tvTime;


    private boolean isReceive;       //接收到播放列表
    private boolean isStartPlay;     //开始播放

    @Override
    protected MainPresenter bindPresenter() {
        mPresenter=new MainPresenter();
        return mPresenter;
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onReceive(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case LoginSuccess:
                Logger.e("接收粘性事件LoginSuccess");
                break;
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





    @Override
    protected void onDestroy() {
        super.onDestroy();
        TcpClient.getInstance(context, ClientMessageDispatcher.getInstance()).disConnect();
        VideoPreLoader.getInstance().onDestroy();
        ToastUtils.show("退出广告播放！");
    }
}