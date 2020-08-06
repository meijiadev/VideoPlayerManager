package com.example.videoplayermanager.base;

public interface IBasePresenter <V extends IBaseView> {
    /**
     * 绑定
     * @param view 绑定的View
     */
    void attachView(V view);

    /**
     * 解绑
     */
    void detachView();

}
