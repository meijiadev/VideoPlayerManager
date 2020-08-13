package com.example.videoplayermanager.ui;

import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseActivity;
import com.example.videoplayermanager.service.GuardService;
import com.example.videoplayermanager.tcp.TcpClient;

import butterknife.BindView;

public class SplashActivity extends BaseActivity implements Animation.AnimationListener {
    private static final int ANIM_TIME = 2000;
    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    private TcpClient tcpClient;
    private String ip="192.168.0.95";
    private int port=88;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
        AlphaAnimation alphaAnimation=new AlphaAnimation(0.4f,1.0f);
        alphaAnimation.setDuration(ANIM_TIME);
        alphaAnimation.setAnimationListener(this);
        ivSplash.startAnimation(alphaAnimation);

    }

    @Override
    protected void initData() {
        //启动服务
        Intent serviceIntent=new Intent(context, GuardService.class);
        startService(serviceIntent);


    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        startActivityFinish(MainActivity.class);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onBackPressed() {
        //禁用返回键
        //super.onBackPressed();

    }
}
