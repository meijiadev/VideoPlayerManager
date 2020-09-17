package com.example.videoplayermanager.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.other.ActivityStackManager;
import com.example.videoplayermanager.other.EventBusManager;

import com.example.videoplayermanager.other.Logger;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * desc: Mvp模式Activity的基类
 * @param <P> 泛型是实现了IBasePresenter的类
 */
public abstract class BaseMvpActivity<P extends IBasePresenter> extends AppCompatActivity implements IBaseView {
    protected P mPresenter;
    private Unbinder mButterKnife;
    protected boolean isStatusBarEnabled;
    protected Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("-------------"+this.getClass().getSimpleName());
        if (getLayoutId()>0){
            setContentView(getLayoutId());
        }
        context= MyApplication.context;
        mButterKnife= ButterKnife.bind(this);
        EventBusManager.register(this);
        ActivityStackManager.getInstance().onCreated(this);
        mPresenter=bindPresenter();
        mPresenter.attachView(this);
        initView();
        initData();
        if (isStatusBarEnabled){
            initState();
        }
    }

    /**
     * 是否启用沉浸式状态栏
     * @param statusBarEnabled true启动
     */
    public void setStatusBarEnabled(boolean statusBarEnabled) {
        isStatusBarEnabled = statusBarEnabled;
    }

    /**
     * 沉浸式状态栏（已适配 ）
     */
    private  void initState() {
        Logger.e("启动沉浸式状态栏");
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        ActionBar actionBar = getSupportActionBar();
        //assert仅在Debug版本中有用
        assert actionBar != null;
        actionBar.hide();
    }


    /**
     * 初始化mPresenter
     */
    protected abstract P bindPresenter();

    /**
     * 获取布局
     * @return 布局ID
     */
    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化参数
     */
    protected abstract void initData();


    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        if (mButterKnife!=null)
        mButterKnife.unbind();
        EventBusManager.unregister(this);
        EventBus.getDefault().removeAllStickyEvents();
        ActivityStackManager.getInstance().onDestroyed(this);
        Logger.d("-------------"+this.getClass().getSimpleName());
    }

    /**
     * startActivity 方法优化
     */
    public void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    /**
     * 跳转并销毁当前activity
     * @param cls
     */
    public void startActivityFinish(Class<? extends Activity> cls) {
        startActivityFinish(new Intent(this, cls));
    }

    private void startActivityFinish(Intent intent) {
        startActivity(intent);
        finish();
    }
}
