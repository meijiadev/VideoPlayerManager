# VideoPlayerManager 广告机项目
一款视频播放器，支持边播边缓存、预缓存. 列表循环播放

增加开机自启动，崩溃重启

-对MVP进行了封装 

-基于protobuf的数据结构

-增加定时任务

-增加Banner轮播图



 
   用到了第三方库：
   
    implementation 'com.google.protobuf:protobuf-java:3.4.0'
    implementation 'com.google.protobuf:protoc:3.4.0'
    implementation 'com.shuyu:GSYVideoPlayer:7.1.4'
    // ButterKnife 注解库：https://github.com/JakeWharton/butterknife
    implementation 'com.jakewharton:butterknife:10.1.0'
    annotationProcessor 'com.jakewharton:butterknife-compiler:10.1.0'
    //万能适配器
    implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.47'

    implementation 'me.jessyan:retrofit-url-manager:1.4.0'
    implementation 'com.google.code.gson:gson:2.8.5'   //gson解析
    implementation 'com.squareup.retrofit2:retrofit:2.3.0' //retrofit2.0
    implementation 'com.squareup.retrofit2:adapter-rxjava2:2.3.0' //配合Rxjava 使用
    implementation 'com.squareup.retrofit2:converter-gson:2.1.0' //ConverterFactory的Gson:
    implementation 'io.reactivex.rxjava2:rxjava:2.1.8' //rxjava
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
    implementation 'androidx.recyclerview:recyclerview:1.1.0'//rxandroid 线程调度

    // 权限请求框架：https://github.com/getActivity/XXPermissions
    implementation 'com.hjq:xxpermissions:6.0'

    // EventBus 事件总线：https://github.com/greenrobot/EventBus
    implementation "org.greenrobot:eventbus:3.1.1"
    annotationProcessor 'org.greenrobot:eventbus-annotation-processor:3.1.1'

    // 本地异常捕捉框架：https://github.com/Ereza/CustomActivityOnCrash
    implementation 'cat.ereza:customactivityoncrash:2.2.0'
    //oksocket的tcp连接库
        api 'com.tonystark.android:socket:latest.release'

        implementation 'com.hjq:xtoast:5.5'

        implementation 'com.github.BolexLiu:TimeTask:1.1'
        //百分比布局
        //noinspection GradleCompatible
        implementation 'com.android.support:percent:27.0.2'

        //今日头条适配方案框架
        implementation 'me.jessyan:autosize:0.9.5'

        // 吐司工具类：https://github.com/getActivity/ToastUtils
        implementation 'com.hjq:toast:8.2'
        //图片下载
        implementation 'com.github.bumptech.glide:glide:4.9.0'
        annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
        implementation 'com.github.open-android:RoundedImageView:v1.0.0'
        implementation 'com.tencent.bugly:crashreport:latest.release' //其中lat est.release指代最新版本号，也可以指定明确的版本号
        implementation 'com.tencent.bugly:nativecrashreport:latest.release' //其中latest.release指代最新版本号，也可以指定明确的版本号
        //AgentWeb 一个基于的 Android WebView 的库
        implementation 'com.just.agentweb:agentweb-androidx:4.1.4' // (必选)
        implementation 'com.just.agentweb:filechooser-androidx:4.1.4'// (可选)
        implementation 'com.youth.banner:banner:1.4.10'   //banner依赖
