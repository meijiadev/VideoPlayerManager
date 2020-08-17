package com.example.videoplayermanager.other;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.common.GlobalParameter;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * desc: 视频预加载
 * time:2020/8/26
 */
public class VideoPreLoader {

    private static VideoPreLoader videoPreLoader;

    /**
     * 下载线程
     */
    private Thread downloadThread;


    public static VideoPreLoader getInstance() {
        if (videoPreLoader==null){
            videoPreLoader=new VideoPreLoader();
        }
        return videoPreLoader;
    }

    private VideoPreLoader(){

    }

    /**
     * 制定下载队列中的视频
     * @param urls
     */
    public void setPreLoadUrls(List<String> urls){
        if (downloadThread==null){
            downloadThread= new Thread(()->{
                for (int i=0;i<urls.size();i++){
                    //Logger.d("下载到；"+i);
                    realPreload(urls.get(i));
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                downloadThread=null;
            });
        }
        downloadThread.start();
    }

    /**
     * 正式开始下载
     * @param url
     */
    private  void realPreload(String url){
        HttpURLConnection connection;
        HttpProxyCacheServer httpProxyCacheServer= ProxyCacheManager.getProxy(MyApplication.context, GlobalParameter.getDownloadFile());
        String mUrl=httpProxyCacheServer.getProxyUrl(url);
        Logger.d("---------:"+mUrl);
        if (mUrl.contains("http")){
            try {
                URL myURL=new URL(mUrl);
                connection= (HttpURLConnection) myURL.openConnection();
                connection.connect();
                InputStream inputStream=connection.getInputStream();
                byte[]buffer=new byte[1024];
                int download=0;
                do {
                    int numRed=inputStream.read(buffer);
                    download+=numRed;
                    Logger.e("读取下载进度："+download/1024/1024);
                    if (numRed==-1){
                        break;
                    }
                }while (true);
                inputStream.close();
                Logger.e("读取下载完毕！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy(){
        videoPreLoader=null;
        downloadThread=null;

    }



}
