package com.example.videoplayermanager.tcp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import com.example.videoplayermanager.R;
import com.example.videoplayermanager.common.GlobalParameter;
import com.example.videoplayermanager.other.ActivityStackManager;
import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.protobufProcessor.MessageRoute;
import com.example.videoplayermanager.protobufProcessor.dispatcher.BaseMessageDispatcher;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.hjq.xtoast.XToast;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import java.nio.ByteOrder;
import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;


/**
 * 基于OkSocket库的TCP客户端
 * create by ezreal.mei 2019/10/16
 */
public class TcpClient extends BaseSocketConnection {
    public Context context;
    public static TcpClient tcpClient;
    private ConnectionInfo info;
    public IConnectionManager manager;
    private boolean isConnected; //是否连接
    private SocketCallBack socketCallBack;
    private byte[] heads=new byte[4];  //存储头部长度信息的字节数组
    private byte [] bodyLenths=new byte[4];        //存储body体的信息长度
    private XToast xToast;

    /**
     * 获取客户端
     * @param context
     * @param baseMessageDispatcher
     * @return
     */
    public static TcpClient getInstance(Context context, BaseMessageDispatcher baseMessageDispatcher){
        if (tcpClient==null){
            synchronized (TcpClient.class){
                if (tcpClient==null){
                    tcpClient=new TcpClient(context,baseMessageDispatcher);
                }
            }
        }
        return tcpClient;
    }


    private TcpClient(Context context, BaseMessageDispatcher baseMessageDispatcher) {
        this.context=context.getApplicationContext();         //使用Application的context 避免造成内存泄漏
        m_MessageRoute=new MessageRoute(context,this,baseMessageDispatcher);
    }

    /**
     * 创建连接通道
     * @param ip
     * @param port
     */
    public synchronized void createConnect(String ip, int port){
        Logger.e("连接tcp:"+ip+";"+port);
        info=new ConnectionInfo(ip,port);
        manager=OkSocket.open(info);
        OkSocketOptions.Builder clientOptions=new OkSocketOptions.Builder();
        clientOptions.setPulseFeedLoseTimes(100);
        clientOptions.setReaderProtocol(new ReaderProtocol());
        manager.option(clientOptions.build());
        socketCallBack=new SocketCallBack();
        manager.registerReceiver(socketCallBack);
        manager.connect();
    }




    /**
     * 连接的状态信息
     */
    public class SocketCallBack extends SocketActionAdapter{

        public SocketCallBack() {
            super();
        }

        /**
         * 当客户端连接成功会回调这个方法
         * @param info
         * @param action
         */
        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            isConnected=true;
            Logger.e("--------连接成功---------");
            Activity activity= ActivityStackManager.getInstance().getTopActivity();
            if (activity!=null){
                requestLogin(GlobalParameter.ACCOUNT,GlobalParameter.PASSWORD);
                if (xToast!=null){
                    xToast.cancel();
                }
            }
            sendHeartBeat();
        }

        /**
         * 当客户端连接失败会调用
         * @param info
         * @param action
         * @param e
         */
        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            isConnected=false;
        }

        /**
         * 当连接断开时会调用此方法
         * @param info
         * @param action
         * @param e
         */
        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            isConnected=false;
            Activity activity=ActivityStackManager.getInstance().getTopActivity();
            if (activity!=null){
                Logger.e("网络连接断开，当前处于"+activity.getLocalClassName());
                showXToast(activity);
            }
        }

        /**
         * 当接收tcp服务端数据时调用此方法
         * @param info
         * @param action
         * @param data
         */
        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            byte[] headBytes=data.getHeadBytes();
            System.arraycopy(headBytes,8,heads,0,4);
            int headLength=bytesToIntLittle(heads,0);
            try {
                m_MessageRoute.parseBody(data.getBodyBytes(),headLength);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            Logger.d("---------"+action);
        }

    }

    /**
     * 显示全局弹窗
     * @param activity
     */
    private void showXToast(Activity activity){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                xToast=new XToast(activity.getApplication())
                        .setView(R.layout.xtoast_layout)
                        .setDraggable()
                        .setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                        .setAnimStyle(android.R.style.Animation_Dialog)
                        .setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP)
                        .show();
            }
        });
    }

    /**
     * 自定义解析头
     */
    public class ReaderProtocol implements IReaderProtocol{

        /**
         * 返回固定的头部长度
         * @return
         */
        @Override
        public int getHeaderLength() {
            return 12;
        }

        /**
         * 返回不固定长的body包长度
         * @param header
         * @param byteOrder
         * @return
         */
        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {
            if (header == null || header.length < getHeaderLength()) {
                return 0;
            }
            System.arraycopy(header,4,bodyLenths,0,4);
            return bytesToIntLittle(bodyLenths,0)-8;
        }
    }



    /**
     * 以小端模式将byte[]转成int
     */
    public int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    /**
     * 喂狗操作，否则当超过一定次数的心跳发送,未得到喂狗操作后,狗将会将此次连接断开重连.
     */
    public void feedDog(){
        if (manager!=null){
            manager.getPulseManager().feed();
           Logger.d("---喂狗");
        }
    }

    /**
     * 断开连接
     */
    public void disConnect(){
        if (manager!=null){
            manager.unRegisterReceiver(socketCallBack);
            manager.disconnect();
            isConnected=false;
            manager=null;
            Logger.e("断开tcp连接");
        }
    }


    /**
     * 发送消息
     * @param commonHeader
     * @param message
     */
    public void sendData(BaseCmd.CommonHeader commonHeader, GeneratedMessageLite message){
        if (manager!=null){
            byte[] data=m_MessageRoute.serialize(commonHeader,message);
            Logger.d("--------sendData");
            manager.send(new SendData(data));
        }
    }

    private int index=-1;

    public void setIndex(int index){
        this.index=index;
    }

    /**
     * 持续发送心跳
     */
    public void sendHeartBeat(){
        DDRADServiceCmd.ADHeartBeat heartBeat=DDRADServiceCmd.ADHeartBeat.newBuilder()
                .setIndex(index)
                .build();
        BaseCmd.CommonHeader header=BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eAdClient)
                .setToCltType(BaseCmd.eCltType.eAIServer)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnected&&manager!=null){
                    try {
                        manager.getPulseManager().setPulseSendable(new PulseData(m_MessageRoute.serialize(header,heartBeat))).pulse();
                        Logger.d("发送心跳包");
                        Thread.sleep(1000);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 请求视频地址
     */
    public void requestVideoAddress(){
        DDRADServiceCmd.reqVideoSeq reqVideoSeq= DDRADServiceCmd.reqVideoSeq.newBuilder()
                .setWhatever("request")
                .build();
        BaseCmd.CommonHeader header=BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eAdClient)
                .setToCltType(BaseCmd.eCltType.eAIServer)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        sendData(header,reqVideoSeq);
    }


    /**
     * 请求登录
     * @param account
     * @param password
     */
    public void requestLogin(String account,String password){
        BaseCmd.reqLogin mreqLogin=BaseCmd.reqLogin.newBuilder()
                .setUsername(account)
                .setUserpwd(password)
                .setType(BaseCmd.eCltType.eAdClient)
                .build();
        BaseCmd.CommonHeader header=BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eAdClient)
                .setToCltType(BaseCmd.eCltType.eAIServer)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        sendData(header,mreqLogin);
    }


}
