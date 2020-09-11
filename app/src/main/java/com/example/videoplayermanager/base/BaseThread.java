package com.example.videoplayermanager.base;

import android.util.Log;

import com.example.videoplayermanager.other.Logger;

import androidx.annotation.Nullable;

/**
 * 线程基类，让所有的线程都继承该类
 */
public class BaseThread extends Thread {


    @Override
    public synchronized void start() {
        Logger.e("线程启动："+this.getClass().toString());
        super.start();
    }

}
