package com.example.videoplayermanager.common;

import android.os.Environment;

import com.example.videoplayermanager.bean.ADConfig;
import com.example.videoplayermanager.other.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * time： 2019/11/11
 * desc： 全局参数
 */
public class GlobalParameter {
    public static final String VIDEO_FOLDER= Environment.getExternalStorageDirectory().getPath()+"/"+"DownloadVideo"+"/";      //存储地址
    public static final String ROBOT_FOLDER_LOG= Environment.getExternalStorageDirectory().getPath()+"/"+"DDRMapLog"+"/";      //日志存储地址
    public static final String ROBOT_FOLDER_DOWNLOAD= Environment.getExternalStorageDirectory().getPath()+"/"+"DDRMapDownload"+"/"; //下载文件夹
    public static final String LOCAL_AD_FILE=Environment.getExternalStorageDirectory().getPath()+"/"+"广告机参数"+"/";
    public static final String AD_CONFIG_NAME="Config.txt";

    public static  String ACCOUNT_1="admin_ad1";
    public static  String ACCOUNT_2="admin_ad2";
    public static  String ACCOUNT_3="admin_ad3";
    public static  String IP="192.168.0.95";
    public static  String PORT="88";
    public static  String ACCOUNT="admin_ad1";
    public static  String PASSWORD="admin_ad";

    public static File getDownloadFile(){
        File dir=new File(VIDEO_FOLDER);
        if (dir.exists()){
            //Logger.e("文件夹已存在，无须创建");
        }else {
            dir.mkdirs();
            Logger.e("文件夹创建");
        }
        return dir;
    }

    /**
     * 参数文件是否存在,不存在就创建
     * @return
     */
    public static void  initConfigFile(){
        new Thread(()->{
            File dir;
            File txtFile;
            GsonBuilder gsonBuilder=new GsonBuilder();
            Gson gson=gsonBuilder.create();
            try {
                dir=new File(LOCAL_AD_FILE);
                if (!dir.exists()){
                    dir.mkdir();
                }
                txtFile=new File(dir,AD_CONFIG_NAME);
                if (!txtFile.exists()){
                    txtFile.createNewFile();
                    ADConfig adConfig=new ADConfig();
                    String jsonMessage=gson.toJson(adConfig);
                    PrintStream printStream=new PrintStream(new FileOutputStream(txtFile));
                    printStream.println(jsonMessage);
                }else {
                    ADConfig adConfig=gson.fromJson(ReadTxtFile(LOCAL_AD_FILE+AD_CONFIG_NAME),ADConfig.class);
                    IP=adConfig.getIP();
                    ACCOUNT=adConfig.getAccount();
                    PASSWORD=adConfig.getPassword();
                    PORT=adConfig.getPort();
                    Logger.e(IP+";"+ACCOUNT+";"+PASSWORD+";"+PORT);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    public static String ReadTxtFile(String strFilePath)
    {
        String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            Logger.d("The File doesn't not exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                Logger.d( "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Logger.d( e.getMessage());
            }
        }
        return content;
    }
}
