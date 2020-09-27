package com.example.videoplayermanager.common;

import com.example.videoplayermanager.other.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.videoplayermanager.common.GlobalParameter.IMAGE_LOGO_FOLDER;

public class FileUtil {

    public static void createLogoDir(){
        File dir=new File(IMAGE_LOGO_FOLDER);
        if (dir.exists()){
            Logger.e("Logo文件夹已存在无需创建！");
        }else {
            dir.mkdirs();
            Logger.e("Logo文件夹创建成功！");
        }
    }

    /**
     * 在Sd卡创建文件
     * @return
     */
    public static File createSdFile(String fileName) throws IOException {
        String finalFileName=IMAGE_LOGO_FOLDER+fileName;
        //Logger.e("最终的文件名和路径："+finalFileName);
        File file=new File(finalFileName);
        if (!file.exists()){
            file.createNewFile();
        }
        return file;
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fileInputStream != null;
                fileInputStream.close();
                assert fileOutputStream != null;
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
