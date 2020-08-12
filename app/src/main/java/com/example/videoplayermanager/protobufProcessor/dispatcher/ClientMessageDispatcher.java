package com.example.videoplayermanager.protobufProcessor.dispatcher;

import com.example.videoplayermanager.other.Logger;
import com.example.videoplayermanager.protobufProcessor.processor.RspHeartBeatProcess;

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
        DDRADServiceCmd.HeartBeat heartBeat= DDRADServiceCmd.HeartBeat.newBuilder().build();
        m_ProcessorMap.put(heartBeat.getClass().toString(),new RspHeartBeatProcess());

        BaseCmd.rspLogin rspLogin= BaseCmd.rspLogin.newBuilder().build();
        Logger.e("------"+rspLogin.getClass().toString());
        //m_ProcessorMap.put(rspLogin.getClass().toString(),new RspLoginProcessor());

        BaseCmd.bcLSAddr bcLSAddr= BaseCmd.bcLSAddr.newBuilder().build();
        //m_ProcessorMap.put(bcLSAddr.getClass().toString(),new ServerInformationProcessor());


    }
}
