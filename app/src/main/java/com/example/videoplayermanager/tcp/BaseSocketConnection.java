package com.example.videoplayermanager.tcp;

import com.example.videoplayermanager.protobufProcessor.MessageRoute;
import com.google.protobuf.GeneratedMessageLite;

import java.io.IOException;
import java.net.Socket;

import DDRCommProto.BaseCmd;


public  class BaseSocketConnection {
    protected MessageRoute m_MessageRoute;

    void readData(byte[] buf,int len) throws IOException {

    }

    void sendData(BaseCmd.CommonHeader commonHeader, GeneratedMessageLite message) throws IOException {

    }
    Socket getSocket(){
        return null;
    }

    void close(){

    }


}
