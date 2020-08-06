package com.example.videoplayermanager;

import com.example.videoplayermanager.base.BaseActivity;
import com.example.videoplayermanager.contract.MainContract;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.presenter.MainPresenter;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends BaseActivity<MainPresenter>implements MainContract.View, OnPermission {
    private String [] permission=new String[]{ Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE};
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