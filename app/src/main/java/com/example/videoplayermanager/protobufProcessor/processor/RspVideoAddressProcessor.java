package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.bean.VideoInfo;
import com.example.videoplayermanager.common.FileUtil;
import com.example.videoplayermanager.other.ListUtil;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.download.DownLoadCallBack;
import com.example.videoplayermanager.other.download.DownLoadImageService;
import com.example.videoplayermanager.other.download.VideoPreLoader;
import com.example.videoplayermanager.other.VideoResourcesManager;
import com.example.videoplayermanager.protobufProcessor.dispatcher.ClientMessageDispatcher;
import com.example.videoplayermanager.tcp.TcpClient;
import com.google.protobuf.GeneratedMessageLite;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;
import androidx.annotation.RequiresApi;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RspVideoAddressProcessor extends BaseProcessor  {
    List<VideoInfo> videoInfos;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        videoInfos=new ArrayList<>();
        DDRADServiceCmd.rspVideoSeq rspVideoSeq= (DDRADServiceCmd.rspVideoSeq) msg;
        for (DDRADServiceCmd.VideoInfo videoInfo:rspVideoSeq.getInfosList()){
            VideoInfo videoInfo1=new VideoInfo();
            videoInfo1.setUrl(videoInfo.getUrl());
            videoInfo1.setAdType(videoInfo.getAdType());
            videoInfo1.setBusinessInfo(videoInfo.getBusinessInfo());
            videoInfo1.setDuration(videoInfo.getDuration());
            videoInfo1.setFloor(videoInfo.getFloor());
            videoInfo1.setLogo(videoInfo.getLogo());
            videoInfo1.setName(videoInfo.getName());
            videoInfo1.setNumber(videoInfo.getNumber());
            videoInfo1.setProgramNum(videoInfo.getProgramNum());
            videoInfo1.setPutMode(videoInfo.getPutMode());
            videoInfo1.setMd5(videoInfo.getMd5());
            videoInfos.add(videoInfo1);
        }
        Logger.e("返回所有视频下载地址"+videoInfos.size());
        if (!ListUtil.isListEqual(VideoResourcesManager.getInstance().getVideoUrls(),videoInfos)){
            Logger.e("准备下载列表视频！");
            VideoPreLoader.getInstance().setPreLoadUrls(videoInfos);
            EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.allPlayVideos));
            for (int i=0;i<videoInfos.size();i++){
                downloadImageLogo(i);
            }
        }else {
            Logger.e("列表和本地视频一样！");
            TcpClient.getInstance(context, ClientMessageDispatcher.getInstance()).notifyService();
        }
    }

    /**
     * 下载图片
     */
    private void downloadImageLogo(int position){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Context context = MyApplication.context;
                    FutureTarget<File> target = Glide.with(context)
                            .load(videoInfos.get(position).getLogo())
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    final File imageFile = target.get();
                    Logger.e("--------缓存的地址："+imageFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
