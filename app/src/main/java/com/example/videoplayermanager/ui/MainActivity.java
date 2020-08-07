package com.example.videoplayermanager.ui;

import android.view.View;
import android.widget.Button;

import com.example.videoplayermanager.R;
import com.example.videoplayermanager.base.BaseMvpActivity;
import com.example.videoplayermanager.contract.MainContract;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.presenter.MainPresenter;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContract.View, OnPermission {
    private String [] permission=new String[]{ Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE};
    @BindView(R.id.bt_error)
    Button btError;

    private String s;

    @Override
    protected MainPresenter bindPresenter() {
        mPresenter=new MainPresenter();
        return mPresenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        setStatusBarEnabled(true);
    }

    @Override
    protected void initData() {
        requestPermission();
    }


    @OnClick(R.id.bt_error)
    public void onViewClicked(View view){
        Logger.d("权限请求成功"+s.equals("oooo"));
    }
    /**
     * 请求权限
     */
    private void requestPermission(){
        XXPermissions.with(this)
                .permission(permission)
                .request(this);
    }

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
}