package com.example.videoplayermanager.protobufProcessor.dispatcher;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.protobufProcessor.processor.NotifyCurrentVideoSeqProcessor;
import com.example.videoplayermanager.protobufProcessor.processor.RspHeartBeatProcess;
import com.example.videoplayermanager.protobufProcessor.processor.RspLoginProcessor;
import com.example.videoplayermanager.protobufProcessor.processor.RspVideoAddressProcessor;

import DDRADServiceProto.DDRADServiceCmd;
import DDRCommProto.BaseCmd;



public class ClientMessageDispatcher extends BaseMessageDispatcher {
    static ClientMessageDispatcher clientMessageDispatcher;

    public static ClientMessageDispatcher getInstance(){
        if (clientMessageDispatcher==null){
            clientMessageDispatcher=new ClientMessageDispatcher();
        }
        return clientMessageDispatcher;
    }

    private ClientMessageDispatcher(){
        DDRADServiceCmd.ADHeartBeat heartBeat= DDRADServiceCmd.ADHeartBeat.newBuilder().build();
        m_ProcessorMap.put(heartBeat.getClass().toString(),new RspHeartBeatProcess());

        BaseCmd.rspLogin rspLogin= BaseCmd.rspLogin.newBuilder().build();
        Logger.e("------"+rspLogin.getClass().toString());
        m_ProcessorMap.put(rspLogin.getClass().toString(),new RspLoginProcessor());

        DDRADServiceCmd.rspVideoSeq rspVideoSeq= DDRADServiceCmd.rspVideoSeq.newBuilder().build();
        m_ProcessorMap.put(rspVideoSeq.getClass().toString(),new RspVideoAddressProcessor());

        DDRADServiceCmd.notifyCurrentVideoSeq notifyCurrentVideoSeq= DDRADServiceCmd.notifyCurrentVideoSeq.newBuilder().build();
        m_ProcessorMap.put(notifyCurrentVideoSeq.getClass().toString(),new NotifyCurrentVideoSeqProcessor());




    }
}
