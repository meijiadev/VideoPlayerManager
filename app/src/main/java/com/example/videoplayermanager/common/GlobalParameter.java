package com.example.videoplayermanager.common;

import android.os.Environment;

import com.example.videoplayermanager.other.Logger;

import java.io.File;

/**
 * time： 2019/11/11
 * desc： 全局参数
 */
public class GlobalParameter {
    public static final String VIDEO_FOLDER= Environment.getExternalStorageDirectory().getPath()+"/"+"DownloadVideo"+"/";      //存储地址
    public static final String ROBOT_FOLDER_LOG= Environment.getExternalStorageDirectory().getPath()+"/"+"DDRMapLog"+"/";      //日志存储地址
    public static final String ROBOT_FOLDER_DOWNLOAD= Environment.getExternalStorageDirectory().getPath()+"/"+"DDRMapDownload"+"/"; //下载文件夹

    public static File getDownloadFile(){
        File dir=new File(VIDEO_FOLDER);
        if (dir.exists()){
            Logger.e("文件夹已存在，无须创建");
        }else {
            dir.mkdirs();
            Logger.e("文件夹创建");
        }
        return dir;
    }
}
