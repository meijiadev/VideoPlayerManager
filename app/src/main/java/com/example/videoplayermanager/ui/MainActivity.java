package com.example.videoplayermanager.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;


import com.example.videoplayermanager.R;
import com.example.videoplayermanager.adapter.VideoListAdapter;
import com.example.videoplayermanager.base.BaseMvpActivity;
import com.example.videoplayermanager.bean.VideoMessage;
import com.example.videoplayermanager.bean.VideoModel;
import com.example.videoplayermanager.contract.MainContract;
import com.example.videoplayermanager.http.HttpManager;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.VideoPreLoader;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.presenter.MainPresenter;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static com.example.videoplayermanager.http.Api.APP_DEFAULT_DOMAIN;
import static com.example.videoplayermanager.http.Api.VIDEO_LIST_DOMAIN_NAME;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContract.View, OnPermission {
    @BindView(R.id.videoRecycler)
    RecyclerView videoRecycler;

    private String [] permission=new String[]{ Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE};
    private List<VideoMessage.TrailersBean> videos=new ArrayList<>();
    private List<VideoModel> videoModels=new ArrayList<>();
    private List<String> urls=new ArrayList<>();
    private VideoListAdapter videoListAdapter;
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
        RetrofitUrlManager.getInstance().putDomain(VIDEO_LIST_DOMAIN_NAME,APP_DEFAULT_DOMAIN);
        videoListAdapter=new VideoListAdapter(R.layout.item_video_list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        videoRecycler.setLayoutManager(linearLayoutManager);
        videoRecycler.setAdapter(videoListAdapter);
        onItemClick();
    }

    @Override
    protected void initData() {
        requestPermission();
    }

    private void onItemClick(){
        videoListAdapter.setOnItemClickListener((adapter, view, position) -> {
            Log.e("onItemClick","----------------d");
            Intent intent=new Intent(MainActivity.this,VideoActivity.class);
            intent.putExtra("videoPosition",position);
            startActivity(intent);
        });
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
        HttpManager.getInstance().getHttpServer().requestVideoList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<VideoMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("-----onSubscribe","");
                    }

                    @Override
                    public void onNext(VideoMessage videoList) {
                        Log.e("-----onNext","");
                        videos=videoList.getTrailers();
                        videoListAdapter.setNewData(videos);
                        videoModels.clear();
                        for (VideoMessage.TrailersBean trailersBean:videos){
                            VideoModel videoModel=new VideoModel(trailersBean.getHightUrl(),trailersBean.getMovieName());
                            videoModels.add(videoModel);
                            urls.add(trailersBean.getHightUrl());
                        }
                        VideoResourcesManager.getInstance().setVideoModels(Collections.unmodifiableList(videoModels));
                        VideoPreLoader.getInstance().setPreLoadUrls(urls);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError:", Objects.requireNonNull(e.getMessage()));
                        Logger.e("电影列表加载失败！");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("-----onComplete","");
                    }
                });

    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {

    }
}