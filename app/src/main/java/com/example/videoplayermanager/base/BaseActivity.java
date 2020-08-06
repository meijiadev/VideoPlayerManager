package com.example.videoplayermanager.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.videoplayermanager.other.EventBusManager;
import com.example.videoplayermanager.other.Logger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<P extends IBasePresenter> extends AppCompatActivity implements IBaseView {
    protected P mPresenter;
    private Unbinder mButterKnife;
    protected boolean isStatusBarEnabled;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e("-------------"+this.getClass().getSimpleName());
        if (getLayoutId()>0){
            setContentView(getLayoutId());
        }
        mButterKnife= ButterKnife.bind(this);
        EventBusManager.register(this);
        mPresenter=bindPresenter();
        mPresenter.attachView(this);
        initView();
        initData();
        if (isStatusBarEnabled){
            initState(this);
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
    private  void initState(Activity activity) {
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

    protected abstract void initView();

    protected abstract void initData();


    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.e("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.e("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.e("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.e("-------------"+this.getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        if (mButterKnife!=null)
        mButterKnife.unbind();
        EventBusManager.unregister(this);
        Logger.e("-------------"+this.getClass().getSimpleName());
    }
}
