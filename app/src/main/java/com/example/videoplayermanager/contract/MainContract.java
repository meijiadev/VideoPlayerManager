package com.example.videoplayermanager.contract;

import com.example.videoplayermanager.base.IBasePresenter;
import com.example.videoplayermanager.base.IBaseView;

/**
 * desc:契约类
 */
public interface MainContract {
    interface View extends IBaseView{

    }
    interface Presenter extends IBasePresenter<View>{
        void downloadVideo();
    }
}
