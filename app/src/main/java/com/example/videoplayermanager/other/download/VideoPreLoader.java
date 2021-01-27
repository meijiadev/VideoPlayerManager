package com.example.videoplayermanager.other.download;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.base.BaseThread;
import com.example.videoplayermanager.bean.VideoInfo;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.ActivityStackManager;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MD5Utils;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.ProxyCacheManager;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.hjq.toast.ToastUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import DDRADServiceProto.DDRADServiceCmd;

/**
 * desc: 视频预加载
 * time:2020/8/26
 */
public class VideoPreLoader {

    private static VideoPreLoader videoPreLoader;
    private boolean isRunning;
    private List<VideoInfo> videoInfos;
    /**
     * 下载线程
     */
    private DownloadThread downloadThread;
    public class DownloadThread extends BaseThread{

        @Override
        public void run() {
            super.run();
            for (int i=0;i<videoInfos.size();i++){
                //Logger.d("下载到；"+i);
                String url=videoInfos.get(i).getUrl();
                HttpURLConnection connection;
                HttpProxyCacheServer httpProxyCacheServer= ProxyCacheManager.getProxy(MyApplication.context, GlobalParameter.getDownloadFile());
                String mUrl=httpProxyCacheServer.getProxyUrl(url);
                //Logger.e("正在下载的视频链接:"+url+";"+mUrl+";"+httpProxyCacheServer.isCached(url));
                if (mUrl.contains("http")){
                    Logger.e("正在下载的视频链接:"+url+";"+mUrl+";"+ProxyCacheManager.isNotDownloadUp(url));
                    if (ProxyCacheManager.isNotDownloadUp(url)){
                        Logger.e("当前视频并未下载完成！");
                        //主线程清除缓存
                        ActivityStackManager.getInstance().getTopActivity().runOnUiThread(()->{
                            GSYVideoManager.instance().clearCache(ActivityStackManager.getInstance().getTopActivity(),GlobalParameter.getDownloadFile(),url);
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i--;
                        continue;
                    }
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
                            Logger.d("读取下载进度："+download/1024/1024);
                            if (numRed==-1){
                                break;
                            }
                        }while (true);
                        inputStream.close();
                        Logger.e("读取下载完毕！");
                    } catch (IOException e) {
                        Logger.e("--------------------下载出错-----------------:"+e.getLocalizedMessage());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.downloadFailed));
                        downloadThread=null;
                        return;
                    }
                }
                //通过url解析出视频文件的尾部信息  如：xxxxxx.mp4
                String videoName=getVideoName(url);
                //获取视频文件
                File file=new File(GlobalParameter.getDownloadFile().getPath()+"/"+videoName);
                try {
                    //生成新的Md5值
                    String newMd5=MD5Utils.getFileMD5String(file);
                    //获取该完整视频MD5值
                    String oldMd5=videoInfos.get(i).getMd5();
                    //Logger.e("-------新MD5值："+newMd5+"原始视频Md5："+oldMd5);
                    //校验文件完整性
                    if (oldMd5.isEmpty()){
                        Logger.e("--------Md5值为空此视频不校验！");
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.downloadIndex,i));
                        ToastUtils.show("Md5值为空此视频不校验！");
                    }else if (oldMd5.equals(newMd5)){
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.downloadIndex,i));
                    }else {
                        //主线程清除缓存
                        ActivityStackManager.getInstance().getTopActivity().runOnUiThread(()->{
                            GSYVideoManager.instance().clearCache(ActivityStackManager.getInstance().getTopActivity(),GlobalParameter.getDownloadFile(),url);
                        });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i--;
                        continue;
                        //EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.downloadIndex,i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!isRunning)
                    return;
            }
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.downloadFinish));
            VideoResourcesManager.getInstance().setVideoUrls(videoInfos);
            downloadThread=null;
        }
    }




    /**
     * 获取视频的名字
     * @return
     */
    private String getVideoName(String url){
        String[]strs=url.split("/");
        return strs[strs.length-1];
    }


    public static VideoPreLoader getInstance() {
        if (videoPreLoader==null){
            videoPreLoader=new VideoPreLoader();
        }
        return videoPreLoader;
    }

    private VideoPreLoader(){
        isRunning=true;
    }

    /**
     * 制定下载队列中的视频
     * @param
     */
    public void setPreLoadUrls(List<VideoInfo> videoInfos){
        this.videoInfos=videoInfos;
        if (downloadThread==null){
           downloadThread=new DownloadThread();
           downloadThread.start();
           Logger.e("启动视频下载线程！");
        }
    }

    public List<VideoInfo> getUrls() {
        return videoInfos==null?videoInfos=new ArrayList<>():videoInfos;
    }

    /**
     * 正式开始下载
     * @param url
     */
    private  void realPreload(String url){

    }

    public void onDestroy(){
        downloadThread=null;
        videoPreLoader=null;
        isRunning=false;
    }

}
