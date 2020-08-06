package com.example.videoplayermanager.base;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {
    private WeakReference<V> mViewRef;
    public V mView;

    @Override
    public void attachView(V view) {
        mViewRef=new WeakReference<V>(view);
        mView=mViewRef.get();
    }

    @Override
    public void detachView() {
        mViewRef.clear();
        mView=null;
    }
}
