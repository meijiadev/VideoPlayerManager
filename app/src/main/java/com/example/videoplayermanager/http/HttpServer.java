package com.example.videoplayermanager.http;
import com.example.videoplayermanager.bean.VideoMessage;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;


import static com.example.videoplayermanager.http.Api.APP_UPDATE_DOMAIN_NAME;
import static com.example.videoplayermanager.http.Api.VIDEO_LIST_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

/**
 * desc:请求接口
 * time:2020/7/1
 */

public interface HttpServer {
    //下载apk
    @Headers({DOMAIN_NAME_HEADER+APP_UPDATE_DOMAIN_NAME})
    @Streaming
    @GET("links/4636")
    Observable<ResponseBody>downloadApk();

    @Headers({DOMAIN_NAME_HEADER+VIDEO_LIST_DOMAIN_NAME})
    @GET("PageSubArea/TrailerList.api")
    Observable<VideoMessage> requestVideoList();


}
