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
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.presenter.MainPresenter;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static com.example.videoplayermanager.http.Api.APP_DEFAULT_DOMAIN;
import static com.example.videoplayermanager.http.Api.VIDEO_LIST_DOMAIN_NAME;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContract.View, OnPermission {
    @BindView(R.id.tv_time)
    TextView tvTime;
    private String [] permission=new String[]{ Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE};
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
        RetrofitUrlManager.getInstance().putDomain(VIDEO_LIST_DOMAIN_NAME,APP_DEFAULT_DOMAIN);
    }

    @Override
    protected void initData() {
        requestPermission();
        new TimeThread().start();
    }


    /**
     * 请求权限
     */
    private void requestPermission(){
        XXPermissions.with(this)
                .permission(permission)
                .request(this);
    }

    public boolean isRunning=true;
    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(500);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (isRunning);
        }
    }

    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String sysTimeStr= TimeUtils.getCurrentTime1();
                    tvTime.setText(sysTimeStr); //更新时间
                    break;
                default:
                    break;

            }
        }
    };
    /**
     * 权限通过
     * @param granted
     * @param isAll
     */
    @Override
    public void hasPermission(List<String> granted, boolean isAll) {
        Logger.d("权限请求成功");
    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning=false;
    }
}