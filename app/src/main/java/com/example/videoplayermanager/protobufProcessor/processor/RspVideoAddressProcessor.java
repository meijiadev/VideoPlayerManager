package com.example.videoplayermanager.protobufProcessor.processor;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.example.videoplayermanager.MyApplication;
import com.example.videoplayermanager.common.FileUtil;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.other.MessageEvent;
import com.example.videoplayermanager.other.download.DownLoadCallBack;
import com.example.videoplayermanager.other.download.DownLoadImageService;
import com.example.videoplayermanager.other.download.VideoPreLoader;
import com.example.videoplayermanager.other.VideoResourcesManager;
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
    List<DDRADServiceCmd.VideoInfo> videoInfos;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        videoInfos=new ArrayList<>();
        DDRADServiceCmd.rspVideoSeq rspVideoSeq= (DDRADServiceCmd.rspVideoSeq) msg;
        videoInfos=rspVideoSeq.getInfosList();
        VideoResourcesManager.getInstance().setVideoUrls(videoInfos);
        VideoPreLoader.getInstance().setPreLoadUrls(videoInfos);
        Logger.e("返回所有视频下载地址"+videoInfos.size());
        EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.allPlayVideos));
        for (int i=0;i<videoInfos.size();i++){
            downloadImageLogo(i);
        }

       /* List<String> urls=rspVideoSeq.getUrlList();
        List<VideoModel> videoModels=new ArrayList<>();
        for (int i=0;i<urls.size();i++){
            VideoModel videoModel=new VideoModel(urls.get(i),"测试");
            videoModels.add(videoModel);
        }
        VideoResourcesManager.getInstance().setVideoModels(videoModels);*/

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
