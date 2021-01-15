package com.example.videoplayermanager.common;

import android.app.smdt.SmdtManager;
import android.os.Environment;

import com.example.videoplayermanager.MyApplication;
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
    public static final String ROBOT_FOLDER_LOG= Environment.getExternalStorageDirectory().getPath()+"/"+"DDRADLog"+"/";      //日志存储地址
    public static final String ROBOT_FOLDER_DOWNLOAD= Environment.getExternalStorageDirectory().getPath()+"/"+"VideoPlayerApkDownload"+"/"; //下载文件夹
    public static final String LOCAL_AD_FILE=Environment.getExternalStorageDirectory().getPath()+"/"+"广告机参数"+"/";
    public static final String IMAGE_LOGO_FOLDER=Environment.getExternalStorageDirectory().getPath()+"/"+"ADLogoImage"+"/";    //广告机商家图片
    public static final String AD_CONFIG_NAME="Config.txt";
    //测试视频文件夹，将视频拷贝到该文件夹下打开程序自动播放
    public static final String VIDEO_TEST_5S_FOLDER=Environment.getExternalStorageDirectory().getPath()+"/"+"TestVideo5S"+"/";
    public static final String VIDEO_TEST_10S_FOLDER=Environment.getExternalStorageDirectory().getPath()+"/"+"TestVideo10s"+"/";

    public static  String ACCOUNT_1="admin_ad1";
    public static  String ACCOUNT_2="admin_ad2";
    public static  String ACCOUNT_3="admin_ad3";
    public static  String IP="192.168.0.96";
    public static  String PORT="189";
    public static  String ACCOUNT="admin_ad1";
    public static  String PASSWORD="admin_ad";
    public static int VIDEO_TIME=5;              //视屏播放时长
    public static float PLAY_SPEED=1;
    public static String START_WORK_TIME="00:00";       // 开始播放的时间
    public static String END_WORK_TIME="00:00";         // 结束工作时间

    public static File getDownloadFile(){
        File dir=new File(VIDEO_FOLDER);
        if (dir.exists()){
            //Logger.e("视频缓存文件夹已存在无需创建！");
        }else {
            dir.mkdirs();
            Logger.e("视频缓存文件夹创建");
        }
        return dir;
    }

    /**
     * 创建测试视频文件夹
     */
    public static void initTestFile(){
        File dir=new File(VIDEO_TEST_5S_FOLDER);
        if (!dir.exists()){
            dir.mkdirs();
            Logger.e("创建5S文件夹");
        }
        File dir1=new File(VIDEO_TEST_10S_FOLDER);
        if (!dir1.exists()){
            dir1.mkdirs();
            Logger.e("创建10S文件夹");
        }
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
                    adConfig.setAccount(ACCOUNT);
                    String jsonMessage=gson.toJson(adConfig);
                    //将fileOutputStream包装成字符串写入器
                    PrintStream printStream=new PrintStream(new FileOutputStream(txtFile));
                    printStream.println(jsonMessage);
                }else {
                    ADConfig adConfig=gson.fromJson(ReadTxtFile(LOCAL_AD_FILE+AD_CONFIG_NAME),ADConfig.class);
                    IP=adConfig.getIP();
                    ACCOUNT=adConfig.getAccount();
                    PASSWORD=adConfig.getPassword();
                    PORT=adConfig.getPort();
                    VIDEO_TIME=adConfig.getVideoTime();
                    PLAY_SPEED=adConfig.getSpeed();
                    START_WORK_TIME=adConfig.getStartWorkTime();
                    END_WORK_TIME=adConfig.getEndWorkTime();
                    Logger.e(IP+";"+PORT+";"+ACCOUNT+";"+PASSWORD+";"+VIDEO_TIME+";"+PLAY_SPEED+";"+START_WORK_TIME+";"+END_WORK_TIME);
                    if (END_WORK_TIME.equals("00:00")){
                        txtFile.delete();
                        initConfigFile();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }



    /**
     * 读取配置参数
     * @param strFilePath
     * @return
     */
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
                    //字节读取流
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    //字符串读取
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
