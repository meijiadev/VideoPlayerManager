package com.example.videoplayermanager.other;

/**
 * desc: EvenBus的信息传递类
 */
public class MessageEvent {
    private Type type;
    private Object data;
    private String bitmapPath;
    public enum Type{
        updateIPList,          //更新IP列表
        updatePort,           // 更新端口号
        tcpConnected,         //tcp已连接
        LoginSuccess,        //登陆成功

    }

    public MessageEvent(Type type) {
      this.type=type;
    }

    public MessageEvent(Type type, Object object){
        this.type=type;
        this.data=object;
    }


    public Type getType() {
        return type;
    }


    public Object getData(){
        return data;
    }
}