package com.example.videoplayermanager.common;

import com.example.videoplayermanager.other.Logger;

import java.io.File;

import static com.example.videoplayermanager.common.GlobalParameter.IMAGE_LOGO_FOLDER;

class FileUtil {
    public static File createLogoDir(){
        File dir=new File(IMAGE_LOGO_FOLDER);
        if (dir.exists()){
            Logger.e("Logo文件夹已存在无需创建！");
        }else {
            dir.mkdirs();
            Logger.e("Logo文件夹创建成功！");
        }
        return dir;
    }


}
